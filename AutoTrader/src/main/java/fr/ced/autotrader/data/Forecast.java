package fr.ced.autotrader.data;

import org.apache.log4j.Logger;


/**
 * Created by cwaadd on 15/10/2017.
 */

public class Forecast {
    private static Logger logger = Logger.getLogger(Forecast.class);


    private double oneMonthPrice;
    private double threeMonthsPrice;
    private double yearPrice;

    public double getOneMonthPrice() {
        return oneMonthPrice;
    }

    public void setOneMonthPrice(double oneMonthPrice) {
        this.oneMonthPrice = oneMonthPrice;
    }

    public double getThreeMonthsPrice() {
        return threeMonthsPrice;
    }

    public void setThreeMonthsPrice(double threeMonthsPrice) {
        this.threeMonthsPrice = threeMonthsPrice;
    }

    public double getYearPrice() {
        return yearPrice;
    }

    public void setYearPrice(double yearPrice) {
        this.yearPrice = yearPrice;
    }
}
