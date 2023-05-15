package fr.ced.autotrader.data;

import java.math.BigDecimal;


/**
 * Created by cwaadd on 30/09/2019.
 */

public class Formatter {
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
