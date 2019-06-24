/**
 * 
 */
package domainapp.modules.ref.fixture;

import org.apache.isis.applib.fixturescripts.BuilderScriptAbstract;

import domainapp.modules.ref.dom.SubCategory;
import domainapp.modules.ref.service.SubCategoryService;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Builder script implementation for SubCategory
 * 
 * @author Prajapati
 */
@Accessors(chain = true)
public class SubCategoryBuilder extends BuilderScriptAbstract<SubCategory, SubCategoryBuilder> {

    @Getter @Setter
    private String name;

    @Getter
    private SubCategory object;

	@Override
	protected void execute(ExecutionContext ec) {

        checkParam("name", ec, String.class);

        object = subCategoryService.create(name, "");
	}

    @javax.inject.Inject
    SubCategoryService subCategoryService;

}
