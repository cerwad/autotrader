package fr.ced.autotrader.algo.moyenne;

import fr.ced.autotrader.data.GraphPoint;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by cwaadd on 25/09/2019.
 */

public class MoyenneExpoDouble {
    private static Logger logger = Logger.getLogger(MoyenneExpoDouble.class);
    private List<GraphPoint> allList;

    public MoyenneExpoDouble(List<GraphPoint> allList){
        this.allList = allList;
    }

    public List<GraphPoint> getLineData(int period){
        List<GraphPoint> mmList = new ArrayList<>();

        MoyenneExpo moyenneExpo = new MoyenneExpo(allList);
        List<GraphPoint> listMoyExpo = moyenneExpo.getLineData(period);
        MoyenneExpo moyenneExpoDouble = new MoyenneExpo(listMoyExpo);
        List<GraphPoint> listDEMA = moyenneExpoDouble.getLineData(period);

        for(int i = 0; i < allList.size(); i++){
            Double mm = getMoyenne(listMoyExpo, listDEMA, i);
            GraphPoint p = new GraphPoint(mm, allList.get(i).getDate());
            mmList.add(p);
        }
        return  mmList;
    }

    public Double getMoyenne(List<GraphPoint> listMoyExpo, List<GraphPoint> listDEMA, int index) {
        return 2*listMoyExpo.get(index).getPrice() - listDEMA.get(index).getPrice();
    }
}
