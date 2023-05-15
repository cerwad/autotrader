package fr.ced.autotrader.algo;

import fr.ced.autotrader.algo.baseline.Line;
import fr.ced.autotrader.algo.baseline.Resistance;
import fr.ced.autotrader.algo.baseline.Support;
import fr.ced.autotrader.algo.moyenne.MoyenneExpo;
import fr.ced.autotrader.algo.moyenne.MoyenneExpoDouble;
import fr.ced.autotrader.algo.moyenne.MoyenneMobile;
import fr.ced.autotrader.data.GraphPoint;
import org.apache.log4j.Logger;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


/**
 * Created by cwaadd on 25/09/2017.
 */

public class AnalyticsTools {
    private static Logger logger = Logger.getLogger(AnalyticsTools.class);

    public Pivot getPivot(List<Double> prices){
        Pivot p = new Pivot();
        double max = prices.get(prices.size()-1);
        double min = prices.get(prices.size()-1);
        double last = prices.get(prices.size()-1);
        for(double pr : prices){
            if(pr > max){
                max = pr;
            }
            if(pr < min){
                min = pr;
            }
        }
        double pivot = (max + min+ last)/3;
        double s1 = 2*pivot - max;
        double s2 = pivot - (max - min);
        double r1 = 2*pivot - min;
        double r2 = pivot + (max - min);
        p.setPivot(pivot);
        p.setR1(r1);
        p.setR2(r2);
        p.setS1(s1);
        p.setS2(s2);

        return p;
    }

    /**
     * Calculate list of resistance points
     * @param prices
     * @return
     */
    public List<GraphPoint> calculateResistances(List<GraphPoint> prices){
        GraphPoint tempMax= new GraphPoint(0d, LocalDate.now());
        List<GraphPoint> maxPoints = new ArrayList<>();
        List<Double> pricesList = prices.stream().map(GraphPoint::getPrice).collect(Collectors.toList());
        if(prices.size() - 1 > 0) {
            int mmPeriod = prices.size() / 6;
            boolean upMM50 = false;
            for (int i = 0; i < prices.size();i++) {
                double p = prices.get(i).getPrice();
                MoyenneMobile moyenneMobile = new MoyenneMobile(prices);
                Double MM50 = moyenneMobile.getMoyenne(pricesList, i, mmPeriod);
                if ((MM50 == null || MM50 < p) && p > tempMax.getPrice()) {
                    tempMax.setPrice(p);
                    tempMax.setDate(prices.get(i).getDate());
                }
                // Flipping states up or down MM
                if(!upMM50 && MM50 != null && MM50 < p){
                    upMM50 = true;
                } else if(upMM50 && MM50 > p){
                    upMM50 = false;
                    // tempMax.setPrice(tempMax.getPrice()+(tempMax.getPrice()/20)); to display points in graph
                    maxPoints.add(tempMax);
                    tempMax = new GraphPoint(0d, LocalDate.now());
                }
            }
            if(upMM50){
                maxPoints.add(tempMax);
            }
            if(maxPoints.size() < 2){
                List<GraphPoint> minPrices = new ArrayList<>(prices);
                minPrices.sort((GraphPoint p1, GraphPoint p2) -> p1.getPrice().compareTo(p2.getPrice()));
                int size = minPrices.size();
                minPrices = minPrices.subList(size-2, size);
                maxPoints = minPrices;
            }
        }
        return maxPoints;
    }

    public List<Resistance> computeResistanceLines(List<GraphPoint> res){
        List<Resistance> baseLines = new ArrayList<>();
        for (GraphPoint point : res) {
            double price = point.getPrice();
            double lower = price - price / 100;
            double higher = price + price / 100;
            boolean alreadyFound = false;
            for (Resistance r : baseLines) {
                if (r.getValue() > lower && r.getValue() < higher) {
                    if (price > r.getValue()) {
                        r.setValue(price);
                        alreadyFound = true;
                    }
                    r.setStrength(r.getStrength() + 1);
                    break;
                }
            }
            if (!alreadyFound) {
                Resistance r = new Resistance();
                r.setValue(point.getPrice());
                r.setStrength(1);
                baseLines.add(r);
            }

        }
        return baseLines;
    }

