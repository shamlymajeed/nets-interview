package eu.nets.uni.apps.settlement.interview.service;

import eu.nets.uni.apps.settlement.interview.enums.Currency;
import eu.nets.uni.apps.settlement.interview.exception.ExchangeRateNotAvailableException;
import eu.nets.uni.apps.settlement.interview.model.ReportDto;

public interface ReportService {

    ReportDto getAverageExchangeRate(Currency baseCurrency) throws ExchangeRateNotAvailableException;
}
