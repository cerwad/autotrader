package fr.ced.autotrader.data;

import fr.ced.autotrader.algo.AnalyticsTools;
import fr.ced.autotrader.algo.ComputingException;
import fr.ced.autotrader.algo.baseline.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;


/**
 * Created by cwaadd on 15/10/2017.
 */
@Slf4j
@Component
public class MarketDataComputing {
    private boolean firstComputation = false;

    @Autowired
    private ActionDataCsvWriter actionDataCsvWriter;

    @Autowired
    private MarketDataRepository marketDataRepository;

    private AnalyticsTools analyticsTools = new AnalyticsTools();

    @Scheduled(fixedDelay = 600000, initialDelay = 12000)
    public void computeMarketDataTask(){
        // Recurrent actions
        log.debug("Write action data");
        Collection<Action> actions = marketDataRepository.getAllActions();
        if(!firstComputation) {
            log.debug("Compute market data");
            computeMarketData(actions);
            firstComputation = true;
        }
        // Write updated action data file while all calculation is finished
        actionDataCsvWriter.write(actions);
    }

    public void computeMarketData(Collection<Action> actions){
        // Do all the calculations you need
        for (Action action : actions) {
            List<GraphPoint> prices = marketDataRepository.getAllGraphData(action.getTicker());
            if(prices != null && !prices.isEmpty()) {
                computeMinMaxValue(action);
                computeResistances(action);
                computeSupports(action);
                setTrend(action, prices);
                computeTechnicalMark(action, prices);
                computeGlobalMark(action);
                Optional<DayQuote> quote = marketDataRepository.getLastQuoteFromAction(action.getTicker());
                quote.ifPresent(dayQuote -> action.setLastPrice(dayQuote.getClosePrice()));
            } else {
                log.error("No prices for action {}", action.getTicker());
            }
        }


    }

    public void computeGlobalMark(Action action) {
        int nbP = 3;
        Double abcMark = action.getAbcMark();
        if(abcMark == null){
            nbP--;
            abcMark = 0d;
        }
        Double boursoMark = action.getBoursoMark();
        if(boursoMark == null){
            nbP--;
            boursoMark = 0d;
        }
        Double techMark = action.getTechnicalMark();
        if(techMark == null){
            nbP--;
            techMark = 0d;
        }
        if(nbP > 0){
            Double globalMark = (abcMark + boursoMark + techMark)/nbP;
            action.setGlobalMark(Formatter.round2Digits(globalMark));
        }
    }

    public void computeMinMaxValue(Action action){
        Collection<Double> prices = marketDataRepository.getAllPrices(action.getTicker()).values();
        Optional<Double> max = prices.stream().max(Comparator.naturalOrder());
        if(max.isPresent()){
            action.setMax(max.get());
        }
        Optional<Double> min = prices.stream().min(Comparator.naturalOrder());
        if(min.isPresent()){
            action.setMin(min.get());
        }

    }

    public void computeResistances(Action action){
        try {
            List<GraphPoint> prices = marketDataRepository.getAllGraphData(action.getTicker());
            List<GraphPoint> res = analyticsTools.calculateResistances(prices);
            List<Resistance> baseLines = analyticsTools.computeResistanceLines(res);

            ResistanceAnalysis resistanceAnalysis = new ResistanceAnalysis(prices.get(prices.size() - 1).getPrice(), baseLines);
            //Major Resistance
            List<GraphPoint> majorResistances = analyticsTools.chooseResPoints(res, prices);
            Resistance majorResistance = new Resistance();
            majorResistance.setStrength(majorResistances.size());
            majorResistance.setLine(new Line(majorResistances.get(0), majorResistances.get(1), majorResistances.get(0).getLocalDate()));
            resistanceAnalysis.setMajorResistance(majorResistance);

            //Current resistance
            LocalDate from = LocalDate.now().minus(6, ChronoUnit.MONTHS);
            List<GraphPoint> currentPrices = marketDataRepository.getGraphDataFromDate(action.getTicker(), from);
            List<GraphPoint> currentRes = analyticsTools.calculateResistances(currentPrices);
            Resistance currentResistance = new Resistance();
            currentResistance.setStrength(currentRes.size());
            if (currentRes.isEmpty()) {
                double maxPrice = analyticsTools.findMax(currentPrices);
                currentResistance.setLine(new Line(new GraphPoint(maxPrice, from), new GraphPoint(maxPrice, LocalDate.now()), from));
            } else {
                currentRes = analyticsTools.chooseResPoints(currentRes, currentPrices);
                currentResistance.setLine(new Line(currentRes.get(0), currentRes.get(1), from));
            }
            resistanceAnalysis.setCurrentResistance(currentResistance);

            action.setResistanceAnalysis(resistanceAnalysis);
        } catch(Exception e){
            throw new ComputingException(action, e);
        }
    }


    public void computeSupports(Action action){
        try {
            List<GraphPoint> prices = marketDataRepository.getAllGraphData(action.getTicker());
            List<GraphPoint> sup = analyticsTools.calculateSupports(prices);
            List<Support> baseLines = analyticsTools.computeBaseLines(sup);

            SupportAnalysis supportAnalysis = new SupportAnalysis(prices.get(prices.size() - 1).getPrice(), baseLines);
            //Major Support
            List<GraphPoint> majorSupports = analyticsTools.chooseSupPoints(sup, prices);
            Support majorSupport = new Support();
            majorSupport.setStrength(majorSupports.size());
            majorSupport.setLine(new Line(majorSupports.get(0), majorSupports.get(1), majorSupports.get(0).getLocalDate()));
            supportAnalysis.setMajorSupport(majorSupport);

            //Current support
            LocalDate from = LocalDate.now().minusYears(1);
            Support currentSupport = getCurrentSupport(action, from);
            if (currentSupport == null) {
                currentSupport = getCurrentSupport(action, LocalDate.now().minus(6, ChronoUnit.MONTHS));
            }
            supportAnalysis.setCurrentSupport(currentSupport);

            action.setSupportAnalysis(supportAnalysis);
        } catch (Exception e){
            throw new ComputingException(action, e);
        }
    }

