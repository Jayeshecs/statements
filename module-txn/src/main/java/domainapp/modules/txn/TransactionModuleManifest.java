package domainapp.modules.txn;

import org.apache.isis.applib.AppManifestAbstract;

/**
 * Used by <code>isis-maven-plugin</code> (build-time validation of the module) and also by module-level integration tests.
 */
public class TransactionModuleManifest extends AppManifestAbstract {

    public static final Builder BUILDER = Builder.forModules(
            TransactionModule.class
    );

    public TransactionModuleManifest() {
        super(BUILDER);
    }


}
