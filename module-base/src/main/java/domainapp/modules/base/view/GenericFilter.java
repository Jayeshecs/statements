package domainapp.modules.base.view;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@EqualsAndHashCode
@ToString(of = {"filter", "parameters"})
public class GenericFilter implements Serializable {

	private static final long serialVersionUID = 1L;

	@Getter @Setter
	private String filter = "";
	
	@Getter @Setter
	private Map<String, Value> parameters = new HashMap<>();
	
	@Getter @Setter
	private Set<String> exclude = new HashSet<>();
	
}