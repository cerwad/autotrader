package fr.ced.autotrader.config;

import fr.ced.autotrader.AppProperties;
import fr.ced.autotrader.data.*;
import fr.ced.autotrader.data.csv.columns.ActionCol;
import fr.ced.autotrader.data.csv.columns.QuotesCol;
import fr.ced.autotrader.data.csv.columns.RefCol;
import fr.ced.autotrader.webCrawler.AbcBourseCrawler;
import fr.ced.autotrader.webCrawler.BoursoCrawler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;

@Configuration
@ComponentScan(basePackages = {"fr.ced.autotrader.data", "fr.ced.autotrader.webCrawler"})
public class DataConfig {

    private final ActionCol[] cols = {ActionCol.ISIN, ActionCol.DATE, ActionCol.ABCMARK, ActionCol.BOURSOMARK, ActionCol.MORNINGSTARMARK, ActionCol.GLOBALMARK};
    private final String csvDelimiter = ";";

    @Bean
    public AppProperties appProperties() throws FileNotFoundException {
        String dataPath = System.getenv("DATADIR");
        if(dataPath == null){
            throw new FileNotFoundException("Can't read data path");
        }
        Path path = Path.of(dataPath);
        if(!Files.exists(path)){
            throw new FileNotFoundException("Data folder does not exist or is unreachable: "+dataPath);
        }

        AppProperties appProperties = new AppProperties();
        appProperties.setCsvDelimiter(csvDelimiter);
        appProperties.setBasePath(dataPath);
        appProperties.setDbFilepath(dataPath+"/dbFiles/");
        appProperties.setActionFile(new File(appProperties.getDbFilepath()+"/actionFile.csv"));
        appProperties.setCotationsPath(dataPath+"/cotations");
        appProperties.setIntradayPath(dataPath+"/intraday");
        return appProperties;
    }

    @Bean
    public MarketDataReader dataReader(AppProperties appProperties, AllQuotesData allQuotesData) {
        QuotesCsvReader reader = new QuotesCsvReader(allQuotesData);
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

    @Bean
    public ActionDataCsvWriter getActionDataCsvWriter() throws FileNotFoundException {
        ActionDataCsvWriter actionDataCsvWriter = new ActionDataCsvWriter();
        actionDataCsvWriter.setActionFile(appProperties().getActionFile());
        actionDataCsvWriter.setActionCols(cols);
        actionDataCsvWriter.setCsvDelimiter(csvDelimiter);
        return actionDataCsvWriter;
    }

    @Bean
    public MarketDataRepository dataRepository(AllQuotesData allQuotesData){
        return new MarketDataRepositoryImpl(allQuotesData);
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
