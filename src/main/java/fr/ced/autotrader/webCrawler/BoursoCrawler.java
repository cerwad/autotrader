package fr.ced.autotrader.webCrawler;

/**
 * Get from the web boursorama Marks
 *
 * Created by cwaadd on 28/09/2017.
 */
public class BoursoCrawler extends MarkCrawler{

    private final static String USER_AGENT = "Mozilla/5.0";


    //<div class="c-median-gauge__tooltip">1.53</div>
    @Override
    protected String getLineMarker() {
        return "<div class=\"c-median-gauge__tooltip\">";
    }

    @Override
    public String buildUrl(String actionId) {
        return getUrl()+actionId+"/";
    }


    @Override
    public Double extractMark(String line) {
        Double mark = null;
        if(line != null){
            int from = line.indexOf(getLineMarker());
            line = line.substring(from+1);
            from = line.indexOf(">");
            int to = line.indexOf("<");
            String markStr = line.substring(from+1, to);
            mark = Double.parseDouble(markStr);
            // Check if equal 0 !!!
            if(mark == 0){
                mark = null;
            }
        }

        return mark;
    }
}
