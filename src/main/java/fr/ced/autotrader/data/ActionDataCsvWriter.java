package fr.ced.autotrader.data;

import com.google.common.io.CharSink;
import com.google.common.io.Files;
import fr.ced.autotrader.data.csv.columns.ActionCol;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;


/**
 * Created by cwaadd on 13/10/2017.
 */
public class ActionDataCsvWriter {
    private static Logger logger = Logger.getLogger(ActionDataCsvWriter.class);

    private File actionFile;
    private ActionCol[] actionCols;
    private String csvDelimiter;
    private DecimalFormat df = (DecimalFormat) NumberFormat.getNumberInstance(Locale.ENGLISH);//new DecimalFormat("###.##");

    public File getActionFile() {
        return actionFile;
    }

    public void setActionFile(File actionFile) {
        this.actionFile = actionFile;
    }

    public ActionCol[] getActionCols() {
        return actionCols;
    }

    public void setActionCols(ActionCol[] actionCols) {
        this.actionCols = actionCols;
    }

    public String getCsvDelimiter() {
        return csvDelimiter;
    }

    public void setCsvDelimiter(String csvDelimiter) {
        this.csvDelimiter = csvDelimiter;
    }

    public void write(Collection<Action> actions){
        logger.info("Saving action data");
        df.applyPattern("###.##");
        List<String> lines = new ArrayList<>();
        for (Action action : actions){
            String line = "";
            for (int i = 0; i < actionCols.length ; i++) {

                switch (actionCols[i]) {
                    case ISIN:
                        line += action.getIsin() + csvDelimiter;
                        break;
                    case DATE:
                        if(action.getInfoDate() != null) {
                            line += action.getInfoDate().format(DateTimeFormatter.BASIC_ISO_DATE) + csvDelimiter;
                        } else {
                            line += LocalDate.now().minus(5, ChronoUnit.DAYS);
                        }
                        break;
                    case ABCMARK:
                        line += String.format("%s %s", format(action.getAbcMark()), csvDelimiter);
                        break;
                    case BOURSOMARK:
                        line += String.format("%s %s", format(action.getBoursoMark()), csvDelimiter);
                        break;
                    case MORNINGSTARMARK:
                        line += String.format("%s %s", format(action.getMorningStarMark()), csvDelimiter);
                        break;
                    case GLOBALMARK:
                        line += String.format("%s", format(action.getGlobalMark()));
                        break;
                }
            }
            lines.add(line);
        }


        CharSink sink = Files.asCharSink(actionFile, Charset.defaultCharset());
        try {
            sink.writeLines(lines);
        } catch (IOException e) {
            logger.error("Error while writing action file", e);
        }
    }


    private String format(Double mark){
        String markS = "null";
        markS = mark != null ? df.format(mark):markS;
        return markS;
    }
}
