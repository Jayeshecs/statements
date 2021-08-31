/**
 * 
 */
package domainapp.modules.base.result;

/**
 * Specification for beans or POJO that are aware of contained error.
 * 
 * @author jayesh.prajapati
 */
public interface IErrorAware {

	String getError();
	
	void setError(String error);
}
