package fr.ced.autotrader;

import fr.ced.autotrader.data.*;
import fr.ced.autotrader.data.time.MonthInterval;
import fr.ced.autotrader.webCrawler.MarketDataCrawler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;


/**
 * Created by cwaadd on 14/02/2018.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class Startup implements ApplicationContextAware {

    private final MarketDataReader dataReader;

    private final MarketDataCrawler marketDataCrawler;

    private final AppProperties properties;

    private final MissingCotations missingCotations;

    private final MarketDataRepository marketDataRepository;
    private final ThreadPoolTaskExecutor taskExecutor;

    private final IntraDayCotations intraDayCotations;
    private ApplicationContext applicationContext;


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

        log.info("initializing DB");
        log.info("There are "+marketDataRepository.getAllPrices("BNP").size()+" prices for bnp");
        log.info("There are "+actions.size()+" actions");
        // Search data on the web if needed (abcbourse mark and bourso mark for now)
        for(Action action : actions){

            if(action.getInfoDate() == null || action.getInfoDate().isBefore(LocalDate.now())) {
                ActionDataWebCrawler actionDataCalculator = (ActionDataWebCrawler) applicationContext.getBean("actionDataWebCrawler");
                actionDataCalculator.setAction(action);
                taskExecutor.execute(actionDataCalculator);
            }

        }

        // Download quotes of the current day
        intraDayCotations.saveCurrentQuotes();

        // HERE Check if there is missing data for a specific action
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
