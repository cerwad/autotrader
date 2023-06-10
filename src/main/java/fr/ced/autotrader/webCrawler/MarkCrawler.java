package fr.ced.autotrader.webCrawler;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * Created by cwaadd on 02/03/2018.
 */
@Slf4j
public abstract class MarkCrawler {
    private final static String USER_AGENT = "Mozilla/5.0";

    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Double getShareMark(String actionId) {
        String url = buildUrl(actionId);
        Double mark = null;
        URL obj = null;
        try {
            Document doc = Jsoup.connect(url).get();

            Elements elements = doc.select(getSelector());
            if(elements.size() != 1){
                log.warn("Cannot find the mark");
            } else {
                mark = extractMark(elements.get(0));
            }

        } catch (MalformedURLException e) {
            log.error("Malformed url : " + url, e);
        } catch (IOException ex) {
            log.error("Connexion problem", ex);
        }

        return mark;
    }

    protected abstract String getSelector();

    public abstract String buildUrl(String actionId);

    public Double extractMark(Element node){
        if(node == null || node.childNodeSize() != 1 || StringUtils.isBlank(node.childNode(0).toString())){
            return null;
        }
        return Double.parseDouble(node.childNode(0).toString().trim());
    }

}
