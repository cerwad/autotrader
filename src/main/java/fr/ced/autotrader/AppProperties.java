package fr.ced.autotrader;

import lombok.Data;

import java.io.File;
import java.time.format.DateTimeFormatter;


/**
 * Created by cwaadd on 13/02/2018.
 */
@Data
public class AppProperties {

    private String basePath;
    private String dbFilepath;
    private File actionFile;
    private String csvDelimiter;
    private String cotationsPath;
    private String intradayPath;

    public static DateTimeFormatter frenchFormat = DateTimeFormatter.ofPattern("dd/MM/uuuu");

}
