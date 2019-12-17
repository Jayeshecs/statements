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
@ToString(of = {"dataType", "value"})
public class Value implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Getter @Setter
	private String dataType;
	
	@Getter @Setter
	private String value;
	
	@Getter @Setter
	private List<String> list;
	
	@Builder
	public Value(String dataType, String... values) {
		this.dataType = dataType;
		if (values != null) {
			if (values.length > 1) {
				list = Arrays.asList(values);
			} else {
				this.value = values[0];
			}
		}
	}
	
	public boolean isList() {
		return list != null && !list.isEmpty();
	}
}