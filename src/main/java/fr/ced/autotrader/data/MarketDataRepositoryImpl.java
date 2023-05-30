package fr.ced.autotrader.data;

import fr.ced.autotrader.Startup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by cwaadd on 20/09/2017.
 */
@Slf4j
@RequiredArgsConstructor
public class MarketDataRepositoryImpl implements MarketDataRepository, ApplicationContextAware {

    private AllQuotesData allQuotesData;

    private final MarketDataReader marketDataReader;

    private final ThreadPoolTaskExecutor taskExecutor;

    private ApplicationContext applicationContext;

    protected void init(){
        log.info("initializing DB");
        allQuotesData = marketDataReader.getData();
        log.info("There are "+getAllPrices("BNP").size()+" prices for bnp");
        Collection<Action> actions = getAllActions();
        log.info("There are "+actions.size()+" actions");
        // Search data on the web if needed (abcbourse mark and bourso mark for now)
        for(Action action : actions){

            if(action.getInfoDate() == null || action.getInfoDate().isBefore(LocalDate.now())) {
                ActionDataWebCrawler actionDataCalculator = (ActionDataWebCrawler) applicationContext.getBean("actionDataWebCrawler");
                actionDataCalculator.setAction(action);
                taskExecutor.execute(actionDataCalculator);
            }

        }


    }

    @Override
    public Set<Action> getAllActions() {
        Set<Action> set = new HashSet<>();
        set.addAll(allQuotesData.getActionMap().values());
        return  set;
    }

    @Override
    public Action getActionFromId(String id) {
        Action action = null;
        if(allQuotesData.getActionMap().containsKey(id)){
            action = allQuotesData.getActionMap().get(id);
        }
        return action;
    }

    @Override
    public List<Action> getActionsFromName(String name) {
        List<Action> actions = allQuotesData.getActionMap().values().stream().filter(a -> a.getName().toLowerCase().contains(name.toLowerCase())).distinct().limit(5).collect(Collectors.toList());
        return actions;
    }

    @Override
    public Double getPriceFromAction(String id, LocalDate day) {
        Double ret = null;
        DayQuote quote = getQuoteByDate(id, day);
        if(quote != null){
            ret = quote.getClosePrice();
        }
        return ret;
    }

    @Override
    public Map<String, Double> getAllPrices(String id) {
        Map<String, Double> map = null;
        if(allQuotesData.getQuotes().containsKey(id)){
            map = new HashMap<>();
            Map<String, DayQuote> quotes = allQuotesData.getQuotes().get(id);
            for (Map.Entry<String, DayQuote> entry : quotes.entrySet()){
                map.put(entry.getKey(), entry.getValue().getClosePrice());
            }
        }
        return map;
    }

    @Override
    public Map<String, DayQuote> getAllQuotes(String id) {
        Map<String, DayQuote> map = null;
        if(allQuotesData.getQuotes().containsKey(id)){
            map = allQuotesData.getQuotes().get(id);

        }
        return map;
    }

    @Override
    public List<DayQuote> getQuotesBetweenDates(String id, LocalDate from, LocalDate to) {
        List<DayQuote> ret = null;
        if(allQuotesData.getSortedQuotes().containsKey(id)){
            List<DayQuote> quotes = allQuotesData.getSortedQuotes().get(id);
            DayQuote d1 = new DayQuote();
            d1.setId(id);
            d1.setDate(from);
            int fromIndex = getIndexFromDate(quotes, d1);
            DayQuote d2 = new DayQuote();
            d2.setId(id);
            d2.setDate(to);
            int toIndex = getIndexFromDate(quotes, d2);
            if(fromIndex < toIndex && fromIndex >= 0) {
                ret = quotes.subList(fromIndex, toIndex + 1);
            }
        }
        return ret;
    }

    @Override
    public List<DayQuote> getLastXDaysQuotes(String id, int xDays) {
        List<DayQuote> ret = null;
        if(allQuotesData.getSortedQuotes().containsKey(id)) {
            LocalDate from = LocalDate.now().minusDays(xDays);
            ret = getQuotesFromDate(id, from);
        }
        return ret;
    }

