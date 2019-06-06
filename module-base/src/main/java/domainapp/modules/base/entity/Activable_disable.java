/**
 * 
 */
package domainapp.modules.base.entity;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

/**
 * Mixin for update of name for entity with name
 * 
 * @author jayeshecs
 * @see WithName
 */
@Mixin
public class Activable_disable {

	private final Activable entity;
	
	public Activable_disable(Activable entity) {
		this.entity = entity;
	}
	
	@ActionLayout(
			describedAs = "Disable this record"
	)
	@Action(semantics = SemanticsOf.IDEMPOTENT)
	public Activable $$() {
		entity.setActive(false);
		return entity;
	}
	
}
