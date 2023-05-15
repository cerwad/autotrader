package fr.ced.autotrader.test.algo.baseLine;

import fr.ced.autotrader.algo.baseline.Line;
import fr.ced.autotrader.data.GraphPoint;
import org.apache.log4j.Logger;
import org.apache.xalan.xsltc.util.IntegerArray;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Created by cwaadd on 30/03/2018.
 */

public class LineTest {
    private static Logger logger = Logger.getLogger(LineTest.class);

    @Test
    public void testGetGraphPriceFlatLine(){
        LocalDate d1 = LocalDate.of(2018, 3, 1);
        LocalDate d2 = LocalDate.of(2018, 3, 12);
        GraphPoint p1 = new GraphPoint(20.0, d1);
        GraphPoint p2 = new GraphPoint(20.0, d2);
        List<GraphPoint> prices = new ArrayList<>();
        prices.add(new GraphPoint(32.0, d1.plusDays(1)));
        prices.add(new GraphPoint(31.2, d1.plusDays(2)));
        prices.add(new GraphPoint(28.3, d1.plusDays(3)));
        prices.add(new GraphPoint(25.6, d1.plusDays(4)));
        prices.add(new GraphPoint(20.4, d1.plusDays(5)));
        prices.add(new GraphPoint(21.3, d1.plusDays(8)));
        prices.add(new GraphPoint(23.4, d1.plusDays(9)));
        prices.add(new GraphPoint(26.7, d1.plusDays(10)));
        prices.add(new GraphPoint(28.3, d1.plusDays(11)));
        prices.add(new GraphPoint(26.2, d1.plusDays(12)));

        Line l = new Line(p1, p2, d1);
        double p = l.getGraphPrice(d1.plusDays(3));
        Assert.assertEquals(20, p, 0.05);
        p = l.getGraphPrice(d1.plusDays(7));
        Assert.assertEquals(20, p, 0.05);
        p = l.getGraphPrice(d1.plusDays(12));
        Assert.assertEquals(20, p, 0.05);
    }


    @Test
    public void testGetGraphPriceRisingLine(){
        LocalDate d1 = LocalDate.of(2018, 3, 1);
        LocalDate d2 = LocalDate.of(2018, 3, 16);
        GraphPoint p1 = new GraphPoint(20.0, d1);
        GraphPoint p2 = new GraphPoint(30.0, d2);
        List<GraphPoint> prices = new ArrayList<>();
        prices.add(new GraphPoint(30.8, d1));
        prices.add(new GraphPoint(32.0, d1.plusDays(1)));
        prices.add(new GraphPoint(31.2, d1.plusDays(2)));
        prices.add(new GraphPoint(28.3, d1.plusDays(3)));
        prices.add(new GraphPoint(25.6, d1.plusDays(4)));
        prices.add(new GraphPoint(20.4, d1.plusDays(5)));
        prices.add(new GraphPoint(21.3, d1.plusDays(8)));
        prices.add(new GraphPoint(23.4, d1.plusDays(9)));
        prices.add(new GraphPoint(26.7, d1.plusDays(10)));
        prices.add(new GraphPoint(28.3, d1.plusDays(11)));
        prices.add(new GraphPoint(27.5, d1.plusDays(12)));
        prices.add(new GraphPoint(25.4, d1.plusDays(13)));
        prices.add(new GraphPoint(25.8, d1.plusDays(14)));
        prices.add(new GraphPoint(23.2, d1.plusDays(15)));
        prices.add(new GraphPoint(24.9, d1.plusDays(16)));

        Line l = new Line(p1, p2, d1);
        double p = l.getGraphPrice(d1.plusDays(3));
        Assert.assertEquals(22, p, 0.01);
        p = l.getGraphPrice(d1.plusDays(7));
        Assert.assertEquals(24.67, p, 0.03);
        p = l.getGraphPrice(d1.plusDays(15));
        Assert.assertEquals(30, p, 0.01);
    }
}
