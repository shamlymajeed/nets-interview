package eu.nets.uni.apps.settlement.interview.controller;

import eu.nets.uni.apps.settlement.interview.enums.Currency;
import eu.nets.uni.apps.settlement.interview.exception.ExchangeRateNotAvailableException;
import eu.nets.uni.apps.settlement.interview.model.BaseCurrencyToCurrencyAmountDTO;
import eu.nets.uni.apps.settlement.interview.model.ExchangeRates;
import eu.nets.uni.apps.settlement.interview.service.ExchangeRateService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@RestController
public class ExchangeRateController {

    private ExchangeRateService exchangeRateService;

    public ExchangeRateController(ExchangeRateService exchangeRateService){
        this.exchangeRateService = exchangeRateService;
    }

    @Operation(summary = "Latest exchange rates for base currency in json document",
            description = "Latest exchange rates for base currency in json document.Provision to fetch exchange rate at a given time is also added." +
                    "If the exchange rate for given time is not available,then the latest available rate before the given time will be provided ",
            tags = { "exchangerate" })
    @GetMapping(value = "/v1/exchange-rates/{baseCurrency}",produces = "application/json")
    public ResponseEntity<ExchangeRates> getLatestExchangeRate(@PathVariable Currency baseCurrency,
                                                               @RequestParam(value = "datetime", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)LocalDateTime datetime) throws ExchangeRateNotAvailableException {

        ExchangeRates exchangeRates = exchangeRateService.getExchangeRates(baseCurrency, datetime);
        return new ResponseEntity<>(exchangeRates, HttpStatus.OK);
    }

    @Operation(summary = "Calculate a currency exchange amount, from amount in base-currency to currency, based on the latest rate.",
                tags = { "exchangerate" })
    @GetMapping(value = "/v1/exchange-rates/{baseCurrency}/{currency}",produces = "application/json")
    public ResponseEntity<BaseCurrencyToCurrencyAmountDTO> getAmountFromLatestExchangeRate(@PathVariable Currency baseCurrency,
                                                                                           @PathVariable Currency currency,
                                                                                           @RequestParam(value = "base-currency-amount") BigDecimal baseCurrencyAmount) throws ExchangeRateNotAvailableException {

        BaseCurrencyToCurrencyAmountDTO dto = exchangeRateService.getAmountFromLatestExchangeRates(baseCurrency,currency,baseCurrencyAmount);
        return new ResponseEntity<>(dto,HttpStatus.OK);
    }



}
