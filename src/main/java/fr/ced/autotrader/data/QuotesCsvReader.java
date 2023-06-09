package fr.ced.autotrader.data;

import com.google.common.base.Splitter;
import com.google.common.io.CharSource;
import com.google.common.io.Files;
import fr.ced.autotrader.data.csv.columns.ActionCol;
import fr.ced.autotrader.data.csv.columns.QuotesCol;
import fr.ced.autotrader.data.csv.columns.RefCol;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Created by cwaadd on 20/09/2017.
 */
@Slf4j
@RequiredArgsConstructor
public class QuotesCsvReader implements MarketDataReader {

    private String csvFilePath;
    private File referenceFile;
    private String fileExtension;
    private String csvDelimiter;
    private final AllQuotesData allQuotesData;
    private RefCol[] refFileColumns;
    private QuotesCol[] dataFileColumns;
    private final DateTimeFormatter dTF = DateTimeFormatter.ofPattern("dd/MM/uu");
    private File actionDataFile;
    private String dbFilePath;
    private ActionCol[] actionCols;
    private LocalDate lastDateOfCotation;

    protected int l = 0;


    public void setRefFileColumns(RefCol[] refFileColumns) {
        this.refFileColumns = refFileColumns;
    }

    public void setDataFileColumns(QuotesCol[] dataFileColumns) {
        this.dataFileColumns = dataFileColumns;
    }

    public String getCsvDelimiter() {
        return csvDelimiter;
    }

    public void setCsvDelimiter(String csvDelimiter) {
        this.csvDelimiter = csvDelimiter;
    }

    public String getCsvFilePath() {
        return csvFilePath;
    }

    public void setCsvFilePath(String csvFilePath) {
        this.csvFilePath = csvFilePath;
    }

    public File getReferenceFile() {
        return referenceFile;
    }

