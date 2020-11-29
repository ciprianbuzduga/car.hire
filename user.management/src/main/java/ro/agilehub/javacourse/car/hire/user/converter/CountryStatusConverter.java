package ro.agilehub.javacourse.car.hire.user.converter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import ro.agilehub.javacourse.car.hire.user.document.CountryStatusEnum;

@ReadingConverter
public enum CountryStatusConverter implements Converter<String, CountryStatusEnum> {
    INSTANCE;

    private static final Log LOGGER = LogFactory.getLog(CountryStatusConverter.class);

    @Override
    public CountryStatusEnum convert(String source) {
        LOGGER.info("Convert " + source + " into " + CountryStatusEnum.class.getSimpleName());
        CountryStatusEnum status = null;
        try {
            status = CountryStatusEnum.fromValue(source);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return status;
    }

}
