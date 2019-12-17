package domainapp.modules.ref;

import org.apache.isis.applib.AppManifestAbstract;

/**
 * Used by <code>isis-maven-plugin</code> (build-time validation of the module) and also by module-level integration tests.
 */
public class StaticModuleManifest extends AppManifestAbstract {

    public static final Builder BUILDER = Builder.forModules(
            StaticModule.class
    );

    public StaticModuleManifest() {
        super(BUILDER);
    }

}
