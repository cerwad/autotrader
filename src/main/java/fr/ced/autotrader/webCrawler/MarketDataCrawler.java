package fr.ced.autotrader.webCrawler;

import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
import fr.ced.autotrader.AppProperties;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class MarketDataCrawler {
    @Autowired
    private AppProperties properties;

    public final static String CAC40_ID = "xcac40p";
    public final static String SBF120_ID = "xsbf120p";
    public final static String DOWNLOAD_BUTTON_ID = "Button1";
    public final static String STARTDATE_ID = "ctl00$BodyABC$strDateDeb";
    public final static String ENDDATE_ID = "ctl00$BodyABC$strDateFin";

    /**
     * Download today's cotations for all shares
     */
    public void downloadCurrentCotations(){

        try {
            WebClient webClient = getWebClient();
            // Visit abcBourse.com
            HtmlPage page = webClient.getPage("https://www.abcbourse.com/download/historiques.aspx");

            // Select CAC40 shares
            DomElement element = page.getElementById(CAC40_ID);
            element.click();

            // Select SBF 120 shares
            element = page.getElementById(SBF120_ID);
            element.click();

            // Click on download Button
            DomElement buttonDownload = page.getElementById(DOWNLOAD_BUTTON_ID);
            TextPage textPage = buttonDownload.click();
            WebResponse response = textPage.getWebResponse();
            LocalDate now = LocalDate.now();
            String fileName = buildFileName(now);

            File file = new File(properties.getCotationsPath()+"/"+fileName);
            downloadFile(response, file);


        } catch (IOException ioe){
            log.error("Impossible to connect to abcbourse website and download the cotations of this day", ioe);
        }
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
            HtmlPage page = webClient.getPage("https://www.abcbourse.com/download/historiques.aspx");

            DomElement element = null;
            String prefix = null;
            // Select SBF 120 shares
            element = page.getElementById(SBF120_ID);
            prefix = "SBF";

            element.click();
            final HtmlForm form = page.getFormByName("aspnetForm");


            // Select start Date
            HtmlTextInput textField = form.getInputByName(STARTDATE_ID);
            textField.setValueAttribute(startDate.format(AppProperties.frenchFormat));

            // Select end Date
            textField = form.getInputByName(ENDDATE_ID);
            textField.setValueAttribute(endDate.format(AppProperties.frenchFormat));

            // Click on download Button
            DomElement buttonDownload = page.getElementById(DOWNLOAD_BUTTON_ID);
            TextPage textPage = buttonDownload.click();
            WebResponse response = textPage.getWebResponse();
            String fileName = buildFileName(prefix, startDate);

            File file = new File(properties.getCotationsPath() + "/" + fileName);
            downloadFile(response, file);
            log.info("Downloading file "+fileName);
        } catch (IOException ioe){
            log.error("Impossible to connect to abcbourse website and download cotations between "+ startDate.format(DateTimeFormatter.BASIC_ISO_DATE) + " and "+ endDate.format(DateTimeFormatter.BASIC_ISO_DATE));
        }
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
