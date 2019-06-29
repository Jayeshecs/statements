/**
 * 
 */
package domainapp.modules.rdr.fixture;

import java.util.List;

import org.apache.isis.applib.fixturescripts.BuilderScriptAbstract;

import domainapp.modules.base.entity.NamedQueryConstants;
import domainapp.modules.rdr.dom.StatementReader;
import domainapp.modules.rdr.dom.StatementReaderType;
import domainapp.modules.rdr.service.StatementReaderService;
import domainapp.modules.rdr.service.StatementReaderTypeService;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Builder script implementation for Category
 * 
 * @author Prajapati
 */
@Accessors(chain = true)
public class StatementReaderBuilder extends BuilderScriptAbstract<StatementReader, StatementReaderBuilder> {

    @Getter @Setter
    private String name;

    @Getter @Setter
    private String readerType;

    @Getter
    private StatementReader object;

	@Override
	protected void execute(ExecutionContext ec) {

        checkParam("name", ec, String.class);

        checkParam("readerType", ec, String.class);

        List<StatementReaderType> list = statementReaderTypeService.search(NamedQueryConstants.QUERY_FIND_BY_NAME, "name", readerType);
        StatementReaderType statementReaderType = null;
        if (list != null && !list.isEmpty()) {
        	for (StatementReaderType statementReaderType2 : list) {
        		if (statementReaderType2.getName().equals(readerType)) {
        			statementReaderType = statementReaderType2;
        		}
        	}
        }
        if (statementReaderType == null) {
        	throw new IllegalArgumentException("StatementReaderType with name " + readerType + " could not be found");
        }
		object = statementReaderService.create(name, "", statementReaderType, "#dateFormat=dd/MM/yyyy");
	}

    @javax.inject.Inject
    StatementReaderTypeService statementReaderTypeService;

    @javax.inject.Inject
    StatementReaderService statementReaderService;

}
