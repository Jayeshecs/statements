package domainapp.modules.ref.datatype.definition;

import javax.inject.Inject;

import domainapp.modules.base.datatype.definition.IDataTypeDefinition;
import domainapp.modules.base.datatype.definition.WithNameDataTypeDefinition;
import domainapp.modules.base.service.AbstractService;
import domainapp.modules.ref.dom.SubCategory;
import domainapp.modules.ref.service.SubCategoryService;

/**
 * {@link IDataTypeDefinition} implementation for {@link SubCategory}
 * 
 * @author jayeshecs
 */
public class SubCategoryDataTypeDefinition extends WithNameDataTypeDefinition<SubCategory> {
	
	@Override
	protected AbstractService<SubCategory> getService() {
		return subCategoryService;
	}
		
	@Inject
	SubCategoryService subCategoryService;
}
