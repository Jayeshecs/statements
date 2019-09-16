package domainapp.modules.ref.datatype.definition;

import javax.inject.Inject;

import domainapp.modules.base.datatype.definition.IDataTypeDefinition;
import domainapp.modules.base.datatype.definition.WithNameDataTypeDefinition;
import domainapp.modules.base.service.AbstractService;
import domainapp.modules.ref.dom.Category;
import domainapp.modules.ref.service.CategoryService;

/**
 * {@link IDataTypeDefinition} implementation for {@link Category}
 * 
 * @author jayeshecs
 */
public class CategoryDataTypeDefinition extends WithNameDataTypeDefinition<Category> {
	
	@Override
	protected AbstractService<Category> getService() {
		return categoryService;
	}
		
	@Inject
	CategoryService categoryService;
}
