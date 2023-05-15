package fr.ced.autotrader.webCrawler;

import org.apache.log4j.Logger;

import javax.annotation.Nullable;


/**
 * Get from the web abcBourse Marks
 *
 * Created by cwaadd on 28/09/2017.
 */
public class AbcBourseCrawler extends MarkCrawler{
    private static Logger logger = Logger.getLogger(AbcBourseCrawler.class);

    private final static String USER_AGENT = "Mozilla/5.0";


    @Override
    protected String getLineMarker() {
        return "alt=\"Conseil bourse\"";
    }

    @Override
    public String buildUrl(String actionId) {
        String url = getUrl()+actionId+"p";
        return url;
    }

    @Override
    public Double extractMark(@Nullable String line) {
        Double mark = null;
        if(line != null){
            int from = line.indexOf("src=");
            line = line.substring(from);
            char c = line.charAt(11);
            int markInt = c - '0';
            mark = Double.valueOf(markInt);
            // Check if equal 0 !!!
            if(mark == 0){
                mark = null;
            }
        }
        return mark;
    }
}
