package eu.nets.uni.apps.settlement.interview.controller;

import eu.nets.uni.apps.settlement.interview.enums.Currency;
import eu.nets.uni.apps.settlement.interview.model.ReportDto;
import eu.nets.uni.apps.settlement.interview.service.ReportService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

@RestController
public class ReportController {

    private ReportService reportService;

    //Cannot autowire XmlMapper bean directly as it will conflict with ObjectMapper
    // bean created by spring.
    private MappingJackson2XmlHttpMessageConverter xmlMapperProvider;

    public ReportController(ReportService reportService , MappingJackson2XmlHttpMessageConverter xmlMapperProvider){
        this.reportService = reportService;
        this.xmlMapperProvider = xmlMapperProvider;
    }

    @GetMapping(value = "/v1/exchange-rates/{baseCurrency}/report")
    public ResponseEntity<Resource> downloadAverageExchangeRateReport(@PathVariable Currency baseCurrency) throws Exception {

        ReportDto reportDto = reportService.getAverageExchangeRate(baseCurrency);

        String xml = xmlMapperProvider.getObjectMapper().writeValueAsString(reportDto);
        InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));

        return ResponseEntity.ok()
                .contentLength(xml.getBytes().length)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);

    }

}
