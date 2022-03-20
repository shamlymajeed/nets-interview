package eu.nets.uni.apps.settlement.interview.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.nets.uni.apps.settlement.interview.model.ExchangeRates;
import eu.nets.uni.apps.settlement.interview.repository.ExchangeRateRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
@Slf4j
@AllArgsConstructor
@Component
public class KafkaConsumer {
    private final ObjectMapper objectMapper;
    private ExchangeRateRepository currencyExchangeRateRepository;

    @KafkaListener(
            id="exchange-rates",
            topics = "${interview.kafka-topic-exchange-rates}")
    void consume(String jsonMessage) {
        try {
            log.info("Got message: {}", jsonMessage);
            ExchangeRates exchangeRates = objectMapper.readValue(jsonMessage, ExchangeRates.class);
            currencyExchangeRateRepository.save(exchangeRates);
        } catch(Exception e) {
            log.error("Exception occured while listening to  kafka topic",e);
        }
    }
}
