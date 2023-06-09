package fr.ced.autotrader.data;

import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by cwaadd on 20/09/2017.
 */
@Repository
public class AllQuotesData {
    private final Map<String, Action> actionMap;

    private final Map<String, Map<String, DayQuote>> quotes;

    private final Map<String, List<DayQuote>> sortedQuotes;

    public AllQuotesData(){
        actionMap = new HashMap<>();
        quotes = new HashMap<>();
        sortedQuotes = new HashMap<>();
    }

    public Map<String, List<DayQuote>> getSortedQuotes() {
        return sortedQuotes;
    }

    public Map<String, Action> getActionMap() {
        return actionMap;
    }

    public Map<String, Map<String, DayQuote>> getQuotes() {
        return quotes;
    }

}
