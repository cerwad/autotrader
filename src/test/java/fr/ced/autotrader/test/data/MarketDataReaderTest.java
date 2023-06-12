package fr.ced.autotrader.test.data;

import fr.ced.autotrader.data.AllQuotesData;
import fr.ced.autotrader.data.MarketDataRepositoryImpl;
import fr.ced.autotrader.data.QuotesCsvReader;
import fr.ced.autotrader.test.MarketDataCrawlerTestConfiguration;
import fr.ced.autotrader.webCrawler.MarketDataCrawler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.File;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;


@ActiveProfiles("test")
@SpringBootTest(classes = MarketDataCrawlerTestConfiguration.class)
public class MarketDataReaderTest {

    @Autowired
    private MarketDataCrawler marketDataCrawler;

    @Autowired
    private QuotesCsvReader quotesCsvReader;

    @Autowired
    private AllQuotesData allQuotesData;

    @Autowired
    private MarketDataRepositoryImpl marketDataRepository;
    @Test
    void testReadIntraDayCotations(){
        quotesCsvReader.readRefFile();
        File file = marketDataCrawler.downloadCurrentCotations();
        quotesCsvReader.readDataFile(file);
        assertThat(marketDataRepository.getQuoteByDate("GTT", LocalDate.now())).isNotNull().extracting("closePrice").isInstanceOf(Double.class);

        file.delete();
    }
}
