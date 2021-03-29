package com.mrhi.projectnext.object;

import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

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

/**
 * 알고리즘의 결과물들을 구현할 클래스
 * 싱글톤 적용
 * ObjectAlgorithm에서 작성한 데이터셋을 이용해 결과 계산 및 도식화
 * AnyChart 라이브러리를 이용해 차트를 그릴 수 있다.
 * 참고 : https://github.com/AnyChart/AnyChart-Android
 *
 * @author 송훈일
 */
public class ObjectAnyChart {
    private static ObjectAnyChart instance = new ObjectAnyChart();
    private ObjectAnyChart() {}
    public static ObjectAnyChart getInstance() {
        return instance;
    }

    private int fragmentMainViewId = 0;
    private int fragmentAlgorithmResultViewId = 0;

    /** 시고저종 그래프의 기본형을 그린다.
     * 현재 ObjectAlgorithm에 selectedTicker의 데이터가 있어야 함.
     *
     * @param view  그래프를 담을 Layout
     * @param selectedTicker
     */
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

    /*
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
    */

    /**
     * 전일 대비 거래량이 100% 상승했을 때 high에서 매수한 시점에서
     * day1, day2, day3, day4 이후의 가격 평균을 도출하는 알고리즘.
     *
     * @author 송훈일 (freean2468@gmail.com)
     * @param strSelectedAlgorithm 알고리즘 제목
     * @param viewGroup 알고리즘 결과가 담길 Layout (FragmentAlgorithmResult 안 LinearLayout)
     * @param resultList 알고리즘의 결과물. 이 함수 안에서 사용할 데이터 셋
     * @param day1
     * @param day2
     * @param day3
     * @param day4
     */
    public void drawAlgorithmTestResult(String strSelectedAlgorithm, ViewGroup viewGroup, LinkedList<ArrayList<ModelTicker.Daily>> resultList, int day1, int day2, int day3, int day4) {
        /**
         * 기존에 있던 view들을 모두 정리하고 새하얀 도화지로 만든다.
         */
        viewGroup.removeAllViewsInLayout();
        String name = String.valueOf(day1) + " | " + String.valueOf(day2) + " | " + String.valueOf(day3) + " | " + String.valueOf(day4);

        /**
         * 알고리즘 매칭되는 부분이 없을 수도 있다.
         */
        if (resultList.get(0).size() > 0) {
            int size = resultList.get(0).size();

            /**
             * 새로운 AnyChartView를 xml이 아니라 코드상에서 직접 생성하고
             */
            AnyChartView anyChartView = new AnyChartView(viewGroup.getContext());
            /**
             * Layout을 설정
             */
            anyChartView.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    1));
            anyChartView.setPadding(10, 0, 0, 0);

            /**
             * FragmentAlgorithmResult의 LinearLayout에 추가해준다.
             */
            viewGroup.addView(anyChartView);

