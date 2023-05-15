package fr.ced.autotrader.algo;

import fr.ced.autotrader.data.Action;
import org.apache.log4j.Logger;


/**
 * Created by cwaadd on 30/09/2019.
 */

public class ComputingException extends RuntimeException{
    private static Logger logger = Logger.getLogger(ComputingException.class);

    public ComputingException(Action action, Throwable cause){
        super("Computing error for action : "+ action.toString(), cause);
    }
}
