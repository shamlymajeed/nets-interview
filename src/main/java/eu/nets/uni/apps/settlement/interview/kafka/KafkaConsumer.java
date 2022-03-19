package eu.nets.uni.apps.settlement.interview.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.nets.uni.apps.settlement.interview.model.ExchangeRates;
import eu.nets.uni.apps.settlement.interview.repository.CurrencyExchangeRateRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
@Slf4j
@AllArgsConstructor
@Component
public class KafkaConsumer {
    private final ObjectMapper objectMapper;
    private CurrencyExchangeRateRepository currencyExchangeRateRepository;

    @KafkaListener(
            id="exchange-rates",
            topics = "${interview.kafka-topic-exchange-rates}")
    void consume(String jsonMessage) throws Exception{
        log.info("Got message: {}", jsonMessage);
        ExchangeRates exchangeRates = objectMapper.readValue(jsonMessage, ExchangeRates.class);
        currencyExchangeRateRepository.save(exchangeRates);
    }
}
