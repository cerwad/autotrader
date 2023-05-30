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
    private final AppProperties properties;

    public final static String CAC40_ID = "xcac40p";
    public final static String SBF120_ID = "xsbf120p";

    /**
     * Download today's cotations for all shares
     */
    public void downloadCurrentCotations(){

        try {
            WebClient webClient = getWebClient();
            // Visit abcBourse.com
            HtmlPage page = webClient.getPage(ABCBOURSE_URL);

            // Find page title
            DomNodeList<DomElement> elements = page.getElementsByTagName("h1");
            Asserts.notNull(elements, "Must have a title");
            Asserts.check(elements.size() == 1, "Must be only one title");
            DomElement element = elements.get(0);

            System.out.println(element.getTextContent());
            Asserts.check("Téléchargement des cotations".equals(element.getTextContent()), "Title must be Téléchargement des cotations");

            // Select CAC40 shares
            DomElement cboxCac = getFirstCbox(page, CAC40_ID);
            cboxCac.click();

            // Select SBF 120 shares
            DomElement cboxSbf = getFirstCbox(page, SBF120_ID);
            cboxSbf.click();

            // Click on download Button
            DomElement buttonDownload = getDownloadButton(page);
            Page downloadPage = buttonDownload.click();
            WebResponse response = downloadPage.getWebResponse();
            LocalDate now = LocalDate.now();
            String fileName = buildFileName(now);

            File file = new File(properties.getCotationsPath()+"/"+fileName);
            downloadFile(response, file);


        } catch (IOException ioe){
            log.error("Impossible to connect to abcbourse website and download the cotations of this day", ioe);
        }
    }

    public static DomElement getFirstCbox(HtmlPage page, String id) {
        return page.getFirstByXPath(String.format(INPUT_XPATH, id));
    }

    public void downloadMonthlyBulkCotations(LocalDate startDate, LocalDate endDate){


        Assert.isTrue(endDate.isAfter(startDate) && ChronoUnit.DAYS.between(startDate, endDate) < 32, "The duration must not exceed 1 month");
        WebClient webClient = getWebClient();

        log.info("Downloading cotations from "+startDate+" to "+endDate);
        downloadMonthlyBulkCotations(startDate, endDate, webClient);
    }

    private void downloadMonthlyBulkCotations(LocalDate startDate, LocalDate endDate, WebClient webClient){

        try {

            // Visit abcBourse.com
            HtmlPage page = webClient.getPage(ABCBOURSE_URL);

            String prefix = null;
            // Select CAC40 shares
            getFirstCbox(page, CAC40_ID).click();
            // Select SBF 120 shares
            getFirstCbox(page, SBF120_ID).click();
            prefix = "SBF";

            final HtmlForm form = page.getForms().get(0);

            // Select start Date
            HtmlDateInput dateField = form.getFirstByXPath("//input[@id='txtFrom'][@type='date']");
            dateField.setValueAttribute(startDate.format(AppProperties.frenchFormat));

            // Select end Date
            dateField = form.getFirstByXPath("//input[@id='txtTo'][@type='date']");
            dateField.setValueAttribute(endDate.format(AppProperties.frenchFormat));

            // Click on download Button
            DomElement buttonDownload = getDownloadButton(page);
            Page downloadPage = buttonDownload.click();
            WebResponse response = downloadPage.getWebResponse();
            String fileName = buildFileName(prefix, startDate);

            File file = new File(properties.getCotationsPath() + "/" + fileName);
            downloadFile(response, file);
            log.info("Downloading file "+fileName);
        } catch (IOException ioe){
            log.error("Impossible to connect to abcbourse website and download cotations between "+ startDate.format(DateTimeFormatter.BASIC_ISO_DATE) + " and "+ endDate.format(DateTimeFormatter.BASIC_ISO_DATE));
        }
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

    public static void downloadFile(WebResponse response, File testFile) throws IOException {
        try (InputStream inputStream = response.getContentAsStream();
             OutputStream outputStream = new FileOutputStream(testFile)){

            int read = 0;
            byte[] bytes = new byte[1024];

            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }

        }
    }

    public static String buildFileName(LocalDate date){
        return "Cotations"+date.format(DateTimeFormatter.BASIC_ISO_DATE)+".txt";
    }

    public static String buildFileName(String prefix, LocalDate date){
        return prefix + buildFileName(date);
    }
}
