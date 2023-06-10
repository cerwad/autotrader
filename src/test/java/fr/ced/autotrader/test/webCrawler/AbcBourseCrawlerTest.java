package fr.ced.autotrader.test.webCrawler;

import fr.ced.autotrader.test.MarketDataCrawlerTestConfiguration;
import fr.ced.autotrader.webCrawler.AbcBourseCrawler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertNotNull;


/**
 * Created by cwaadd on 04/04/2018.
 */
@ActiveProfiles("test")
@SpringBootTest(classes = MarketDataCrawlerTestConfiguration.class)
public class AbcBourseCrawlerTest {

    @Autowired
    AbcBourseCrawler abcBourseCrawler;

    @Test
    public void testExtractOrangeMark(){
        Double mark = abcBourseCrawler.getShareMark("ORA");
        assertNotNull(mark);
    }
}

