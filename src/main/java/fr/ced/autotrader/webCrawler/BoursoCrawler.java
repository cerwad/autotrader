package fr.ced.autotrader.webCrawler;

/**
 * Get from the web boursorama Marks
 *
 * Created by cwaadd on 28/09/2017.
 */
public class BoursoCrawler extends MarkCrawler{

    private final static String USER_AGENT = "Mozilla/5.0";


    //<div class="c-median-gauge__tooltip ">1.53</div>
    @Override
    protected String getSelector() {
        return ".c-median-gauge__tooltip ";
    }

    @Override
    public String buildUrl(String actionId) {
        return getUrl()+actionId+"/";
    }


}
