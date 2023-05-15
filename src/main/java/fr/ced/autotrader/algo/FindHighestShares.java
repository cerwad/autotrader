package fr.ced.autotrader.algo;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.List;


/**
 * Created by cwaadd on 25/09/2017.
 */
@Component
public class FindHighestShares {
    private static Logger logger = Logger.getLogger(FindHighestShares.class);

    public boolean isHistoricallyHigh(List<Double> prices){
        boolean ret = false;
        if(prices.size() - 1 > 0) {
            ret = true;
            double lastPrice = prices.get(prices.size() - 1);
            for (double p : prices) {
                if (p > lastPrice) {
                    ret = false;
                    break;
                }
            }
        }
        return ret;
    }


}
