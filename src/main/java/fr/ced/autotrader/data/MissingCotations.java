package fr.ced.autotrader.data;

import fr.ced.autotrader.AppProperties;
import fr.ced.autotrader.data.time.MonthInterval;
import fr.ced.autotrader.webCrawler.MarketDataCrawler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.File;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class MissingCotations {

    private final MarketDataReader dataReader;

    private final MarketDataCrawler marketDataCrawler;

    private final AppProperties properties;

    @Async("singleThreadExecutor")
    public void downloadMissingCotations() throws InterruptedException {
        MonthInterval monthInterval = new MonthInterval();
        // We catch the missing days of cotation
        LocalDate startDate = dataReader.getLastDateOfCotation();
        LocalDate lastDayOfMonth = startDate.with(TemporalAdjusters.lastDayOfMonth());
        if(startDate.isEqual(lastDayOfMonth)){
            startDate = startDate.plusDays(1);
        } else {
            startDate = monthInterval.getStartDate(startDate);
            deleteFileIfExists(startDate);
        }
        LocalDate currentDate = LocalDate.now();
        while(startDate.isBefore(currentDate)) {
            log.info("Downloading data for: "+startDate);
            LocalDate end = monthInterval.getEndDate(startDate);
            marketDataCrawler.downloadMonthlyBulkCotations(startDate, end);
            startDate = monthInterval.getNextDate(startDate);
            Thread.sleep(1000);
        }
        long today = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(1);
        File directory = new File(properties.getCotationsPath());
        File[] files = directory.listFiles(f -> f.lastModified() >= today);
        for (File file : files) {
            dataReader.readDataFile(file);
        }
    }

    public void getAllHistory() {
        // Get 5 years history for all actions CAC 40 and SBF 120
        LocalDate startDate = LocalDate.now().minus(10, ChronoUnit.YEARS);
        LocalDate startOfMonth = LocalDate.now().withDayOfMonth(1);
        startDate = startDate.withDayOfMonth(1);
        LocalDate iterDate = startDate;
        while (iterDate.isBefore(startOfMonth) || iterDate.isEqual(startOfMonth)){
            try {
                LocalDate endDate = iterDate.plus(1, ChronoUnit.MONTHS).minus(1, ChronoUnit.DAYS);
                if (endDate.isAfter(LocalDate.now())) {
                    endDate = LocalDate.now();
                }
                marketDataCrawler.downloadMonthlyBulkCotations(iterDate, endDate);
                iterDate = iterDate.plus(1, ChronoUnit.MONTHS);
                Thread.sleep(1000);
            } catch (InterruptedException ie){
                log.error("Thread Interruption", ie);
            }
        }
    }
    public void deleteFileIfExists(LocalDate date){
        File lastCotations = new File(MarketDataCrawler.buildFileName(date));
        if(lastCotations.exists()){
            lastCotations.delete();
        }
    }
}
