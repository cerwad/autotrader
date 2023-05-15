package fr.ced.autotrader.algo;

import fr.ced.autotrader.data.Action;


/**
 * Created by cwaadd on 30/09/2019.
 */

public class ComputingException extends RuntimeException{

    public ComputingException(Action action, Throwable cause){
        super("Computing error for action : "+ action.toString(), cause);
    }
}