    private Support getCurrentSupport(Action action, LocalDate from){
        List<GraphPoint> currentPrices = marketDataRepository.getGraphDataFromDate(action.getTicker(), from);
        List<GraphPoint> currentSup = analyticsTools.calculateSupports(currentPrices);
        Support currentSupport = new Support();
        currentSupport.setStrength(currentSup.size());
        if(currentSup.isEmpty()) {
            double minPrice = analyticsTools.findMin(currentPrices);
            currentSupport.setLine(new Line(new GraphPoint(minPrice, from), new GraphPoint(minPrice, LocalDate.now()), from));
        } else {
            currentSup = analyticsTools.chooseSupPoints(currentSup, currentPrices);
            currentSupport.setLine(new Line(currentSup.get(0), currentSup.get(1), from));
        }
        return currentSupport;
    }

    public void setTrend(Action action, List<GraphPoint> points){
        Trend trend = new Trend(points);
        double coef = action.getSupportAnalysis().getCurrentSupport().getLine().getCoef();
        action.setCoef(Formatter.round4Digits(coef));
        double perc = coef / points.get(points.size() - 1).getPrice();
        if(perc < -0.001) {
            trend.setTendency(Tendency.LOW);
        } else if(perc <= 0.001){
            trend.setTendency(Tendency.SLACK);
        } else {
            trend.setTendency(Tendency.HIGH);
        }
        action.setTrend(trend);
    }

    public void setPivot(Action action){

    }

    /**
     * Compute my own technical mark based on Support analysis
     * @param action
     * @param prices
     */
    public void computeTechnicalMark(Action action, List<GraphPoint> prices){
        long mark = 0;
        double technicalMark = 5;

        if(action.getTrend().isRising() || action.getTrend().isStagnating()) {
            // Top Mark = 100 points then converted to /5
            // Support growth /10
            double coef = action.getSupportAnalysis().getCurrentSupport().getLine().getCoef();
            mark = coef < 0 ? 0 : Math.min(Math.round(coef * 10), 10);

            // MM20 growth /40
            List<GraphPoint> mm20 = analyticsTools.getMM20LineData(prices);
            BigDecimal mmCoef = analyticsTools.findLastCoef(mm20);
            long mmMark = mmCoef.doubleValue() < 0 ? 0 : Math.min(Math.round(mmCoef.doubleValue() * 10), 10) * 40;

            // Above MM20 /20
            long aboveMm = 0;
            if(prices.get(prices.size()-1).getPrice() > mm20.get(mm20.size()-1).getPrice()){
                aboveMm = 20;
            }

            // Near Support /10
            GraphPoint lastPrice = prices.get(prices.size() - 1);
            // Attention ! Problème ici le prix n'est pas bon
            double bottomPrice = action.getSupportAnalysis().getCurrentSupport().getLine().getGraphPrice(lastPrice.getLocalDate());
            double price = lastPrice.getPrice();
            double mark2 = (price - bottomPrice) / price * 100;
            mark2 = mark2 < 0 ? 0 : mark2;
            mark2 = mark2 > 10 ? 10 : mark2;

            // Strength of support / 10
            int mark3 = action.getSupportAnalysis().getCurrentSupport().getStrength();
            mark3 = Math.min(mark3, 5)*2;

            // Potential / 10
            // Should be calculated with the estimated reaping time
            double mark4 = 0;
            double potential = computePotential(action, prices);
            mark4 = Math.min(Math.round(potential * 10), 10);
            if(mark4 < 0){
                mark4 = 0;
            }

            technicalMark = (mark + mmMark + aboveMm + mark2 + mark3 + mark4) / 20;
            technicalMark = Math.min(6 - technicalMark, 5);
            if(technicalMark <= 0){
                technicalMark = 5;
            }
        }
        action.setTechnicalMark(technicalMark);

    }

    /**
     * Probablement revoir ça en ne prenant en compte que la résistance
     * @param action
     * @param prices
     * @return
     */
    public double computePotential(Action action, List<GraphPoint> prices){
        GraphPoint lastPrice = prices.get(prices.size() - 1);
        double potential = 0;
        if(action.getTrend().isRising()) {
            double mm20 = analyticsTools.getMM20LineData(prices).get(prices.size() - 1).getPrice();
            double percent = 0;
            if(mm20 > lastPrice.getPrice()){
                percent = Math.max( (mm20 - lastPrice.getPrice()) / mm20, percent);
            }
            double upperPrice = action.getSupportAnalysis().getCurrentSupport().getLine().getGraphPrice(lastPrice.getLocalDate().plus(1, ChronoUnit.MONTHS));
            upperPrice = upperPrice + upperPrice*percent;
            LocalDate from = LocalDate.now().minus(6, ChronoUnit.MONTHS);
            LocalDate to = LocalDate.now().minus(1, ChronoUnit.MONTHS);
            double max = analyticsTools.findMaxPrice(prices, from, to);
            upperPrice = Math.min(max, upperPrice);

            potential = (upperPrice - lastPrice.getPrice()) / lastPrice.getPrice();
            if (potential < 0) {
                potential = 0;
            } else {
                action.setPotential(Formatter.round2Digits(potential));
            }
        }
        return potential;
    }
}
