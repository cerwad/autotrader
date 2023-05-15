package fr.ced.autotrader.algo.moyenne;

import fr.ced.autotrader.data.GraphPoint;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


/**
 * Created by cwaadd on 25/09/2019.
 */

public abstract class Moyenne {
    private static Logger logger = Logger.getLogger(Moyenne.class);

    private List<GraphPoint> allList;

    public Moyenne(List<GraphPoint> allList){
        this.allList = allList;
    }

    public List<GraphPoint> getLineData(int period){
        List<GraphPoint> mmList = new ArrayList<>();
        List<Double> prices = allList.stream().map(GraphPoint::getPrice).collect(Collectors.toList());

        GraphPoint first = allList.get(0);
        for(int i = 0; i < prices.size(); i++){
            Double mm = getMoyenne(prices, i, period);
            if(mm != null) {
                GraphPoint p = new GraphPoint(mm, allList.get(i).getDate());
                mmList.add(p);
            } else {
                mmList.add(new GraphPoint(first.getPrice(), allList.get(i).getLocalDate()));
            }
        }
        return  mmList;
    }

    public abstract Double getMoyenne(List<Double> prices, int index, int period);
}
