package domainapp.modules.dl4j.categorizer;

/**
 * @author jayeshecs
 *
 * @param <R> Record or data
 * @param <L> Label
 */
public interface ICategorizerDataHandler<R, L> {
	
	/**
	 * @param <R> data
	 * @return <L> Label for given <R> data if present else return null
	 */
	L getLabel(R data);
	
	/**
	 * Label key to Label data
	 * 
	 * @param key
	 * @return <L>
	 */
	L toLabel(String key);
	
	/**
	 * @param <R> data
	 * @return sentence or paragraph or sequence of words for given <R> data
	 */
	String getParagraph(R data);
}