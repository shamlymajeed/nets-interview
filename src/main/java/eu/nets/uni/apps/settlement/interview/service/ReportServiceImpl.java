package eu.nets.uni.apps.settlement.interview.service;

import eu.nets.uni.apps.settlement.interview.enums.Currency;
import eu.nets.uni.apps.settlement.interview.exception.ExchangeRateNotAvailableException;
import eu.nets.uni.apps.settlement.interview.model.ExchangeRateEntry;
import eu.nets.uni.apps.settlement.interview.model.ExchangeRates;
import eu.nets.uni.apps.settlement.interview.model.ReportDto;
import eu.nets.uni.apps.settlement.interview.repository.ExchangeRateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


@RequiredArgsConstructor
@Component
public class ReportServiceImpl implements  ReportService{

    private final ExchangeRateRepository exchangeRateRepository;

    private final ReportDto reportDto;

    @Value("${interview.fetch-report-of-last-minutes:10}")
    private Integer fetchReportOfLastMinutes;


    public ReportDto getAverageExchangeRate(Currency baseCurrency) throws ExchangeRateNotAvailableException{

        LocalDateTime dateTimeBefore = LocalDateTime.now(ZoneOffset.UTC).minus(fetchReportOfLastMinutes, ChronoUnit.MINUTES);
        List<ExchangeRates> exchangeRatesList = exchangeRateRepository.findByBaseCurrencyAndTimestampGreaterThanOrderByTimestampAsc(baseCurrency,dateTimeBefore.toInstant(ZoneOffset.UTC));

        if(exchangeRatesList.size() == 0)
            throw new ExchangeRateNotAvailableException();


        Iterator iterator = exchangeRatesList.iterator();
        Instant prevTimestamp = null;
        Map<Currency, BigDecimal> averageMap = new HashMap<>();
        Integer numberOfEntriesInAMinute = 0;
        reportDto.setBaseCurrency(baseCurrency);

        while(iterator.hasNext()) {

            ExchangeRates exchangeRates = (ExchangeRates) iterator.next();

            if(prevTimestamp==null || isOfSameMinute(prevTimestamp,exchangeRates.getTimestamp())) {
                numberOfEntriesInAMinute++;
                for(ExchangeRateEntry exchangeRateEntry : exchangeRates.getExchangeRateEntries()){
                    if(averageMap.containsKey(exchangeRateEntry.getCurrency())){
                        // Rate already exist, so sum up the value with the new rate comes in
                        BigDecimal existingRate = averageMap.get(exchangeRateEntry.getCurrency());
                        BigDecimal newrate = existingRate.add(exchangeRateEntry.getRate());
                        averageMap.put(exchangeRateEntry.getCurrency(),newrate);
                    }
                    else{
                        averageMap.put(exchangeRateEntry.getCurrency(),exchangeRateEntry.getRate());
                    }
                }
            } else{
                this.createDto(prevTimestamp , averageMap,numberOfEntriesInAMinute);
                numberOfEntriesInAMinute = 0;
                averageMap.clear();
            }

            prevTimestamp = exchangeRates.getTimestamp();
        }

        //To store last minute's entries in report dto.
        this.createDto(prevTimestamp , averageMap,numberOfEntriesInAMinute);

        return this.reportDto;
    }

    private void createDto(Instant time , Map<Currency, BigDecimal> averageMap, int numberOfEntriesInAMinute){

            List<ReportDto.TimeWiseReportDTO>  timeWiseReports = reportDto.getReports();
            List<ReportDto.CurrencyAverageRateDto> currencyAverageRateDtoList = new ArrayList<>();

            for (var entry : averageMap.entrySet()) {
                Currency currency = entry.getKey();
                BigDecimal rateSum = entry.getValue();
                BigDecimal averageRate = rateSum.divide(BigDecimal.valueOf(numberOfEntriesInAMinute),RoundingMode.HALF_UP);

                ReportDto.CurrencyAverageRateDto averageRateDto =  new ReportDto.CurrencyAverageRateDto();
                averageRateDto.setAverageRate(averageRate);
                averageRateDto.setCurrency(currency);

                currencyAverageRateDtoList.add(averageRateDto);
            }

            ReportDto.TimeWiseReportDTO timeWiseReportDTO = new ReportDto.TimeWiseReportDTO();
            timeWiseReportDTO.setCurrencyAverageRates(currencyAverageRateDtoList);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm").withZone(ZoneId.from(ZoneOffset.UTC));
            timeWiseReportDTO.setTime(formatter.format(time));

            timeWiseReports.add(timeWiseReportDTO);
            reportDto.setReports(timeWiseReports);

    }


    private boolean isOfSameMinute(Instant newTime, Instant oldTime){

        return newTime.truncatedTo(ChronoUnit.MINUTES).equals(oldTime.truncatedTo(ChronoUnit.MINUTES));
    }
}
