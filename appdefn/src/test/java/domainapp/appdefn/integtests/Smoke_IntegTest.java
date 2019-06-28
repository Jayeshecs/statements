package domainapp.appdefn.integtests;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.fixturescripts.FixtureScripts;
import org.apache.isis.applib.services.xactn.TransactionService;
import org.junit.Test;

import domainapp.appdefn.fixture.teardown.DomainAppTearDown;
import domainapp.modules.txn.dom.StatementsMenu;
import domainapp.modules.txn.dom.Transaction;
import domainapp.modules.txn.view.dashboard.ManageTransactionDashboard;

public class Smoke_IntegTest extends DomainAppIntegTestAbstract {

    @Inject
    FixtureScripts fixtureScripts;
    @Inject
    TransactionService transactionService;
    @Inject
    StatementsMenu menu;


    @Test
    public void create() throws Exception {

        // given
        DomainAppTearDown fs = new DomainAppTearDown();
        fixtureScripts.runFixtureScript(fs, null);
        transactionService.nextTransaction();


        // when
        // TODO: [JP]
        ManageTransactionDashboard manageTransaction = wrap(menu).manageTransaction();

        // then
        assertThat(manageTransaction).isNotNull();
        
        List<Transaction> transactions = manageTransaction.getTransactions();

        // then
        assertThat(transactions).isNotNull();
        
        transactionService.flushTransaction();

    }

}

