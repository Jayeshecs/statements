/**
 * 
 */
package domainapp.modules.base.result;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Boolean result which is {@link IErrorAware}
 * 
 * @author jayesh.prajapati
 */
@Builder
public class BooleanResult implements IErrorAware {

	@Getter
	@Setter
	private Boolean success;
	
	@Getter
	@Setter
	private String error;
	
}
