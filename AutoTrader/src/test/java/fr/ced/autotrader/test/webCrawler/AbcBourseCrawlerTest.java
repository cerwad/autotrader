package fr.ced.autotrader.test.webCrawler;

import fr.ced.autotrader.test.MarketDataCrawlerTestConfiguration;
import fr.ced.autotrader.webCrawler.AbcBourseCrawler;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


/**
 * Created by cwaadd on 04/04/2018.
 */
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = MarketDataCrawlerTestConfiguration.class)
public class AbcBourseCrawlerTest {
    private static Logger logger = Logger.getLogger(AbcBourseCrawlerTest.class);

    @Autowired
    AbcBourseCrawler abcBourseCrawler;

    @Test
    public void testExtractOrangeMark(){
        Double mark = abcBourseCrawler.getShareMark("ORA");
        Assert.assertNotNull(mark);
    }
}

