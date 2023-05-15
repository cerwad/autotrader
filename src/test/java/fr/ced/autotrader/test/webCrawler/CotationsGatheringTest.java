package fr.ced.autotrader.test.webCrawler;

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.TextPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.html.*;
import com.google.common.io.CharSource;
import com.google.common.io.Files;
import fr.ced.autotrader.webCrawler.MarketDataCrawler;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.time.LocalDate;


/**
 * Created by cwaadd on 23/02/2018.
 */

public class CotationsGatheringTest {
    private static Logger logger = Logger.getLogger(CotationsGatheringTest.class);


    @Before
    public void init(){
        Logger rootLogger = Logger.getRootLogger();
        rootLogger.setLevel(Level.INFO);
        Logger htmlLogger = Logger.getLogger("com.gargoylesoftware");
        htmlLogger.setLevel(Level.WARN);
    }


    @Test
    public void testDownload() throws IOException {
        File testFile = new File("/Users/cwaadd/Documents/Autotrader/test/test.txt");
        if(testFile.exists()){
            testFile.delete();
        }
        try {
            WebClient webClient = MarketDataCrawler.getWebClient();
            // Visit abcBourse.com
            HtmlPage page = webClient.getPage("https://www.abcbourse.com/download/historiques.aspx");

            // Find page title
            DomNodeList<DomElement> elements = page.getElementsByTagName("h1");
            Assert.assertEquals(1, elements.size());
            DomElement element = elements.get(0);

            System.out.println(element.getTextContent());
            Assert.assertEquals("Téléchargement des cotations", element.getTextContent());

            // Select CAC40 shares
            element = page.getElementById("ctl00_BodyABC_xcac40p");
            Assert.assertNotNull(element);
            element.click();

            // Click on download Button
            DomElement buttonDownload = page.getElementById("ctl00_BodyABC_Button1");
            Assert.assertNotNull(buttonDownload);
            TextPage textPage = buttonDownload.click();
            WebResponse response = textPage.getWebResponse();
            Assert.assertEquals(200, response.getStatusCode());


            // Download File
            MarketDataCrawler.downloadFile(response, testFile);

            Assert.assertTrue(testFile.exists());

        } finally {
            if(testFile.exists()){
                testFile.delete();
            }
        }


        Assert.assertFalse(testFile.exists());

    }

    @Test
    public void testBulkDownload(){
        try {
            WebClient webClient = MarketDataCrawler.getWebClient();
            // Visit abcBourse.com
            HtmlPage page = webClient.getPage("https://www.abcbourse.com/download/historiques.aspx");

            // Select CAC40 shares
            DomElement element = page.getElementById("xcac40p");
            Assert.assertNotNull(element);
            element.click();

            // Select SBF 120 shares
            element = page.getElementById("xsbf120p");
            Assert.assertNotNull(element);
            element.click();

            // Click on download Button
            DomElement buttonDownload = page.getElementById("Button1");
            TextPage textPage = buttonDownload.click();
            WebResponse response = textPage.getWebResponse();
            LocalDate now = LocalDate.now();
            String fileName = MarketDataCrawler.buildFileName(now);

            File file = new File("/Users/cwaadd/Documents/Autotrader/test"+"/"+fileName);
            MarketDataCrawler.downloadFile(response, file);


        } catch (IOException ioe){
            logger.error("Impossible to connect to abcbourse website and download the cotations of this day", ioe);
        }
    }


    @Test
    public void testDownloadForDay() throws IOException {
        File testFile = new File("/Users/cwaadd/Documents/Autotrader/test/test.txt");
        if(testFile.exists()){
            testFile.delete();
        }
        try{
            WebClient webClient = MarketDataCrawler.getWebClient();
            // Visit abcBourse.com
            HtmlPage page = webClient.getPage("https://www.abcbourse.com/download/historiques.aspx");

            final HtmlForm form = page.getFormByName("aspnetForm");
            String randomDateStr = "09/02/2018";


            // Select start Date
            HtmlTextInput textField = form.getInputByName("ctl00$BodyABC$strDateDeb");
            Assert.assertNotNull(textField);
            textField.setValueAttribute(randomDateStr);

            // Select end Date
            textField = form.getInputByName("ctl00$BodyABC$strDateFin");
            Assert.assertNotNull(textField);
            textField.setValueAttribute(randomDateStr);

            // Select CAC40 shares
            DomElement element = page.getElementById("ctl00_BodyABC_xcac40p");
            Assert.assertNotNull(element);
            element.click();

            // Click on download Button
            DomElement buttonDownload = page.getElementById("ctl00_BodyABC_Button1");
            Assert.assertNotNull(buttonDownload);
            Page textPage = buttonDownload.click();
            WebResponse response = textPage.getWebResponse();
            Assert.assertEquals(200, response.getStatusCode());

            // Download File
            MarketDataCrawler.downloadFile(response, testFile);

            Assert.assertTrue(testFile.exists());
            CharSource charSource = Files.asCharSource(testFile, Charset.defaultCharset());
            Assert.assertTrue(!charSource.isEmpty() && charSource.lines().findFirst().isPresent());
            String line = charSource.lines().findFirst().get();
            String actualDate = line.substring(13, 21);
            Assert.assertEquals("09/02/18", actualDate);

        } finally {
            if(testFile.exists()){
                testFile.delete();
            }
        }

    }

    @Test
    public void testDownloadForIsin() throws IOException {
        File testFile = new File("/Users/cwaadd/Documents/Autotrader/test/test.txt");
        if(testFile.exists()){
            testFile.delete();
        }
        try{
            WebClient webClient = MarketDataCrawler.getWebClient();
            // Visit abcBourse.com
            HtmlPage page = webClient.getPage("https://www.abcbourse.com/download/historiques.aspx");

            final HtmlForm form = page.getFormByName("aspnetForm");

            // Click on Une seule valeur
            DomElement element = page.getElementById("ctl00_BodyABC_oneSico");
            Assert.assertNotNull(element);
            element.click();

            // Select ISIN
            HtmlTextInput textField = form.getInputByName("ctl00$BodyABC$txtOneSico");
            Assert.assertNotNull(textField);
            textField.setValueAttribute("FR0000121204");

            // Click on download Button
            DomElement buttonDownload = page.getElementById("ctl00_BodyABC_Button1");
            Assert.assertNotNull(buttonDownload);
            Page textPage = buttonDownload.click();
            WebResponse response = textPage.getWebResponse();
            Assert.assertEquals(200, response.getStatusCode());

            // Download File
            MarketDataCrawler.downloadFile(response, testFile);

            Assert.assertTrue(testFile.exists());
            CharSource charSource = Files.asCharSource(testFile, Charset.defaultCharset());
            Assert.assertTrue(!charSource.isEmpty() && charSource.lines().findFirst().isPresent());
            String line = charSource.lines().findFirst().get();
            String isin = line.substring(0, 12);
            Assert.assertEquals("FR0000121204", isin);

        } finally {
            if(testFile.exists()){
                testFile.delete();
            }
        }

    }
}
