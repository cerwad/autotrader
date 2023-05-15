package fr.ced.autotrader.web.dto;

/**
 * Created by cwaadd on 04/04/2018.
 */

public class MarkDto {

    private String name;
    private String ticker;
    private double mark;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public double getMark() {
        return mark;
    }

    public void setMark(double mark) {
        this.mark = mark;
    }
}
