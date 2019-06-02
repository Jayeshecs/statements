/**
 * 
 */
package domainapp.modules.ref.view.dashboard;

import java.util.List;
import java.util.Objects;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.ActionLayout.Position;
import org.apache.isis.applib.annotation.Collection;
import org.apache.isis.applib.annotation.CollectionLayout;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.LabelPosition;
import org.apache.isis.applib.annotation.Nature;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.PromptStyle;
import org.apache.isis.applib.annotation.SemanticsOf;

import domainapp.modules.base.entity.WithName;
import domainapp.modules.ref.dom.Category;
import domainapp.modules.ref.dom.SubCategory;
import domainapp.modules.ref.service.CategoryService;
import domainapp.modules.ref.service.SubCategoryService;

/**
 * @author jayeshecs
 *
 */
@DomainObject(
		nature = Nature.VIEW_MODEL,
		objectType = "stmt.ManageCategoryDashboard"
)
public class ManageCategoryDashboard {

	public String title() {
		return "Manage Category";
	}
	
	@Collection
	@CollectionLayout(defaultView = "table")
	public List<Category> getCategories() {
		return categoryService.all();
	}
	
	@Collection
	@CollectionLayout(defaultView = "table")
	public List<SubCategory> getSubCategories() {
		return subCategoryService.all();
	}
	
	@Action(associateWith = "categories", semantics = SemanticsOf.NON_IDEMPOTENT_ARE_YOU_SURE, typeOf = Category.class)
	@ActionLayout(named = "Delete")
	public ManageCategoryDashboard deleteCategories(List<Category> categories) {
		categoryService.delete(categories);
		return this;
	}
	
	@Action(associateWith = "subCategories", semantics = SemanticsOf.NON_IDEMPOTENT_ARE_YOU_SURE, typeOf = SubCategory.class)
	@ActionLayout(named = "Delete")
	public ManageCategoryDashboard deleteSubCategories(List<SubCategory> subCategories) {
		subCategoryService.delete(subCategories);
		return this;
	}
    
    @Action(
    		domainEvent = Category.CreateEvent.class,
    		semantics = SemanticsOf.SAFE,
    		typeOf = Category.class,
    		associateWith = "categories"
    )
    @ActionLayout(
    		named = "Create",
    		position = Position.RIGHT,
    		promptStyle = PromptStyle.DIALOG,
    		describedAs = "Create new category"
    )
    public ManageCategoryDashboard createCategory(
    		@Parameter(maxLength = WithName.MAX_LEN, optionality = Optionality.MANDATORY)
    		@ParameterLayout(labelPosition = LabelPosition.LEFT, named = "Name", describedAs = "Enter name of new category to be created")
    		final String name,
    		@Parameter(optionality = Optionality.OPTIONAL)
    		@ParameterLayout(labelPosition = LabelPosition.TOP, named = "Description", multiLine = 4, describedAs = "Enter description of new category that will be created")
    		final String description
    		) {
    	Category category = categoryService.create(name, description);
    	Objects.requireNonNull(category, "Category could not be created, check log for more detail");
    	return this;
    }
    
    @Action(
    		domainEvent = SubCategory.CreateEvent.class,
    		semantics = SemanticsOf.SAFE,
    		typeOf = SubCategory.class,
    		associateWith = "subCategories"
    )
    @ActionLayout(
    		named = "Create",
    		position = Position.RIGHT,
    		promptStyle = PromptStyle.DIALOG,
    		describedAs = "Create new sub category"
    )
    public ManageCategoryDashboard createSubCategory(
    		@Parameter(maxLength = WithName.MAX_LEN, optionality = Optionality.MANDATORY)
    		@ParameterLayout(labelPosition = LabelPosition.LEFT, named = "Name", describedAs = "Enter name of new sub category to be created")
    		final String name,
    		@Parameter(optionality = Optionality.OPTIONAL)
    		@ParameterLayout(labelPosition = LabelPosition.TOP, named = "Description", multiLine = 4, describedAs = "Enter description of new sub category that will be created")
    		final String description
    		) {
    	SubCategory subCategory = subCategoryService.create(name, description);
    	Objects.requireNonNull(subCategory, "Category could not be created, check log for more detail");
    	return this;
    }
	
    @javax.inject.Inject
    protected SubCategoryService subCategoryService;
	
    @javax.inject.Inject
    protected CategoryService categoryService;
    
}
