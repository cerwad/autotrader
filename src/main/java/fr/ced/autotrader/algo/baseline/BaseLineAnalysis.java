package fr.ced.autotrader.algo.baseline;

import java.util.List;


/**
 * Created by cwaadd on 15/10/2017.
 */
public class BaseLineAnalysis<T extends BaseLine> {

    protected double lastPrice;
    protected List<T> baseLines;

    public BaseLineAnalysis(double lastPrice, List<T> baseLines){
        this.lastPrice = lastPrice;
        this.baseLines = baseLines;
    }


    public double spread(double v1, double v2){
        return Math.abs((v1 - v2)/v2 * 100);
    }
}