    public void setReferenceFile(File referenceFile) {
        this.referenceFile = referenceFile;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    public AllQuotesData getData() {
        return allQuotesData;
    }

    public File getActionDataFile() {
        return actionDataFile;
    }

    public void setActionDataFile(File actionDataFile) {
        this.actionDataFile = actionDataFile;
    }

    public String getDbFilePath() {
        return dbFilePath;
    }

    public void setDbFilePath(String dbFilePath) {
        this.dbFilePath = dbFilePath;
    }

    public ActionCol[] getActionCols() {
        return actionCols;
    }

    public void setActionCols(ActionCol[] actionCols) {
        this.actionCols = actionCols;
    }


    @Override
    public LocalDate getLastDateOfCotation() {
        return lastDateOfCotation;
    }


    @Override
    public void loadData() {


        // List of files in working folder
        File directory = new File(getCsvFilePath());
        //get all the files from a directory
        File[] fList = directory.listFiles();

        if (fList != null) {
            List<File> fileList = Arrays.stream(fList).sorted(Comparator.comparing(File::getName)).toList();
            for (File file : fileList) {
                // Ignore csv file because ref File
                if (Files.getFileExtension(file.getName()).equals(fileExtension)) {
                    System.out.println(file.getName());
                    readDataFile(file);
                }

            }
        }
        // Read saved data on actions
        readActionFile();

    }

    private void readActionFile() {
        l = 0;
        if (actionDataFile.exists()) {
            CharSource charSource = Files.asCharSource(actionDataFile, Charset.defaultCharset());
            try {
                charSource.forEachLine(this::readActionFileLine);
            } catch (IOException ioe) {
                log.error("Impossible to read Reference file", ioe);
            }
        }
    }


    protected void readActionFileLine(String line) {
        Splitter splitterOnString = Splitter.on(csvDelimiter);
        Iterable<String> split = splitterOnString.split(line);
        Iterator<String> iter = split.iterator();
        Action actionTemp = new Action();
        for (ActionCol actionCol : actionCols) {
            String value = iter.next().trim();
            try {
                switch (actionCol) {
                    case ISIN:
                        actionTemp.setIsin(value);
                        break;
                    case DATE:
                        actionTemp.setInfoDate(LocalDate.parse(value, DateTimeFormatter.BASIC_ISO_DATE));
                        break;
                    case ABCMARK:
                        Double abcMark = null;
                        if (!"null".equals(value)) {
                            abcMark = Double.parseDouble(value);
                            if (abcMark == 0) {
                                abcMark = null;
                            }
                        }
                        actionTemp.setAbcMark(abcMark);
                        break;
                    case BOURSOMARK:
                        Double boursoMark = null;
                        if (!"null".equals(value)) {
                            boursoMark = Double.parseDouble(value);
                            if (boursoMark == 0) {
                                boursoMark = null;
                            }
                        }
                        actionTemp.setBoursoMark(boursoMark);
                        break;
                    case MORNINGSTARMARK:
                        Double morningStarMark = null;
                        if (!"null".equals(value)) {
                            morningStarMark = Double.parseDouble(value);
                            if (morningStarMark == 0) {
                                morningStarMark = null;
                            }
                        }
                        actionTemp.setMorningStarMark(morningStarMark);
                        break;
                    case GLOBALMARK:
                        Double globalMark = null;
                        if (!"null".equals(value)) {
                            globalMark = Double.parseDouble(value);
                        }
                        actionTemp.setGlobalMark(globalMark);
                        break;
                }
            } catch (NumberFormatException ne) {
                log.error("Could not read value " + value, ne);
            }
        }

        if (allQuotesData.getActionMap().containsKey(actionTemp.getIsin())) {
            Action action = allQuotesData.getActionMap().get(actionTemp.getIsin());
            action.fillComputedData(actionTemp);

            l++;
        }

    }

    public void readDataFile(File file) {
        l = 0;
        CharSource charSource = Files.asCharSource(file, Charset.defaultCharset());
        try {
            charSource.forEachLine(s -> readDataFileLine(s, file));
        } catch (IOException ioe) {
            log.error("Impossible to read Reference file", ioe);
        }
    }

    protected void readDataFileLine(String line, File file) {
        Splitter splitterOnString = Splitter.on(csvDelimiter);
        Iterable<String> split = splitterOnString.split(line);
        Iterator<String> iter = split.iterator();
        String isin = "";
        String ticker = "";
        LocalDate date = null;
        double openQuote = 0;
        double minQuote = 0;
        double maxQuote = 0;
        double closeQuote = 0;
        long volQuote = 0;
        for (int i = 0; i < dataFileColumns.length; i++) {
            try {
                String value = iter.next();
                if (!value.trim().isEmpty()) {
                    switch (dataFileColumns[i]) {
                        case ISIN:
                            isin = value;
                            break;
                        case DATE:
                            date = LocalDate.parse(value, dTF);
                            break;
                        case OPEN_QUOTE:
                            openQuote = Double.parseDouble(value);
                            break;
                        case MAX_QUOTE:
                            maxQuote = Double.parseDouble(value);
                            break;
                        case MIN_QUOTE:
                            minQuote = Double.parseDouble(value);
                            break;
                        case CLOSE_QUOTE:
                            closeQuote = Double.parseDouble(value);
                            break;
                        case VOL_QUOTE:
                            volQuote = Long.parseLong(value);
                            break;
                        case TICKER:
                            ticker = value;
                            break;
                    }
                } else {
                    log.warn("empty value for line " + l + " col " + i + " and file " + file.getName());
                }
            } catch (NumberFormatException e) {
                log.error("Bad format value for line " + l + " col " + i + " and file " + file.getName());
            }
        }
        // Check if action exists in referential
        boolean actionExists;
        if (ticker.isEmpty()) {
            actionExists = allQuotesData.getActionMap().containsKey(isin);
        } else {
            actionExists = allQuotesData.getActionMap().containsKey(ticker);
        }

        if (actionExists) {
            DayQuote dayQuote = new DayQuote();
            if (ticker.isEmpty()) {
                dayQuote.setId(allQuotesData.getActionMap().get(isin).getTicker());
            } else {
                dayQuote.setId(ticker);
            }
            dayQuote.setClosePrice(closeQuote);
            dayQuote.setDate(date);
            dayQuote.setMaxPrice(maxQuote);
            dayQuote.setMinPrice(minQuote);
            dayQuote.setOpenPrice(openQuote);
            dayQuote.setVolume(volQuote);
            if (!allQuotesData.getQuotes().containsKey(dayQuote.getId())) {
                allQuotesData.getQuotes().put(dayQuote.getId(), new HashMap<>());
            }
            allQuotesData.getQuotes().get(dayQuote.getId()).put(MarketDataReader.STANDARD_DATE_FORMAT.format(date), dayQuote);
            if (!allQuotesData.getSortedQuotes().containsKey(dayQuote.getId())) {
                allQuotesData.getSortedQuotes().put(dayQuote.getId(), new ArrayList<>());
            }
            allQuotesData.getSortedQuotes().get(dayQuote.getId()).add(dayQuote);
            allQuotesData.getSortedQuotes().get(dayQuote.getId()).sort(MarketDataRepository.dateComparator);

            if (lastDateOfCotation == null) {
                lastDateOfCotation = dayQuote.getDate();
            } else if (lastDateOfCotation.isBefore(dayQuote.getDate())) {
                lastDateOfCotation = dayQuote.getDate();
            }
        } // else {
        //log.error("Unknown action "+isin+ticker+" in file "+file.getName());
        //}
        //System.out.println("is:"+isin+" line:"+l);
        l++;
    }

    @Override
    public List<Action> readRefFile() {
        CharSource charSource = Files.asCharSource(referenceFile, Charset.defaultCharset());
        List<Action> actions = new ArrayList<>();
        try {
            charSource.forEachLine(s -> readRefFileLine(s, actions));
            log.info("Reading reference file successfully");
        } catch (IOException ioe) {
            log.error("Impossible to read Reference file", ioe);
        }
        return actions;
    }

    protected void readRefFileLine(String line, List<Action> actions) {
        Splitter splitterOnString = Splitter.on(csvDelimiter);
        Iterable<String> split = splitterOnString.split(line);
        Iterator<String> iter = split.iterator();
        String isin = "";
        String name = "";
        String ticker = "";
        for (int i = 0; i < refFileColumns.length; i++) {
            String value = iter.next();
            switch (refFileColumns[i]) {
                case ISIN:
                    isin = value;
                    break;
                case NAME:
                    name = value;
                    break;
                case TICKER:
                    ticker = value;
                    break;
            }
        }
        if (!ticker.equals("ticker")) {
            Action action = new Action(isin, name, ticker);
            allQuotesData.getActionMap().put(isin, action);
            allQuotesData.getActionMap().put(ticker, action);
            actions.add(action);
            l++;
        }

    }
}
