package fr.ced.autotrader.data;

import fr.ced.autotrader.webCrawler.AbcBourseCrawler;
import fr.ced.autotrader.webCrawler.BoursoCrawler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;


/**
 * Class calculating all the properties for one action
 * Crawling through the web to get marks from abcBourse, Boursorama
 * Calculating own mark from technical analysis
 * Created by cwaadd on 29/09/2017.
 */
@Component
@org.springframework.context.annotation.Scope("prototype")
public class ActionDataWebCrawler implements Runnable {
    @Autowired
    private AbcBourseCrawler abcBourseCrawler;

    @Autowired
    private BoursoCrawler boursoCrawler;

    private Action action = null;

    public void setAction(Action action){
        this.action = action;
    }

    @Override
    public void run() {

        int nbP = 3;
        Double abcMark = abcBourseCrawler.getShareMark(action.getTicker());
        Double boursoMark = boursoCrawler.getShareMark(action.getTicker());
        action.setAbcMark(abcMark);
        action.setBoursoMark(boursoMark);

        action.setInfoDate(LocalDate.now());
    }
}
