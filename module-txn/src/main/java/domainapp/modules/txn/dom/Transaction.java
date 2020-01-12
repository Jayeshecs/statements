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
			+ "FROM domainapp.modules.txn.dom.Transaction "),
	@javax.jdo.annotations.Query(name = Transaction.QUERY_FIND_BY_RAWDATA, value = "SELECT "
			+ "FROM domainapp.modules.txn.dom.Transaction "
			+ "WHERE source == :source && rawdata == :rawdata && (rawdataSequence == null || rawdataSequence == :rawdataSequence)")
})
@javax.jdo.annotations.Unique(name="Transaction_hash_UNQ", members = {"source", "rawdata", "rawdataSequence"})
@DomainObject(
        auditing = Auditing.ENABLED
) // objectType inferred from @PersistenceCapable#schema
@XmlJavaTypeAdapter(PersistentEntityAdapter.class)
@EqualsAndHashCode(of = {"source", "type", "transactionDate", "narration", "reference", "amount", "rawdataSequence"})
@ToString(of = {"source", "type", "transactionDate", "narration", "reference", "amount", "rawdataSequence"})
public class Transaction implements Comparable<Transaction> {
	
	public interface FieldConstants {
		String SOURCE = "source";
		String TYPE = "type";
		String CATEGORY = "category";
		String SUB_CATEGORY = "subCategory";
		String AMOUNT = "amount";
		String TRANSACTION_DATE = "transactionDate";
		String NARRATION = "narration";
		String REFERENCE = "reference";
		String RAWDATA = "rawdata";
		String RAWDATA_SEQUENCE = "rawdataSequence";
	}
	
	public static final String QUERY_FIND_BY_RAWDATA = "findByRawdata"; //$NON-NLS-1$
    
    public static class CreateEvent extends ActionDomainEvent<Transaction> {
		private static final long serialVersionUID = 1L;
    }
	
	/**
	 * @return
	 */
	public String title() {
		return String.format("%s %tF %.2f %s, %s", source.getName(), transactionDate, amount, narration == null ? "" : narration, reference == null ? "" : reference);
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
	
    @javax.jdo.annotations.Column(allowsNull = "true")
	@Property(editing = Editing.DISABLED)
    @PropertyLayout(hidden = Where.EVERYWHERE)
	@lombok.Getter @lombok.Setter
	private Integer rawdataSequence;

    @Builder
    public Transaction(final StatementSource source, final TransactionType type, final Date transactionDate, final String narration, final String reference, final BigDecimal amount, final String rawdata, final Integer rawdataSequence) {
        setSource(source);
        setType(type);
        setTransactionDate(transactionDate);
        setNarration(narration);
        setReference(reference);
        setAmount(amount);
        setRawdata(rawdata);
        setRawdataSequence(rawdataSequence);
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