package fr.ced.autotrader.data;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Created by cwaadd on 20/09/2017.
 */
public interface MarketDataReader {

    void loadData();

    List<Action> readRefFile();

    void readDataFile(File file);

    DateTimeFormatter STANDARD_DATE_FORMAT = DateTimeFormatter.ofPattern("dd-MM-uuuu");

    AllQuotesData getData();

    LocalDate getLastDateOfCotation();
}
