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
import org.apache.isis.schema.utils.jaxbadapters.PersistentEntityAdapter;

import domainapp.modules.base.entity.NamedQueryConstants;
import domainapp.modules.ref.StaticModule.ActionDomainEvent;
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
@javax.jdo.annotations.Queries({
	@javax.jdo.annotations.Query(name = NamedQueryConstants.QUERY_ALL, value = "SELECT "
			+ "FROM domainapp.modules.txn.dom.Transaction ")
})
@javax.jdo.annotations.Unique(name="Transaction_hash_UNQ", members = {"source", "type", "transactionDate", "narration", "reference", "amount"})
@DomainObject(
        auditing = Auditing.ENABLED
) // objectType inferred from @PersistenceCapable#schema
@XmlJavaTypeAdapter(PersistentEntityAdapter.class)
@EqualsAndHashCode(of = {"source", "type", "transactionDate", "narration", "reference", "amount"})
@ToString(of = {"source", "type", "transactionDate", "narration", "reference", "amount"})
public class Transaction implements Comparable<Transaction> {
    
    public static class CreateEvent extends ActionDomainEvent<Transaction> {
		private static final long serialVersionUID = 1L;
    }
	
	@javax.jdo.annotations.Column(name="sourceId", allowsNull = "false")
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
	
    @javax.jdo.annotations.Column(name="categoryId")
	@Property(editing = Editing.ENABLED)
	@MemberOrder(sequence = "3")
	@lombok.Getter @lombok.Setter
	private Category category;
	
    @javax.jdo.annotations.Column(name="subCategoryId")
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
//	TODO: is this needed ? Mostly NO
//    @javax.jdo.annotations.Column(allowsNull = "false")
//	@Property(editing = Editing.ENABLED)
//	@MemberOrder(sequence = "7")
//	@lombok.Getter @lombok.Setter @lombok.NonNull
//	private Date valueDate;
	
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
	@Property(editing = Editing.DISABLED)
    @PropertyLayout(hidden = Where.EVERYWHERE)
	@lombok.Getter @lombok.Setter
	private String rawdata;

    @Builder
    public Transaction(final StatementSource source, final TransactionType type, final Date transactionDate, final String narration, final String reference, final BigDecimal amount, final String rawdata) {
        setSource(source);
        setType(type);
        setTransactionDate(transactionDate);
        setNarration(narration);
        setReference(reference);
        setAmount(amount);
        setRawdata(rawdata);
    }

    @Override
    public int compareTo(final Transaction other) {
    	if (other == null) {
    		return -1;
    	}
    	int result = 0;
    	result = source.compareTo(other.source);
    	if (result != 0) {
    		return result;
    	}
    	result = type.compareTo(other.type);
    	if (result != 0) {
    		return result;
    	}
    	result = transactionDate.compareTo(other.transactionDate);
    	if (result != 0) {
    		return result;
    	}
    	result = narration.compareTo(other.narration);
    	if (result != 0) {
    		return result;
    	}
    	result = reference.compareTo(other.reference);
    	if (result != 0) {
    		return result;
    	}
    	result = amount.compareTo(other.amount);
    	return result;
    }

}