package eu.nets.uni.apps.settlement.interview.model;

import eu.nets.uni.apps.settlement.interview.enums.Currency;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
public class BaseCurrencyToCurrencyAmountDTO {

    private Currency baseCurrency;
    private BigDecimal baseCurrencyAmount;
    private Currency currency;
    private BigDecimal exchangeRate;
    private BigDecimal currencyAmount;

}
