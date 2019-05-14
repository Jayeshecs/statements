package domainapp.appdefn;

import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;

import org.apache.isis.applib.Module;
import org.apache.isis.applib.ModuleAbstract;

import com.google.common.collect.Sets;

import domainapp.modules.rdr.ReaderModule;
import domainapp.modules.ref.StaticModule;
import domainapp.modules.txn.TransactionModule;

@XmlRootElement(name = "module")
public class DomainAppAppDefnModule extends ModuleAbstract {

    @Override
    public Set<Module> getDependencies() {
        return Sets.newHashSet(new StaticModule(), new TransactionModule(), new ReaderModule());
    }
}
