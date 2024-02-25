package oxahex.asker.server.type;

import org.springframework.core.convert.converter.Converter;

public class SortTypeConverter implements Converter<String, SortType> {

	@Override
	public SortType convert(String source) {
		try {
			return SortType.valueOf(source.toUpperCase());
		} catch (IllegalArgumentException e) {
			return null;
		}
	}
}