    /**
     * Calculate list of support points
     * @param prices
     * @return
     */
    public List<GraphPoint> calculateSupports(List<GraphPoint> prices){
        GraphPoint tempMin= new GraphPoint(1000000d, LocalDate.now());
        List<GraphPoint> minPoints = new ArrayList<>();
        List<Double> priceList = prices.stream().map(GraphPoint::getPrice).collect(Collectors.toList());
        int mmPeriod = prices.size() / 6;
        if(prices.size() - 1 > 0) {
            boolean downMM50 = false;
            for (int i = 0; i < prices.size();i++) {
                double p = prices.get(i).getPrice();
                MoyenneMobile moyenneMobile = new MoyenneMobile(prices);
                Double MM50 = moyenneMobile.getMoyenne(priceList, i, mmPeriod);
                if ((MM50 == null || MM50 > p) && p < tempMin.getPrice()) {
                    tempMin.setPrice(p);
                    tempMin.setDate(prices.get(i).getDate());
                }
                // Flipping states up or down MM
                if(!downMM50 && MM50 != null && MM50 > p){
                    downMM50 = true;
                } else if(downMM50 && MM50 < p){
                    downMM50 = false;
                    // tempMin.setPrice(tempMin.getPrice()-(tempMin.getPrice()/20)); to display points
                    minPoints.add(tempMin);
                    tempMin = new GraphPoint(1000000d, LocalDate.now());
                }
            }
            if(downMM50){
                minPoints.add(tempMin);
            }
            if(minPoints.size() < 2){
                List<GraphPoint> minPrices = new ArrayList<>(prices);
                minPrices.sort((GraphPoint p1, GraphPoint p2) -> p1.getPrice().compareTo(p2.getPrice()));
                minPrices = minPrices.subList(0, 2);
                minPoints = minPrices;
            }
        }
        return minPoints;
    }

    public List<Support> computeBaseLines(List<GraphPoint> supports){
        List<Support> baseLines = new ArrayList<>();
        for (GraphPoint point : supports) {
            double price = point.getPrice();
            double lower = price - price / 100;
            double higher = price + price / 100;
            boolean alreadyFound = false;
            for (Support s : baseLines) {
                if (s.getValue() > lower && s.getValue() < higher) {
                    if (price < s.getValue()) {
                        s.setValue(price);
                        alreadyFound = true;
                    }
                    s.setStrength(s.getStrength() + 1);
                    break;
                }
            }
            if (!alreadyFound) {
                Support s = new Support();
                s.setValue(point.getPrice());
                s.setStrength(1);
                baseLines.add(s);
            }

        }
        return baseLines;
    }

    public List<GraphPoint> getMM50LineData(List<GraphPoint> allList){
        MoyenneMobile moyenneMobile = new MoyenneMobile(allList);
        return moyenneMobile.getLineData(50);
    }

    public List<GraphPoint> getMM20LineData(List<GraphPoint> allList){
        MoyenneMobile moyenneMobile = new MoyenneMobile(allList);
        return moyenneMobile.getLineData(20);
    }

    public List<GraphPoint> getEMA20LineData(List<GraphPoint> allList){
        MoyenneExpo moyenneExpo = new MoyenneExpo(allList);
        return moyenneExpo.getLineData(20);
    }

    public List<GraphPoint> keepXbiggest(List<GraphPoint> list, int x){
        list.sort((GraphPoint p1, GraphPoint p2) -> p1.getPrice().compareTo(p2.getPrice()));
        int size = list.size();
        x = x > size ? size : x;
        List<GraphPoint> subList = list.subList(size-x, size);
        subList.sort((GraphPoint p1, GraphPoint p2) -> p1.getLocalDate().compareTo(p2.getLocalDate()));
        return subList;
    }

    public List<GraphPoint> keepXlowest(List<GraphPoint> list, int x){
        list.sort((GraphPoint p1, GraphPoint p2) -> p1.getPrice().compareTo(p2.getPrice()));
        x = x > list.size() ? list.size() : x;
        List<GraphPoint> subList = list.subList(0, x);
        subList.sort((GraphPoint p1, GraphPoint p2) -> p1.getLocalDate().compareTo(p2.getLocalDate()));
        return subList;
    }

    public List<GraphPoint> chooseResPoints(List<GraphPoint> resPoints, List<GraphPoint> priceList){
        List<GraphPoint> subList = new ArrayList<>();
        resPoints.sort((GraphPoint p1, GraphPoint p2) -> p1.getPrice().compareTo(p2.getPrice()));
        int size = resPoints.size();
        LocalDate firstDate = priceList.get(0).getLocalDate();
        if(size >= 2) {
            GraphPoint p1 = null;
            GraphPoint p2 = null;
            boolean above = false;
            for(int i = size - 1; i >= 0; i--){
                p1 = resPoints.get(i);
                for(int j = i-1; j >= 0; j--) {
                    p2 = resPoints.get(j);
                    Line line = new Line(p1, p2, firstDate);
                    above = line.isCompletelyAbove(priceList);
                    if (above) {
                        break;
                    }
                }
                if (above) {
                    break;
                }
            }

            if(p2 != null && !p2.equals(p1)) {
                subList.add(p1);
                subList.add(p2);
            } else {
                p1 = resPoints.get(size-1);
                subList.add(p1);
                subList.add(new GraphPoint(p1.getPrice(), LocalDate.now()));
            }
            subList.sort((GraphPoint gp1, GraphPoint gp2) -> gp1.getLocalDate().compareTo(gp2.getLocalDate()));

        }

        return subList;
    }

