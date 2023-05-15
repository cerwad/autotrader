package fr.ced.autotrader.web.controllers;

import fr.ced.autotrader.algo.AnalyticsTools;
import fr.ced.autotrader.algo.GraphAdapter;
import fr.ced.autotrader.data.Action;
import fr.ced.autotrader.data.GraphPoint;
import fr.ced.autotrader.data.MarketDataRepository;
import fr.ced.autotrader.data.Tendency;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.*;


/**
 * Created by cwaadd on 26/09/2017.
 */
@Controller
public class ShareGraphController {
    private static Logger logger = Logger.getLogger(ShareGraphController.class);

    private Set<String> lines = new HashSet<>();
    {
        lines.add("mm50");
        lines.add("mm20");
        lines.add("support");
        lines.add("resistance");
    }

    private int nbMonths = 12;

    private String actionTicker = "CAP";

    @Autowired
    MarketDataRepository repo;

    @RequestMapping("/graph")
    public String displayGraph(@RequestParam(value="period", required = false) String period, @RequestParam(value="actionId", required = false) String actionId, @RequestParam(value="lines", required = false) String[] linesArray, Model model) {

        String view = "actionNotFound";
        // Read request parameters
        if(actionId != null){
            actionTicker = actionId.toUpperCase();
        }
        if(period != null){
            nbMonths = Integer.parseInt(period);
        }
        if(linesArray != null){
            lines.clear();
            Collections.addAll(lines, linesArray);
        }


        Action action = null;
        List<Action> actions = null;
        if(actionTicker.length() >= 2 && actionTicker.length() <= 5) {
            action = repo.getActionFromId(actionTicker);
        }
        if(action == null){
            actions = repo.getActionsFromName(actionId);
            if(actions.size() == 1){
                action = actions.get(0);
                actionTicker = action.getTicker();
            }
        }
        if(action != null) {
            LocalDate from = LocalDate.now().minusMonths(nbMonths);

            GraphAdapter adapter = GraphAdapter.of().withStart(from).withEnd(LocalDate.now());
            List<GraphPoint> list = adapter.adaptPrices(repo.getGraphDataFromDate(actionTicker, from));
            model.addAttribute("graphData", list);
            if (list.size() > 0) {
                AnalyticsTools tools = new AnalyticsTools();
                List<GraphPoint> listAll = repo.getAllGraphData(actionTicker);
                if (lines.contains("mm50")) {
                    List<GraphPoint> mmList = adapter.adaptAverage(tools.getMM50LineData(listAll));
                    model.addAttribute("mm50Data", mmList);
                }
                if (lines.contains("mm20")) {
                    List<GraphPoint> mm20List = adapter.adaptAverage(tools.getMM20LineData(listAll));
                    model.addAttribute("mm20Data", mm20List);
                }
                if (lines.contains("ema20")) {
                    List<GraphPoint> ema20List = adapter.adaptAverage(tools.getEMA20LineData(listAll));
                    model.addAttribute("ema20Data", ema20List);
                }
                if (lines.contains("dema20")) {
                    List<GraphPoint> dema20List = adapter.adaptAverage(tools.getDEMA20LineData(listAll));
                    model.addAttribute("dema20Data", dema20List);
                }
                if (lines.contains("resistance")) {
                    List<GraphPoint> resistances = tools.calculateResistances(list);
                    if (resistances.size() > 1) {
                        resistances = tools.chooseResPoints(resistances, list);
                        resistances = tools.calculateLinePoints(resistances.get(0), resistances.get(1), list);
                    }
                    model.addAttribute("resistances", resistances);
                }
                if (lines.contains("support")) {
                    List<GraphPoint> supports = tools.calculateSupports(list);
                    if (supports.size() > 1) {
                        supports = tools.chooseSupPoints(supports, list);
                        supports = tools.calculateLinePoints(supports.get(0), supports.get(1), list);
                    }
                    model.addAttribute("supports", supports);
                }
                model.addAttribute("trend_img_url", "/img/"+getImage(action.getTrend().getTendency()));
            }
            model.addAttribute("action", action);
            view = "graph";
            model.addAttribute("view", view);
        } else if(actions != null && actions.size() > 1){
            model.addAttribute("actions", actions);
            view = "actionChoice";
        }
        return view;
    }

    private String getImage(Tendency tendency){
        String img;
        switch (tendency){
            case HIGH:
                img = "arrow_up";
                break;
            case LOW:
                img = "arrow_down";
                break;
            case SLACK:
                img = "stagnation";
                break;
            default:
                img = "error";
                break;
        }
        return img + ".png";
    }
}
