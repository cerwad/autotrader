package fr.ced.autotrader.algo;

import fr.ced.autotrader.data.Action;
import fr.ced.autotrader.data.MarketDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * Created by cwaadd on 25/09/2017.
 */
@Component
public class SharePickerImpl implements SharePicker {

    @Autowired
    private MarketDataRepository repository;

    @Autowired
    private FindHighestShares findHighestShares;

    @Override
    public List<AnalysisResult> getTopFive() {
        List<AnalysisResult> results = getBestShares();
        List<AnalysisResult> topFive = results;
        if(results.size() > 5) {
            topFive = results.subList(0, 5);
        }
        return topFive;
    }

    @Override
    public List<AnalysisResult> getTopTen() {
        List<AnalysisResult> results = getBestShares();
        List<AnalysisResult> topTen = results;
        if(results.size() > 10){
            topTen = results.subList(0, 10);
        }

        return topTen;
    }

    private List<AnalysisResult> getBestShares(){
        List<AnalysisResult> results = new ArrayList<>();
        Collection<Action> actions = repository.getAllActions();
        for(Action a : actions) {
            boolean isHighest  = findHighestShares.isHistoricallyHigh(repository.getAllPricesSorted(a.getTicker()));
            if(isHighest){
                AnalysisResult res = new AnalysisResult();
                res.setAction(a);
                res.setMark(5);

                results.add(res);
            }
        }
        return results;
    }
}
