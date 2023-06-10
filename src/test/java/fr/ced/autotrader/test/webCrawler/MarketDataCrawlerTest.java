package fr.ced.autotrader.test.webCrawler;

import fr.ced.autotrader.AppProperties;
import fr.ced.autotrader.test.MarketDataCrawlerTestConfiguration;
import fr.ced.autotrader.webCrawler.MarketDataCrawler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.File;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by cwaadd on 13/02/2018.
 */
@ActiveProfiles("test")
@SpringBootTest(classes = MarketDataCrawlerTestConfiguration.class)
public class MarketDataCrawlerTest {

    @Autowired
    private AppProperties appProperties;

    @Autowired
    private MarketDataCrawler marketDataCrawler;

    @Test
    public void testMDataCrawlDwCurrentCotations(){

        File intraDayFile = marketDataCrawler.downloadCurrentCotations();
        if(marketDataCrawler.isWeekDay()) {
            assertNotNull(intraDayFile);
            assertTrue(intraDayFile.exists());
            if (intraDayFile.exists()) {
                intraDayFile.delete();
            }
        } else {
            assertNull(intraDayFile);
        }
    }

    @Test
    public void testMDataCrawlDwBulkCotations(){
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.withDayOfMonth(1);
        File downloadFile = marketDataCrawler.downloadMonthlyBulkCotations(startDate, endDate);
        assertTrue(downloadFile.exists());
        if(downloadFile.exists()){
            downloadFile.delete();
        }
    }
}
