package ro.agilehub.javacourse.car.hire.converter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

import ro.agilehub.javacourse.car.hire.api.model.CarStatusDTO;

@ReadingConverter
public enum CarStatusConverter implements Converter<String, CarStatusDTO> {
	INSTANCE;

	private static final Log LOGGER = LogFactory.getLog(CarStatusConverter.class);

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
