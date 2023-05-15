package fr.ced.autotrader.data;

import org.apache.log4j.Logger;

import java.math.BigDecimal;


/**
 * Created by cwaadd on 30/09/2019.
 */

public class Formatter {
    private static Logger logger = Logger.getLogger(Formatter.class);

    public static double round2Digits(double value){
        BigDecimal bigVal = new BigDecimal(value);
        bigVal = bigVal.setScale(2, BigDecimal.ROUND_HALF_UP);
        return bigVal.doubleValue();
    }

    public static double round4Digits(double value){
        BigDecimal bigVal = new BigDecimal(value);
        bigVal = bigVal.setScale(4, BigDecimal.ROUND_HALF_UP);
        return bigVal.doubleValue();
    }
}
