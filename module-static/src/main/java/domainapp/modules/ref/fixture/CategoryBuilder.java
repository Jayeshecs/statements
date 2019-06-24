/**
 * 
 */
package domainapp.modules.ref.fixture;

import org.apache.isis.applib.fixturescripts.BuilderScriptAbstract;

import domainapp.modules.ref.dom.Category;
import domainapp.modules.ref.service.CategoryService;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Builder script implementation for Category
 * 
 * @author Prajapati
 */
@Accessors(chain = true)
public class CategoryBuilder extends BuilderScriptAbstract<Category, CategoryBuilder> {

    @Getter @Setter
    private String name;

    @Getter
    private Category object;

	@Override
	protected void execute(ExecutionContext ec) {

        checkParam("name", ec, String.class);

        object = categoryService.create(name, "");
	}

    @javax.inject.Inject
    CategoryService categoryService;

}
