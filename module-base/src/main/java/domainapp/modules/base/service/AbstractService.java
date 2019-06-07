/**
 * 
 */
package domainapp.modules.base.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.services.queryresultscache.QueryResultsCache;
import org.apache.isis.applib.services.repository.RepositoryService;

/**
 * @author jayeshecs
 *
 */
public abstract class AbstractService<T> {
	
	private Class<T> entityClass;

	protected AbstractService(Class<T> clazz) {
		this.entityClass = clazz;
	}
	
	@Programmatic
	public List<T> search(String queryName, Object... arguments) {
		return queryResultsCache.execute(() -> execute(entityClass, queryName, arguments), entityClass, queryName, arguments);
	}
	
	private <A> List<A> execute(Class<A> clazz, String queryName, Object... arguments) { 
		return repositoryService.allMatches(new QueryDefault<A>(clazz, queryName, arguments));
	}

	@Programmatic
	public void delete(List<T> records) {
		for (T record : records) { 
			repositoryService.remove(record);
		}
		clearCache();
	}
	
	/**
	 * Clear query result cache for next transaction
	 */
	protected void clearCache() {
		queryResultsCache.resetForNextTransaction();
	}
	
	@Programmatic
	public void save(List<T> records) {
		List<T> result = new ArrayList<>(records.size());
		for (int i = 0; i < records.size(); i++) {
			if (i == records.size() - 1) {
				result.add(repositoryService.persistAndFlush(records.get(i)));
				continue ;
			}
			result.add(repositoryService.persist(records.get(i)));
		}
		clearCache();
	}
    
    @javax.inject.Inject
    protected RepositoryService repositoryService;
    
    @javax.inject.Inject
    protected QueryResultsCache queryResultsCache;

}
