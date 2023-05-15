package fr.ced.autotrader.algo.baseline;

import org.apache.log4j.Logger;

import java.util.List;


/**
 * Created by cwaadd on 15/10/2017.
 */

public class SupportAnalysis extends BaseLineAnalysis<Support> {
    private static Logger logger = Logger.getLogger(SupportAnalysis.class);


    public SupportAnalysis(double lastPrice, List<Support> baseLines){
        super(lastPrice, baseLines);
    }

    private Support majorSupport;
    private Support currentSupport;

    public Support getMajorSupport() {
        return majorSupport;
    }

    public void setMajorSupport(Support majorSupport) {
        this.majorSupport = majorSupport;
    }

    public Support getCurrentSupport() {
        return currentSupport;
    }

    public void setCurrentSupport(Support currentSupport) {
        this.currentSupport = currentSupport;
    }

    public boolean hasOvercomeCurrentSupport() {

        return currentSupport.getValue() > lastPrice;
    }

    public boolean hasOvercomeAllSupport() {

        return majorSupport.getValue() > lastPrice;
    }


    public double getCurrentSpreadPercentage() {
        return spread(currentSupport.getValue(), lastPrice);
    }


    public double getMaxPotentialPercentage() {
        return spread(majorSupport.getValue(), lastPrice);
    }

}
