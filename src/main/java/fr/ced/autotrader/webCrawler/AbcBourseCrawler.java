package fr.ced.autotrader.webCrawler;

import org.jsoup.nodes.Element;
import org.w3c.dom.Node;

import javax.annotation.Nullable;


/**
 * Get from the web abcBourse Marks
 * <p>
 * Created by cwaadd on 28/09/2017.
 */
public class AbcBourseCrawler extends MarkCrawler {

    private final static String USER_AGENT = "Mozilla/5.0";


    @Override
    protected String getSelector() {
        return "img[alt=\"Conseil bourse\"]";
    }

    @Override
    public String buildUrl(String actionId) {
        String url = getUrl() + actionId + "p";
        return url;
    }

    @Override
    public Double extractMark(@Nullable Element node) {
        if(node == null){
            return null;
        }
        Double mark = null;
        String img = node.attr("src");
        if(img.length() > 6) {
            char c = img.charAt(6);
            int markInt = c - '0';
            mark = (double) markInt;
            if (mark == 0) {
                mark = null;
            }
        }
        // Check if equal 0 !!!
        return mark;
    }
}
