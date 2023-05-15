package fr.ced.autotrader.data;

import org.apache.log4j.Logger;

import java.util.Comparator;


/**
 * Created by cwaadd on 04/04/2018.
 */

public class ActionComparators {
    private static Logger logger = Logger.getLogger(ActionComparators.class);

    public static Comparator<Action> globalMarkComparator = (a1, a2) -> Double.compare(conv(a1.getGlobalMark()), conv(a2.getGlobalMark()));

    public static Comparator<Action> techMarkComparator = (a1, a2) -> Double.compare(conv(a1.getTechnicalMark()), conv(a2.getTechnicalMark()));

    public static Comparator<Action> boursoMarkComparator = (a1, a2) -> Double.compare(conv(a1.getBoursoMark()), conv(a2.getBoursoMark()));

    public static double conv(Double f){
        return f == null ? -1 : f;
    }
}
