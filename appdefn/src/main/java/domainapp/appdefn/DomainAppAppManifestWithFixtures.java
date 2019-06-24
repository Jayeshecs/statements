package domainapp.appdefn;

import java.util.List;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import domainapp.modules.ref.fixture.Category_persona;
import domainapp.modules.ref.fixture.SubCategory_persona;

public class DomainAppAppManifestWithFixtures extends DomainAppAppManifest {

    @Override
    protected void overrideFixtures(final List<Class<? extends FixtureScript>> fixtureScripts) {
        super.overrideFixtures(fixtureScripts);
        // TODO
        fixtureScripts.add(Category_persona.PersistAll.class);
        fixtureScripts.add(SubCategory_persona.PersistAll.class);
    }

}
