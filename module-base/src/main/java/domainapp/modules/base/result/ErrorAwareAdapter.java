/**
 * 
 */
package domainapp.modules.base.result;

import lombok.Getter;
import lombok.Setter;

/**
 * Adapter implementation of {@link IErrorAware}
 * 
 * @author jayesh.prajapati
 */
public abstract class ErrorAwareAdapter implements IErrorAware {

	@Getter
	@Setter
	private String error;

}
