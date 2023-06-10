package fr.ced.autotrader.data;

import java.time.LocalDate;
import java.util.*;

/**
 * Created by cwaadd on 20/09/2017.
 */
public interface MarketDataRepository {

    Set<Action> getAllActions();

    /**
     * Get action from ticker
     * @param id
     * @return action
     */
    Action getActionFromId(String id);

    /**
     * Get the actions containing the name in argument (ex: "or" will return actions with the name containing "or")
     * Return max 5 actions
     * @param name
     * @return
     */
    List<Action> getActionsFromName(String name);
    Double getPriceFromAction(String id, LocalDate day);
    Map<String, Double> getAllPrices(String id);
    List<Double> getAllPricesSorted(String id);
    List<GraphPoint> getAllGraphData(String id);
    List<GraphPoint> getGraphDataFromDate(String id, LocalDate from);
    Map<String, DayQuote> getAllQuotes(String id);
    DayQuote getQuoteByDate(String id, LocalDate date);
    List<DayQuote> getQuotesBetweenDates(String id, LocalDate from, LocalDate to);
    List<DayQuote> getLastWeekQuotes(String id);
    List<DayQuote> getLastMonthQuotes(String id);
    List<DayQuote> getLast3MonthsQuotes(String id);
    List<DayQuote> getLastXDaysQuotes(String id, int xDays);
    List<DayQuote> getLastYearQuotes(String id);
    List<DayQuote> getQuotesFromDate(String id, LocalDate from);
    Optional<DayQuote> getLastQuoteFromAction(String id);

    Comparator<DayQuote> dateComparator = new Comparator<DayQuote>() {
        @Override
        public int compare(DayQuote d1, DayQuote d2) {
            return d1.getDate().compareTo(d2.getDate());
        }
    };
}
