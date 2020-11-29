package ro.agilehub.javacourse.car.hire.fleet.converter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import ro.agilehub.javacourse.car.hire.fleet.document.CarClazzEnum;

@ReadingConverter
public enum CarClazzConverter implements Converter<String, CarClazzEnum> {

	INSTANCE;

	private static final Log LOGGER = LogFactory.getLog(CarStatusConverter.class);

	@Override
	public CarClazzEnum convert(String source) {
		LOGGER.info("Convert " + source + " into " + CarClazzEnum.class.getSimpleName());
		CarClazzEnum carClazz = null;
		try {
			carClazz = CarClazzEnum.valueOf(source);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return carClazz;
	}

}
