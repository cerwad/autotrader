package fr.ced.autotrader.algo.baseline;

import org.apache.log4j.Logger;

import java.util.List;


/**
 * Created by cwaadd on 25/09/2017.
 */

public class ResistanceAnalysis  extends BaseLineAnalysis<Resistance> {
    private static Logger logger = Logger.getLogger(ResistanceAnalysis.class);

    public ResistanceAnalysis(double lastPrice, List<Resistance> baseLines){
        super(lastPrice, baseLines);
    }

    private Resistance majorResistance;
    private Resistance currentResistance;

    public Resistance getMajorResistance() {
        return majorResistance;
    }

    public void setMajorResistance(Resistance majorResistance) {
        this.majorResistance = majorResistance;
    }

    public Resistance getCurrentResistance() {
        return currentResistance;
    }

    public void setCurrentResistance(Resistance currentResistance) {
        this.currentResistance = currentResistance;
    }

    public boolean hasOvercomeCurrentResistance() {

        return currentResistance.getValue() < lastPrice;
    }

    public boolean hasOvercomeAllResistance() {

        return majorResistance.getValue() < lastPrice;
    }


    public double getCurrentSpreadPercentage() {
        return spread(currentResistance.getValue(), lastPrice);
    }


    public double getMaxPotentialPercentage() {
        return spread(majorResistance.getValue(), lastPrice);
    }


}