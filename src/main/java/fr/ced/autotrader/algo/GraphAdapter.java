package fr.ced.autotrader.algo;

import fr.ced.autotrader.data.GraphPoint;

import java.time.LocalDate;
import java.time.chrono.ChronoPeriod;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by cwaadd on 26/09/2019.
 */
public class GraphAdapter {
    private static GraphAdapter instance;

    private LocalDate startDate;
    private LocalDate endDate;

    private GraphAdapter() {

    }

    public static GraphAdapter of() {
        if (instance == null) {
            instance = new GraphAdapter();
        }
        return instance;
    }

    public GraphAdapter withStart(LocalDate startDate){
        this.startDate = startDate;
        return this;
    }
    public GraphAdapter withEnd(LocalDate endDate){
        this.endDate = endDate;
        return this;
    }

    public List<GraphPoint> adaptPrices(List<GraphPoint> allPrices, LocalDate startDate, LocalDate endDate) {
        return adapt(allPrices, startDate, endDate, 1);
    }

    public List<GraphPoint> adaptAverage(List<GraphPoint> allPrices, LocalDate startDate, LocalDate endDate) {
        return adapt(allPrices, startDate, endDate, 5);
    }

    public List<GraphPoint> adaptPrices(List<GraphPoint> allPrices){
        if(startDate == null){
            startDate = allPrices.get(0).getLocalDate();
        }
        if(endDate == null){
            endDate = LocalDate.now();
        }
        return adapt(allPrices, startDate, endDate, 1);
    }

    public List<GraphPoint> adaptAverage(List<GraphPoint> allPrices){
        if(startDate == null){
            startDate = allPrices.get(0).getLocalDate();
        }
        if(endDate == null){
            endDate = LocalDate.now();
        }
        return adapt(allPrices, startDate, endDate, 5);
    }

    private List<GraphPoint> adapt(List<GraphPoint> prices, LocalDate startDate, LocalDate endDate, long offset) {
        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("The endDate can't be before startDate");
        }
        if (prices == null || prices.isEmpty()) {
            throw new IllegalArgumentException("allPrices can't be empty");
        }
        List<GraphPoint> ret = new ArrayList<>();
        long pas = (1 + ChronoPeriod.between(startDate, endDate).get(ChronoUnit.YEARS)) * offset;
        long months = ChronoUnit.MONTHS.between(startDate, endDate);

        if(months < 12 && months > 0){
            pas = Math.max(1, pas / (12 / months));
        }
        for (int i = 0; i < prices.size(); i++) {
            if (i % pas == 0 || i == prices.size() - 1) {
                GraphPoint p = prices.get(i);
                if ((p.getLocalDate().equals(startDate) || p.getLocalDate().isAfter(startDate)) && p.getLocalDate().isBefore(endDate)) {
                    ret.add(p);
                }
            }
        }
        return ret;
    }
}