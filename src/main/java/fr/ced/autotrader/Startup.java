package fr.ced.autotrader;

import fr.ced.autotrader.data.Action;
import fr.ced.autotrader.data.MarketDataReader;
import fr.ced.autotrader.data.MissingCotations;
import fr.ced.autotrader.data.time.MonthInterval;
import fr.ced.autotrader.webCrawler.MarketDataCrawler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
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
@Slf4j
@Component
public class Startup {

    @Autowired
    private MarketDataReader dataReader;

    @Autowired
    private MarketDataCrawler marketDataCrawler;

    @Autowired
    private AppProperties properties;

    @Autowired
    private MissingCotations missingCotations;

    @PostConstruct
    private void init() {
        // Shut the hell of htmlUnit
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
            log.error("No data Found");
            missingCotations.getAllHistory();
        }

        dataReader.loadData();
        LocalDate yesterday = LocalDate.now().minusDays(1);
        if(dataReader.getLastDateOfCotation().isBefore(yesterday)){
            try {
                missingCotations.downloadMissingCotations();
            } catch (Exception e){
                log.error("Error while downloading missing cotations", e);
            }
        }

        // HERE Check if there is missing data for a specific action
    }

}
