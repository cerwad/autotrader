package fr.ced.autotrader.test.data;

import fr.ced.autotrader.data.QuotesCsvReader;

import java.io.File;

public class MarketDataComputingTest {

    public void testCalculatingMMCoefDir(){
        QuotesCsvReader reader = new QuotesCsvReader();
        reader.readDataFile(new File("Cotations20210601.txt"));
    }
}
