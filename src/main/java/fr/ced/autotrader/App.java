package fr.ced.autotrader;

import fr.ced.autotrader.data.*;
import fr.ced.autotrader.data.csv.columns.ActionCol;
import fr.ced.autotrader.data.csv.columns.QuotesCol;
import fr.ced.autotrader.data.csv.columns.RefCol;
import fr.ced.autotrader.webCrawler.AbcBourseCrawler;
import fr.ced.autotrader.webCrawler.BoursoCrawler;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;

@SpringBootApplication
@EnableScheduling
public class App {

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @Autowired
    @Value("${app.data-path}")
    private String dataPath;
    private final ActionCol[] cols = {ActionCol.ISIN, ActionCol.DATE, ActionCol.ABCMARK, ActionCol.BOURSOMARK, ActionCol.MORNINGSTARMARK, ActionCol.GLOBALMARK};
    private final String csvDelimiter = ";";

    @Bean("singleThreadExecutor")
    TaskExecutor singleThreadExecutor(){
        SimpleAsyncTaskExecutor executor = new SimpleAsyncTaskExecutor();
        executor.setConcurrencyLimit(1);
        return executor;
    }
    @Bean
    public AppProperties appProperties(){
        AppProperties appProperties = new AppProperties();
        appProperties.setCsvDelimiter(csvDelimiter);
        appProperties.setBasePath(dataPath);
        appProperties.setDbFilepath(dataPath+"/dbFiles/");
        appProperties.setActionFile(new File(appProperties.getDbFilepath()+"/actionFile.csv"));
        appProperties.setCotationsPath(dataPath+"/cotations");
        return appProperties;
    }

    @Bean
    public MarketDataReader dataReader(AppProperties appProperties) throws FileNotFoundException {
        if(dataPath == null){
            throw new FileNotFoundException("Can't read data path");
        }
        Path path = Path.of(dataPath);
        if(!Files.exists(path)){
            throw new FileNotFoundException("Data folder does not exist or is unreachable: "+dataPath);
        }
        QuotesCsvReader reader = new QuotesCsvReader();
        reader.setCsvFilePath(appProperties.getCotationsPath());
        File refFile = new File(appProperties.getDbFilepath()+"/libelles.csv");
        reader.setReferenceFile(refFile);
        reader.setFileExtension("txt");
        reader.setCsvDelimiter(csvDelimiter);
        RefCol[] columns = {RefCol.ISIN, RefCol.NAME, RefCol.TICKER};
        reader.setRefFileColumns(columns);
        QuotesCol[] columns2 = {QuotesCol.ISIN, QuotesCol.DATE, QuotesCol.OPEN_QUOTE, QuotesCol.MIN_QUOTE, QuotesCol.MAX_QUOTE, QuotesCol.CLOSE_QUOTE, QuotesCol.VOL_QUOTE};
        reader.setDataFileColumns(columns2);

        reader.setActionCols(cols);
        reader.setDbFilePath(appProperties.getDbFilepath());

        reader.setActionDataFile(appProperties.getActionFile());
        return reader;
    }

    @Bean(initMethod = "init")
    public MarketDataRepository dataRepository(MarketDataReader dataReader, ThreadPoolTaskExecutor taskExecutor){
        return new MarketDataRepositoryImpl(dataReader, taskExecutor);
    }

    @Bean
    public ThreadPoolTaskExecutor taskExecutor(){
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(10);
        taskExecutor.setMaxPoolSize(15);
        taskExecutor.setQueueCapacity(150);
        return taskExecutor;
    }

    @Bean
    public ActionDataCsvWriter getActionDataCsvWriter(){
        ActionDataCsvWriter actionDataCsvWriter = new ActionDataCsvWriter();
        actionDataCsvWriter.setActionFile(appProperties().getActionFile());
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