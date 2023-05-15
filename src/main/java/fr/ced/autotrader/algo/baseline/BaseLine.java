package fr.ced.autotrader.algo.baseline;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;


/**
 * Mother class of Resistance and support lines
 *
 * Created by cwaadd on 15/10/2017.
 */
 @Slf4j
public class BaseLine {

    protected Line line;
    protected double value;
    protected int strength;
    protected LocalDate lastDate;

    /**
     * Value when the line is straight and line = null
     * @return
     */
    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    /**
     * Get Number of times the action touches the line
     * The more the action is defeated by the line, the stronger it gets
     * @return strength
     */
    public int getStrength() {
        return strength;
    }

    /**
     * Set number of time the action is supported by the line
     * @param strength
     */
    public void setStrength(int strength) {
        this.strength = strength;
    }

    public LocalDate getLastDate() {
        return lastDate;
    }

    public void setLastDate(LocalDate lastDate) {
        this.lastDate = lastDate;
    }

    public boolean isAscending() {
        boolean ascending = false;
        if(line != null){
            ascending = line.getCoef() > 0;
        }
        return ascending;
    }

    public Line getLine() {
        return line;
    }

    public void setLine(Line line) {
        this.line = line;
    }
}
