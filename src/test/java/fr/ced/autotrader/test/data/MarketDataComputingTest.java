package fr.ced.autotrader.test.data;

import fr.ced.autotrader.algo.AnalyticsTools;
import fr.ced.autotrader.data.*;
import fr.ced.autotrader.data.csv.columns.QuotesCol;
import fr.ced.autotrader.data.csv.columns.RefCol;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
public class MarketDataComputingTest {

    private static AllQuotesData allData = new AllQuotesData();
    private static MarketDataReader reader;
    private static MarketDataRepository marketDataRepository;

    private static MarketDataComputing marketDataComputing;

    private static AnalyticsTools analyticsTools = new AnalyticsTools();

    @BeforeAll
    public static void init() throws URISyntaxException {
        reader = buildReader(allData);
        allData.getActionMap().put("FR0000121014", new Action("FR0000121014", "LVMH", "MC"));
        URL resource = MarketDataComputingTest.class.getClassLoader().getResource("Cotations20210601.txt");
        if (resource != null) {
            reader.readDataFile(new File(resource.toURI()));
        }
        Action st = new Action("NL0000226223", "STMICROELECTRONICS", "STMPA");
        allData.getActionMap().put("NL0000226223", st);
        resource = MarketDataComputingTest.class.getClassLoader().getResource("Cotations20210614.txt");
        if (resource != null) {
            reader.readDataFile(new File(resource.toURI()));
        }
        marketDataRepository = new MarketDataRepositoryImpl(allData);
        marketDataComputing = new MarketDataComputing(marketDataRepository, new IntraDayCotations(reader, null));

        List<GraphPoint> prices = marketDataRepository.getAllGraphData(st.getTicker());
        if(prices != null && !prices.isEmpty()) {
            marketDataComputing.computeMinMaxValue(st);
            marketDataComputing.computeResistances(st);
            marketDataComputing.computeSupports(st);
            marketDataComputing.setTrend(st, prices);
        }
    }

    @Test
    public void testCalculatingMM20CoefDir() throws URISyntaxException {

        List<GraphPoint> mm20 = analyticsTools.getMM20LineData(marketDataRepository.getAllGraphData("MC"));
        double mmCoef = analyticsTools.findLastCoef(mm20).doubleValue();
        double coefPerc = analyticsTools.findLastCoefPercent(mm20);

        log.info("mmCoef: {} {}%", mmCoef, coefPerc);
        assertEquals(-3.395, mmCoef, 0.01);
        assertEquals(-11.87, coefPerc, 0.01);

    }

    @Test
    public void testZeroPotential() throws URISyntaxException {
        double potential = marketDataComputing.computeShortPotential(marketDataRepository.getAllGraphData("MC"));
        assertEquals(0, potential, 0.1);
    }


    @Test
    public void testPotential() throws URISyntaxException {
        double potential = marketDataComputing.computeShortPotential(marketDataRepository.getAllGraphData("STMPA"));
        assertEquals(7.9, potential, 0.1);
    }

    @Test
    public void testComputingTechMark() {
        long techMark = marketDataComputing.computeTechnicalMark(marketDataRepository.getActionFromId("NL0000226223"), marketDataRepository.getAllGraphData("STMPA"));
        assertTrue(techMark <= 100 && techMark >= 0);
        assertEquals(88, techMark);
    }

    public static MarketDataReader buildReader(AllQuotesData allData) {
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
