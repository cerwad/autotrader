package fr.ced.autotrader;

import fr.ced.autotrader.data.Action;
import fr.ced.autotrader.data.MarketDataReader;
import fr.ced.autotrader.webCrawler.MarketDataCrawler;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;


/**
 * Created by cwaadd on 14/02/2018.
 */
@Component
public class Startup {
    private static Logger logger = Logger.getLogger(Startup.class);


    @Autowired
    private MarketDataReader dataReader;

    @Autowired
    private MarketDataCrawler marketDataCrawler;

    @Autowired
    private AppProperties properties;

    @PostConstruct
    private void init() {
        // Shut the hell of htmlUnit
        LogFactory.getFactory().setAttribute("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");
        java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);
        java.util.logging.Logger.getLogger("c.g").setLevel(Level.OFF);
        java.util.logging.Logger.getLogger("c.g.htmlunit").setLevel(Level.OFF);
        java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(Level.OFF);
        java.util.logging.Logger.getLogger("org.apache.commons.httpclient").setLevel(Level.OFF);

        List<Action> actions = dataReader.readRefFile();

        // List of files in working folder
        File directory = new File(properties.getCotationsPath());
        //get all the files from a directory
        File[] fList = directory.listFiles();
        if (fList == null || fList.length == 0){
            getAllHistory();
        }

        dataReader.loadData();
        LocalDate yesterday = LocalDate.now().minusDays(1);
        if(dataReader.getLastDateOfCotation().isBefore(yesterday) && yesterday.getDayOfWeek() != DayOfWeek.SATURDAY && yesterday.getDayOfWeek() != DayOfWeek.SUNDAY){
            // We catch the missing days of cotation
            LocalDate startDate = dataReader.getLastDateOfCotation().plusDays(1);
            LocalDate endDate = LocalDate.now();
            LocalDate end = endDate;
            while(startDate.isBefore(endDate)) {
                if(ChronoUnit.DAYS.between(startDate, endDate) > 31) {
                    end = startDate.with(TemporalAdjusters.lastDayOfMonth());
                }
                marketDataCrawler.downloadMonthlyBulkCotations(startDate, end);
                startDate = end.plusDays(1);
            }
            long today = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(1);

            File[] files = directory.listFiles(f -> f.lastModified() >= today);
            for (File file : files) {
                dataReader.readDataFile(file);
            }
        }

        // HERE Check if there is missing data for a specific action
    }

    private void getAllHistory() {
        // Get 5 years history for all actions CAC 40 and SBF 120
        LocalDate startDate = LocalDate.now().minus(5, ChronoUnit.YEARS);
        LocalDate startOfMonth = LocalDate.now().withDayOfMonth(1);
        startDate = startDate.withDayOfMonth(1);
        LocalDate iterDate = startDate;
        while (iterDate.isBefore(startOfMonth) || iterDate.isEqual(startOfMonth)){
            LocalDate endDate = iterDate.plus(1, ChronoUnit.MONTHS).minus(1, ChronoUnit.DAYS);
            if(endDate.isAfter(LocalDate.now())){
                endDate = LocalDate.now();
            }
            marketDataCrawler.downloadMonthlyBulkCotations(iterDate, endDate);
            iterDate = iterDate.plus(1, ChronoUnit.MONTHS);
        }
    }
}
