/**
 * 
 */
package domainapp.modules.base.entity;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.LabelPosition;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.i18n.TranslatableString;
//import org.isisaddons.wicket.summernote.cpt.applib.SummernoteEditor;

/**
 * Mixin for update of name for entity with name
 * 
 * @author jayeshecs
 * @see WithName
 */
@Mixin
public class WithNameAndDescription_update {

	private final WithNameAndDescription entity;
	
	public WithNameAndDescription_update(WithNameAndDescription entity) {
		this.entity = entity;
	}
	
	@ActionLayout(
			describedAs = "Update name and description"
	)
	@Action(semantics = SemanticsOf.IDEMPOTENT)
	public WithNameAndDescription $$(
			@Parameter(optionality = Optionality.MANDATORY, maxLength = WithName.MAX_LEN)
			@ParameterLayout(named = "Name", describedAs = "Name of this entity")
			final String name,
			
			@Parameter(optionality = Optionality.OPTIONAL, maxLength = WithDescription.MAX_LEN)
			@ParameterLayout(named = "Description", labelPosition = LabelPosition.TOP, multiLine = 4, describedAs = "Description of this entity")
//			@SummernoteEditor(height = 100, maxHeight = 300)
			final String description
	) {
		entity.setName(name);
		entity.setDescription(description);
		return entity;
	}
	
	public String default0$$() {
		return entity != null ? entity.getName() : null;
	}
	
	public TranslatableString validate0$$(String name) {
        return name != null && name.contains("!") ? TranslatableString.tr("Exclamation mark is not allowed") : null;		
	}
	
	public String default1$$() {
		return entity != null ? entity.getDescription() : null;
	}
	
}
