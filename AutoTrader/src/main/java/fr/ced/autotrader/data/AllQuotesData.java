package fr.ced.autotrader.data;

import java.util.List;
import java.util.Map;

/**
 * Created by cwaadd on 20/09/2017.
 */
public class AllQuotesData {
    private Map<String, Action> actionMap;

    private Map<String, Map<String, DayQuote>> quotes;

    private Map<String, List<DayQuote>> sortedQuotes;

    public Map<String, List<DayQuote>> getSortedQuotes() {
        return sortedQuotes;
    }

    public void setSortedQuotes(Map<String, List<DayQuote>> sortedQuotes) {
        this.sortedQuotes = sortedQuotes;
    }

    public Map<String, Action> getActionMap() {
        return actionMap;
    }

    public void setActionMap(Map<String, Action> actionMap) {
        this.actionMap = actionMap;
    }

    public Map<String, Map<String, DayQuote>> getQuotes() {
        return quotes;
    }

    public void setQuotes(Map<String, Map<String, DayQuote>> quotes) {
        this.quotes = quotes;
    }


}