    @Override
    public List<DayQuote> getQuotesFromDate(String id, LocalDate from) {
        List<DayQuote> ret = null;
        if(allQuotesData.getSortedQuotes().containsKey(id)) {

            List<DayQuote> quotes = allQuotesData.getSortedQuotes().get(id);
            log.debug("{}: nb quotes {}", id, quotes.size());
            DayQuote firstQuote = quotes.get(0);
            if(firstQuote.getDate().isAfter(from)){
                ret = quotes;
            } else {
                DayQuote d1 = new DayQuote();
                d1.setId(id);
                d1.setDate(from);
                int fromIndex = getIndexFromDate(quotes, d1);
                if (fromIndex >= 0) {
                    ret = quotes.subList(fromIndex, quotes.size());
                }
            }
        }
        return ret;
    }

    @Override
    public List<DayQuote> getLastWeekQuotes(String id) {
        LocalDate lastWeek = LocalDate.now().minusWeeks(1);
        return getQuotesFromDate(id, lastWeek);
    }

    @Override
    public List<DayQuote> getLastMonthQuotes(String id) {
        LocalDate lastMonth = LocalDate.now().minusMonths(1);
        return getQuotesFromDate(id, lastMonth);
    }

    @Override
    public List<DayQuote> getLast3MonthsQuotes(String id) {
        LocalDate last3Months = LocalDate.now().minusMonths(3);
        return getQuotesFromDate(id, last3Months);
    }

    @Override
    public List<DayQuote> getLastYearQuotes(String id) {
        LocalDate lastYear = LocalDate.now().minusYears(1);
        return getQuotesFromDate(id, lastYear);
    }


    @Override
    public DayQuote getQuoteByDate(String id, LocalDate date) {
        DayQuote ret = null;
        String dateStr = MarketDataReader.STANDARD_DATE_FORMAT.format(date);
        if(allQuotesData.getQuotes().containsKey(id) && allQuotesData.getQuotes().get(id).containsKey(dateStr)){
            ret = allQuotesData.getQuotes().get(id).get(dateStr);
        }
        return ret;
    }

    public void setAllQuotesData(AllQuotesData allQuotesData){
        this.allQuotesData = allQuotesData;
    }

    private int getIndexFromDate(List<DayQuote> quotes, DayQuote key){
        int index = -1;
        LocalDate date = key.getDate();
        if(quotes.get(0).getDate().isBefore(date)) {
            if(date.getDayOfWeek() == DayOfWeek.SATURDAY){
                date = date.plusDays(2);
            } else if(date.getDayOfWeek() == DayOfWeek.SUNDAY){
                date = date.plusDays(1);
            }
            key.setDate(date);
            index = Collections.binarySearch(quotes, key, MarketDataRepository.dateComparator);
            if(index < 0){
                date = date.plusDays(1);
                key.setDate(date);
                index = Collections.binarySearch(quotes, key, MarketDataRepository.dateComparator);
                if(index < 0){
                    date = date.plusDays(1);
                    key.setDate(date);
                    index = Collections.binarySearch(quotes, key, MarketDataRepository.dateComparator);
                }
            }
        }
        return index;
    }

    @Override
    public List<Double> getAllPricesSorted(String id) {
        final List<Double> prices = new ArrayList<>();
        if(allQuotesData.getSortedQuotes().containsKey(id)){
            allQuotesData.getSortedQuotes().get(id).forEach(quote -> prices.add(quote.getClosePrice()));
        }
        return prices;
    }

    @Override
    public List<GraphPoint> getAllGraphData(String id) {
        final List<GraphPoint> prices = new ArrayList<>();
        if(allQuotesData.getSortedQuotes().containsKey(id)){
            allQuotesData.getSortedQuotes().get(id).forEach(quote -> prices.add( new GraphPoint(quote.getClosePrice(), quote.getDate())));
        }
        return prices;
    }

    @Override
    public List<GraphPoint> getGraphDataFromDate(String id, LocalDate from) {
        log.info("Get prices for action {} and date {}", id, from);
        final List<GraphPoint> prices = new ArrayList<>();
        if(allQuotesData.getSortedQuotes().containsKey(id)){
            log.debug("getQuotesFromDate "+from);
            List<DayQuote> quotes =  getQuotesFromDate(id, from);
            if(quotes != null) {
                log.debug("Nb Quotes for action {}: {}", id, quotes.size());
                quotes.forEach(quote -> prices.add(new GraphPoint(quote.getClosePrice(), quote.getDate())));
            }
        }
        return prices;
    }

    @Override
    public Optional<DayQuote> getLastQuoteFromAction(String id) {
        List<DayQuote> quotes = getLastWeekQuotes(id);
        DayQuote lastQuote = null;
        if(quotes != null){
            lastQuote = quotes.stream().max(Comparator.comparing(DayQuote::getDate)).get();
        }
        return Optional.ofNullable(lastQuote);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
