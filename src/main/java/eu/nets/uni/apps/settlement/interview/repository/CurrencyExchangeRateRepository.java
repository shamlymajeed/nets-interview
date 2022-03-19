package eu.nets.uni.apps.settlement.interview.repository;

import eu.nets.uni.apps.settlement.interview.model.ExchangeRates;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CurrencyExchangeRateRepository extends MongoRepository<ExchangeRates,String> {

}
