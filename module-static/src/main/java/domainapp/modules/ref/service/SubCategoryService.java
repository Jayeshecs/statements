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
import domainapp.modules.ref.dom.SubCategory;

/**
 * @author jayeshecs
 *
 */
@DomainService(
		nature = NatureOfService.DOMAIN,
		repositoryFor = SubCategory.class
)
public class SubCategoryService extends AbstractService<SubCategory>{

	public SubCategoryService() {
		super(SubCategory.class);
	}
	
	@Programmatic
	public List<SubCategory> all() {
		return search(NamedQueryConstants.QUERY_ALL);
	}

	@Programmatic
	public SubCategory create(String name, @Parameter(optionality = Optionality.OPTIONAL) String description) {
		SubCategory subCategory = SubCategory.builder().name(name).description(description).build();
		subCategory = repositoryService.persistAndFlush(subCategory);
		clearCache();
		return subCategory;
	}
	
}
