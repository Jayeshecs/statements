package domainapp.modules.plugin;

import org.apache.isis.applib.AppManifestAbstract;

/**
 * Used by <code>isis-maven-plugin</code> (build-time validation of the module) and also by module-level integration tests.
 */
public class PluginModuleManifest extends AppManifestAbstract {

    public static final Builder BUILDER = Builder.forModules(
            PluginModule.class
    );

    public PluginModuleManifest() {
        super(BUILDER);
    }


}
