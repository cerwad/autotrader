package fr.ced.autotrader.algo;

import java.util.List;

/**
 * Created by cwaadd on 25/09/2017.
 */
public interface SharePicker {
    List<AnalysisResult> getTopFive();
    List<AnalysisResult> getTopTen();
}
