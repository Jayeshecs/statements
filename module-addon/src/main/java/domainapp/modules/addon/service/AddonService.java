/**
 * 
 */
package domainapp.modules.addon.service;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.config.ConfigurationService;
import org.apache.isis.applib.services.i18n.TranslatableString;

import domainapp.modules.addon.dom.Addon;
import domainapp.modules.addon.dom.AddonType;
import domainapp.modules.base.entity.NamedQueryConstants;
import domainapp.modules.base.plugin.AddonException;
import domainapp.modules.base.plugin.IAddonApi;
import domainapp.modules.base.plugin.IAddonService;
import domainapp.modules.base.service.AbstractService;
import lombok.extern.slf4j.Slf4j;

/**
 * @author jayeshecs
 *
 */
/**
 * @author Prajapati
 *
 */
@DomainService(
		nature = NatureOfService.DOMAIN,
		repositoryFor = Addon.class
)
@Slf4j
public class AddonService extends AbstractService<Addon> implements IAddonService {

	public AddonService() {
		super(Addon.class);
	}
	
	@Programmatic
	public List<Addon> all() {
		return search(NamedQueryConstants.QUERY_ALL);
	}

	@Programmatic
	public Addon create(String name, String description, AddonType addonType, String className, String library, Boolean embedded) {
		Addon newPlugin = createNoPersist(name, description, addonType, className, library, embedded);
		Addon plugin = repositoryService.persistAndFlush(newPlugin);
    	return plugin;
	}

	@Programmatic
	public Addon createNoPersist(String name, String description, AddonType addonType, String className, String library, Boolean embedded) {
		return Addon.builder().name(name).description(description).addonType(addonType).className(className).library(library).embedded(embedded).build();
	}
	
	private Map<String, ? super IAddonApi> addonRegistry = new ConcurrentHashMap<>();
	private Map<String, URLClassLoader> libraryRegistry = new ConcurrentHashMap<>();

	@SuppressWarnings("unchecked")
	@Programmatic
	@Override
	public <T extends IAddonApi> T get(String addonName) throws AddonException {
		T addonApi = (T) addonRegistry.get(addonName);
		if (addonApi == null) {
			Addon addon = null;
			List<Addon> list = search(NamedQueryConstants.QUERY_FIND_BY_NAME, "name", addonName);
			for (Addon addon2 : list) {
				if (addon2.getName().equals(addonName)) {
					addon = addon2;
				}
			}
			if (addon == null) {
				return null;
			}
			try {
				Class<T> addonClass = null;
				if (addon.getEmbedded()) {
					addonClass = (Class<T>) Class.forName(addon.getClassName());
				} else {
					URLClassLoader classLoader = libraryRegistry.get(addon.getLibrary());
					if (classLoader == null) {
						String addonLocationStr = configurationService.getProperty("addon.location", System.getProperty("user.home") + "/.addon/");
						classLoader = new URLClassLoader(new URL[] { new File(addonLocationStr, addon.getLibrary()).toURI().toURL() }, Thread.currentThread().getContextClassLoader());
						libraryRegistry.put(addon.getLibrary(), classLoader);
					}
					addonClass = (Class<T>) classLoader.loadClass(addon.getClassName());
				}
				addonApi = addonClass.newInstance();
				log.info("Installing addon - " + addon.getName() + " (" + addon.getClassName() + ")");
				addonApi.install();
				log.info("Registering addon - " + addon.getName() + " (" + addonApi.toString() + ")");
				addonRegistry.put(addon.getName(), addonApi);
			} catch (ClassNotFoundException e) {
				throw new AddonException("Addon class not found :: " + e.getMessage(), e);
			} catch (MalformedURLException e) {
				throw new AddonException("Malformed URL Exception occurred - " + e.getMessage(), e);
			} catch (InstantiationException | IllegalAccessException e) {
				throw new AddonException("Exception occurred while creating new instance of addon - " + e.getMessage(), e);
			} catch (Exception e) {
				throw new AddonException("Unexpected exception occurred while loading addon - " + addonName, e);
			}
		}
		return (T)addonApi;
	}
	
