package eu.nets.uni.apps.settlement.interview.kafka;

import eu.nets.uni.apps.settlement.interview.enums.Currency;
import eu.nets.uni.apps.settlement.interview.model.ExchangeRates;
import eu.nets.uni.apps.settlement.interview.repository.ExchangeRateRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureDataMongo
public class KafkaConsumerTests {

    @Autowired
    private KafkaConsumer kafkaConsumer;

    @Autowired
    private ExchangeRateRepository currencyExchangeRateRepository;

    private static final String kafka_data_sample = "{\"timestamp\":1647695820.001478000,\"baseCurrency\":\"EUR\",\"exchangeRateEntries\":[{\"currency\":\"NOK\",\"rate\":10.5356}]}";

    @Test
    void consume_method_should_save_json_data_to_db(){
        kafkaConsumer.consume(kafka_data_sample);

        List<ExchangeRates> exchangeRates = currencyExchangeRateRepository.findAll();

        assertThat(exchangeRates.size() == 1);
        assertThat(exchangeRates.get(0).getExchangeRateEntries().get(0).getRate() == BigDecimal.valueOf(10.5356));
        assertThat(exchangeRates.get(0).getBaseCurrency() == Currency.EUR);
    }


}
