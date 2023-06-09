package fr.ced.autotrader.webCrawler;


import fr.ced.autotrader.AppProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.util.Asserts;
import org.htmlunit.*;
import org.htmlunit.html.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.io.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;


/**
 * Created by cwaadd on 13/02/2018.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class MarketDataCrawler {
    public static final String INPUT_XPATH = "//input[@value='%s']";
    public static final String ABCBOURSE_URL = "https://www.abcbourse.com/download/historiques.aspx";
    public static final String ABCBOURSE_INTRADAY = "https://www.abcbourse.com/download/telechargement_intraday";
    private final AppProperties properties;

    public final static String CAC40_ID = "xcac40p";
    public final static String SBF120_ID = "xsbf120p";

    public final static String EXCEL = "ex";

    /**
     * Download today's cotations for all shares
     */
    public File downloadCurrentCotations(){
        File file = null;
        if(isWeekDay()) {
            try {
                WebClient webClient = getWebClient();
                // Visit abcBourse.com
                HtmlPage page = webClient.getPage(ABCBOURSE_INTRADAY);

                // Find page title
                DomNodeList<DomElement> elements = page.getElementsByTagName("h1");
                Asserts.notNull(elements, "Must have a title");
                Asserts.check(elements.size() == 1, "Must be only one title");
                DomElement element = elements.get(0);

                System.out.println(element.getTextContent());
                Asserts.check("Téléchargement en cours de séance".equals(element.getTextContent()), "Title must be Téléchargement en cours de séance");

                // Select csv format
                getFirstCbox(page, EXCEL).click();

                // Select SBF 120 shares
                DomElement cboxSbf = getFirstCbox(page, SBF120_ID);
                cboxSbf.click();

                // Click on download Button
                DomElement buttonDownload = getDownloadButton(page);
                Page downloadPage = buttonDownload.click();
                WebResponse response = downloadPage.getWebResponse();
                LocalDate now = LocalDate.now();
                String fileName = buildIntraFileName(now);

                file = new File(properties.getIntradayPath() + "/" + fileName);
                downloadFile(response, file);

            } catch (IOException ioe) {
                log.error("Impossible to connect to abcbourse website and download the intraday cotations", ioe);
            }
        }
        return file;
    }

    public boolean isWeekDay(){
        return LocalDate.now().getDayOfWeek() != DayOfWeek.SATURDAY && LocalDate.now().getDayOfWeek() != DayOfWeek.SUNDAY;
    }
    public static DomElement getFirstCbox(HtmlPage page, String id) {
        return page.getFirstByXPath(String.format(INPUT_XPATH, id));
    }

    public File downloadMonthlyBulkCotations(LocalDate startDate, LocalDate endDate){


        Assert.isTrue(endDate.isAfter(startDate) && ChronoUnit.DAYS.between(startDate, endDate) < 32, "The duration must not exceed 1 month");
        WebClient webClient = getWebClient();

        log.info("Downloading cotations from "+startDate+" to "+endDate);
        return downloadMonthlyBulkCotations(startDate, endDate, webClient);
    }

    private File downloadMonthlyBulkCotations(LocalDate startDate, LocalDate endDate, WebClient webClient){
        File downloadFile = null;
        try {
            // Visit abcBourse.com
            HtmlPage page = webClient.getPage(ABCBOURSE_URL);

            // Select SBF 120 shares contains CAC40
            getFirstCbox(page, SBF120_ID).click();

            final HtmlForm form = page.getForms().get(0);

            // Select start Date
            HtmlDateInput dateField = form.getFirstByXPath("//input[@id='txtFrom'][@type='date']");
            log.debug("Set start date : "+startDate.format(DateTimeFormatter.ISO_DATE));
            dateField.setValue(startDate.format(DateTimeFormatter.ISO_DATE));

            // Select end Date
            dateField = form.getFirstByXPath("//input[@id='txtTo'][@type='date']");
            log.debug("Set end date : "+endDate.format(DateTimeFormatter.ISO_DATE));
            dateField.setValue(endDate.format(DateTimeFormatter.ISO_DATE));

            // Click on download Button
            DomElement buttonDownload = getDownloadButton(page);
            Page downloadPage = buttonDownload.click();
            WebResponse response = downloadPage.getWebResponse();
            String fileName = buildFileName(startDate);

            downloadFile = new File(properties.getCotationsPath() + "/" + fileName);
            downloadFile(response, downloadFile);
            log.info("Downloading file "+fileName);
        } catch (IOException ioe){
            log.error("Impossible to connect to abcbourse website and download cotations between "+ startDate.format(DateTimeFormatter.BASIC_ISO_DATE) + " and "+ endDate.format(DateTimeFormatter.BASIC_ISO_DATE), ioe);
        }
        return downloadFile;
    }

    public static DomElement getDownloadButton(HtmlPage page) {
        return page.getFirstByXPath("//button[@class='btn_abc']");
    }


    public static WebClient getWebClient() {
        WebClient webClient = new WebClient(BrowserVersion.BEST_SUPPORTED);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setRedirectEnabled(true);
        webClient.getOptions().setCssEnabled(false);

        webClient.setAjaxController(new NicelyResynchronizingAjaxController());
        webClient.getCookieManager().setCookiesEnabled(true);
        webClient.waitForBackgroundJavaScript(4000);
        return webClient;
    }

    public static void downloadFile(WebResponse response, File file) throws IOException {
        try (InputStream inputStream = response.getContentAsStream();
             OutputStream outputStream = new FileOutputStream(file)){

            int read = 0;
            byte[] bytes = new byte[1024];

            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }

        }
    }

    public static String buildFileName(LocalDate date){
        return "SBFCotations"+date.format(DateTimeFormatter.BASIC_ISO_DATE)+".txt";
    }

    public static String buildIntraFileName(LocalDate date){
        return "IntraCotations"+date.format(DateTimeFormatter.BASIC_ISO_DATE)+".txt";
    }
}
