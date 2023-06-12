package fr.ced.autotrader.data;

import fr.ced.autotrader.AppProperties;
import fr.ced.autotrader.webCrawler.MarketDataCrawler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
@RequiredArgsConstructor
@Slf4j
public class IntraDayCotations {
    private final MarketDataReader dataReader;

    private final MarketDataCrawler marketDataCrawler;

    public void saveCurrentQuotes(){
        if(marketDataCrawler.isWeekDay()) {
            log.info("Downloading intraday Quotes");
            File file = marketDataCrawler.downloadCurrentCotations();
            dataReader.readDataFile(file);
            file.delete();
        }
    }
}