	@Programmatic
	public void deploy(Properties properties, Path tempFile, String libraryName) throws AddonException {
		
		log.info("Reading addon metadata from properties file");
    	/**
    	 * 1. Read metadata of library i.e. library properties
    	 */
		if (!properties.containsKey("addon.count") || !properties.containsKey("addon.type")) {
    		log.error("Requried properties not found in library properties file");
			throw new AddonException(TranslatableString.tr("Invalid addon library properties file"), getClass());
		}
		
		/**
		 * Get or Create addon type
		 */
		AddonType addonType = null;
		String addonTypeName = properties.getProperty("addon.type");
		log.info("Ensure to have addon type - " + addonTypeName);
		List<AddonType> addonTypeList = addonTypeService.search(NamedQueryConstants.QUERY_FIND_BY_NAME, "name", addonTypeName);
		if (addonTypeList != null && !addonTypeList.isEmpty()) {
			for (AddonType type : addonTypeList) {
				if (type.getName().equals(addonTypeName)) {
					addonType = type;
					break;
				}
			}
		}
		if (addonType == null) {
			log.info(String.format("Addon type with name %s not found and hence creating new", addonTypeName));
			addonType = addonTypeService.create(addonTypeName, properties.getProperty("addon.type.description", "Description of " + addonTypeName));
		}
		/**
		 * Prepare addon detail from properties file
		 */
		log.info("Validating add-on detail...");
		int addonCount = Integer.parseInt(properties.getProperty("addon.count"));
		List<String[]> addonDetails = new ArrayList<>();
		for (int i = 1; i <= addonCount; i++) {
			String addonName = properties.getProperty("addon.name." + i);
			String addonDescription = properties.getProperty("addon.description." + i);
			String className = properties.getProperty("addon.className." + i);
			String embedded = properties.getProperty("addon.embedded." + i);
			
			if (addonName == null || addonName.trim().isEmpty()) {
				throw new AddonException(TranslatableString.tr("Value for property 'addon.name.${counter}' is not found", "counter", i), getClass());
			}
			
			if (className == null || className.trim().isEmpty()) {
				throw new AddonException(TranslatableString.tr("Value for property 'addon.className.${counter}' is not found", "counter", i), getClass());
			}
			
			addonDetails.add(new String[]{addonName, addonDescription, className, embedded});
		}
    	/**
    	 * Validate all addon entries from metadata against jar file
    	 */
		log.info("Test load of addon from library or as embedded ...");
		for (String[] addonDetail : addonDetails) {
			String addonName = addonDetail[0];
			//String addonDescription = addonDetail[1];
			String className = addonDetail[2];
			String embedded = addonDetail[3];
			Class<?> addonClass = null;
			try {
				if (Boolean.parseBoolean(embedded)) {
					addonClass = Class.forName(className);
				} else {
					try (
							URLClassLoader classLoader = new URLClassLoader(new URL[] { tempFile.toUri().toURL() }, Thread.currentThread().getContextClassLoader())
							) {
						addonClass = classLoader.loadClass(className);
					}
				}
				if (!IAddonApi.class.isAssignableFrom(addonClass)) {
					throw new AddonException(TranslatableString.tr("Class '${className}' does not implement IAddonApi interface", "className", className), getClass());
				}
			} catch (ClassNotFoundException e) {
				throw new AddonException(TranslatableString.tr("Class '${className}' not found for addon ${addon}", "className", className, "addon", addonName), getClass());
			} catch (MalformedURLException e) {
				throw new AddonException(TranslatableString.tr("Malformed URL exception occurred while loading class '${className}' for addon ${addon}", "className", className, "addon", addonName), getClass());
			} catch (IOException e) {
				throw new AddonException(TranslatableString.tr("I/O error occurred while loading class '${className}' for addon ${addon}", "className", className, "addon", addonName), getClass());
			}
		}
    	/**
    	 * All found good then create addon entries
    	 */
		log.info("Creating/updating addon(s) ...");
		List<Addon> addonToSave = new ArrayList<>();
		for (String[] addonDetail : addonDetails) {
			String addonName = addonDetail[0];
			String addonDescription = addonDetail[1];
			String className = addonDetail[2];
			String embeddedStr = addonDetail[3];
			boolean embedded = Boolean.parseBoolean(embeddedStr);
			
			Addon addon = null;
			List<Addon> addonList = search(NamedQueryConstants.QUERY_FIND_BY_NAME, "name", addonName);
			if (addonList != null && !addonList.isEmpty()) {
				for (Addon addon2 : addonList) {
					if (addon2.getName().equals(addonName)) {
						addon = addon2;
						break;
					}
				}
			}
			if (addon == null) {
				addon = createNoPersist(addonName, addonDescription, addonType, className, embedded ? null : libraryName, embedded);
			} else {
				addon.setDescription(addonDescription);
				addon.setClassName(className);
				addon.setLibrary(embedded ? null : libraryName);
				addon.setEmbedded(embedded);
			}
			addonToSave.add(addon);
		}
		save(addonToSave);
		/**
		 * Move addon library (jar) file to addon location
		 */
		if (tempFile != null) {
			String addonFolder = configurationService.getProperty("addon.folder", System.getProperty("user.home") + "/.addon/");
			try {
				log.info("Deploying addon library (jar) file to addon location - " + addonFolder);
				File addonFolderFile = new File(addonFolder);
				addonFolderFile.mkdirs();
				Files.copy(tempFile, new File(addonFolderFile, libraryName).toPath(), StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				throw new AddonException(TranslatableString.tr("I/O error occurred while transferring library '${libraryName}' file to addon location ${addonLocation}", "libraryName", libraryName, "addonLocation", addonFolder), getClass());
			}	    	
		}
	}

	@Inject
	AddonTypeService addonTypeService;
	
	@Inject
	ConfigurationService configurationService;
	
}
