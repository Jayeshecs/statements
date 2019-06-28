/**
 * 
 */
package domainapp.modules.addon.view.dashboard;

import java.util.List;
import java.util.Objects;

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

import domainapp.modules.addon.dom.Addon;
import domainapp.modules.addon.dom.AddonType;
import domainapp.modules.addon.service.AddonService;
import domainapp.modules.addon.service.AddonTypeService;
import domainapp.modules.base.entity.WithName;

/**
 * Manage addons and their types
 * 
 * @author jayeshecs
 */
@DomainObject(
		nature = Nature.VIEW_MODEL,
		objectType = "addon.ManageAddonDashboard"
)
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
    	AddonType addonType = addonTypeService.create(name, description);
    	Objects.requireNonNull(addonType, "Addon type could not be created, check log for more detail");
    	return this;
    }
    
    @Action(
    		domainEvent = Addon.CreateEvent.class,
    		semantics = SemanticsOf.SAFE,
    		typeOf = Addon.class,
    		associateWith = "addons"
    )
    @ActionLayout(
    		named = "Create",
    		position = Position.RIGHT,
    		promptStyle = PromptStyle.DIALOG,
    		describedAs = "Create new addon"
    )
    public ManageAddonDashboard createAddon(
    		@Parameter(maxLength = WithName.MAX_LEN, optionality = Optionality.MANDATORY)
    		@ParameterLayout(labelPosition = LabelPosition.LEFT, named = "Name", describedAs = "Enter name of new addon to be created")
    		final String name,
    		@Parameter(optionality = Optionality.OPTIONAL)
    		@ParameterLayout(labelPosition = LabelPosition.TOP, named = "Description", multiLine = 4, describedAs = "Enter description of new addon that will be created")
    		final String description,
    		@Parameter(optionality = Optionality.MANDATORY)
    		@ParameterLayout(named = "Addon Type", describedAs = "Enter addon type of new addon that will be created")
    		final AddonType addonType,
    		@Parameter(optionality = Optionality.MANDATORY)
    		@ParameterLayout(named = "Class Name", describedAs = "Enter qualified class name of new addon that will be created")
    		final String className,
    		@Parameter(optionality = Optionality.MANDATORY)
    		@ParameterLayout(named = "Library", describedAs = "Enter library name of new addon that will be created")
    		final String library
    		) {
    	Addon addon = addonService.create(name, description, addonType, className, library);
    	Objects.requireNonNull(addon, "Addon could not be created, check log for more detail");
    	return this;
    }
	
    @javax.inject.Inject
    protected AddonTypeService addonTypeService;
    
    @javax.inject.Inject
    protected AddonService addonService;
}
