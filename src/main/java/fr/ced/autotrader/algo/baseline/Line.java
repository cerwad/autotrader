package fr.ced.autotrader.algo.baseline;

import fr.ced.autotrader.data.GraphPoint;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

import static java.time.temporal.ChronoUnit.DAYS;


/**
 * Created by cwaadd on 27/09/2017.
 */
@Slf4j
public class Line {
    private GraphPoint p1;
    private GraphPoint p2;
    private BigDecimal coef;
    private BigDecimal offset;
    private LocalDate firstDate;

    public Line(GraphPoint p1, GraphPoint p2, LocalDate firstDate){
        if(p1.getLocalDate().isBefore(p2.getLocalDate())){
            this.p1 = p1;
            this.p2 = p2;
        } else {
            this.p1 = p2;
            this.p2 = p1;
        }

        this.firstDate = firstDate;

        coef = p1.calculateCoef(p2);
        long nbDays = DAYS.between(firstDate, this.p1.getLocalDate());
        offset = BigDecimal.valueOf(this.p1.getPrice()).subtract(coef.multiply(new BigDecimal(nbDays)));
    }

    public double getCoef() {
        return coef.doubleValue();
    }

    public double getOffset() {
        return offset.doubleValue();
    }

    public boolean isCompletelyAbove(List<GraphPoint> list){
        boolean above = true;
        int i = 0;
        for (GraphPoint p : list){
            double y = getGraphPrice(p.getLocalDate());
            above = p.getPrice() <= y + y*0.01;
            if(!above && i > list.size() / 10){
                break;
            }
            i++;
        }
        return above;
    }

    public boolean isCompletelyBelow(List<GraphPoint> list){
        boolean below = true;
        int i = 0;
        for (GraphPoint p : list){
            double y = getGraphPrice(p.getLocalDate());
            below = p.getPrice() >= y - y*0.001;
            if(!below && i > list.size() / 10){
                break;
            }
            i++;
        }
        return below;
    }

    public double getGraphPrice(LocalDate date){
        long dayNb = DAYS.between(firstDate, date);
        BigDecimal priceForDate = coef.multiply(new BigDecimal(dayNb)).add(offset);
        return priceForDate.doubleValue();
    }

}
