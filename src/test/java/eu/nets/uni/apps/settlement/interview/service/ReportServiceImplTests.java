package eu.nets.uni.apps.settlement.interview.service;

import eu.nets.uni.apps.settlement.interview.enums.Currency;
import eu.nets.uni.apps.settlement.interview.exception.ExchangeRateNotAvailableException;
import eu.nets.uni.apps.settlement.interview.model.ExchangeRateEntry;
import eu.nets.uni.apps.settlement.interview.model.ExchangeRates;
import eu.nets.uni.apps.settlement.interview.model.ReportDto;
import eu.nets.uni.apps.settlement.interview.repository.ExchangeRateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureDataMongo
public class ReportServiceImplTests {

    @Autowired
    ReportService reportService;

    @Autowired
    ExchangeRateRepository exchangeRateRepository;

    @Autowired
    ReportDto reportDto;

    private BigDecimal NOK_VALUE = BigDecimal.valueOf(10);
    private BigDecimal ANOTHER_NOK_VALUE = BigDecimal.valueOf(10.02);

    private BigDecimal USD_VALUE = BigDecimal.valueOf(1);

    @BeforeEach
    public void setupBefore(){
        exchangeRateRepository.deleteAll();
    }

    @Test
    void should_return_average_exchange_rate_when_only_one_entry_exist() throws ExchangeRateNotAvailableException{

        ExchangeRateEntry exchangeRateEntry = new ExchangeRateEntry(Currency.NOK, NOK_VALUE);

        List<ExchangeRateEntry> exchangeRateEntries = Arrays.asList(exchangeRateEntry);
        ExchangeRates exchangeRates=new ExchangeRates(null,Instant.now(),Currency.EUR,exchangeRateEntries);
        exchangeRateRepository.save(exchangeRates);

        ReportDto exchangeRatesList = reportService.getAverageExchangeRate(Currency.EUR);

        assertThat(exchangeRatesList.getReports().get(0).getCurrencyAverageRates().size()==1);
        assertThat(exchangeRateEntry.getCurrency() == exchangeRatesList.getReports().get(0).getCurrencyAverageRates().get(0).getCurrency());
        assertThat(NOK_VALUE == exchangeRatesList.getReports().get(0).getCurrencyAverageRates().get(0).getAverageRate());

    }

    @Test
    void should_throw_exchange_rate_not_available_exception_when_last_entry_exist_before_10_minutes(){

        ExchangeRateEntry exchangeRateEntry1 = new ExchangeRateEntry(Currency.NOK, NOK_VALUE);
        ExchangeRateEntry exchangeRateEntry2 = new ExchangeRateEntry(Currency.NOK, USD_VALUE);
        ExchangeRateEntry exchangeRateEntry3 = new ExchangeRateEntry(Currency.NOK, USD_VALUE);

        List<ExchangeRateEntry> exchangeRateEntries = Arrays.asList(exchangeRateEntry1 , exchangeRateEntry2 , exchangeRateEntry3);
        ExchangeRates exchangeRates=new ExchangeRates(null,Instant.now().minus(15, ChronoUnit.MINUTES),Currency.EUR , exchangeRateEntries);
        exchangeRateRepository.save(exchangeRates);

        assertThrows(ExchangeRateNotAvailableException.class,()->{reportService.getAverageExchangeRate(Currency.EUR);});
    }

    @Test
    void should_return_correct_average_rate_for_a_currency_in_different_minutes() throws ExchangeRateNotAvailableException{

        Instant sameTimestamp = Instant.now();

        ExchangeRateEntry exchangeRateEntry1 = new ExchangeRateEntry(Currency.NOK, NOK_VALUE);
        List<ExchangeRateEntry> exchangeRateEntries = Arrays.asList(exchangeRateEntry1);
        ExchangeRates exchangeRates1 = new ExchangeRates(null,sameTimestamp,Currency.EUR , exchangeRateEntries);
        exchangeRateRepository.save(exchangeRates1);

        ExchangeRateEntry exchangeRateEntry2 = new ExchangeRateEntry(Currency.NOK, ANOTHER_NOK_VALUE);
        List<ExchangeRateEntry> exchangeRateEntries2 = Arrays.asList(exchangeRateEntry2);
        ExchangeRates exchangeRates2=new ExchangeRates(null,sameTimestamp,Currency.EUR , exchangeRateEntries2);
        exchangeRateRepository.save(exchangeRates2);

        ExchangeRateEntry exchangeRateEntry3 = new ExchangeRateEntry(Currency.NOK, NOK_VALUE);
        List<ExchangeRateEntry> exchangeRateEntries3 = Arrays.asList(exchangeRateEntry3);
        ExchangeRates exchangeRates3=new ExchangeRates(null,sameTimestamp,Currency.EUR , exchangeRateEntries3);
        exchangeRateRepository.save(exchangeRates3);

        BigDecimal avgNokValueOfsameTimeStamp = (NOK_VALUE.add(ANOTHER_NOK_VALUE).add(NOK_VALUE)).divide(BigDecimal.valueOf(3), RoundingMode.HALF_UP);

        ReportDto reportDto = reportService.getAverageExchangeRate(Currency.EUR);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm").withZone(ZoneId.from(ZoneOffset.UTC));

        assertThat(reportDto.getReports().get(0).getCurrencyAverageRates().get(0).getAverageRate().equals(avgNokValueOfsameTimeStamp));
        assertThat(reportDto.getReports().get(0).getTime()==formatter.format(sameTimestamp));
    }



}
