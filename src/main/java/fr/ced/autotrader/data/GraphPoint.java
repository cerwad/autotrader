package fr.ced.autotrader.data;

import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static java.time.temporal.ChronoUnit.DAYS;


/**
 * Created by cwaadd on 26/09/2017.
 */
@Slf4j
public class GraphPoint {

    private Double price;
    private String date;


    public GraphPoint(Double price, LocalDate date) {
        this.price = price;
        this.date = DateTimeFormatter.ISO_DATE.format(date);
    }

    public GraphPoint(Double price, String date) {
        this.price = price;
        this.date = date;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getDate() {
        return date;
    }

    public LocalDate getLocalDate() {
        return LocalDate.parse(date);
    }

    public void setDate(LocalDate date) {
        this.date = DateTimeFormatter.ISO_DATE.format(date);
    }

    public void setDate(String date) {
        this.date = date;
    }

    public BigDecimal calculateCoef(GraphPoint p2){
        long nbDays = DAYS.between(this.getLocalDate(), p2.getLocalDate());
        if(nbDays == 0){
            log.error("Same data points for date "+getDate());
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(p2.getPrice() - getPrice()).divide(new BigDecimal(nbDays), 10, RoundingMode.HALF_UP);

    }
    @Override
    public String toString() {
        return "GraphPoint{" +date+":"+Formatter.round2Digits(price)+'}';
    }
}
