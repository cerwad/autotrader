package fr.ced.autotrader.web.dto;

import fr.ced.autotrader.data.Action;
import fr.ced.autotrader.data.ActionComparators;
import org.apache.log4j.Logger;


/**
 * Created by cwaadd on 04/04/2018.
 */

public class MarkDtoMapper {
    private static Logger logger = Logger.getLogger(MarkDtoMapper.class);

    public static MarkDto mapGlobalMark(Action a){
        MarkDto m = new MarkDto();
        m.setMark(ActionComparators.conv(a.getGlobalMark()));
        m.setName(a.getName());
        m.setTicker(a.getTicker());

        return  m;
    }

    public static MarkDto mapTechnicalMark(Action a){
        MarkDto m = new MarkDto();
        m.setMark(ActionComparators.conv(a.getTechnicalMark()));
        m.setName(a.getName());
        m.setTicker(a.getTicker());
        return  m;
    }

    public static MarkDto mapBoursoMark(Action a){
        MarkDto m = new MarkDto();
        m.setMark(ActionComparators.conv(a.getBoursoMark()));
        m.setName(a.getName());
        m.setTicker(a.getTicker());
        return  m;
    }
}
