package fr.ced.autotrader.algo;

import fr.ced.autotrader.data.Action;


/**
 * Created by cwaadd on 25/09/2017.
 */

public class AnalysisResult {
    private Action action;
    private double potential;
    private double risk;
    private double mark;

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public double getPotential() {
        return potential;
    }

    public void setPotential(double potential) {
        this.potential = potential;
    }

    public double getRisk() {
        return risk;
    }

    public void setRisk(double risk) {
        this.risk = risk;
    }

    public double getMark() {
        return mark;
    }

    public void setMark(double mark) {
        this.mark = mark;
    }
}
