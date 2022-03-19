package eu.nets.uni.apps.settlement.interview.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class ExchangeRateEntry {

    private String currency;
    private BigDecimal rate;

}