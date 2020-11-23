package ro.agilehub.javacourse.car.hire.converter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

import ro.agilehub.javacourse.car.hire.api.model.CarClazzCodeDTO;

@ReadingConverter
public enum CarClazzConverter implements Converter<String, CarClazzCodeDTO> {

	INSTANCE;

	private static final Log LOGGER = LogFactory.getLog(CarStatusConverter.class);

	@Override
	public CarClazzCodeDTO convert(String source) {
		LOGGER.info("Convert " + source + " into " + CarClazzCodeDTO.class.getSimpleName());
		CarClazzCodeDTO carClazz = null;
		try {
			carClazz = CarClazzCodeDTO.valueOf(source);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return carClazz;
	}

}