            /**
             * 계산해낸 평균 결과 도출은 그래프를 통해 화면 절반에 도식하고
             * 나머지 절반에 도출해낸 수치들을 표시하기 위해
             * LinearLayout을 또 만들어 추가해준다.
             */
            LinearLayout linearLayout = new LinearLayout(viewGroup.getContext());
            linearLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    1
            ));
            linearLayout.setPadding(10, 10, 10, 10);

            viewGroup.addView(linearLayout);

            /**
             * 실제 AnyChart Library를 이용해 꺽은선 그래프를 그리는 부분
             * 자세한 사항들은
             * https://github.com/AnyChart/AnyChart-Android 코드 예제들을 보고 적용할 것.
             */
            Cartesian cartesian = AnyChart.line();

            cartesian.animation(false);

            cartesian.padding(10d, 20d, 5d, 20d);

            cartesian.crosshair().enabled(true);
            cartesian.crosshair()
                    .yLabel(true)
                    .yStroke((Stroke) null, null, null, (String) null, (String) null);

            cartesian.tooltip().positionMode(TooltipPositionMode.POINT);

            cartesian.title(strSelectedAlgorithm);

            cartesian.yAxis(0).title("price");
            cartesian.xAxis(0).labels().padding(5d, 5d, 5d, 5d);

            List<DataEntry> seriesData = new ArrayList<>();

            /**
             * AnyChart graph를 그리는 데 필요한 dataset을 담을
             * Set을 생성하는데 그래프와 굉장히 강력하게 bind 되어 있어
             * 아예 위에서 봤듯이 뷰 전체를 비우는 방식으로 초기화를 하고 있다.
             */
            Set set = Set.instantiate();

            final int BUY = 0;
            final int DAY_1 = 1;
            final int DAY_2 = 2;
            final int DAY_3 = 3;
            final int DAY_4 = 4;

            double avgBuy = 0.0;
            double avgDay1 = 0.0;
            double avgDay2 = 0.0;
            double avgDay3 = 0.0;
            double avgDay4 = 0.0;

            int count = 0;

            /**
             * 조건에 해당하는 결과들의 평균을 구하고
             */
            for (int k = 0; k < size; ++k) {
                ModelTicker.Daily buy = resultList.get(BUY).get(k);
                ModelTicker.Daily day1Daily = resultList.get(DAY_1).get(k);
                ModelTicker.Daily day2Daily = resultList.get(DAY_2).get(k);
                ModelTicker.Daily day3Daily = resultList.get(DAY_3).get(k);
                ModelTicker.Daily day4Daily = resultList.get(DAY_4).get(k);

                if (day4Daily != null) {
                    avgDay4 += day4Daily.getHigh();
                    avgDay3 += day3Daily.getHigh();
                    avgDay2 += day2Daily.getHigh();
                    avgDay1 += day1Daily.getHigh();
                    avgBuy += buy.getHigh();
                } else {
                    count++;
                }
            }

            avgBuy /= size - count;
            avgDay1 /= size - count;
            avgDay2 /= size - count;
            avgDay3 /= size - count;
            avgDay4 /= size - count;

            /**
             * 셋에 데이터를 집어 넣어 그래프를 그릴 준비
             */
            seriesData.add(new AlgorithmDataEntry("buy", avgBuy));
            seriesData.add(new AlgorithmDataEntry(day1 + "d", avgDay1));
            seriesData.add(new AlgorithmDataEntry(day2 + "d", avgDay2));
            seriesData.add(new AlgorithmDataEntry(day3 + "d", avgDay3));
            seriesData.add(new AlgorithmDataEntry(day4 + "d", avgDay4));

            set.data(seriesData);

            /**
             * x : 가로축 값
             * value : 세로축 값
             */
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
            cartesian.legend().fontSize(14d);
            cartesian.legend().padding(0d, 0d, 10d, 0d);

            /**
             * 만들어두었던 anychartview에 그래프를 셋팅
             */
            anyChartView.setChart(cartesian);

            /**
             * 알고리즘 계산 결과를 이제 직접 필요한 view를 만들고 세팅해 도식화 하는 부분.
             */
            TextView textViewOccurrence = new TextView(viewGroup.getContext());
            textViewOccurrence.setText("알고리즘 매칭 횟수 : " + size);
            linearLayout.addView(textViewOccurrence);
        }
    }

    /**
     * 시고저종 그래프를 그리는데 필요한 데이터 셋
     */
    private class OHCLDataEntry extends HighLowDataEntry {
        public OHCLDataEntry(Long x, Double open, Double high, Double low, Double close, Long volume) {
            super(x, high, low);
            setValue("open", open);
            setValue("close", close);
            setValue("volume", volume);
        }
    }

    /**
     * 일반 꺽은선 그래프를 그리는데 필요한 데이터 셋
     */
    private class AlgorithmDataEntry extends ValueDataEntry {
        AlgorithmDataEntry(String x, Double value) {
            super(x, value);
        }
    }
}
