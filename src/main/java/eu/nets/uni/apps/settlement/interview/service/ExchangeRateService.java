package eu.nets.uni.apps.settlement.interview.service;

import eu.nets.uni.apps.settlement.interview.enums.Currency;
import eu.nets.uni.apps.settlement.interview.exception.ExchangeRateNotAvailableException;
import eu.nets.uni.apps.settlement.interview.model.BaseCurrencyToCurrencyAmountDTO;
import eu.nets.uni.apps.settlement.interview.model.ExchangeRates;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface ExchangeRateService {

    ExchangeRates getExchangeRates(Currency baseCurrency, LocalDateTime timestamp) throws ExchangeRateNotAvailableException;
    BaseCurrencyToCurrencyAmountDTO getAmountFromLatestExchangeRates(Currency baseCurrency, Currency currency, BigDecimal baseCurrencyAmount) throws ExchangeRateNotAvailableException;

}
