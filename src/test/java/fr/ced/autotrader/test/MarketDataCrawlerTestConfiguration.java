package fr.ced.autotrader.test;

import fr.ced.autotrader.AppProperties;
import fr.ced.autotrader.config.DataConfig;
import fr.ced.autotrader.data.*;
import fr.ced.autotrader.webCrawler.AbcBourseCrawler;
import fr.ced.autotrader.webCrawler.BoursoCrawler;
import fr.ced.autotrader.webCrawler.MarketDataCrawler;
import org.mockito.Mockito;
import org.springframework.context.annotation.*;

import static org.mockito.Mockito.when;

@Profile("test")
@Configuration
@Import(DataConfig.class)
public class MarketDataCrawlerTestConfiguration {
    @Bean
    @Primary
    public AppProperties testProperties() {
        AppProperties properties = Mockito.mock(AppProperties.class);
        String baseTestPath = System.getenv("DATADIR")+"/test";
        when(properties.getBasePath()).thenReturn(baseTestPath);
        when(properties.getCotationsPath()).thenReturn(baseTestPath);
        when(properties.getIntradayPath()).thenReturn(baseTestPath);
        when(properties.getDbFilepath()).thenReturn(baseTestPath);
        return properties;
    }


}