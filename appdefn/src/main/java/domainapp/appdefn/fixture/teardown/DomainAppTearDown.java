package domainapp.appdefn.fixture.teardown;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import domainapp.modules.rdr.fixture.StatementReaderType_persona;
import domainapp.modules.rdr.fixture.StatementReader_persona;
import domainapp.modules.rdr.fixture.addon.StatementReaderAddonType_persona;
import domainapp.modules.rdr.fixture.addon.StatementReaderAddon_persona;
import domainapp.modules.ref.fixture.Category_persona;
import domainapp.modules.ref.fixture.SubCategory_persona;

public class DomainAppTearDown extends FixtureScript {

    @Override
    protected void execute(ExecutionContext executionContext) {
        executionContext.executeChildren(this, Category_persona.class);
        executionContext.executeChildren(this, SubCategory_persona.class);
        executionContext.executeChildren(this, StatementReader_persona.class);
        executionContext.executeChildren(this, StatementReaderType_persona.class);
        executionContext.executeChildren(this, StatementReaderAddon_persona.class);
        executionContext.executeChildren(this, StatementReaderAddonType_persona.class);
    }

}
