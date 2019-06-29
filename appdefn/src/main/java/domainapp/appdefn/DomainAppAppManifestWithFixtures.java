package domainapp.appdefn;

import java.util.List;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import domainapp.modules.rdr.fixture.StatementReaderType_persona;
import domainapp.modules.rdr.fixture.StatementReader_persona;
import domainapp.modules.rdr.fixture.addon.StatementReaderAddonType_persona;
import domainapp.modules.rdr.fixture.addon.StatementReaderAddon_persona;
import domainapp.modules.ref.fixture.Category_persona;
import domainapp.modules.ref.fixture.SubCategory_persona;

public class DomainAppAppManifestWithFixtures extends DomainAppAppManifest {

    @Override
    protected void overrideFixtures(final List<Class<? extends FixtureScript>> fixtureScripts) {
        super.overrideFixtures(fixtureScripts);
        fixtureScripts.add(Category_persona.PersistAll.class);
        fixtureScripts.add(SubCategory_persona.PersistAll.class);
        fixtureScripts.add(StatementReaderAddonType_persona.PersistAll.class);
        fixtureScripts.add(StatementReaderAddon_persona.PersistAll.class);
        fixtureScripts.add(StatementReaderType_persona.PersistAll.class);
        fixtureScripts.add(StatementReader_persona.PersistAll.class);
    }

}
