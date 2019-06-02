/**
 * 
 */
package domainapp.modules.base.entity;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.i18n.TranslatableString;

/**
 * Mixin for update of name for entity with name
 * 
 * @author jayeshecs
 * @see WithName
 */
@Mixin
public class WithName_update {

	private final WithName entity;
	
	public WithName_update(WithName entity) {
		this.entity = entity;
	}
	
	@ActionLayout(
			describedAs = "Update name"
	)
	@Action(semantics = SemanticsOf.IDEMPOTENT)
	public WithName $$(
			@Parameter(optionality = Optionality.MANDATORY, maxLength = WithName.MAX_LEN)
			@ParameterLayout(describedAs = "Name of this entity")
			final String name) {
		entity.setName(name);
		return entity;
	}
	
	public String default0$$() {
		return entity != null ? entity.getName() : null;
	}
	
	public TranslatableString validate0$$(String name) {
        return name != null && name.contains("!") ? TranslatableString.tr("Exclamation mark is not allowed") : null;		
	}
	
}
