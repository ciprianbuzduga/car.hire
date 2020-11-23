package ro.agilehub.javacourse.car.hire.converter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

import ro.agilehub.javacourse.car.hire.api.model.CarStatusDTO;

@ReadingConverter
public enum CountryStatusConverter implements Converter<String, CarStatusDTO> {
	INSTANCE;

	private static final Log LOGGER = LogFactory.getLog(CountryStatusConverter.class);

	@Override
	public CarStatusDTO convert(String source) {
		LOGGER.info("Convert " + source + " into " + CarStatusDTO.class.getSimpleName());
		CarStatusDTO status = null;
		try {
			status = CarStatusDTO.fromValue(source);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		return status;
	}

}
