/**
 * 
 */
package domainapp.modules.plugin.view.dashboard;

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

import domainapp.modules.base.entity.WithName;
import domainapp.modules.plugin.dom.Plugin;
import domainapp.modules.plugin.dom.PluginType;
import domainapp.modules.plugin.service.PluginService;
import domainapp.modules.plugin.service.PluginTypeService;

/**
 * Manage readers and their types
 * 
 * @author jayeshecs
 */
@DomainObject(
		nature = Nature.VIEW_MODEL,
		objectType = "plugin.ManagePluginDashboard"
)
public class ManagePluginDashboard {

	public String title() {
		return "Manage Plugin";
	}
	
	@Collection
	@CollectionLayout(defaultView = "table")
	public List<PluginType> getPluginTypes() {
		return pluginTypeService.all();
	}
	
	@Collection
	@CollectionLayout(defaultView = "table")
	public List<Plugin> getPlugins() {
		return pluginService.all();
	}
	
	@Action(associateWith = "pluginTypes", semantics = SemanticsOf.NON_IDEMPOTENT_ARE_YOU_SURE, typeOf = PluginType.class)
	@ActionLayout(named = "Delete")
	public ManagePluginDashboard deletePluginTypes(List<PluginType> pluginTypes) {
		pluginTypeService.delete(pluginTypes);
		return this;
	}
	
	@Action(associateWith = "plugins", semantics = SemanticsOf.NON_IDEMPOTENT_ARE_YOU_SURE, typeOf = Plugin.class)
	@ActionLayout(named = "Delete")
	public ManagePluginDashboard deletePlugins(List<Plugin> plugins) {
		pluginService.delete(plugins);
		return this;
	}
    
    @Action(
    		domainEvent = PluginType.CreateEvent.class,
    		semantics = SemanticsOf.SAFE,
    		typeOf = PluginType.class,
    		associateWith = "pluginTypes"
    )
    @ActionLayout(
    		named = "Create",
    		position = Position.RIGHT,
    		promptStyle = PromptStyle.DIALOG,
    		describedAs = "Create new Plugin Type"
    )
    public ManagePluginDashboard createPluginType(
    		@Parameter(maxLength = WithName.MAX_LEN, optionality = Optionality.MANDATORY)
    		@ParameterLayout(labelPosition = LabelPosition.LEFT, named = "Name", describedAs = "Enter name of new Plugin Type to be created")
    		final String name,
    		@Parameter(optionality = Optionality.OPTIONAL)
    		@ParameterLayout(labelPosition = LabelPosition.TOP, named = "Description", multiLine = 4, describedAs = "Enter description of new Plugin Type that will be created")
    		final String description
    		) {
    	PluginType PluginType = pluginTypeService.create(name, description);
    	Objects.requireNonNull(PluginType, "Plugin type could not be created, check log for more detail");
    	return this;
    }
    
    @Action(
    		domainEvent = Plugin.CreateEvent.class,
    		semantics = SemanticsOf.SAFE,
    		typeOf = Plugin.class,
    		associateWith = "plugins"
    )
    @ActionLayout(
    		named = "Create",
    		position = Position.RIGHT,
    		promptStyle = PromptStyle.DIALOG,
    		describedAs = "Create new plugin"
    )
    public ManagePluginDashboard createPlugin(
    		@Parameter(maxLength = WithName.MAX_LEN, optionality = Optionality.MANDATORY)
    		@ParameterLayout(labelPosition = LabelPosition.LEFT, named = "Name", describedAs = "Enter name of new plugin to be created")
    		final String name,
    		@Parameter(optionality = Optionality.OPTIONAL)
    		@ParameterLayout(labelPosition = LabelPosition.TOP, named = "Description", multiLine = 4, describedAs = "Enter description of new plugin that will be created")
    		final String description,
    		@Parameter(optionality = Optionality.MANDATORY)
    		@ParameterLayout(named = "Plugin Type", describedAs = "Enter plugin type of new plugin that will be created")
    		final PluginType pluginType,
    		@Parameter(optionality = Optionality.MANDATORY)
    		@ParameterLayout(named = "Class Name", describedAs = "Enter qualified class name of new plugin that will be created")
    		final String className,
    		@Parameter(optionality = Optionality.MANDATORY)
    		@ParameterLayout(named = "Library", describedAs = "Enter library name of new plugin that will be created")
    		final String library
    		) {
    	Plugin Plugin = pluginService.create(name, description, pluginType, className, library);
    	Objects.requireNonNull(Plugin, "Plugin could not be created, check log for more detail");
    	return this;
    }
	
    @javax.inject.Inject
    protected PluginTypeService pluginTypeService;
    
    @javax.inject.Inject
    protected PluginService pluginService;
}
