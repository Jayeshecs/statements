package domainapp.modules.addon;

import org.apache.isis.applib.AppManifestAbstract;

/**
 * Used by <code>isis-maven-plugin</code> (build-time validation of the module) and also by module-level integration tests.
 */
public class AddonModuleManifest extends AppManifestAbstract {

    public static final Builder BUILDER = Builder.forModules(
            AddonModule.class
    );

    public AddonModuleManifest() {
        super(BUILDER);
    }


}
