package eu.nets.uni.apps.settlement.interview.model;

import eu.nets.uni.apps.settlement.interview.enums.Currency;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class ExchangeRateEntry {

    private Currency currency;
    private BigDecimal rate;

}