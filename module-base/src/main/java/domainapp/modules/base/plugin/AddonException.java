/**
 * 
 */
package domainapp.modules.base.plugin;

import org.apache.isis.applib.services.exceprecog.TranslatableException;
import org.apache.isis.applib.services.i18n.TranslatableString;

/**
 * @author Prajapati
 * @see IAddonApi
 *
 */
public class AddonException extends Exception implements TranslatableException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String translationContext;
	private TranslatableString translatableMessage;
	private Class<?> contextClass;
	
	public AddonException(String message) {
		super(message);
		this.translatableMessage = TranslatableString.tr(message);
		ensureTranslationContext();
	}
	
	public AddonException(String message, Exception cause) {
		super(message, cause);
		this.translatableMessage = TranslatableString.tr(message);
		ensureTranslationContext();
	}
	
	public AddonException(TranslatableString message, Class<?> contextClass) {
		super(message.getPattern());
		this.translatableMessage = message;
		this.contextClass = contextClass;
		ensureTranslationContext();
	}
	
	public AddonException(TranslatableString message, Class<?> contextClass, Exception cause) {
		super(message.getPattern(), cause);
		this.translatableMessage = message;
		this.contextClass = contextClass;
		ensureTranslationContext();
	}
	
	private void ensureTranslationContext() {
		translationContext = (contextClass != null 
				? contextClass.getName() 
						: getStackTrace()[0].getClassName()) 
				+ "#" + getStackTrace()[0].getMethodName();
	}

	@Override
	public TranslatableString getTranslatableMessage() {
		return translatableMessage;
	}

	@Override
	public String getTranslationContext() {
		return translationContext;
	}
	
	public Class<?> getContextClass() {
		return contextClass;
	}

}
