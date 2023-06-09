package fr.ced.autotrader.test.data;

import fr.ced.autotrader.algo.AnalyticsTools;
import fr.ced.autotrader.data.*;
import fr.ced.autotrader.data.csv.columns.QuotesCol;
import fr.ced.autotrader.data.csv.columns.RefCol;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@Slf4j
public class MarketDataComputingTest {

    @Test
    public void testCalculatingMM20CoefDir() throws URISyntaxException {
        AllQuotesData allData = new AllQuotesData();
        MarketDataReader reader = buildReader(allData);
        reader.getData().getActionMap().put("FR0000121014", new Action("FR0000121014", "LVMH", "MC"));
        URL resource = getClass().getClassLoader().getResource("Cotations20210601.txt");
        if (resource != null) {
            reader.readDataFile(new File(resource.toURI()));
            MarketDataRepository marketDataRepository = new MarketDataRepositoryImpl(allData);
            AnalyticsTools analyticsTools = new AnalyticsTools();

            List<GraphPoint> mm20 = analyticsTools.getMM20LineData(marketDataRepository.getAllGraphData("MC"));
            double mmCoef = analyticsTools.findLastCoef(mm20).doubleValue();
            double coefPerc = analyticsTools.findLastCoefPercent(mm20);

            log.info("mmCoef: {} {}%", mmCoef, coefPerc);
            assertEquals(-3.395, mmCoef, 0.01);
            assertEquals(-11.87, coefPerc, 0.01);
        }

    }

    @Test
    public void testZeroPotential()  throws URISyntaxException {
        AllQuotesData allData = new AllQuotesData();
        MarketDataReader reader = buildReader(allData);
        reader.getData().getActionMap().put("FR0000121014", new Action("FR0000121014", "LVMH", "MC"));
        URL resource = getClass().getClassLoader().getResource("Cotations20210601.txt");
        if (resource != null) {
            reader.readDataFile(new File(resource.toURI()));
            MarketDataRepository marketDataRepository = new MarketDataRepositoryImpl(allData);
            MarketDataComputing marketDataComputing = new MarketDataComputing(marketDataRepository);

            double potential = marketDataComputing.computeShortPotential(marketDataRepository.getAllGraphData("MC"));
            assertEquals(0, potential, 0.1);
        }
    }


    @Test
    public void testPotential()  throws URISyntaxException {
        AllQuotesData allData = new AllQuotesData();
        MarketDataReader reader = buildReader(allData);
        reader.getData().getActionMap().put("NL0000226223", new Action("NL0000226223", "STMICROELECTRONICS", "STMPA"));
        URL resource = getClass().getClassLoader().getResource("Cotations20210614.txt");
        if (resource != null) {
            reader.readDataFile(new File(resource.toURI()));
            MarketDataRepository marketDataRepository = new MarketDataRepositoryImpl(allData);
            MarketDataComputing marketDataComputing = new MarketDataComputing(marketDataRepository);

            double potential = marketDataComputing.computeShortPotential(marketDataRepository.getAllGraphData("STMPA"));
            assertEquals(7.9, potential, 0.1);
        }
    }

    public MarketDataReader buildReader(AllQuotesData allData){
        QuotesCsvReader reader = new QuotesCsvReader(allData);
        reader.setFileExtension("txt");
        reader.setCsvDelimiter(";");
        RefCol[] columns = {RefCol.ISIN, RefCol.NAME, RefCol.TICKER};
        reader.setRefFileColumns(columns);
        QuotesCol[] columns2 = {QuotesCol.ISIN, QuotesCol.DATE, QuotesCol.OPEN_QUOTE, QuotesCol.MIN_QUOTE, QuotesCol.MAX_QUOTE, QuotesCol.CLOSE_QUOTE, QuotesCol.VOL_QUOTE};
        reader.setDataFileColumns(columns2);

        return reader;
    }
}
