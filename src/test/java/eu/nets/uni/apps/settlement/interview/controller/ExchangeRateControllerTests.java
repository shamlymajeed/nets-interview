package eu.nets.uni.apps.settlement.interview.controller;

import eu.nets.uni.apps.settlement.interview.enums.Currency;
import eu.nets.uni.apps.settlement.interview.exception.ExchangeRateNotAvailableException;
import eu.nets.uni.apps.settlement.interview.model.BaseCurrencyToCurrencyAmountDTO;
import eu.nets.uni.apps.settlement.interview.model.ExchangeRates;
import eu.nets.uni.apps.settlement.interview.service.ExchangeRateService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.time.Instant;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ExchangeRateController.class)
@Import(ControllerExceptionHandler.class)
public class ExchangeRateControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ExchangeRateService exchangeRateService;

    private static final String BASE_CURRENCY_CODE      = Currency.EUR.name();
    private static final String UNKNOWN_CURRENCY_CODE   = "UNKNOWN";


    @Test
    public void should_return_currency_exchange_rate() throws Exception{

        ExchangeRates exchangeRates = new ExchangeRates(null, Instant.now() , Currency.valueOf("EUR") , null);

        when(exchangeRateService.getExchangeRates(any(Currency.class),any())).thenReturn(exchangeRates);

        this.mockMvc.perform(MockMvcRequestBuilders.get("/v1/exchange-rates/"+BASE_CURRENCY_CODE)
                        .accept(MediaType.APPLICATION_JSON))
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andExpect(MockMvcResultMatchers.jsonPath("$.baseCurrency").value("EUR"))
                        ;
    }

    @Test
    public void should_not_return_any_result_if_exchange_rate_not_found() throws Exception{

        when(exchangeRateService.getAmountFromLatestExchangeRates(any(Currency.class),any(Currency.class),any(BigDecimal.class))).thenThrow(ExchangeRateNotAvailableException.class);

        this.mockMvc.perform(MockMvcRequestBuilders.get("/v1/exchange-rates/EUR/YER?base-currency-amount=100")
                    .accept(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Exchange rate for the given currency is not available."));
    }

    @Test
    public void should_return_correct_currency_amount() throws Exception{

        BaseCurrencyToCurrencyAmountDTO dto = new BaseCurrencyToCurrencyAmountDTO(Currency.EUR,BigDecimal.valueOf(500),Currency.NOK,BigDecimal.valueOf(9.8357),BigDecimal.valueOf(4917.8500));

        when(exchangeRateService.getAmountFromLatestExchangeRates(any(Currency.class),any(Currency.class),any(BigDecimal.class))).thenReturn(dto);

        this.mockMvc.perform(MockMvcRequestBuilders.get("/v1/exchange-rates/EUR/YER?base-currency-amount=100")
                    .accept(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().json("{\"baseCurrency\":\"EUR\",\"baseCurrencyAmount\":500,\"currency\":\"NOK\",\"exchangeRate\":9.8357,\"currencyAmount\":4917.8500}"));

    }

    @Test
    public void should_return_400_bad_request_for_unknown_base_currency() throws Exception{
        this.mockMvc.perform(MockMvcRequestBuilders.get("/v1/exchange-rates/"+UNKNOWN_CURRENCY_CODE).accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());
    }


}
