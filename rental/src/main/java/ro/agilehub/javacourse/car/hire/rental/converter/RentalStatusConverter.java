package ro.agilehub.javacourse.car.hire.rental.converter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import ro.agilehub.javacourse.car.hire.rental.document.RentalStatusEnum;

@ReadingConverter
public enum RentalStatusConverter implements Converter<String, RentalStatusEnum> {
	INSTANCE;

	private static final Log LOGGER = LogFactory.getLog(RentalStatusConverter.class);

	@Override
	public RentalStatusEnum convert(String source) {
		LOGGER.info("Convert " + source + " into " + RentalStatusEnum.class.getSimpleName());
		RentalStatusEnum status = null;
		try {
			status = RentalStatusEnum.fromValue(source);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		return status;
	}

}
