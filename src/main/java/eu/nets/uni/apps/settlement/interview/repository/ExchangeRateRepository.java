package eu.nets.uni.apps.settlement.interview.repository;

import eu.nets.uni.apps.settlement.interview.enums.Currency;
import eu.nets.uni.apps.settlement.interview.model.ExchangeRates;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import java.time.Instant;
import java.util.List;

public interface ExchangeRateRepository extends MongoRepository<ExchangeRates,String> {

        ExchangeRates findFirstByBaseCurrencyOrderByTimestampDesc(Currency baseCurrency);

        ExchangeRates findFirstByBaseCurrencyAndTimestampLessThanEqualOrderByTimestampDesc(Currency baseCurrency , Instant timeStamp);

        List<ExchangeRates> findByBaseCurrencyAndTimestampGreaterThanOrderByTimestampAsc(Currency baseCurrency , Instant timeStamp);

        @Query(fields = "{ 'timestamp': 1,'baseCurrency':1, 'exchangeRateEntries.$': 1 }")
        ExchangeRates findFirstByBaseCurrencyAndExchangeRateEntriesCurrencyOrderByTimestampDesc(Currency baseCurrency,Currency currency);

}
