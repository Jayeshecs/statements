/**
 * 
 */
package domainapp.modules.addon.view.dashboard;

import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.List;
import java.util.Properties;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.ActionLayout.Position;
import org.apache.isis.applib.annotation.Collection;
import org.apache.isis.applib.annotation.CollectionLayout;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.LabelPosition;
import org.apache.isis.applib.annotation.Nature;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.PromptStyle;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.i18n.TranslatableString;
import org.apache.isis.applib.services.message.MessageService;
import org.apache.isis.applib.value.Blob;

import domainapp.modules.addon.dom.Addon;
import domainapp.modules.addon.dom.AddonType;
import domainapp.modules.addon.service.AddonService;
import domainapp.modules.addon.service.AddonTypeService;
import domainapp.modules.base.entity.NamedQueryConstants;
import domainapp.modules.base.entity.WithName;
import domainapp.modules.base.plugin.AddonException;
import lombok.extern.slf4j.Slf4j;

/**
 * Manage addons and their types
 * 
 * @author jayeshecs
 */
@DomainObject(
		nature = Nature.VIEW_MODEL,
		objectType = "addon.ManageAddonDashboard"
)
@Slf4j
public class ManageAddonDashboard {

	public String title() {
		return "Manage Addon";
	}
	
	@Collection
	@CollectionLayout(defaultView = "table")
	public List<AddonType> getAddonTypes() {
		return addonTypeService.all();
	}
	
	@Collection
	@CollectionLayout(defaultView = "table")
	public List<Addon> getAddons() {
		return addonService.all();
	}
	
	@Action(associateWith = "addonTypes", semantics = SemanticsOf.NON_IDEMPOTENT_ARE_YOU_SURE, typeOf = AddonType.class)
	@ActionLayout(named = "Delete")
	public ManageAddonDashboard deleteAddonTypes(List<AddonType> addonTypes) {
		addonTypeService.delete(addonTypes);
		return this;
	}
	
	@Action(associateWith = "addons", semantics = SemanticsOf.NON_IDEMPOTENT_ARE_YOU_SURE, typeOf = Addon.class)
	@ActionLayout(named = "Delete")
	public ManageAddonDashboard deleteAddons(List<Addon> addons) {
		addonService.delete(addons);
		return this;
	}
    
    @Action(
    		domainEvent = AddonType.CreateEvent.class,
    		semantics = SemanticsOf.SAFE,
    		typeOf = AddonType.class,
    		associateWith = "addonTypes"
    )
    @ActionLayout(
    		named = "Create",
    		position = Position.RIGHT,
    		promptStyle = PromptStyle.DIALOG,
    		describedAs = "Create new Addon Type"
    )
    public ManageAddonDashboard createAddonType(
    		@Parameter(maxLength = WithName.MAX_LEN, optionality = Optionality.MANDATORY)
    		@ParameterLayout(labelPosition = LabelPosition.LEFT, named = "Name", describedAs = "Enter name of new Addon Type to be created")
    		final String name,
    		@Parameter(optionality = Optionality.OPTIONAL)
    		@ParameterLayout(labelPosition = LabelPosition.TOP, named = "Description", multiLine = 4, describedAs = "Enter description of new Addon Type that will be created")
    		final String description
    		) {
    	List<AddonType> addonTypeList = addonTypeService.search(NamedQueryConstants.QUERY_FIND_BY_NAME, "name", name);
    	if (addonTypeList != null && !addonTypeList.isEmpty()) {
    		for (AddonType type : addonTypeList) {
    			if (type.getName().equals(name)) {
    	    		messageService.raiseError(TranslatableString.tr("Addon type with same name already exists!"), getClass(), Thread.currentThread().getStackTrace()[0].getMethodName());
    	    		return this;
    			}
    		}
    	}
    	AddonType addonType = addonTypeService.create(name, description);
    	if (addonType == null) {
    		messageService.raiseError(TranslatableString.tr("Addon type could not be created, check log for more detail"), getClass(), Thread.currentThread().getStackTrace()[0].getMethodName());
    	}
    	return this;
    }
    
    @Action(
    		domainEvent = Addon.CreateEvent.class,
    		semantics = SemanticsOf.SAFE,
    		typeOf = Addon.class,
    		associateWith = "addons"
    )
    @ActionLayout(
    		named = "Deploy Library",
    		position = Position.RIGHT,
    		promptStyle = PromptStyle.DIALOG,
    		describedAs = "Deploy addon(s) from library"
    )
    public ManageAddonDashboard deployAddonLibrary(
    		@Parameter(optionality = Optionality.MANDATORY)
    		@ParameterLayout(labelPosition = LabelPosition.LEFT, named = "Properties", describedAs = "Select addon-library.properties file")
    		final Blob libraryProperties,
    		@Parameter(optionality = Optionality.MANDATORY, fileAccept = ".jar")
    		@ParameterLayout(labelPosition = LabelPosition.LEFT, named = "Library", describedAs = "Select addon library (jar) file")
    		final Blob library
    		) {
    	try {
    		Properties properties = new Properties();
    		properties.load(new ByteArrayInputStream(libraryProperties.getBytes()));
    		
    		Path tempFile = null;
    		if (library != null) {
    			/**
    			 * Transfer jar file to library location
    			 */
    			log.info("Transfering addon library to temporary location ...");			
    			tempFile = Files.createTempFile(library.getName().replace(".jar", ""), ".jar", PosixFilePermissions.asFileAttribute(PosixFilePermissions.fromString("rw-rw-rw-")));
    			Files.copy(new ByteArrayInputStream(library.getBytes()), tempFile, StandardCopyOption.REPLACE_EXISTING);
    		}
    		
			addonService.deploy(properties, tempFile, library != null ? library.getName() : null);
    	} catch (AddonException e) {
    		log.error("Addon exception occurred while deploying - " + e.getMessage(), e);
    		messageService.raiseError(e.getTranslatableMessage(), e.getContextClass(), e.getStackTrace()[0].getMethodName());
    	} catch (Exception e) {
    		log.error("Exception has occurred while deploying addon library", e);
			messageService.raiseError(TranslatableString.tr("Exception has occurred while deploying addon library '${libraryname}'", "libraryname", library != null ? library.getName() : "embedded"), getClass(), Thread.currentThread().getStackTrace()[0].getMethodName());
    	}
    	return this;
    }
	
    @javax.inject.Inject
    protected AddonTypeService addonTypeService;
    
    @javax.inject.Inject
    protected AddonService addonService;
    
    @javax.inject.Inject
    protected MessageService messageService;
}
