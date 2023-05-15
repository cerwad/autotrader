package fr.ced.autotrader.algo;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;


/**
 * Created by cwaadd on 25/09/2017.
 */
@Component
public class FindResistanceBreakers {
    private static Logger logger = Logger.getLogger(FindResistanceBreakers.class);

/*

    public ResistanceAnalysis getResistance(String actionId, List<GraphPoint> prices){
        AnalyticsTools tools = new AnalyticsTools();
        List<GraphPoint> resistances = tools.calculateResistances(prices);
        ResistanceAnalysis res = new ResistanceAnalysis();
        if(resistances.size() == 1 && prices.get(prices.size()-1) == resistances.get(0)){
            // No resistance, it keeps growing
            res = null;
        } else {
            // Still work to do !!!!
            double max = 0;
            for (GraphPoint p : resistances) {
                if(p.getPrice() > max){
                    max = p.getPrice();
                }
            }
            res.setMajorResistance(max);
        }
        return res;
    }
    */
}
