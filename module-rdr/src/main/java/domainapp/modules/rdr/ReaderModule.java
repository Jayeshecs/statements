package domainapp.modules.rdr;

import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;

import org.apache.isis.applib.Module;
import org.apache.isis.applib.ModuleAbstract;
import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.fixturescripts.teardown.TeardownFixtureAbstract2;

import com.google.common.collect.Sets;

import domainapp.modules.base.BaseModule;
import domainapp.modules.rdr.dom.StatementReader;
import domainapp.modules.rdr.dom.StatementReaderType;

@XmlRootElement(name = "module")
public class ReaderModule extends ModuleAbstract {

    @Override
    public Set<Module> getDependencies() {
        return Sets.newHashSet(new BaseModule());
    }
    
    @Override
    public FixtureScript getTeardownFixture() {
        return new TeardownFixtureAbstract2() {
            @Override
            protected void execute(ExecutionContext executionContext) {
                deleteFrom(StatementReader.class);
                deleteFrom(StatementReaderType.class);
            }
        };
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
