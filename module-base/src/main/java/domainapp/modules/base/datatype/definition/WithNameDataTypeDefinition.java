package domainapp.modules.base.datatype.definition;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import domainapp.modules.base.entity.NamedQueryConstants;
import domainapp.modules.base.entity.WithName;
import domainapp.modules.base.service.AbstractService;

/**
 * Abstract implementation of {@link IDataTypeDefinition} for entities that are {@link WithName}
 * 
 * @author jayeshecs
 * @param <T> Sub-class of {@link WithName}
 */
public abstract class WithNameDataTypeDefinition<T extends WithName> implements IDataTypeDefinition<T> {
	
	
	/* (non-Javadoc)
	 * @see domainapp.modules.base.datatype.IDataTypeDefinition#parse(java.lang.String)
	 */
	@Override
	public T parse(String value) {
		if (value == null) {
			return null;
		}
		List<T> records = getService().search(NamedQueryConstants.QUERY_FIND_BY_NAME, WithName.FIELD_NAME, value);
		Optional<T> record = records.stream().filter(p -> {
			return p.getName().equals(value);
		}).findFirst();
		if (!record.isPresent()) {
			return null;
		}
		return record.get();
	}
	
	/* (non-Javadoc)
	 * @see domainapp.modules.base.datatype.IDataTypeDefinition#parseAsList(java.lang.String)
	 */
	@Override
	public List<T> parseAsList(String values) {
		if (values == null) {
			return null;
		}
		final List<T> result = new ArrayList<>();
		for (String value : values.split(VALUE_DELIMITER)) {
			List<T> records = getService().search(NamedQueryConstants.QUERY_FIND_BY_NAME, WithName.FIELD_NAME, value);
			Optional<T> record = records.stream().filter(p -> {
				return p.getName().equals(value);
			}).findFirst();
			if (record.isPresent()) {
				result.add(record.get());
			}
		}
		return result;
	}
	
	@Override
	public String format(T value) {
		if (value == null) {
			return null;
		}
		return value.getName();
	}

	/* (non-Javadoc)
	 * @see domainapp.modules.base.datatype.IDataTypeDefinition#format(java.util.List)
	 */
	@Override
	public String format(List<T> values) {
		if (values == null || values.isEmpty()) {
			return null;
		}
		StringBuilder result = new StringBuilder();
		values.forEach(record -> {
			result.append(record.getName()).append(VALUE_DELIMITER);
		});
		result.setLength(result.length() - VALUE_DELIMITER.length());
		return result.toString();
	}
	
	@Override
	public String[] formatAsArray(List<T> values) {
		if (values == null) {
			return null;
		}
		String[] result = new String[values.size()];
		int[] counter = new int[] {0};
		values.forEach(record -> {
			result[counter[0]++] = record.getName();
		});
		return result;
	}

	/**
	 * @return return instance of {@link AbstractService} of generic type T for searching purpose
	 * @see sub-classes of {@link AbstractService}
	 */
	protected abstract AbstractService<T> getService();

}
