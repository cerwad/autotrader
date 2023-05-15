package fr.ced.autotrader.webCrawler;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * Created by cwaadd on 02/03/2018.
 */

public abstract class MarkCrawler {
    private static Logger logger = Logger.getLogger(MarkCrawler.class);

    private final static String USER_AGENT = "Mozilla/5.0";

    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Double getShareMark(String actionId){
        String url = buildUrl(actionId);
        String line = null;
        int mark = 0;
        URL obj = null;
        HttpURLConnection con = null;
        try {
            obj = new URL(url);
            con = (HttpURLConnection) obj.openConnection();

            // optional default is GET
            con.setRequestMethod("GET");

            //add request header
            con.setRequestProperty("User-Agent", USER_AGENT);

            int responseCode = con.getResponseCode();
            System.out.println("\nSending 'GET' request to URL : " + url);
            if(responseCode != 200) {
                System.out.println("Response Code : " + responseCode+ " for url "+url);
            }
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            try {
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    if (inputLine.contains(getLineMarker())) {
                        line = inputLine;
                        break;
                    }
                }
            } finally {
                in.close();
            }


        } catch (MalformedURLException e) {
            logger.error("Malformed url : "+url, e);
        } catch (IOException ex){
            logger.error("Connexion problem", ex);
        } finally {
            if(con != null){
                con.disconnect();
            }
        }

        return extractMark(line);
    }

    protected abstract String getLineMarker();

    public abstract String buildUrl(String actionId);

    public abstract Double extractMark(String line);

}
