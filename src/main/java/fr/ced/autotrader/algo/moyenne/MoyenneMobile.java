package fr.ced.autotrader.algo.moyenne;

import fr.ced.autotrader.data.GraphPoint;

import java.util.List;


/**
 * Created by cwaadd on 25/09/2019.
 */

public class MoyenneMobile extends Moyenne {

    public MoyenneMobile(List<GraphPoint> allList) {
        super(allList);
    }


    @Override
    public Double getMoyenne(List<Double> prices, int index, int period) {
        Double ret = null;
        if(index - period >=0 ){
            double cumul = 0;
            for(int i = 0; i < period; i++){
                cumul += prices.get(index - i);
            }
            ret = cumul / period;
        }
        return ret;
    }

}
