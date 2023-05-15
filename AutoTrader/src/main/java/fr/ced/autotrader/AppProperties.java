package fr.ced.autotrader;

import org.apache.log4j.Logger;

import java.io.File;
import java.time.format.DateTimeFormatter;


/**
 * Created by cwaadd on 13/02/2018.
 */

public class AppProperties {
    private static Logger logger = Logger.getLogger(AppProperties.class);

    private String basePath;
    private String dbFilepath;
    private File actionFile;
    private String csvDelimiter;
    private String cotationsPath;

    public static DateTimeFormatter frenchFormat = DateTimeFormatter.ofPattern("dd/MM/uuuu");

    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public String getDbFilepath() {
        return dbFilepath;
    }

    public void setDbFilepath(String dbFilepath) {
        this.dbFilepath = dbFilepath;
    }

    public File getActionFile() {
        return actionFile;
    }

    public void setActionFile(File actionFile) {
        this.actionFile = actionFile;
    }

    public String getCsvDelimiter() {
        return csvDelimiter;
    }

    public void setCsvDelimiter(String csvDelimiter) {
        this.csvDelimiter = csvDelimiter;
    }

    public String getCotationsPath() {
        return cotationsPath;
    }

    public void setCotationsPath(String cotationsPath) {
        this.cotationsPath = cotationsPath;
    }
}
