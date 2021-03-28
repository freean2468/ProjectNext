package com.mrhi.projectnext.object;

import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.LinearLayout;

import androidx.core.view.ViewCompat;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.HighLowDataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.charts.Stock;
import com.anychart.core.cartesian.series.Line;
import com.anychart.core.stock.Plot;
import com.anychart.data.Mapping;
import com.anychart.data.Set;
import com.anychart.data.Table;
import com.anychart.data.TableMapping;
import com.anychart.enums.Anchor;
import com.anychart.enums.MarkerType;
import com.anychart.enums.MovingAverageType;
import com.anychart.enums.StockSeriesType;
import com.anychart.enums.TooltipPositionMode;
import com.anychart.graphics.vector.Stroke;
import com.mrhi.projectnext.model.ModelTicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class ObjectAnyChart {
    private static ObjectAnyChart instance = new ObjectAnyChart();
    private ObjectAnyChart() {}
    public static ObjectAnyChart getInstance() {
        return instance;
    }

    private int fragmentMainViewId = 0;
    private int fragmentAlgorithmResultViewId = 0;

    public void drawOHLCChart(ViewGroup view, String selectedTicker) {
        AnyChartView anyChartView = view.findViewById(fragmentMainViewId);
        if (anyChartView != null) {
            view.removeView(anyChartView);
        }

        anyChartView = new AnyChartView(view.getContext());
        anyChartView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
        anyChartView.setPadding(10, 0, 0, 0);

        fragmentMainViewId = ViewCompat.generateViewId();
        anyChartView.setId(fragmentMainViewId);
        view.addView(anyChartView);

        /**
         * draw graph
         */
        ObjectAlgorithm objectAlgorithm = ObjectAlgorithm.getInstance();
        ModelTicker modelTicker = objectAlgorithm.getTicker(selectedTicker);

        java.util.Set<ModelTicker.Daily> tickerCopy =  modelTicker.getCopy();
        Iterator<ModelTicker.Daily> iterator = tickerCopy.iterator();
        List<DataEntry> data = new ArrayList<>();

        while(iterator.hasNext()) {
            ModelTicker.Daily daily = iterator.next();
            data.add(new OHCLDataEntry(daily.getDate().getTime(), daily.getOpen(), daily.getHigh(),
                    daily.getLow(), daily.getClose(), daily.getVolume()));
        }

        Table table = Table.instantiate("x");
        table.addData(data);

        TableMapping mapping = table.mapAs("{open: 'open', high: 'high', low: 'low', close: 'close', volume: 'volume'}");

        Stock stock = AnyChart.stock();

        Plot plot = stock.plot(0);
        plot.yGrid(true)
                .xGrid(true)
                .yMinorGrid(true)
                .xMinorGrid(true);

        // 이동평균선
        plot.ema(table.mapAs("{value: 'close'}"), 20d, StockSeriesType.LINE);

        plot.ohlc(mapping)
                .name(selectedTicker)
                .legendItem("{\n" +
                        "        iconType: 'rising-falling'\n" +
                        "      }");

        Plot plot2 = stock.plot(1);
        plot2.yGrid(true)
                .xGrid(true)
                .yMinorGrid(true)
                .xMinorGrid(true)
                .height("30%")
                .yAxis(0).labels().format("{%Value}{scale:(1000)|(k)}");;

        plot2.volumeMa(mapping, 20d, MovingAverageType.EMA, StockSeriesType.COLUMN, StockSeriesType.LINE);

        stock.scroller().ohlc(mapping);

        anyChartView.setChart(stock);
    }
    
    public void drawAlgorithmTestResultInOnePlot(ViewGroup viewGroup, LinkedList<ArrayList<ModelTicker.Daily>> resultList) {
        AnyChartView anyChartView = viewGroup.findViewById(fragmentAlgorithmResultViewId);
        if (anyChartView != null) {
            viewGroup.removeView(anyChartView);
        }

        anyChartView = new AnyChartView(viewGroup.getContext());
        anyChartView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
        anyChartView.setPadding(10, 0, 0, 0);

        fragmentAlgorithmResultViewId = ViewCompat.generateViewId();
        anyChartView.setId(fragmentAlgorithmResultViewId);
        viewGroup.addView(anyChartView);

        /**
         * draw graph
         */
        Stock stock = AnyChart.stock();

        Plot plot = stock.plot(0);

        plot.yGrid(true)
                .xGrid(true)
                .yMinorGrid(true)
                .xMinorGrid(true);

        Plot plot2 = stock.plot(1);
        plot2.yGrid(true)
                .xGrid(true)
                .yMinorGrid(true)
                .xMinorGrid(true)
                .height("30%")
                .yAxis(0).labels().format("{%Value}{scale:(1000)|(k)}");

        final int BUY_POS = 0;
        final int POS1 = 1;
        final int POS2 = 2;
        final int POS3 = 3;
        final int POS4 = 4;

        if (resultList.get(0).size() > 0) {
            for (int i = 0; i < resultList.get(0).size(); ++i) {
                Table table = Table.instantiate("x");
                List<DataEntry> data = new ArrayList<>();

                for (int j = 0; j < resultList.size(); ++j) {
                    ModelTicker.Daily daily = resultList.get(j).get(i);
                    if (daily != null) {
                        try {
                            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                            Date date = formatter.parse(formatter.format(daily.getDate()));
                            data.add(new OHCLDataEntry(date.getTime(), daily.getOpen(), daily.getHigh(),
                                    daily.getLow(), daily.getClose(), daily.getVolume()));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }

                table.addData(data);

                TableMapping mapping = table.mapAs("{open: 'open', high: 'high', low: 'low', close: 'close', volume: 'volume'}");

                plot.line(table.mapAs("{value: 'high'}")).name(String.valueOf(i+1));
                plot2.volumeMa(mapping, 0d, MovingAverageType.EMA, StockSeriesType.COLUMN, StockSeriesType.LINE);
//                stock.scroller().ohlc(mapping);
            }

            stock.scroller(false);

            anyChartView.setChart(stock);
        }
    }

    public void drawAlgorithmTestResult(ViewGroup viewGroup, LinkedList<ArrayList<ModelTicker.Daily>> resultList, int day1, int day2, int day3, int day4) {
        viewGroup.removeAllViewsInLayout();
        String name = String.valueOf(day1) + " | " + String.valueOf(day2) + " | " + String.valueOf(day3) + " | " + String.valueOf(day4);

        if (resultList.get(0).size() > 0) {
            int size = resultList.get(0).size();

            for (int i = 0; i < size; ++i) {
                AnyChartView anyChartView = new AnyChartView(viewGroup.getContext());
//                anyChartView.setLayoutParams(new LinearLayout.LayoutParams(
//                        LinearLayout.LayoutParams.MATCH_PARENT,
//                        LinearLayout.LayoutParams.MATCH_PARENT));
                anyChartView.setLayoutParams(new LinearLayout.LayoutParams(
                        1000,
                        1000));
                anyChartView.setPadding(10, 0, 0, 0);

                viewGroup.addView(anyChartView);

                /**
                 * draw graph
                 */
                Cartesian cartesian = AnyChart.line();

                cartesian.animation(false);

                cartesian.padding(10d, 20d, 5d, 20d);

                cartesian.crosshair().enabled(true);
                cartesian.crosshair()
                        .yLabel(true)
                        .yStroke((Stroke) null, null, null, (String) null, (String) null);

                cartesian.tooltip().positionMode(TooltipPositionMode.POINT);

                cartesian.title("전일대비 거래량이 100% 증가한 날 high에 매수 후 가격 변화");

                cartesian.yAxis(0).title("price");
                cartesian.xAxis(0).labels().padding(5d, 5d, 5d, 5d);

                List<DataEntry> seriesData = new ArrayList<>();

                Set set = Set.instantiate();

                for (int j = 0; j < resultList.size(); ++j) {
                    ModelTicker.Daily daily = resultList.get(j).get(i);
                    if (daily != null) {
                        try {
                            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                            Date date = formatter.parse(formatter.format(daily.getDate()));
                            seriesData.add(new AlgorithmDataEntry(date.toString(), daily.getHigh(), daily.getVolume()));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }

                set.data(seriesData);
                Mapping series1Mapping = set.mapAs("{ x: 'x', value: 'value' }");

                Line series1 = cartesian.line(series1Mapping);
                series1.name(name);
                series1.hovered().markers().enabled(true);
                series1.hovered().markers()
                        .type(MarkerType.CIRCLE)
                        .size(4d);
                series1.tooltip()
                        .position("right")
                        .anchor(Anchor.LEFT_CENTER)
                        .offsetX(5d)
                        .offsetY(5d);

                cartesian.legend().enabled(true);
                cartesian.legend().fontSize(7d);
                cartesian.legend().padding(0d, 0d, 10d, 0d);

                anyChartView.setChart(cartesian);
            }
        }
    }

    private class OHCLDataEntry extends HighLowDataEntry {
        public OHCLDataEntry(Long x, Double open, Double high, Double low, Double close, Long volume) {
            super(x, high, low);
            setValue("open", open);
            setValue("close", close);
            setValue("volume", volume);
        }
    }

    private class AlgorithmDataEntry extends ValueDataEntry {
        AlgorithmDataEntry(String x, Double value, Long volume) {
            super(x, value);
            setValue("volume", volume);
        }

    }
}
