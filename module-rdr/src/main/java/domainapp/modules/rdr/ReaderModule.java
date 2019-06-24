package domainapp.modules.rdr;

import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.collect.Sets;

import org.apache.isis.applib.Module;
import org.apache.isis.applib.ModuleAbstract;

import domainapp.modules.base.BaseModule;

@XmlRootElement(name = "module")
public class ReaderModule extends ModuleAbstract {

    @Override
    public Set<Module> getDependencies() {
        return Sets.newHashSet(new BaseModule());
    }

    public static class PropertyDomainEvent<S,T>
            extends org.apache.isis.applib.services.eventbus.PropertyDomainEvent<S,T> {
		private static final long serialVersionUID = 1L;
	}
    public static class CollectionDomainEvent<S,T>
            extends org.apache.isis.applib.services.eventbus.CollectionDomainEvent<S,T> {
		private static final long serialVersionUID = 1L;
	}
    public static class ActionDomainEvent<S> extends
            org.apache.isis.applib.services.eventbus.ActionDomainEvent<S> {
		private static final long serialVersionUID = 1L;
	}
}
