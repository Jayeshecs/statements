package domainapp.modules.base.view;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@EqualsAndHashCode
@ToString(of = {"dataType", "list", "values"})
public class Value implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Getter @Setter
	private String dataType;
	
	@Getter @Setter
	private Boolean list;
	
	@Getter @Setter
	private List<String> values;
	
	@Builder
	public Value(String dataType, boolean list, String... values) {
		this.dataType = dataType;
		if (values != null) {
			this.values = Arrays.asList(values[0]);
		}
		this.list = list;
	}
	
	public boolean isList() {
		return list;
	}
}