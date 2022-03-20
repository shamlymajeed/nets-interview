package eu.nets.uni.apps.settlement.interview.service;

import eu.nets.uni.apps.settlement.interview.enums.Currency;
import eu.nets.uni.apps.settlement.interview.exception.ExchangeRateNotAvailableException;
import eu.nets.uni.apps.settlement.interview.model.BaseCurrencyToCurrencyAmountDTO;
import eu.nets.uni.apps.settlement.interview.model.ExchangeRateEntry;
import eu.nets.uni.apps.settlement.interview.model.ExchangeRates;
import eu.nets.uni.apps.settlement.interview.repository.ExchangeRateRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
@Slf4j
@Service
public class ExchangeRateServiceImpl implements ExchangeRateService{

    private final ExchangeRateRepository exchangeRateRepository;

    public ExchangeRateServiceImpl(ExchangeRateRepository exchangeRateRepository){
         this.exchangeRateRepository = exchangeRateRepository;
    }

    @Override
    public ExchangeRates getExchangeRates(Currency currency , LocalDateTime timestamp) throws ExchangeRateNotAvailableException{

        ExchangeRates exchangeRates = null;
        if (timestamp == null) {
            exchangeRates = exchangeRateRepository.findFirstByBaseCurrencyOrderByTimestampDesc(currency);

        } else {
            exchangeRates = exchangeRateRepository.findFirstByBaseCurrencyAndTimestampLessThanEqualOrderByTimestampDesc(currency, timestamp.toInstant(ZoneOffset.UTC));
        }
        if(exchangeRates == null){
            throw new ExchangeRateNotAvailableException();
        }
        return exchangeRates;
    }

    public BaseCurrencyToCurrencyAmountDTO getAmountFromLatestExchangeRates(Currency baseCurrency, Currency currency,BigDecimal baseCurrencyAmount) throws ExchangeRateNotAvailableException{

        ExchangeRates exchangeRates = exchangeRateRepository.findFirstByBaseCurrencyAndExchangeRateEntriesCurrencyOrderByTimestampDesc(baseCurrency,currency);
        if(exchangeRates == null){
            throw new ExchangeRateNotAvailableException();
        }
        ExchangeRateEntry exchangeRateEntry = exchangeRates.getExchangeRateEntries().get(0);
        BigDecimal amount = baseCurrencyAmount.multiply(exchangeRateEntry.getRate());
        BaseCurrencyToCurrencyAmountDTO baseCurrencyToCurrencyAmount = BaseCurrencyToCurrencyAmountDTO.builder()
                                                                            .baseCurrency(baseCurrency)
                                                                            .baseCurrencyAmount(baseCurrencyAmount)
                                                                            .currency(currency)
                                                                            .exchangeRate(exchangeRateEntry.getRate())
                                                                            .currencyAmount(amount)
                                                                            .build();
        return  baseCurrencyToCurrencyAmount;
    }
}
