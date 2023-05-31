package fr.ced.autotrader.data.time;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

public class MonthInterval {

    public LocalDate getStartDate(LocalDate date){
        return LocalDate.of(date.getYear(), date.getMonth(), 1);
    }

    public LocalDate getEndDate(LocalDate date){
        return date.with(TemporalAdjusters.lastDayOfMonth());
    }

    public LocalDate getNextDate(LocalDate date){
        LocalDate currentDate = getStartDate(date);
        return currentDate.plusMonths(1);
    }
}
