package fr.ced.autotrader;

import fr.ced.autotrader.data.*;
import fr.ced.autotrader.data.csv.columns.ActionCol;
import fr.ced.autotrader.data.csv.columns.QuotesCol;
import fr.ced.autotrader.data.csv.columns.RefCol;
import fr.ced.autotrader.webCrawler.AbcBourseCrawler;
import fr.ced.autotrader.webCrawler.BoursoCrawler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.io.File;

@SpringBootApplication
@EnableScheduling
public class App {

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    private String basePath = "/Users/cwaadd/Documents/Autotrader/";
    private String dbFilePath = basePath+"dbFiles";
    File actionFile = new File(dbFilePath+"/actionFile.csv");
    ActionCol[] cols = {ActionCol.ISIN, ActionCol.DATE, ActionCol.ABCMARK, ActionCol.BOURSOMARK, ActionCol.MORNINGSTARMARK, ActionCol.GLOBALMARK};
    String csvDelimiter = ";";
    String cotationsPath = basePath+"cotations";

    @Bean
    public AppProperties appProperties(){
        AppProperties appProperties = new AppProperties();
        appProperties.setCsvDelimiter(csvDelimiter);
        appProperties.setBasePath(basePath);
        appProperties.setDbFilepath(dbFilePath);
        appProperties.setActionFile(actionFile);
        appProperties.setCotationsPath(cotationsPath);
        return appProperties;
    }

    @Bean(initMethod = "init")
    public MarketDataReader dataReader() {
        QuotesCsvReader reader = new QuotesCsvReader();
        reader.setCsvFilePath(cotationsPath);
        File refFile = new File(dbFilePath+"/libelles.csv");
        reader.setReferenceFile(refFile);
        reader.setFileExtension("txt");
        reader.setCsvDelimiter(csvDelimiter);
        RefCol[] columns = {RefCol.ISIN, RefCol.NAME, RefCol.TICKER};
        reader.setRefFileColumns(columns);
        QuotesCol[] columns2 = {QuotesCol.ISIN, QuotesCol.DATE, QuotesCol.OPEN_QUOTE, QuotesCol.MIN_QUOTE, QuotesCol.MAX_QUOTE, QuotesCol.CLOSE_QUOTE, QuotesCol.VOL_QUOTE};
        reader.setDataFileColumns(columns2);

        reader.setActionCols(cols);
        reader.setDbFilePath(dbFilePath);

        reader.setActionDataFile(actionFile);
        return reader;
    }

    @Bean(initMethod = "init")
    public MarketDataRepository dataRepository(){
        return new MarketDataRepositoryImpl();
    }

    @Bean
    public ThreadPoolTaskExecutor getTaskExecutor(){
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(10);
        taskExecutor.setMaxPoolSize(15);
        taskExecutor.setQueueCapacity(150);
        return taskExecutor;
    }

    @Bean
    public ActionDataCsvWriter getActionDataCsvWriter(){
        ActionDataCsvWriter actionDataCsvWriter = new ActionDataCsvWriter();
        actionDataCsvWriter.setActionFile(actionFile);
        actionDataCsvWriter.setActionCols(cols);
        actionDataCsvWriter.setCsvDelimiter(csvDelimiter);
        return actionDataCsvWriter;
    }

    @Bean
    public AbcBourseCrawler getAbcBourseCrawler(){
        AbcBourseCrawler abcBourseCrawler = new AbcBourseCrawler();
        abcBourseCrawler.setUrl("https://www.abcbourse.com/analyses/conseil.aspx?s=");
        return abcBourseCrawler;
    }

    @Bean
    public BoursoCrawler getBoursoCrawler(){
        BoursoCrawler boursoCrawler = new BoursoCrawler();
        boursoCrawler.setUrl("https://www.boursorama.com/cours/consensus/1rP");
        return boursoCrawler;
    }

}