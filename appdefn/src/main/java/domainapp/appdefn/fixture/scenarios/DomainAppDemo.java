package domainapp.appdefn.fixture.scenarios;

import javax.annotation.Nullable;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import domainapp.appdefn.fixture.teardown.DomainAppTearDown;
import lombok.Getter;
import lombok.Setter;

public class DomainAppDemo extends FixtureScript {

    public DomainAppDemo() {
        withDiscoverability(Discoverability.DISCOVERABLE);
    }

    @Nullable
    @Getter @Setter
    private Integer number;

    @Override
    protected void execute(final ExecutionContext ec) {

        // execute
        ec.executeChild(this, new DomainAppTearDown());

    }
}
