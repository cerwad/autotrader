package fr.ced.autotrader.test.data;

import fr.ced.autotrader.data.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.*;


/**
 * Created by cwaadd on 24/09/2017.
 */

public class MarketDataRepositoryImplTest {

    private MarketDataRepositoryImpl dataRepository = new MarketDataRepositoryImpl();

    @Before
    public void init(){
        AllQuotesData allQuotesData = new AllQuotesData();
        allQuotesData.setActionMap(new HashMap<>());
        allQuotesData.setQuotes(new HashMap<>());
        allQuotesData.setSortedQuotes(new HashMap<>());
        
        String wendelKey = "MF";
        String havasKey = "HAV";

        allQuotesData.getActionMap().put(wendelKey, new Action("FR0000121204", "Wendel", wendelKey));
        allQuotesData.getActionMap().put(havasKey, new Action("FR0000121881", "Havas", havasKey));

        // Wendel cotations
        DayQuote d = new DayQuote();
        d.setId(wendelKey);
        d.setDate(LocalDate.of(2017, 9, 15));
        d.setOpenPrice(128.23);
        d.setMinPrice(128.18);
        d.setMaxPrice(129.97);
        d.setClosePrice(129.23);
        d.setVolume(117389);

        DayQuote d1 = new DayQuote();
        d1.setId(wendelKey);
        d1.setDate(LocalDate.of(2017, 9, 18));
        d1.setOpenPrice(129.23);
        d1.setMinPrice(129.18);
        d1.setMaxPrice(130.97);
        d1.setClosePrice(130.67);
        d1.setVolume(117389);

        DayQuote d2 = new DayQuote();
        d2.setId(wendelKey);
        d2.setDate(LocalDate.of(2017, 9, 19));
        d2.setOpenPrice(130.67);
        d2.setMinPrice(130.67);
        d2.setMaxPrice(132.43);
        d2.setClosePrice(130.85);
        d2.setVolume(107230);

        DayQuote d3 = new DayQuote();
        d3.setId(wendelKey);
        d3.setDate(LocalDate.of(2017, 9, 20));
        d3.setOpenPrice(130.85);
        d3.setMinPrice(125.27);
        d3.setMaxPrice(130.90);
        d3.setClosePrice(125.83);
        d3.setVolume(217389);

        DayQuote d4 = new DayQuote();
        d4.setId(wendelKey);
        d4.setDate(LocalDate.of(2017, 9, 21));
        d4.setOpenPrice(125.83);
        d4.setMinPrice(125.03);
        d4.setMaxPrice(127.3);
        d4.setClosePrice(126.2);
        d4.setVolume(137389);

        DayQuote d5 = new DayQuote();
        d5.setId(wendelKey);
        d5.setDate(LocalDate.of(2017, 9, 22));
        d5.setOpenPrice(126.2);
        d5.setMinPrice(126.17);
        d5.setMaxPrice(126.35);
        d5.setClosePrice(126.25);
        d5.setVolume(53200);

        Map<String, DayQuote> mapQuotes = new HashMap<>();
        mapQuotes.put(MarketDataReader.STANDARD_DATE_FORMAT.format(d.getDate()), d);
        mapQuotes.put(MarketDataReader.STANDARD_DATE_FORMAT.format(d1.getDate()), d1);
        mapQuotes.put(MarketDataReader.STANDARD_DATE_FORMAT.format(d2.getDate()), d2);
        mapQuotes.put(MarketDataReader.STANDARD_DATE_FORMAT.format(d3.getDate()), d3);
        mapQuotes.put(MarketDataReader.STANDARD_DATE_FORMAT.format(d4.getDate()), d4);
        mapQuotes.put(MarketDataReader.STANDARD_DATE_FORMAT.format(d5.getDate()), d5);
        allQuotesData.getQuotes().put(wendelKey, mapQuotes);

        List<DayQuote> sortedQuotes = new ArrayList<>();
        sortedQuotes.add(d);
        sortedQuotes.add(d1);
        sortedQuotes.add(d2);
        sortedQuotes.add(d3);
        sortedQuotes.add(d4);
        sortedQuotes.add(d5);
        allQuotesData.getSortedQuotes().put(wendelKey, sortedQuotes);

        // Havas cotations
        d1 = new DayQuote();
        d1.setId(havasKey);
        d1.setDate(LocalDate.of(2017, 9, 18));
        d1.setOpenPrice(8.1);
        d1.setMinPrice(8);
        d1.setMaxPrice(8.6);
        d1.setClosePrice(8.5);
        d1.setVolume(11389);

        d2 = new DayQuote();
        d2.setId(havasKey);
        d2.setDate(LocalDate.of(2017, 9, 19));
        d2.setOpenPrice(8.5);
        d2.setMinPrice(8.5);
        d2.setMaxPrice(9.2);
        d2.setClosePrice(9.17);
        d2.setVolume(90230);

        mapQuotes = new HashMap<>();
        mapQuotes.put(MarketDataReader.STANDARD_DATE_FORMAT.format(d1.getDate()), d1);
        mapQuotes.put(MarketDataReader.STANDARD_DATE_FORMAT.format(d2.getDate()), d2);
        allQuotesData.getQuotes().put(havasKey, mapQuotes);

        sortedQuotes = new ArrayList<>();
        sortedQuotes.add(d1);
        sortedQuotes.add(d2);
        allQuotesData.getSortedQuotes().put(havasKey, sortedQuotes);

        dataRepository.setAllQuotesData(allQuotesData);
    }

