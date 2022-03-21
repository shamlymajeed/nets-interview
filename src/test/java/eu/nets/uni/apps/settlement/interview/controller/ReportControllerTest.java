package eu.nets.uni.apps.settlement.interview.controller;

import eu.nets.uni.apps.settlement.interview.enums.Currency;
import eu.nets.uni.apps.settlement.interview.model.ReportDto;
import eu.nets.uni.apps.settlement.interview.service.ReportService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.MediaType.APPLICATION_OCTET_STREAM;
import static org.springframework.http.MediaType.APPLICATION_OCTET_STREAM_VALUE;

@WebMvcTest(controllers = ReportController.class)
public class ReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReportService reportService;

    @Test
    public void test_download_report_file() throws Exception {

        ReportDto reportDto = generateSampleReportDto();

        when(reportService.getAverageExchangeRate(any(Currency.class))).thenReturn(reportDto);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/v1/exchange-rates/EUR/report")
                            .contentType(APPLICATION_OCTET_STREAM)).andExpect(MockMvcResultMatchers.status().is(200)).andReturn();
        assertEquals(200, result.getResponse().getStatus());
        assertEquals(APPLICATION_OCTET_STREAM_VALUE, result.getResponse().getContentType());
    }

    private ReportDto generateSampleReportDto(){
        ReportDto reportDto = new ReportDto();
        reportDto.setBaseCurrency(Currency.EUR);
        ReportDto.TimeWiseReportDTO timeWiseReportDTO = new ReportDto.TimeWiseReportDTO();
        ReportDto.CurrencyAverageRateDto currencyAverageRateDto = new ReportDto.CurrencyAverageRateDto();
        currencyAverageRateDto.setCurrency(Currency.NOK);
        currencyAverageRateDto.setAverageRate(BigDecimal.valueOf(10));
        List<ReportDto.CurrencyAverageRateDto> currencyAverageRateDtos = Arrays.asList(currencyAverageRateDto);
        timeWiseReportDTO.setCurrencyAverageRates(currencyAverageRateDtos);
        List<ReportDto.TimeWiseReportDTO> timeWiseReportDTOS = Arrays.asList(timeWiseReportDTO);
        reportDto.setReports(timeWiseReportDTOS);

        return reportDto;
    }
}
