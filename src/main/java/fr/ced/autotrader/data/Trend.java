package fr.ced.autotrader.data;

import org.apache.log4j.Logger;

import java.time.LocalDate;
import java.util.List;


/**
 * Get the spread in percent between the price now and another date
 * Shows the trend of the action during this period
 * Created by cwaadd on 15/10/2017.
 */
public class Trend {
    private static Logger logger = Logger.getLogger(Trend.class);

    public Trend(List<GraphPoint> prices){
        this.prices = prices;
        currentPrice = prices.get(prices.size()-1).getPrice();
    }

    private List<GraphPoint> prices;
    private Double monthTrend = null;
    private Double threeMonthsTrend = null;
    private Double sixMonthsTrend = null;
    private Double yearTrend = null;
    private double currentPrice;
    private Tendency tendency;

    public double getMonthTrend() {
        if(monthTrend == null){
            LocalDate oneMonth = LocalDate.now().minusMonths(1);
            monthTrend = getPriceForDate(oneMonth);
        }
        return monthTrend.doubleValue();
    }

    public double getThreeMonthsTrend() {
        if(threeMonthsTrend == null){
            LocalDate oneMonth = LocalDate.now().minusMonths(3);
            threeMonthsTrend = getPriceForDate(oneMonth);
        }
        return threeMonthsTrend.doubleValue();
    }

    public double getSixMonthsTrend() {
        if(sixMonthsTrend == null){
            LocalDate oneMonth = LocalDate.now().minusMonths(6);
            sixMonthsTrend = getPriceForDate(oneMonth);
        }
        return sixMonthsTrend.doubleValue();
    }

    public double getYearTrend() {

        if(yearTrend == null){
            LocalDate oneMonth = LocalDate.now().minusMonths(6);
            yearTrend = getPriceForDate(oneMonth);
        }
        return yearTrend.doubleValue();
    }

    public double getTrendForDate(LocalDate date){
        double price = getPriceForDate(date);
        return (currentPrice - price)/currentPrice;
    }

    private double getPriceForDate(LocalDate date){
        double price = 0;
        for(GraphPoint p : prices){
            if(p.getLocalDate().equals(date) || p.getLocalDate().isAfter(date)){
                price = p.getPrice();
                break;
            }
        }
        return price;
    }

    public Tendency getTendency() {
        return tendency;
    }

    public void setTendency(Tendency tendency) {
        this.tendency = tendency;
    }


    public boolean isRising(){
        return tendency == Tendency.HIGH;
    }

    public boolean isDropping(){
        return tendency == Tendency.LOW;
    }

    public boolean isStagnating(){
        return tendency == Tendency.SLACK;
    }
}