    @Test
    public void testNbActions(){
        Collection<Action> actions = dataRepository.getAllActions();
        Assert.assertEquals(2, actions.size());

    }

    @Test
    public void testGetAction(){
        Action action = dataRepository.getActionFromId("MF");
        Assert.assertNotNull(action);
        Assert.assertEquals("MF", action.getTicker());
        Assert.assertEquals("FR0000121204", action.getIsin());
        Assert.assertEquals("Wendel", action.getName());
    }

    @Test
    public void testGetQuoteByDate(){
        DayQuote quote = dataRepository.getQuoteByDate("MF", LocalDate.of(2017, 9, 20));
        Assert.assertNotNull(quote);
        Assert.assertEquals(LocalDate.of(2017, 9, 20), quote.getDate());

        quote = dataRepository.getQuoteByDate("MF", LocalDate.of(2017, 9, 17));
        Assert.assertNull(quote);
    }

    @Test
    public void testGetPriceFromAction(){
        Double price = dataRepository.getPriceFromAction("MF", LocalDate.of(2017, 9, 20));
        Assert.assertNotNull(price);
        Assert.assertEquals(Double.valueOf( 125.83),  price);

        price = dataRepository.getPriceFromAction("HAV", LocalDate.of(2017, 9, 18));
        Assert.assertNotNull(price);
        Assert.assertEquals(Double.valueOf( 8.5),  price);
    }

    @Test
    public void testGetQuotesInterval(){
        List<DayQuote> dayQuotes = dataRepository.getQuotesBetweenDates("MF", LocalDate.of(2017, 9, 18), LocalDate.of(2017, 9, 22));
        Assert.assertNotNull(dayQuotes);
        Assert.assertEquals(5, dayQuotes.size());
    }

    @Test
    public void testGetQuotesFromDate(){
        List<DayQuote> dayQuotes = dataRepository.getQuotesFromDate("MF", LocalDate.of(2017, 9, 20));
        Assert.assertNotNull(dayQuotes);
        Assert.assertEquals(LocalDate.of(2017, 9, 20), dayQuotes.get(0).getDate());
        Assert.assertEquals(LocalDate.of(2017, 9, 22), dayQuotes.get(dayQuotes.size()-1).getDate());
        Assert.assertEquals(3, dayQuotes.size());

        dayQuotes = dataRepository.getQuotesFromDate("MF", LocalDate.of(2017, 9, 17)); // Un dimanche
        Assert.assertNotNull(dayQuotes);
        Assert.assertEquals(5, dayQuotes.size());
    }

}
