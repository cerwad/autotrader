package fr.ced.autotrader.test.webCrawler;


import fr.ced.autotrader.AppProperties;
import fr.ced.autotrader.webCrawler.MarketDataCrawler;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.util.Asserts;
import org.htmlunit.Page;
import org.htmlunit.WebClient;
import org.htmlunit.WebResponse;
import org.htmlunit.html.DomElement;
import org.htmlunit.html.DomNodeList;
import org.htmlunit.html.HtmlPage;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;

import static fr.ced.autotrader.webCrawler.MarketDataCrawler.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


/**
 * Created by cwaadd on 23/02/2018.
 */
@Slf4j
public class CotationsGatheringTest {

    public static final String ABCBOURSE_URL = "https://www.abcbourse.com/download/historiques.aspx";


    @Test
    public void testDownload() throws IOException {
        File testFile = new File("C:\\Programmation\\test\\test.txt");
        if (testFile.exists()) {
            testFile.delete();
        }
        try {
            WebClient webClient = MarketDataCrawler.getWebClient();
            // Visit abcBourse.com
            HtmlPage page = webClient.getPage(ABCBOURSE_URL);

            // Find page title
            DomNodeList<DomElement> elements = page.getElementsByTagName("h1");
            Assert.assertEquals(1, elements.size());
            DomElement element = elements.get(0);

            System.out.println(element.getTextContent());
            Assert.assertEquals("Téléchargement des cotations", element.getTextContent());

            // Select CAC40 shares
            DomElement cboxCac = page.getFirstByXPath("//input[@value='xcac40p']");
            Assert.assertNotNull(cboxCac);
            element.click();

            // Click on download Button
            DomElement buttonDownload = page.getFirstByXPath("//button[@class='btn_abc']");
            Assert.assertNotNull(buttonDownload);
            HtmlPage htmlPage = buttonDownload.click();
            WebResponse response = htmlPage.getWebResponse();
            Assert.assertEquals(200, response.getStatusCode());


            // Download File
            MarketDataCrawler.downloadFile(response, testFile);

            assertTrue(testFile.exists());

        } finally {
            if (testFile.exists()) {
                testFile.delete();
            }
        }


        assertFalse(testFile.exists());

    }

    @Test
    public void testCurrentCotations() {
        File testFile = new File("C:\\Programmation\\test\\test.txt");
        if (testFile.exists()) {
            testFile.delete();
        }
        try {
            WebClient webClient = MarketDataCrawler.getWebClient();
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
            DomElement cboxCac = getFirstCbox(page, MarketDataCrawler.CAC40_ID);
            cboxCac.click();

            // Select SBF 120 shares
            //DomElement cboxSbf = getFirstCbox(page, SBF120_ID);
            //cboxSbf.click();

            // Click on download Button
            DomElement buttonDownload = getDownloadButton(page);
            Page downloadPage = buttonDownload.click();
            WebResponse response = downloadPage.getWebResponse();

            downloadFile(response, testFile);


        } catch (IOException ioe) {
            log.error("Impossible to connect to abcbourse website and download the cotations of this day", ioe);
        } finally {
            if (testFile.exists()) {
                testFile.delete();
            }
        }


        assertFalse(testFile.exists());
    }

    @Test
    public void testMarketDataCrawler() {
        File testFile = new File("C:\\Programmation\\test\\test.txt");
        if (testFile.exists()) {
            testFile.delete();
        }
        try {
            AppProperties appProperties = Mockito.mock(AppProperties.class);
            Mockito.when(appProperties.getCotationsPath()).thenReturn("C:\\Programmation\\test\\");
            MarketDataCrawler marketDataCrawler = new MarketDataCrawler(appProperties);
            marketDataCrawler.downloadCurrentCotations();

        } finally {
            if (testFile.exists()) {
                testFile.delete();
            }
        }


        assertFalse(testFile.exists());
    }

}
