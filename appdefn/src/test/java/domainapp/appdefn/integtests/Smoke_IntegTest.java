package domainapp.appdefn.integtests;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.fixturescripts.FixtureScripts;
import org.apache.isis.applib.services.xactn.TransactionService;
import org.junit.Rule;
import org.junit.Test;
import org.togglz.junit.TogglzRule;

import domainapp.appdefn.fixture.teardown.DomainAppTearDown;
import domainapp.modules.base.togglz.TogglzFeature;
import domainapp.modules.txn.dom.StatementsMenu;
import domainapp.modules.txn.dom.Transaction;

public class Smoke_IntegTest extends DomainAppIntegTestAbstract {

    @Inject
    FixtureScripts fixtureScripts;
    @Inject
    TransactionService transactionService;
    @Inject
    StatementsMenu menu;

    @Rule
    public TogglzRule togglzRule = TogglzRule.allEnabled(TogglzFeature.class);


    @Test
    public void create() throws Exception {

        // given
        DomainAppTearDown fs = new DomainAppTearDown();
        fixtureScripts.runFixtureScript(fs, null);
        transactionService.nextTransaction();


        // when
        List<Transaction> all = wrap(menu).listAll();

        // then
        assertThat(all).isEmpty();
        
        transactionService.flushTransaction();

    }

}

