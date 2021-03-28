package com.mrhi.projectnext.object;

import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.core.view.ViewCompat;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.HighLowDataEntry;
import com.anychart.charts.Stock;
import com.anychart.core.stock.Plot;
import com.anychart.data.Table;
import com.anychart.data.TableMapping;
import com.anychart.enums.MovingAverageType;
import com.anychart.enums.StockSeriesType;
import com.mrhi.projectnext.model.ModelTicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class ObjectAnyChart {
    private static ObjectAnyChart instance = new ObjectAnyChart();
    private ObjectAnyChart() {}
    public static ObjectAnyChart getInstance() {
        return instance;
    }

    private int tickerViewId = 0;
    private int algorithmViewId;

    public void drawMainChart(ViewGroup view, String selectedTicker) {
        AnyChartView anyChartView = view.findViewById(tickerViewId);
        if (anyChartView != null) {
            view.removeView(anyChartView);
        }

        anyChartView = new AnyChartView(view.getContext());
        anyChartView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
        anyChartView.setPadding(10, 0, 0, 0);

        tickerViewId = ViewCompat.generateViewId();
        anyChartView.setId(tickerViewId);
        view.addView(anyChartView);

        /**
         * draw graph
         */
        ObjectAlgorithm objectAlgorithm = ObjectAlgorithm.getInstance();
        ModelTicker modelTicker = objectAlgorithm.getTicker(selectedTicker);

        Set<ModelTicker.Daily> tickerCopy =  modelTicker.getCopy();
        Iterator<ModelTicker.Daily> iterator = tickerCopy.iterator();
        List<DataEntry> data = new ArrayList<>();

        while(iterator.hasNext()) {
            try {
                ModelTicker.Daily daily = iterator.next();
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                Date date = formatter.parse(formatter.format(daily.getDate()));
                data.add(new OHCLDataEntry(date.getTime(), daily.getOpen(), daily.getHigh(),
                        daily.getLow(), daily.getClose(), daily.getVolume()));
            } catch (ParseException pe) {
                pe.printStackTrace();
            }
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

    private class OHCLDataEntry extends HighLowDataEntry {
        OHCLDataEntry(Long x, Double open, Double high, Double low, Double close, Long volume) {
            super(x, high, low);
            setValue("open", open);
            setValue("close", close);
            setValue("volume", volume);
        }
    }
}
