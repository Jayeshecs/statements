package domainapp.modules.txn.dom;

import java.math.BigDecimal;
import java.util.Date;

import javax.jdo.annotations.Extension;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.VersionStrategy;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.isis.applib.annotation.Auditing;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.services.message.MessageService;
import org.apache.isis.applib.services.repository.RepositoryService;
import org.apache.isis.applib.services.title.TitleService;
import org.apache.isis.applib.util.ObjectContracts;
import org.apache.isis.schema.utils.jaxbadapters.PersistentEntityAdapter;

import domainapp.modules.ref.dom.Category;
import domainapp.modules.ref.dom.SubCategory;
import domainapp.modules.ref.dom.TransactionType;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@javax.jdo.annotations.PersistenceCapable(
        identityType=IdentityType.DATASTORE,
        schema = "statements"
)
@javax.jdo.annotations.DatastoreIdentity(
        strategy=javax.jdo.annotations.IdGeneratorStrategy.IDENTITY,
        column="id")
@javax.jdo.annotations.Version(
        strategy= VersionStrategy.DATE_TIME,
        column="version")
@javax.jdo.annotations.Unique(name="Transaction_hash_UNQ", members = {"source", "type", "transactionDate", "narration", "reference", "amount"})
@DomainObject(
        auditing = Auditing.ENABLED
) // objectType inferred from @PersistenceCapable#schema
@XmlJavaTypeAdapter(PersistentEntityAdapter.class)
@EqualsAndHashCode(of = {"source", "type", "transactionDate", "narration", "reference", "amount"})
@ToString(of = {"source", "type", "transactionDate", "narration", "reference", "amount"})
public class Transaction implements Comparable<Transaction> {

    @Builder
    public Transaction(final StatementSource source, final TransactionType type, final Date transactionDate, final String narration, final String reference, final BigDecimal amount) {
        setSource(source);
        setType(type);
        setTransactionDate(transactionDate);
        setNarration(narration);
        setReference(reference);
        setAmount(amount);
    }
	
	@javax.jdo.annotations.Column(name="SOURCE_ID", allowsNull = "false")
	@Property(editing = Editing.ENABLED)
	@MemberOrder(sequence = "1")
	@lombok.Getter @lombok.Setter @lombok.NonNull
	private StatementSource source;
	
    @javax.jdo.annotations.Column(allowsNull = "false")
    @Property(editing = Editing.ENABLED)
	@MemberOrder(sequence = "2")
	@Extension(vendorName="datanucleus", key="enum-value-getter", value="id")
	@lombok.Getter @lombok.Setter @lombok.NonNull
	private TransactionType type;
	
    @javax.jdo.annotations.Column(name="CATEGORY_ID")
	@Property(editing = Editing.ENABLED)
	@MemberOrder(sequence = "3")
	@lombok.Getter @lombok.Setter
	private Category category;
	
    @javax.jdo.annotations.Column(name="SUB_CATEGORY_ID")
	@Property(editing = Editing.ENABLED)
    @MemberOrder(sequence = "4")
	@lombok.Getter @lombok.Setter
	private SubCategory subCategory;
	
    @javax.jdo.annotations.Column(allowsNull = "false")
	@Property(editing = Editing.ENABLED)
	@MemberOrder(sequence = "5")
	@lombok.Getter @lombok.Setter @lombok.NonNull
	private BigDecimal amount;
	
    @javax.jdo.annotations.Column(allowsNull = "false")
	@Property(editing = Editing.ENABLED)
	@MemberOrder(sequence = "6")
	@lombok.Getter @lombok.Setter @lombok.NonNull
	private Date transactionDate;
	
    @javax.jdo.annotations.Column(allowsNull = "false")
	@Property(editing = Editing.ENABLED)
	@MemberOrder(sequence = "7")
	@lombok.Getter @lombok.Setter @lombok.NonNull
	private Date valueDate;
	
    @javax.jdo.annotations.Column(allowsNull = "false", length = 4000)
	@Property(editing = Editing.ENABLED)
	@MemberOrder(sequence = "8")
	@lombok.Getter @lombok.Setter @lombok.NonNull
    private String narration;
	
    @javax.jdo.annotations.Column(allowsNull = "true")
	@Property(editing = Editing.ENABLED)
	@MemberOrder(sequence = "9")
	@lombok.Getter @lombok.Setter
	private String reference;
	
    @javax.jdo.annotations.Column(allowsNull = "true", length = 4000)
	@Property(editing = Editing.ENABLED)
    @PropertyLayout(hidden = Where.STANDALONE_TABLES)
	@lombok.Getter @lombok.Setter
	private String rawdata;

    @Override
    public int compareTo(final Transaction other) {
        return ObjectContracts.compare(this, other, "name");
    }

    @javax.inject.Inject
    RepositoryService repositoryService;

    @javax.inject.Inject
    TitleService titleService;

    @javax.inject.Inject
    MessageService messageService;

}