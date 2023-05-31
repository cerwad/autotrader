package fr.ced.autotrader.data;

import java.math.BigDecimal;
import java.math.RoundingMode;


/**
 * Created by cwaadd on 30/09/2019.
 */

public class Formatter {
    public static double round2Digits(double value){
        BigDecimal bigVal = new BigDecimal(value);
        bigVal = bigVal.setScale(2, RoundingMode.HALF_UP);
        return bigVal.doubleValue();
    }

    public static double round4Digits(double value){
        BigDecimal bigVal = new BigDecimal(value);
        bigVal = bigVal.setScale(4, RoundingMode.HALF_UP);
        return bigVal.doubleValue();
    }
}
