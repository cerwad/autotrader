package fr.ced.autotrader.algo.moyenne;

import fr.ced.autotrader.data.GraphPoint;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


/**
 * Created by cwaadd on 25/09/2019.
 */

public class MoyenneExpo {
    private List<GraphPoint> allList;

    public MoyenneExpo(List<GraphPoint> allList){
        this.allList = allList;
    }

    public List<GraphPoint> getLineData(int period) {
        List<GraphPoint> mmList = new ArrayList<>();
        List<Double> prices = allList.stream().map(GraphPoint::getPrice).collect(Collectors.toList());

        GraphPoint first = allList.get(0);

        MoyenneMobile moyenne = new MoyenneMobile(allList);
        double yesterdayEMA = moyenne.getMoyenne(prices, period, period);
        for (int i = 0; i < prices.size(); i++) {
            Double mm = null;
            if(i > period){
                mm = getMoyenne(prices.get(i), period, yesterdayEMA);
                if (mm != null) {
                    GraphPoint p = new GraphPoint(mm, allList.get(i).getDate());
                    mmList.add(p);
                    yesterdayEMA = mm;
                }

            }
            if (mm == null) {
                mmList.add(first);
            }

        }
        return mmList;
    }

    public Double getMoyenne(double price, int period, Double emaYesterday) {
        Double ret = null;
        if(emaYesterday != null) {
            double alpha = 2D / (period + 1);
            ret = price * alpha + emaYesterday * (1 - alpha);
        }
        return ret;
    }

}
