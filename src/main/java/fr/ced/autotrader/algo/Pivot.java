package fr.ced.autotrader.algo;

import org.apache.log4j.Logger;


/**
 * Created by cwaadd on 25/09/2017.
 */

public class Pivot {
    private static Logger logger = Logger.getLogger(Pivot.class);

    private double pivot;
    private double r1;
    private double r2;
    private double s1;
    private double s2;

    public double getPivot() {
        return pivot;
    }

    public void setPivot(double pivot) {
        this.pivot = pivot;
    }

    public double getR1() {
        return r1;
    }

    public void setR1(double r1) {
        this.r1 = r1;
    }

    public double getR2() {
        return r2;
    }

    public void setR2(double r2) {
        this.r2 = r2;
    }

    public double getS1() {
        return s1;
    }

    public void setS1(double s1) {
        this.s1 = s1;
    }

    public double getS2() {
        return s2;
    }

    public void setS2(double s2) {
        this.s2 = s2;
    }
}
