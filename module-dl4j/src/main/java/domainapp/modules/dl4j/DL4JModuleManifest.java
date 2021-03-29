package domainapp.modules.dl4j;

import org.apache.isis.applib.AppManifestAbstract;

/**
 * Used by <code>isis-maven-plugin</code> (build-time validation of the module) and also by module-level integration tests.
 */
public class DL4JModuleManifest extends AppManifestAbstract {

    public static final Builder BUILDER = Builder.forModules(
            DL4JModule.class
    );

    public DL4JModuleManifest() {
        super(BUILDER);
    }

}
