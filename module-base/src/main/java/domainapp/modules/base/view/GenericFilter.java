package domainapp.modules.base.view;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

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
	private Map<String, Object> parameters = new HashMap<>();
}