package fr.ced.autotrader.web.controllers;

import fr.ced.autotrader.data.Action;
import fr.ced.autotrader.data.ActionComparators;
import fr.ced.autotrader.data.MarketDataRepository;
import fr.ced.autotrader.web.dto.MarkDto;
import fr.ced.autotrader.web.dto.MarkDtoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * Created by cwaadd on 28/09/2017.
 */

@Controller
public class WatchSharesController {

    @Autowired
    private MarketDataRepository repo;

    @RequestMapping("/watchlist/{mark}")
    public String getWatchShares(Model model, @PathVariable @Nullable String mark) {
        Set<Action> actions = repo.getAllActions();
        if(mark == null){
            mark = "all";
        }

        Comparator<Action> comparator = ActionComparators.globalMarkComparator;
        Function<Action, MarkDto> mapper = MarkDtoMapper::mapGlobalMark;

        if ("tech".equals(mark)) {
            comparator = ActionComparators.techMarkComparator;
            mapper = MarkDtoMapper::mapTechnicalMark;
        } else if("bourso".equals(mark)){
            comparator = ActionComparators.boursoMarkComparator;
            mapper = MarkDtoMapper::mapBoursoMark;
        }

        List<MarkDto> results = actions.stream().sorted(comparator).limit(20).map(mapper)
                .filter(m -> m.getMark() > 0).collect(Collectors.toList());

        model.addAttribute("results", results);
        model.addAttribute("mark", mark);
        String view = "watchlist";
        model.addAttribute("view", view);
        return view;
    }

    @RequestMapping("/watchlist")
    public String getWatchShares(Model model) {
        return getWatchShares(model, null);

    }
}