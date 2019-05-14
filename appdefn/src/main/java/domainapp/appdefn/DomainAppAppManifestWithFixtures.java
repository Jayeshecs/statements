package domainapp.appdefn;

import java.util.List;

import org.apache.isis.applib.fixturescripts.FixtureScript;

public class DomainAppAppManifestWithFixtures extends DomainAppAppManifest {

    @Override
    protected void overrideFixtures(final List<Class<? extends FixtureScript>> fixtureScripts) {
        super.overrideFixtures(fixtureScripts);
        // TODO
//        fixtureScripts.add(SimpleObject_data.PersistScript.class);
    }

}