    public List<GraphPoint> chooseSupPoints(List<GraphPoint> supPoints, List<GraphPoint> priceList){
        List<GraphPoint> subList = new ArrayList<>();
        supPoints.sort((GraphPoint p1, GraphPoint p2) -> p1.getPrice().compareTo(p2.getPrice()));
        int size = supPoints.size();
        LocalDate firstDate = priceList.get(0).getLocalDate();
        if(size >= 2) {
            GraphPoint p1 = null;
            GraphPoint p2 = null;
            boolean below = false;
            for(int i = 0; i < size; i++){
                p1 = supPoints.get(i);
                for(int j = i+1; j < size; j++) {
                    p2 = supPoints.get(j);
                    Line line = new Line(p1, p2, firstDate);
                    below = line.isCompletelyBelow(priceList);
                    if (below) {
                        break;
                    }
                }
                if (below) {
                    break;
                }
            }
            if(p1 != null && p2 != null && !p2.equals(p1)) {
                subList.add(p1);
                subList.add(p2);
            } else {
                p1 = supPoints.get(0);
                p2 = new GraphPoint(p1.getPrice(), LocalDate.now());
                subList.add(p1);
                subList.add(p2);
            }
            subList.sort((GraphPoint gp1, GraphPoint gp2) -> gp1.getLocalDate().compareTo(gp2.getLocalDate()));

        }

        return subList;
    }

    public List<GraphPoint> calculateLinePoints(GraphPoint p1 , GraphPoint p2, List<GraphPoint> priceList){
        List<GraphPoint> ret = new ArrayList<>();
        LocalDate firstDate = priceList.get(0).getLocalDate();
        Line line = new Line(p1, p2, firstDate);


        for (int i = 0; i < priceList.size(); i = i + priceList.size()/10) {
            LocalDate date = priceList.get(i).getLocalDate();
            double y = line.getGraphPrice(date);
            if(y > 0) {
                double p = priceList.get(i).getPrice();
                if(date.isBefore(p1.getLocalDate()) && Math.abs(y - p)*100/p < 10) {
                    GraphPoint point = new GraphPoint(y, date);
                    ret.add(point);
                }
            }
        }
        ret.add(p1);
        ret.add(p2);
        LocalDate lastDate = priceList.get(priceList.size()-1).getLocalDate();

        ret.add(new GraphPoint(line.getGraphPrice(lastDate), lastDate));
        ret.sort((GraphPoint g1, GraphPoint g2) -> g1.getLocalDate().compareTo(g2.getLocalDate()));
        return ret;
    }

    public double findMax(List<GraphPoint> prices){
        double max = 0;
        if(prices.size() >= 2) {
            for (int i = 0; i < prices.size()-1; i++) {
                if (max < prices.get(i).getPrice()) {
                    max = prices.get(i).getPrice();
                }
            }
        }
        return max;
    }

    public double findMin(List<GraphPoint> prices){
        double min = 0;
        if(prices.size() >= 2) {
            for (int i = 0; i < prices.size()-1; i++) {
                if (min > prices.get(i).getPrice()) {
                    min = prices.get(i).getPrice();
                }
            }
        }
        return min;
    }


    public List<GraphPoint> getDEMA20LineData(List<GraphPoint> listAll) {
        MoyenneExpoDouble moyenneExpoDouble = new MoyenneExpoDouble(listAll);
        return moyenneExpoDouble.getLineData(20);
    }

    public double findMaxPrice(List<GraphPoint> prices){
        double ret = 0;
        List<Double> listPrices = prices.stream().map(GraphPoint::getPrice).collect(Collectors.toList());
        Optional<Double> max = listPrices.stream().max(Comparator.naturalOrder());
        if(max.isPresent()){
            ret = max.get();
        }
        return ret;
    }

    public double findMaxPrice(List<GraphPoint> prices, LocalDate from, LocalDate to){
        List<GraphPoint> extract = extractPrices(prices, from, to);
        return findMaxPrice(extract);
    }

    public List<GraphPoint> extractPrices(List<GraphPoint> prices, LocalDate startDate, LocalDate endDate){
        List<GraphPoint> extract = new ArrayList<>();
        for(GraphPoint p : prices){
            if((p.getLocalDate().isAfter(startDate) || p.getLocalDate().isEqual(p.getLocalDate())) && p.getLocalDate().isBefore(endDate)){
                extract.add(p);
            }
        }
        return extract;
    }
}
