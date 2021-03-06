/**
 * 
 */
package domainapp.modules.ref.service;

import java.util.List;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.Programmatic;

import domainapp.modules.base.entity.NamedQueryConstants;
import domainapp.modules.base.service.AbstractService;
import domainapp.modules.ref.dom.Category;

/**
 * @author jayeshecs
 *
 */
@DomainService(
		nature = NatureOfService.DOMAIN,
		repositoryFor = Category.class
)
public class CategoryService extends AbstractService<Category> {

	public CategoryService() {
		super(Category.class);
	}
	
	@Programmatic
	public List<Category> all() {
		return search(NamedQueryConstants.QUERY_ALL);
	}

	@Programmatic
	public Category create(String name, @Parameter(optionality = Optionality.OPTIONAL) String description) {
    	Category newCategory = Category.builder().name(name).description(description).build();
    	Category category = repositoryService.persistAndFlush(newCategory);
    	return category;
	}
}
