package fr.ced.autotrader.test.webCrawler;

import fr.ced.autotrader.AppProperties;
import fr.ced.autotrader.test.MarketDataCrawlerTestConfiguration;
import fr.ced.autotrader.webCrawler.MarketDataCrawler;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.time.LocalDate;

/**
 * Created by cwaadd on 13/02/2018.
 */
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = MarketDataCrawlerTestConfiguration.class)
public class MarketDataCrawlerTest {

    @Value("${app.data-path}")
    private String cotationsPath;

    @Autowired
    private AppProperties appProperties;

    @Autowired
    private MarketDataCrawler marketDataCrawler;

    @Test
    public void testMDataCrawlDwCurrentCotations(){

        Mockito.when(appProperties.getCotationsPath()).thenReturn(cotationsPath);
        marketDataCrawler.downloadCurrentCotations();
        File downloadFile = new File(appProperties.getCotationsPath() + "/"+MarketDataCrawler.buildFileName(LocalDate.now()));
        Assert.assertTrue(downloadFile.exists());
        if(downloadFile.exists()){
            downloadFile.delete();
        }
    }

    @Test
    public void testMDataCrawlDwBulkCotations(){
        Mockito.when(appProperties.getCotationsPath()).thenReturn(cotationsPath);
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.withDayOfMonth(1);
        marketDataCrawler.downloadMonthlyBulkCotations(startDate, endDate);
        File downloadFile = new File(appProperties.getCotationsPath() + "/"+MarketDataCrawler.buildFileName("SBF", startDate));
        Assert.assertTrue(downloadFile.exists());
        if(downloadFile.exists()){
            downloadFile.delete();
        }
    }
}
