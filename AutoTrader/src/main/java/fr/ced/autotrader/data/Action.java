package fr.ced.autotrader.data;

import fr.ced.autotrader.algo.Pivot;
import fr.ced.autotrader.algo.baseline.ResistanceAnalysis;
import fr.ced.autotrader.algo.baseline.SupportAnalysis;

import java.time.LocalDate;

/**
 * Created by cwaadd on 20/09/2017.
 */
public class Action {

    private String name;
    private String isin;
    private String ticker;
    private Double boursoMark;
    private Double abcMark;
    private Double morningStarMark;
    private Double globalMark;
    private Double technicalMark;
    private LocalDate infoDate;
    private double max;
    private double min;
    private double vol;
    private ResistanceAnalysis resistanceAnalysis;
    private SupportAnalysis supportAnalysis;
    private Trend trend;
    private Pivot pivot;
    private Forecast forecast;
    private double potential;
    private double lastPrice;
    private double coef;


    public Action() {
    }

    public Action(String isin, String name, String ticker){
        this.isin = isin;
        this.name = name;
        this.ticker = ticker;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIsin() {
        return isin;
    }

    public void setIsin(String isin) {
        this.isin = isin;
    }

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public Double getBoursoMark() {
        return boursoMark;
    }

    public void setBoursoMark(Double boursoMark) {
        this.boursoMark = boursoMark;
    }

    public Double getAbcMark() {
        return abcMark;
    }

    public void setAbcMark(Double abcMark) {
        this.abcMark = abcMark;
    }

    public Double getGlobalMark() {
        return globalMark;
    }

    public void setGlobalMark(Double globalMark) {
        this.globalMark = globalMark;
    }

    public LocalDate getInfoDate() {
        return infoDate;
    }

    public void setInfoDate(LocalDate infoDate) {
        this.infoDate = infoDate;
    }

    /**
     * Computed mark
     * @return the mark given by the program
     */
    public Double getTechnicalMark() {
        return technicalMark;
    }

    public void setTechnicalMark(Double technicalMark) {
        this.technicalMark = technicalMark;
    }

    public double getMax() {
        return max;
    }

    public void setMax(double max) {
        this.max = max;
    }

    public double getMin() {
        return min;
    }

    public void setMin(double min) {
        this.min = min;
    }

    public double getVol() {
        return vol;
    }

    public void setVol(double vol) {
        this.vol = vol;
    }

    public ResistanceAnalysis getResistanceAnalysis() {
        return resistanceAnalysis;
    }

    public void setResistanceAnalysis(ResistanceAnalysis resistanceAnalysis) {
        this.resistanceAnalysis = resistanceAnalysis;
    }

    public SupportAnalysis getSupportAnalysis() {
        return supportAnalysis;
    }

    public void setSupportAnalysis(SupportAnalysis supportAnalysis) {
        this.supportAnalysis = supportAnalysis;
    }

    public Trend getTrend() {
        return trend;
    }

    public void setTrend(Trend trend) {
        this.trend = trend;
    }

    public Pivot getPivot() {
        return pivot;
    }

    public void setPivot(Pivot pivot) {
        this.pivot = pivot;
    }

    public Forecast getForecast() {
        return forecast;
    }

    public void setForecast(Forecast forecast) {
        this.forecast = forecast;
    }

    public Double getMorningStarMark() {
        return morningStarMark;
    }

    public void setMorningStarMark(Double morningStarMark) {
        this.morningStarMark = morningStarMark;
    }

    public double getPotential() {
        return potential;
    }

    public void setPotential(double potential) {
        this.potential = potential;
    }

    public double getLastPrice() {
        return lastPrice;
    }

    public void setLastPrice(double lastPrice) {
        this.lastPrice = lastPrice;
    }

    public double getCoef() {
        return coef;
    }

    public void setCoef(double coef) {
        this.coef = coef;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Action action = (Action) o;

        return isin.equals(action.isin);

    }

    @Override
    public int hashCode() {
        return isin.hashCode();
    }

    public void fillComputedData(Action action){
        this.setInfoDate(action.getInfoDate());
        this.setGlobalMark(action.getGlobalMark());
        setAbcMark(action.getAbcMark());
        setBoursoMark(action.getBoursoMark());
        setMax(action.getMax());
        setMin(action.getMin());
        setTechnicalMark(action.getTechnicalMark());

    }

    @Override
    public String toString() {
        return "Action{" +
                name +
                " (" + ticker + ") }";
    }
}
