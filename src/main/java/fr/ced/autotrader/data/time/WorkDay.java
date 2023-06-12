package fr.ced.autotrader.data.time;

import java.time.DayOfWeek;
import java.time.LocalDate;

public class WorkDay {

    public static LocalDate lastWorkDay(LocalDate localDate){
        LocalDate workDay = localDate.minusDays(1);
        while (workDay.getDayOfWeek() == DayOfWeek.SATURDAY || workDay.getDayOfWeek() == DayOfWeek.SUNDAY){
            workDay = workDay.minusDays(1);
        }
        return workDay;
    }
}
