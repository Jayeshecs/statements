package domainapp.modules.rdr;

import org.apache.isis.applib.AppManifestAbstract;

/**
 * Used by <code>isis-maven-plugin</code> (build-time validation of the module) and also by module-level integration tests.
 */
public class ReaderModuleManifest extends AppManifestAbstract {

    public static final Builder BUILDER = Builder.forModules(
            ReaderModule.class
    );

    public ReaderModuleManifest() {
        super(BUILDER);
    }


}
