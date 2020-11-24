package ro.agilehub.javacourse.car.hire.converter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

import ro.agilehub.javacourse.car.hire.fleet.document.CarStatusEnum;

@ReadingConverter
public enum CountryStatusConverter implements Converter<String, CarStatusEnum> {
	INSTANCE;

	private static final Log LOGGER = LogFactory.getLog(CountryStatusConverter.class);

	@Override
	public CarStatusEnum convert(String source) {
		LOGGER.info("Convert " + source + " into " + CarStatusEnum.class.getSimpleName());
		CarStatusEnum status = null;
		try {
			status = CarStatusEnum.fromValue(source);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		return status;
	}

}
