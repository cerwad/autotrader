package fr.ced.autotrader.test;

import fr.ced.autotrader.AppProperties;
import fr.ced.autotrader.webCrawler.AbcBourseCrawler;
import fr.ced.autotrader.webCrawler.BoursoCrawler;
import fr.ced.autotrader.webCrawler.MarketDataCrawler;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import static org.mockito.Mockito.when;

@Profile("test")
@Configuration
public class MarketDataCrawlerTestConfiguration {
    @Bean
    @Primary
    public AppProperties appProperties() {
        AppProperties properties = Mockito.mock(AppProperties.class);
        String baseTestPath = System.getenv("DATADIR")+"/test";
        when(properties.getBasePath()).thenReturn(baseTestPath);
        when(properties.getCotationsPath()).thenReturn(baseTestPath);
        when(properties.getIntradayPath()).thenReturn(baseTestPath);
        return properties;
    }

    @Bean
    @Primary
    public MarketDataCrawler marketDataCrawler() {
        return new MarketDataCrawler(appProperties());
    }

    @Bean
    @Primary
    public BoursoCrawler getBoursoCrawler(){
        BoursoCrawler boursoCrawler = new BoursoCrawler();
        boursoCrawler.setUrl("https://www.boursorama.com/cours/consensus/1rP");
        return boursoCrawler;
    }

    @Bean
    @Primary
    public AbcBourseCrawler getAbcBourseCrawler(){
        AbcBourseCrawler abcBourseCrawler = new AbcBourseCrawler();
        abcBourseCrawler.setUrl("https://www.abcbourse.com/analyses/conseil.aspx?s=");
        return abcBourseCrawler;
    }
/*
    @Bean
    @Primary
    public Startup startup(){
        return Mockito.mock(Startup.class);
    }

    @Bean
    @Primary
    public MarketDataReader marketDataReader(){
        return Mockito.mock(MarketDataReader.class);
    }*/
}