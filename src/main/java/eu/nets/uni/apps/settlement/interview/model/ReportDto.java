package eu.nets.uni.apps.settlement.interview.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import eu.nets.uni.apps.settlement.interview.enums.Currency;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Component
@Getter
@Setter
public class ReportDto {

    private Currency baseCurrency;

    @JacksonXmlElementWrapper(useWrapping = false)
    private List<TimeWiseReportDTO> reports = new ArrayList<>();

    @Data
    public static class TimeWiseReportDTO{
        String time;

        @JacksonXmlElementWrapper(useWrapping = false)
        List<CurrencyAverageRateDto> currencyAverageRates;
    }

    @Data
    public static class CurrencyAverageRateDto {

        Currency currency;
        BigDecimal averageRate;

    }

}
