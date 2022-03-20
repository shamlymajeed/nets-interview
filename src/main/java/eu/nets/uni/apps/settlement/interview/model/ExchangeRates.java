package eu.nets.uni.apps.settlement.interview.model;

import eu.nets.uni.apps.settlement.interview.enums.Currency;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Data
@AllArgsConstructor
@Document("currencyExchangeRates")
public class ExchangeRates {

    @Id
    private String id;
    private Instant timestamp;
    private Currency baseCurrency;

    private List<ExchangeRateEntry> exchangeRateEntries;
}