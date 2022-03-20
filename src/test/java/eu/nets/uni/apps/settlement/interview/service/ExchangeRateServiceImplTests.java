package eu.nets.uni.apps.settlement.interview.service;

import eu.nets.uni.apps.settlement.interview.enums.Currency;
import eu.nets.uni.apps.settlement.interview.exception.ExchangeRateNotAvailableException;
import eu.nets.uni.apps.settlement.interview.model.BaseCurrencyToCurrencyAmountDTO;
import eu.nets.uni.apps.settlement.interview.model.ExchangeRateEntry;
import eu.nets.uni.apps.settlement.interview.model.ExchangeRates;
import eu.nets.uni.apps.settlement.interview.repository.ExchangeRateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.boot.test.context.SpringBootTest;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureDataMongo
public class ExchangeRateServiceImplTests {

    @Autowired
    ExchangeRateRepository exchangeRateRepository;

    @Autowired
    ExchangeRateService exchangeRateService;

    private ExchangeRates exchangeRatesLatest = null;

    @BeforeEach
    void setUp() throws Exception {
        exchangeRateRepository.deleteAll();

        ExchangeRateEntry exchangeRateEntry1 = new ExchangeRateEntry(Currency.NOK,BigDecimal.valueOf(10).add(BigDecimal.valueOf(randomVal(-0.1, 0.1))).setScale(4, RoundingMode.HALF_UP));
        ExchangeRateEntry exchangeRateEntry2 = new ExchangeRateEntry(Currency.USD,BigDecimal.valueOf(1).add(BigDecimal.valueOf(randomVal(-0.1, 0.1))).setScale(4, RoundingMode.HALF_UP));
        List<ExchangeRateEntry> exchangeRateEntries = Arrays.asList(exchangeRateEntry1,exchangeRateEntry2);

        ExchangeRates exchangeRates10MinutesOld= new ExchangeRates(null, Instant.now().minus(10, ChronoUnit.MINUTES), Currency.EUR,exchangeRateEntries);
        Instant now = Instant.now();
        ExchangeRates exchangeRatesNow = new ExchangeRates(null, now , Currency.EUR,exchangeRateEntries);
        ExchangeRates exchangeRatesPrettyOld = new ExchangeRates(null, Instant.now().minus(10, ChronoUnit.HOURS) , Currency.EUR,exchangeRateEntries);
        this.exchangeRatesLatest = exchangeRatesNow;

        exchangeRateRepository.save(exchangeRates10MinutesOld);
        exchangeRateRepository.save(exchangeRatesNow);
        exchangeRateRepository.save(exchangeRatesPrettyOld);
    }

    @Test
    void should_return_latest_exchange_rate() throws ExchangeRateNotAvailableException{
        ExchangeRates exchangeRates  =  exchangeRateService.getExchangeRates(Currency.EUR,null);
        assertThat(exchangeRates.getTimestamp().compareTo(this.exchangeRatesLatest.getTimestamp()) == 0);
        assertThat(exchangeRates.getBaseCurrency() == this.exchangeRatesLatest.getBaseCurrency());
        assertThat(exchangeRates.getExchangeRateEntries().size() == this.exchangeRatesLatest.getExchangeRateEntries().size());
    }

    @Test
    void should_return_latest_exchange_rate_for_given_currency() throws ExchangeRateNotAvailableException {
        BigDecimal amountToConvert = BigDecimal.valueOf(500);
        BaseCurrencyToCurrencyAmountDTO dto = exchangeRateService.getAmountFromLatestExchangeRates(Currency.EUR,Currency.USD,amountToConvert);

        assertThat(dto.getBaseCurrency() == this.exchangeRatesLatest.getBaseCurrency());
        assertThat(dto.getBaseCurrencyAmount() == amountToConvert);
        assertThat(dto.getExchangeRate() == this.exchangeRatesLatest.getExchangeRateEntries().get(0).getRate());
        assertThat(dto.getCurrencyAmount() == amountToConvert.multiply(this.exchangeRatesLatest.getExchangeRateEntries().get(0).getRate()));
    }


    private double randomVal(double min, double max) {
        Random r = new Random();
        return min + (max - min) * r.nextDouble();
    }

}
