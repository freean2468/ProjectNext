package com.mrhi.projectnext.object;

import android.util.Log;
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
import com.anychart.core.cartesian.series.Column;
import com.anychart.core.cartesian.series.Line;
import com.anychart.core.stock.Plot;
import com.anychart.data.Mapping;
import com.anychart.data.Set;
import com.anychart.data.Table;
import com.anychart.data.TableMapping;
import com.anychart.enums.Anchor;
import com.anychart.enums.HoverMode;
import com.anychart.enums.MarkerType;
import com.anychart.enums.MovingAverageType;
import com.anychart.enums.Position;
import com.anychart.enums.StockSeriesType;
import com.anychart.enums.TooltipPositionMode;
import com.anychart.graphics.vector.Stroke;
import com.mrhi.projectnext.help.Helper;
import com.mrhi.projectnext.model.ModelTicker;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

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

    private ObjectAnyChart() {
    }

    public static ObjectAnyChart getInstance() {
        return instance;
    }

    private int fragmentMainViewId = 0;
    private int fragmentAlgorithmResultViewId = 0;

    /**
     * 시고저종 그래프의 기본형을 그린다.
     * 현재 ObjectAlgorithm에 selectedTicker의 데이터가 있어야 함.
     *
     * @param view           그래프를 담을 Layout
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

        java.util.Set<ModelTicker.Daily> tickerCopy = modelTicker.getCopy();
        Iterator<ModelTicker.Daily> iterator = tickerCopy.iterator();
        List<DataEntry> data = new ArrayList<>();

        while (iterator.hasNext()) {
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
                .yAxis(0).labels().format("{%Value}{scale:(1000)|(k)}");
        ;

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

    public void draw_HIGH_LOW(String strSelectedAlgorithm, ViewGroup viewGroup, LinkedList<Double> valueList)
    {
        //Pooh algorithm
        double minValueAvg = 0.0;
        double maxValueAvg = 0.0;
        double avgGap = 0.0;

        maxValueAvg = valueList.get(0);
        minValueAvg = valueList.get(1);
        avgGap = valueList.get(2);
        /**
         * 뷰그룹을 깨끗이 비운다.
         *
         */
        viewGroup.removeAllViewsInLayout();

        /**
         * 애니차트 라이브러리에 있는 애니 차트 뷰를 불러온다
        */
        AnyChartView anyChartView = new AnyChartView(viewGroup.getContext());

        /**
         * 애니 차트 뷰를 리니어 레이아웃을 새로 생성하여 위치시킨다
         * 내부 여백을 왼쪽에만 10을 주었다.
         * 레이아웃 세팅뒤 뷰를 추가하였다.
        */
        anyChartView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT,
                1));
        anyChartView.setPadding(10, 0, 0, 0);

        viewGroup.addView(anyChartView);

        LinearLayout linearLayout = new LinearLayout(viewGroup.getContext());
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT,
                1
        ));

        linearLayout.setPadding(10,10,10,10);

        viewGroup.addView(linearLayout);


        Cartesian cartesian = AnyChart.column();

        List<DataEntry>data = new ArrayList<>();
        data.add(new ValueDataEntry("최대값 평균",maxValueAvg));
        data.add(new ValueDataEntry("최소값 평균",minValueAvg));

        Column column = cartesian.column(data);

        column.tooltip()
                .titleFormat("{%x}")
                .position(Position.CENTER_BOTTOM)
                .anchor(Anchor.CENTER_BOTTOM)
                .offsetX(0d)
                .offsetY(5d)
                .format("${%Value}{groupSeparator : }");

        cartesian.animation(false);
        cartesian.title("최소값과 최대값 평균");

        cartesian.yScale().minimum(0d);

        cartesian.yAxis(0).labels().format("${%Value}{groupsSeparator: }");

        cartesian.tooltip().positionMode(TooltipPositionMode.POINT);
        cartesian.interactivity().hoverMode(HoverMode.BY_X);

        cartesian.xAxis(0).title("최대값 평균");
        cartesian.yAxis(0).title("최소값 평균");

        anyChartView.setChart(cartesian);

        TextView textView = new TextView(viewGroup.getContext());
        textView.setText("최소값과 최대값 차이의 평균값 : " +avgGap );
        linearLayout.addView(textView);
    }

    public void draw_CLOSE_OPEN(String strSelectedAlgorithm, ViewGroup viewGroup, LinkedList<Double> gapList)
    {
        viewGroup.removeAllViewsInLayout();

        /**
         * 애니차트 라이브러리에 있는 애니 차트 뷰를 불러온다
         */
        AnyChartView anyChartView = new AnyChartView(viewGroup.getContext());

        /**
         * 애니 차트 뷰를 리니어 레이아웃을 새로 생성하여 위치시킨다
         * 내부 여백을 왼쪽에만 10을 주었다.
         * 레이아웃 세팅뒤 뷰를 추가하였다.
         */
        anyChartView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT,
                1));
        anyChartView.setPadding(10, 0, 0, 0);

        viewGroup.addView(anyChartView);

        LinearLayout linearLayout = new LinearLayout(viewGroup.getContext());
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT,
                1
        ));

        linearLayout.setPadding(10,10,10,10);

        viewGroup.addView(linearLayout);

        Cartesian cartesian = AnyChart.column();
        List<DataEntry>data = new ArrayList<>();

        double avg = 0.0;

        for (int i = 0; i < gapList.size(); ++i) {
            avg += gapList.get(i);
        }

        avg /= gapList.size();

        data.add(new ValueDataEntry("차이 평균", avg));

        Column column = cartesian.column(data);

        column.tooltip()
                .titleFormat("{%x}")
                .position(Position.CENTER_BOTTOM)
                .anchor(Anchor.CENTER_BOTTOM)
                .offsetX(0d)
                .offsetY(5d)
                .format("${%Value}{groupSeparator : }");

        cartesian.animation(false);
        cartesian.title("어제 닫힐때와 오늘 열릴때의 가격 비교");

        cartesian.yScale().minimum(0d);

        cartesian.yAxis(0).labels().format("${%Value}{groupsSeparator: }");

        cartesian.tooltip().positionMode(TooltipPositionMode.POINT);
        cartesian.interactivity().hoverMode(HoverMode.BY_X);

        cartesian.xAxis(0).title("최대값 평균");
        cartesian.yAxis(0).title("최소값 평균");

        anyChartView.setChart(cartesian);

        TextView textView = new TextView(viewGroup.getContext());
        textView.setText("장이 닫힐떄 가격과 열릴때 시가와의 차이를 구한다");
        linearLayout.addView(textView);
        TextView textView2 = new TextView(viewGroup.getContext());
        textView2.setText("알고리즘 매칭 횟수 : " + gapList.size());
        linearLayout.addView(textView2);
    }
    
    /**
     * 전일 대비 거래량이 100% 상승했을 때 high에서 매수한 시점에서
     * day1, day2, day3, day4 이후의 가격 평균을 도출하는 알고리즘.
     *
     * @param strSelectedAlgorithm 알고리즘 제목
     * @param viewGroup            알고리즘 결과가 담길 Layout (FragmentAlgorithmResult 안 LinearLayout)
     * @param resultList           알고리즘의 결과물. 이 함수 안에서 사용할 데이터 셋
     * @param day1
     * @param day2
     * @param day3
     * @param day4
     * @author 송훈일 (freean2468@gmail.com)
     */
    public void draw_VOLUME_2_TIMES_INCREASED_MORE_THAN_YESTERDAY(String strSelectedAlgorithm, ViewGroup viewGroup, LinkedList<ArrayList<ModelTicker.Daily>> resultList, int day1, int day2, int day3, int day4) {
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
            linearLayout.setOrientation(LinearLayout.VERTICAL);
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

            TextView textViewEarningRateDay1 = new TextView(viewGroup.getContext());
            textViewEarningRateDay1.setText("매수 시점 기준 " + day1 + "일 후 평균 수익률 : " +
                    Helper.round((avgDay1 - avgBuy) / avgBuy * 100, 2) + "%");
            linearLayout.addView(textViewEarningRateDay1);

            TextView textViewEarningRateDay2 = new TextView(viewGroup.getContext());
            textViewEarningRateDay2.setText("매수 시점 기준 " + day2 + "일 후 평균 수익률 : " +
                    Helper.round((avgDay2 - avgBuy) / avgBuy * 100, 2) + "%");
            linearLayout.addView(textViewEarningRateDay2);

            TextView textViewEarningRateDay3 = new TextView(viewGroup.getContext());
            textViewEarningRateDay3.setText("매수 시점 기준 " + day3 + "일 후 평균 수익률 : " +
                    Helper.round((avgDay3 - avgBuy) / avgBuy * 100, 2) + "%");
            linearLayout.addView(textViewEarningRateDay3);

            TextView textViewEarningRateDay4 = new TextView(viewGroup.getContext());
            textViewEarningRateDay4.setText("매수 시점 기준 " + day4 + "일 후 평균 수익률 : " +
                    Helper.round((avgDay4 - avgBuy) / avgBuy * 100, 2) + "%");
            linearLayout.addView(textViewEarningRateDay4);
        }
    }

    public void draw_BUY_OPEN_SELL_CLOSE(String strSelectedAlgorithm, ViewGroup viewGroup, ArrayList<ModelTicker.Daily> resultList) {
        /**
         * 기존에 있던 view들을 모두 정리하고 새하얀 도화지로 만든다.
         */
        viewGroup.removeAllViewsInLayout();
        String name = "Open | Close";

        /**
         * 알고리즘 매칭되는 부분이 없을 수도 있다.
         */
        int size = resultList.size();

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
        linearLayout.setOrientation(LinearLayout.VERTICAL);
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

        double avgOpen = 0.0;
        double avgClose = 0.0;
        int greaterOpenCount = 0;
        int greaterCloseCount = 0;

        /**
         * 조건에 해당하는 결과들의 평균을 구하고
         */
        for (int k = 0; k < size; ++k) {
            ModelTicker.Daily daily = resultList.get(k);

            avgOpen += daily.getOpen();
            avgClose += daily.getClose();

            if (daily.getOpen() >= daily.getClose()) {
                greaterOpenCount++;
            } else {
                greaterCloseCount++;
            }
        }

        avgOpen /= size;
        avgClose /= size;

        /**
         * 셋에 데이터를 집어 넣어 그래프를 그릴 준비
         */
        seriesData.add(new AlgorithmDataEntry("open", avgOpen));
        seriesData.add(new AlgorithmDataEntry("close", avgClose));

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
        textViewOccurrence.setText(size + "일 중");
        linearLayout.addView(textViewOccurrence);

        TextView textView2 = new TextView(viewGroup.getContext());
        textView2.setText("open이 더 높은 일 수 : " + greaterOpenCount + ", close가 더 높은 일 수 : " +
                greaterCloseCount);
        linearLayout.addView(textView2);

        TextView textView3 = new TextView(viewGroup.getContext());
        textView3.setText("수익률 : " + Helper.round((avgClose - avgOpen) / avgOpen, 2) + "%");
        linearLayout.addView(textView3);

        TextView textView4 = new TextView(viewGroup.getContext());
        textView4.setText("승률 : " + Helper.round((double) greaterOpenCount / size * 100, 2) + "%");
        linearLayout.addView(textView4);
    }

    /**
     * 14일 동안 종가의 가격상승이 이루어진 날이 8일 이상일 때,
     * 기준일에 매수하여 14일 후에 매도할 때의 수익률
     *
     * @author 허선영
     */
    public void draw_SEVERAL_DAYS_INCREASE_OUT_OF_2_WEEKS(String strSelectedAlgorithm, ViewGroup viewGroup, LinkedList<ArrayList<ModelTicker.Daily>> resultList) {
        //기존에 있던 뷰 정리
        viewGroup.removeAllViewsInLayout();
        String name = "firstDay, high" + " | " + "lastDay, high";

        //알고리즘에 매칭되는 부분이 없을 수 있음
        if (resultList.get(0).size() > 0) {
            int size = resultList.get(0).size();

            //새로운 anyChart생성 부분
            AnyChartView anyChartView = new AnyChartView(viewGroup.getContext());
            //layout 설정
            anyChartView.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    1));
            anyChartView.setPadding(10, 0, 0, 0);


            //FragmentAlgorithmResult의 LinearLayout에 추가해준다.
            viewGroup.addView(anyChartView);

            //계산해낸 평균 결과 도출은 그래프, 수치표현을 위해 추가로 LinearLayout 생성
            LinearLayout linearLayout = new LinearLayout(viewGroup.getContext());
            linearLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    1
            ));
            linearLayout.setPadding(10, 10, 10, 10);

            viewGroup.addView(linearLayout);

            //////////////////////////////////////////////////////////////////////////////////////

            //실제 AnyChart Library를 이용해 꺽은선 그래프를 그리는 부분
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

            /////////////////////////////////////////////////////////////////////////////////////

            /**
             * AnyChart graph를 그리는 데 필요한 dataset을 담을
             * Set을 생성하는데 그래프와 굉장히 강력하게 bind 되어 있어
             * 아예 위에서 봤듯이 뷰 전체를 비우는 방식으로 초기화를 하고 있다.
             */
            Set set = Set.instantiate();

            final int BUY = 0;
            final int SELL = 1;

            double avgBuy = 0.0;
            double avgSell = 0.0;

            //조건에 해당하는 평균들
            for (int k = 0; k < size; ++k) {
                ModelTicker.Daily buy = resultList.get(BUY).get(k);
                ModelTicker.Daily sell = resultList.get(SELL).get(k);

                if (sell != null) {
                    avgBuy += buy.getHigh();
                    avgSell += sell.getHigh();
                }
            }//end of for

            avgBuy /= size;
            avgSell /= size;

            //셋에 데이터를 집어 넣어 그래프를 그릴 준비
            seriesData.add(new AlgorithmDataEntry("buy", avgBuy));
            seriesData.add(new AlgorithmDataEntry("sell", avgSell));

            set.data(seriesData);

            //x: 가로축, value: 세로축
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

            //만들어두었던 anychartview에 그래프를 셋팅
            anyChartView.setChart(cartesian);

            //알고리즘 계산 결과를 이제 직접 필요한 view를 만들고 세팅해 도식화 하는 부분.
            TextView textViewOccurrence = new TextView(viewGroup.getContext());
            textViewOccurrence.setText("알고리즘 매칭 횟수 : " + size);
            linearLayout.addView(textViewOccurrence);

        }//end of if exsist resultList

    }//end of draw14days

    /**
     * 떨어지는 주가가 다시 상한가로 전환되는데까지 걸리는 평균 일수
     *
     * @author 허선영
     */
    public void draw_RECOVERING(String strSelectedAlgorithm, ViewGroup viewGroup, LinkedList<ArrayList<ModelTicker.Daily>> resultList) {
        //기존에 있던 뷰 정리
        viewGroup.removeAllViewsInLayout();
        String name = "sliding close | recovered close";

        //알고리즘에 매칭되는 부분이 없을 수 있음
        if (resultList.get(0).size() > 0) {
            int size = resultList.get(0).size();

            //새로운 anyChart생성 부분
            AnyChartView anyChartView = new AnyChartView(viewGroup.getContext());
            //layout 설정
            anyChartView.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    1));
            anyChartView.setPadding(10, 0, 0, 0);

            //FragmentAlgorithmResult의 LinearLayout에 추가해준다.
            viewGroup.addView(anyChartView);

            //계산해낸 평균 결과 도출은 그래프, 수치표현을 위해 추가로 LinearLayout 생성
            LinearLayout linearLayout = new LinearLayout(viewGroup.getContext());
            linearLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    1
            ));
            linearLayout.setPadding(10, 10, 10, 10);

            viewGroup.addView(linearLayout);

            //////////////////////////////////////////////////////////////////////////////////////

            //실제 AnyChart Library를 이용해 꺽은선 그래프를 그리는 부분
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

            /////////////////////////////////////////////////////////////////////////////////////

            /**
             * AnyChart graph를 그리는 데 필요한 dataset을 담을
             * Set을 생성하는데 그래프와 굉장히 강력하게 bind 되어 있어
             * 아예 위에서 봤듯이 뷰 전체를 비우는 방식으로 초기화를 하고 있다.
             */
            Set set = Set.instantiate();

            final int SLIDE = 0;
            final int RECOVER = 1;

            double avgSlide = 0.0;
            double avgRecover = 0.0;
            long avgDate = 0;

            //조건에 해당하는 평균들
            for (int k = 0; k < size; ++k) {
                ModelTicker.Daily slide = resultList.get(SLIDE).get(k);
                ModelTicker.Daily recover = resultList.get(RECOVER).get(k);

                if (recover != null) {
                    avgSlide += slide.getClose();
                    avgRecover += recover.getClose();

                    /////////////////////////////////////////////////////////////////////////
                    // 날짜를 시간으로 변환

                    long diffInMillies = Math.abs(recover.getDate().getTime() - slide.getDate().getTime());
                    long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);

//                    Log.d("debug", "걸린 일 수 : " + diff);

                    avgDate += diff;
                }
            }//end of for

            avgSlide /= size;
            avgRecover /= size;
            avgDate /= size;

            //set에 데이터를 집어 넣어 그래프를 그릴 준비
            seriesData.add(new AlgorithmDataEntry("slide", Helper.round(avgSlide, 2)));
            seriesData.add(new AlgorithmDataEntry("recover", Helper.round(avgRecover, 2)));

            set.data(seriesData);

            //x: 가로축, value: 세로축
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

            //만들어두었던 anychartview에 그래프를 셋팅
            anyChartView.setChart(cartesian);

            //알고리즘 계산 결과를 이제 직접 필요한 view를 만들고 세팅해 도식화 하는 부분.
            TextView textViewOccurrence = new TextView(viewGroup.getContext());
            textViewOccurrence.setText("알고리즘 매칭 횟수 : " + size + "   평균 소요 시간: " + avgDate + "일");
            linearLayout.addView(textViewOccurrence);

        }//end of if exsist resultList

    }//end of algorithmSurgeDate

    /**
     * 14일 동안 종가의 가격과 거래량 상승이 이루어진 날이 8일 이상일 때,
     * 기준일에 매수하여 14일 후에 매도할 때의 수익률
     *
     * @author 허선영
     */
    public void draw_SEVERAL_DAYS_INCREASE_OUT_OF_2_WEEKS_AS_WELL_AS_VOLUME(String strSelectedAlgorithm, ViewGroup viewGroup, LinkedList<ArrayList<ModelTicker.Daily>> resultList) {
        //기존에 있던 뷰 정리
        viewGroup.removeAllViewsInLayout();
        String name = "firstDay, high" + " | " + "lastDay, high";

        //알고리즘에 매칭되는 부분이 없을 수 있음
        if (resultList.get(0).size() > 0) {
            int size = resultList.get(0).size();

            //새로운 anyChart생성 부분
            AnyChartView anyChartView = new AnyChartView(viewGroup.getContext());
            //layout 설정
            anyChartView.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    1));
            anyChartView.setPadding(10, 0, 0, 0);


            //FragmentAlgorithmResult의 LinearLayout에 추가해준다.
            viewGroup.addView(anyChartView);

            //계산해낸 평균 결과 도출은 그래프, 수치표현을 위해 추가로 LinearLayout 생성
            LinearLayout linearLayout = new LinearLayout(viewGroup.getContext());
            linearLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    1
            ));
            linearLayout.setPadding(10, 10, 10, 10);

            viewGroup.addView(linearLayout);

            //////////////////////////////////////////////////////////////////////////////////////

            //실제 AnyChart Library를 이용해 꺽은선 그래프를 그리는 부분
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

            /////////////////////////////////////////////////////////////////////////////////////

            /**
             * AnyChart graph를 그리는 데 필요한 dataset을 담을
             * Set을 생성하는데 그래프와 굉장히 강력하게 bind 되어 있어
             * 아예 위에서 봤듯이 뷰 전체를 비우는 방식으로 초기화를 하고 있다.
             */
            Set set = Set.instantiate();

            final int BUY = 0;
            final int SELL = 1;

            double avgBuy = 0.0;
            double avgSell = 0.0;

            //조건에 해당하는 평균들
            for (int k = 0; k < size; ++k) {
                ModelTicker.Daily buy = resultList.get(BUY).get(k);
                ModelTicker.Daily sell = resultList.get(SELL).get(k);

                if (sell != null) {
                    avgBuy += buy.getHigh();
                    avgSell += sell.getHigh();
                }
            }//end of for

            avgBuy /= size;
            avgSell /= size;

            //셋에 데이터를 집어 넣어 그래프를 그릴 준비
            seriesData.add(new AlgorithmDataEntry("buy", avgBuy));
            seriesData.add(new AlgorithmDataEntry("sell", avgSell));

            set.data(seriesData);

            //x: 가로축, value: 세로축
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

            //만들어두었던 anychartview에 그래프를 셋팅
            anyChartView.setChart(cartesian);

            //알고리즘 계산 결과를 이제 직접 필요한 view를 만들고 세팅해 도식화 하는 부분.
            TextView textViewOccurrence = new TextView(viewGroup.getContext());
            textViewOccurrence.setText("알고리즘 매칭 횟수 : " + size);
            linearLayout.addView(textViewOccurrence);

        }//end of if exsist resultList

    }//end of draw14DaysAndVolume

    /**
     * 저가로 매수 했을 시 그날, 다음날, 일주일, 한달, 6개월 후의 종가에 매도했을 때의 수익률
     *
     * @author 허선영
     */
    public void draw_BUY_LOW(String strSelectedAlgorithm, ViewGroup viewGroup, LinkedList<ArrayList<ModelTicker.Daily>> resultList, int day0, int day1, int day2, int day3, int day4) {
        //기존에 있던 뷰 정리
        viewGroup.removeAllViewsInLayout();
        String name = String.valueOf(day0) + " | " + String.valueOf(day1) + " | " + String.valueOf(day2) + " | " + String.valueOf(day3) + " | " + String.valueOf(day4);

        //알고리즘에 매칭되는 부분이 없을 수 있음
        if (resultList.get(0).size() > 0) {
            int size = resultList.get(0).size();

            //새로운 anyChart생성 부분
            AnyChartView anyChartView = new AnyChartView(viewGroup.getContext());
            //layout 설정
            anyChartView.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    1));
            anyChartView.setPadding(10, 0, 0, 0);


            //FragmentAlgorithmResult의 LinearLayout에 추가해준다.
            viewGroup.addView(anyChartView);

            //계산해낸 평균 결과 도출은 그래프, 수치표현을 위해 추가로 LinearLayout 생성
            LinearLayout linearLayout = new LinearLayout(viewGroup.getContext());
            linearLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    1
            ));
            linearLayout.setPadding(10, 10, 10, 10);
            linearLayout.setOrientation(LinearLayout.VERTICAL);

            viewGroup.addView(linearLayout);

            //////////////////////////////////////////////////////////////////////////////////////

            //실제 AnyChart Library를 이용해 꺽은선 그래프를 그리는 부분
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

            /////////////////////////////////////////////////////////////////////////////////////

            /**
             * AnyChart graph를 그리는 데 필요한 dataset을 담을
             * Set을 생성하는데 그래프와 굉장히 강력하게 bind 되어 있어
             * 위에서 봤듯이 아예 뷰 전체를 비우는 방식으로 초기화를 하고 있다.
             */
            Set set = Set.instantiate();

            final int BUY = 0;
            final int DAY_1 = 1;
            final int DAY_2 = 2;
            final int DAY_3 = 3;
            final int DAY_4 = 4;
            final int DAY_5 = 4;

            double avgBuy = 0.0;
            double avgDay1 = 0.0;
            double avgDay2 = 0.0;
            double avgDay3 = 0.0;
            double avgDay4 = 0.0;
            double avgDay5 = 0.0;

            int count = 0;

            //조건에 해당하는 평균들
            for (int k = 0; k < size; ++k) {
                ModelTicker.Daily buy = resultList.get(BUY).get(k);
                ModelTicker.Daily day1Daily = resultList.get(DAY_1).get(k);
                ModelTicker.Daily day2Daily = resultList.get(DAY_2).get(k);
                ModelTicker.Daily day3Daily = resultList.get(DAY_3).get(k);
                ModelTicker.Daily day4Daily = resultList.get(DAY_4).get(k);
                ModelTicker.Daily day5Daily = resultList.get(DAY_5).get(k);

                if (day5Daily != null) {
                    avgDay5 += day5Daily.getClose();
                    avgDay4 += day4Daily.getClose();
                    avgDay3 += day3Daily.getClose();
                    avgDay2 += day2Daily.getClose();
                    avgDay1 += day1Daily.getClose();
                    avgBuy += buy.getLow();
                } else {
                    count++;
                }
            }//end of for

            avgBuy /= size - count;
            avgDay1 /= size - count;
            avgDay2 /= size - count;
            avgDay3 /= size - count;
            avgDay4 /= size - count;
            avgDay5 /= size - count;

            //set에 데이터를 집어 넣어 그래프를 그릴 준비
            seriesData.add(new AlgorithmDataEntry("buy", avgBuy));
            seriesData.add(new AlgorithmDataEntry(day0 + "d", avgDay1));
            seriesData.add(new AlgorithmDataEntry(day1 + "d", avgDay2));
            seriesData.add(new AlgorithmDataEntry(day2 + "d", avgDay3));
            seriesData.add(new AlgorithmDataEntry(day3 + "d", avgDay4));
            seriesData.add(new AlgorithmDataEntry(day4 + "d", avgDay5));

            set.data(seriesData);

            //x: 가로축, value: 세로축
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

            //만들어두었던 anychartview에 그래프를 셋팅
            anyChartView.setChart(cartesian);

            //알고리즘 계산 결과를 이제 직접 필요한 view를 만들고 세팅해 도식화
            TextView textViewOccurrence = new TextView(viewGroup.getContext());
            textViewOccurrence.setText("알고리즘 매칭 횟수 : " + size);
            linearLayout.addView(textViewOccurrence);

            TextView textViewEarningRateDay1 = new TextView(viewGroup.getContext());
            textViewEarningRateDay1.setText("매수 시점 기준 " + day0 + "일 후 평균 수익률 : " +
                    Helper.round((avgDay1 - avgBuy) / avgBuy * 100, 2) + "%");
            linearLayout.addView(textViewEarningRateDay1);

            TextView textViewEarningRateDay2 = new TextView(viewGroup.getContext());
            textViewEarningRateDay2.setText("매수 시점 기준 " + day1 + "일 후 평균 수익률 : " +
                    Helper.round((avgDay2 - avgBuy) / avgBuy * 100, 2) + "%");
            linearLayout.addView(textViewEarningRateDay2);

            TextView textViewEarningRateDay3 = new TextView(viewGroup.getContext());
            textViewEarningRateDay3.setText("매수 시점 기준 " + day2 + "일 후 평균 수익률 : " +
                    Helper.round((avgDay3 - avgBuy) / avgBuy * 100, 2) + "%");
            linearLayout.addView(textViewEarningRateDay3);

            TextView textViewEarningRateDay4 = new TextView(viewGroup.getContext());
            textViewEarningRateDay4.setText("매수 시점 기준 " + day3 + "일 후 평균 수익률 : " +
                    Helper.round((avgDay4 - avgBuy) / avgBuy * 100, 2) + "%");
            linearLayout.addView(textViewEarningRateDay4);

            TextView textViewEarningRateDay5 = new TextView(viewGroup.getContext());
            textViewEarningRateDay5.setText("매수 시점 기준 " + day4 + "일 후 평균 수익률 : " +
                    Helper.round((avgDay5 - avgBuy) / avgBuy * 100, 2) + "%");
            linearLayout.addView(textViewEarningRateDay5);

        }//end of if exsist resultList

    }//end of drawBuyLow

    /**
     * close가 decreaseDay(현재는 3일)일 연속으로 하락 후 매수 시,
     * 다음날, 일주일 후, 한달 후, 6개월 후의 close에 대한 수익률
     *
     * @author 허선영
     */
    public void draw_BUY_CLOSE_WHEN_DECREASED_SEVERAL_DAYS(String strSelectedAlgorithm, ViewGroup viewGroup, LinkedList<ArrayList<ModelTicker.Daily>> resultList, int day1, int day2, int day3, int day4, int decreaseDays) {
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
            linearLayout.setOrientation(LinearLayout.VERTICAL);
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
                    avgDay4 += day4Daily.getClose();
                    avgDay3 += day3Daily.getClose();
                    avgDay2 += day2Daily.getClose();
                    avgDay1 += day1Daily.getClose();
                    avgBuy += buy.getClose();
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
             * 알고리즘 계산 결과를 이제 직접 필요한 view를 만들고 세팅해 도식화
             */
            TextView textViewOccurrence = new TextView(viewGroup.getContext());
            textViewOccurrence.setText("알고리즘 매칭 횟수 : " + size);
            linearLayout.addView(textViewOccurrence);

            TextView textViewEarningRateDay1 = new TextView(viewGroup.getContext());
            textViewEarningRateDay1.setText("매수 시점 기준 " + day1 + "일 후 평균 수익률 : " +
                    Helper.round((avgDay1 - avgBuy) / avgBuy * 100, 2) + "%");
            linearLayout.addView(textViewEarningRateDay1);

            TextView textViewEarningRateDay2 = new TextView(viewGroup.getContext());
            textViewEarningRateDay2.setText("매수 시점 기준 " + day2 + "일 후 평균 수익률 : " +
                    Helper.round((avgDay2 - avgBuy) / avgBuy * 100, 2) + "%");
            linearLayout.addView(textViewEarningRateDay2);

            TextView textViewEarningRateDay3 = new TextView(viewGroup.getContext());
            textViewEarningRateDay3.setText("매수 시점 기준 " + day3 + "일 후 평균 수익률 : " +
                    Helper.round((avgDay3 - avgBuy) / avgBuy * 100, 2) + "%");
            linearLayout.addView(textViewEarningRateDay3);

            TextView textViewEarningRateDay4 = new TextView(viewGroup.getContext());
            textViewEarningRateDay4.setText("매수 시점 기준 " + day4 + "일 후 평균 수익률 : " +
                    Helper.round((avgDay4 - avgBuy) / avgBuy * 100, 2) + "%");
            linearLayout.addView(textViewEarningRateDay4);
        } else {
            Log.d("debug", "no matching");
        }

    }//end of VolumeAndPriceDecrease5days

    /**
     * open, high, low, close의 하루 변동률의 평균
     *
     * @author 허선영
     */
    public void draw_FLUCTUATION_RATE_ONE_DAY(String strSelectedAlgorithm, String strSelectedTicker, ViewGroup viewGroup, List<ModelTicker.Daily> dailyList) {
        /**
         * 기존에 있던 view들을 모두 정리하고 새하얀 도화지로 만든다.
         */
        viewGroup.removeAllViewsInLayout();
        String name = "open" + " | " + "high" + " | " + "low" + " | " + "close";

        /**
         * 알고리즘 매칭되는 부분이 없을 수도 있다.
         */
        if (dailyList.size() > 0) {
            int size = dailyList.size();

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
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            linearLayout.setPadding(10, 10, 10, 10);

            viewGroup.addView(linearLayout);

            /**
             * AnyChart Library를 이용해서 막대 그래프를 그리는 부분
             */
            Cartesian cartesian = AnyChart.column();

            List<DataEntry> seriesData = new ArrayList<>();

            double fluctuationRateOpen = 0.0;
            double fluctuationRateHigh = 0.0;
            double fluctuationRateLow = 0.0;
            double fluctuationRateClose = 0.0;

            double avgOpen = 0.0;
            double avgHigh = 0.0;
            double avgLow = 0.0;
            double avgClose = 0.0;

            ModelTicker.Daily yesterday = null;

            //(today-yesterday)/(yesterday)*100
            for (int i = 0; i < dailyList.size(); ++i) {
                ModelTicker.Daily today = dailyList.get(i);
                if (yesterday != null) {
                    fluctuationRateOpen = (today.getOpen() - yesterday.getOpen()) / yesterday.getOpen() * 100;
                    fluctuationRateHigh = (today.getHigh() - yesterday.getHigh()) / yesterday.getHigh() * 100;
                    fluctuationRateLow = (today.getLow() - yesterday.getLow()) / yesterday.getLow() * 100;
                    fluctuationRateClose = (today.getClose() - yesterday.getClose()) / yesterday.getClose() * 100;

                    avgOpen += fluctuationRateOpen;
                    avgHigh += fluctuationRateHigh;
                    avgLow += fluctuationRateLow;
                    avgClose += fluctuationRateClose;
                }//end of yesterday
                yesterday = today;
            }//end of totalFor

            avgOpen /= size;
            avgHigh /= size;
            avgLow /= size;
            avgClose /= size;

            //set에 데이터를 집어 넣어 그래프를 그릴 준비
            seriesData.add(new AlgorithmDataEntry("open", Helper.round(avgOpen, 2)));
            seriesData.add(new AlgorithmDataEntry("high", Helper.round(avgHigh, 2)));
            seriesData.add(new AlgorithmDataEntry("low", Helper.round(avgLow, 2)));
            seriesData.add(new AlgorithmDataEntry("close", Helper.round(avgClose, 2)));

            Column column = cartesian.column(seriesData); //데이터 자리

            column.tooltip()
                    .title(strSelectedTicker)
                    .titleFormat("{%X}")
                    .position(Position.CENTER_BOTTOM)
                    .anchor(Anchor.CENTER_BOTTOM)
                    .offsetX(0d)
                    .offsetY(5d)
                    .format("${%Value}{groupsSeparator: }");

            cartesian.animation(false);

            cartesian.title(strSelectedAlgorithm);

            cartesian.padding(10d, 20d, 5d, 20d);

            cartesian.yScale().minimum(0d);

            cartesian.yAxis(0).labels().format("${%Value}{groupsSeparator: }");

            cartesian.tooltip().positionMode(TooltipPositionMode.POINT);
            cartesian.interactivity().hoverMode(HoverMode.BY_X);

            cartesian.yAxis(0).title("price");
            cartesian.xAxis(0).labels().padding(5d, 5d, 5d, 5d);

            anyChartView.setChart(cartesian);

            //알고리즘 계산 결과를 이제 직접 필요한 view를 만들고 세팅해 도식화 하는 부분.
            TextView textViewOccurrence = new TextView(viewGroup.getContext());
            textViewOccurrence.setText("알고리즘 매칭 횟수 : " + size);
            linearLayout.addView(textViewOccurrence);

        }//end of if

    }//end of OHLC

    /**
     * 기준일의 종가 대비 다음날, 일주일, 2주, 한달 후 종가의 변동률
     *
     * @author 허선영
     *
     * @param strSelectedAlgorithm
     * @param strSelectedTicker
     * @param viewGroup
     * @param resultList     *
     */
    public void draw_FLUCTUATION_RATE_SEVERAL_DAYS(String strSelectedAlgorithm, String strSelectedTicker, ViewGroup viewGroup, LinkedList<ArrayList<ModelTicker.Daily>> resultList, int day1, int day2, int day3, int day4) {
        /**
         * 기존에 있던 view들을 모두 정리하고 새하얀 도화지로 만든다.
         */
        viewGroup.removeAllViewsInLayout();
        String name = "open" + " | " + "high" + " | " + "low" + " | " + "close";

        /**
         * 알고리즘 매칭되는 부분이 없을 수도 있다.
         */
        if (resultList.size() > 0) {
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
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            linearLayout.setPadding(10, 10, 10, 10);

            viewGroup.addView(linearLayout);

            /**
             * AnyChart Library를 이용해서 막대 그래프를 그리는 부분
             */
            Cartesian cartesian = AnyChart.column();

            List<DataEntry> seriesData = new ArrayList<>();

            double fluctuationRateClose1 = 0.0;
            double fluctuationRateClose2 = 0.0;
            double fluctuationRateClose3 = 0.0;
            double fluctuationRateClose4 = 0.0;

            double totalClose1 = 0.0;
            double totalClose2 = 0.0;
            double totalClose3 = 0.0;
            double totalClose4 = 0.0;

            double avgClose1 = 0.0;
            double avgClose2 = 0.0;
            double avgClose3 = 0.0;
            double avgClose4 = 0.0;

            final int DAY0 = 0;
            final int DAY1 = 1;
            final int DAY2 = 2;
            final int DAY3 = 3;
            final int DAY4 = 4;

            int size = resultList.get(DAY0).size();

            //(today-yesterday)/(yesterday)*100
            for (int i = 0; i < resultList.get(DAY0).size(); ++i) {
                ModelTicker.Daily daily0 = resultList.get(DAY0).get(i);
                ModelTicker.Daily daily1 = resultList.get(DAY1).get(i);
                ModelTicker.Daily daily2 = resultList.get(DAY2).get(i);
                ModelTicker.Daily daily3 = resultList.get(DAY3).get(i);
                ModelTicker.Daily daily4 = resultList.get(DAY4).get(i);

                fluctuationRateClose1 = (daily1.getClose() - daily0.getClose()) / daily0.getClose() * 100;
                fluctuationRateClose2 = (daily2.getClose() - daily0.getClose()) / daily0.getClose() * 100;
                fluctuationRateClose3 = (daily3.getClose() - daily0.getClose()) / daily0.getClose() * 100;
                fluctuationRateClose4 = (daily4.getClose() - daily0.getClose()) / daily0.getClose() * 100;

                totalClose1 += fluctuationRateClose1;
                totalClose2 += fluctuationRateClose2;
                totalClose3 += fluctuationRateClose3;
                totalClose4 += fluctuationRateClose4;
            }//end of totalFor

            avgClose1 = totalClose1 / size;
            avgClose2 = totalClose2 / size;
            avgClose3 = totalClose3 / size;
            avgClose4 = totalClose4 / size;

            /**
             * set에 데이터를 집어 넣어 그래프를 그릴 준비
             */
            seriesData.add(new AlgorithmDataEntry(String.valueOf(day1) + "d", Helper.round(avgClose1, 2)));
            seriesData.add(new AlgorithmDataEntry(String.valueOf(day2) + "d", Helper.round(avgClose2, 2)));
            seriesData.add(new AlgorithmDataEntry(String.valueOf(day3) + "d", Helper.round(avgClose3, 2)));
            seriesData.add(new AlgorithmDataEntry(String.valueOf(day4) + "d", Helper.round(avgClose4, 2)));

            Column column = cartesian.column(seriesData); //데이터 자리

            column.tooltip()
                    .title(strSelectedTicker)
                    .titleFormat("{%X}")
                    .position(Position.CENTER_BOTTOM)
                    .anchor(Anchor.CENTER_BOTTOM)
                    .offsetX(0d)
                    .offsetY(5d)
                    .format("{%Value}{groupsSeparator: }%");

            cartesian.animation(false);

            cartesian.title(strSelectedAlgorithm);

            cartesian.padding(10d, 20d, 5d, 20d);

            cartesian.yAxis(0).labels().format("{%Value}{groupsSeparator: }%");

            cartesian.tooltip().positionMode(TooltipPositionMode.POINT);
            cartesian.interactivity().hoverMode(HoverMode.BY_X);

            cartesian.yAxis(0).title("price");
            cartesian.xAxis(0).labels().padding(5d, 5d, 5d, 5d);

            anyChartView.setChart(cartesian);

            //알고리즘 계산 결과를 이제 직접 필요한 view를 만들고 세팅해 도식화 하는 부분.
            TextView textViewOccurrence = new TextView(viewGroup.getContext());
            textViewOccurrence.setText("알고리즘 매칭 횟수 : " + resultList.get(DAY0).size());
            linearLayout.addView(textViewOccurrence);
        }//end of if

    }//end of CloseFluctuationRate

    /**
     * 전날 대비 종가가 이틀 연속 하락했을 때, 3일째에 상승할 확률
     *
     * @author 허선영
     *
     * @param strSelectedAlgorithm
     * @param strSelectedTicker
     * @param viewGroup
     */
    public void draw_PROBABILITY_CONTINUITY_2DAYS_LOSE(String strSelectedAlgorithm, String strSelectedTicker, ViewGroup viewGroup, LinkedList<ArrayList<ModelTicker.Daily>> resultList, int CELL_DATE) {
        /**
         * 기존에 있던 view들을 모두 정리하고 새하얀 도화지로 만든다.
         */
        viewGroup.removeAllViewsInLayout();
        String name = "2일 연속 종가 하락 후, 3일째에 상승할 확률";

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
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(10, 10, 10, 10);

        viewGroup.addView(linearLayout);

        /**
         * AnyChart Library를 이용해서 막대 그래프를 그리는 부분
         */
        Cartesian cartesian = AnyChart.column();

        List<DataEntry> seriesData = new ArrayList<>();

        final int TWO_DAY = 0;
        final int THREE_DAYS_OR_MORE = 1;

        int twoDaySize = resultList.get(TWO_DAY).size();
        int threeDaySize = resultList.get(THREE_DAYS_OR_MORE).size();

        double increaseProbility = (double)twoDaySize / ( (double)twoDaySize + (double)threeDaySize ) * 100;

        /**
         * set에 데이터를 집어 넣어 그래프를 그릴 준비
         */
        seriesData.add(new AlgorithmDataEntry("day", Helper.round(increaseProbility, 2)));

        Column column = cartesian.column(seriesData); //데이터 자리

        column.tooltip()
                .title(strSelectedTicker)
                .titleFormat("{%X}")
                .position(Position.CENTER_BOTTOM)
                .anchor(Anchor.CENTER_BOTTOM)
                .offsetX(0d)
                .offsetY(5d)
                .format("{%Value}{groupsSeparator: }%");

        cartesian.animation(false);

        cartesian.title(strSelectedAlgorithm);

        cartesian.padding(10d, 20d, 5d, 20d);

        cartesian.yAxis(0).labels().format("{%Value}{groupsSeparator: }%");

        cartesian.tooltip().positionMode(TooltipPositionMode.POINT);
        cartesian.interactivity().hoverMode(HoverMode.BY_X);

        cartesian.yAxis(0).title("probility");
        cartesian.xAxis(0).labels().padding(5d, 5d, 5d, 5d);

        anyChartView.setChart(cartesian);

        //알고리즘 계산 결과를 이제 직접 필요한 view를 만들고 세팅해 도식화 하는 부분
        TextView textViewOccurrence = new TextView(viewGroup.getContext());
        textViewOccurrence.setText("알고리즘 매칭 횟수 : " + resultList.get(TWO_DAY).size());
        linearLayout.addView(textViewOccurrence);

    }//end of probabilityContinuity2daysLose

    /**
     * 나스닥과 선택한 종목과의 하루 데이터 결합성 percentage
     *
     * @author 허선영
     *
     * @param strSelectedAlgorithm
     * @param strSelectedTicker
     * @param viewGroup
     * @param dailyList
     */
    public void draw_NADAQ_CORRELATION(String strSelectedAlgorithm, String strSelectedTicker, ViewGroup viewGroup, List<ModelTicker.Daily> dailyList) {
        ModelTicker nasdaq = ObjectAlgorithm.getInstance().getTicker("^IXIC");

        java.util.Set<ModelTicker.Daily> dailySet = nasdaq.getCopy();
        List<ModelTicker.Daily> dailyListNasdaq = new ArrayList<>();
        dailyListNasdaq.addAll(dailySet);

        /**
         * 기존에 있던 view들을 모두 정리하고 새하얀 도화지로 만든다.
         */
        viewGroup.removeAllViewsInLayout();
        String name = "나스닥과 선택 종목과의 관계성";

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
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(10, 10, 10, 10);

        viewGroup.addView(linearLayout);

        /**
         * AnyChart Library를 이용해서 막대 그래프를 그리는 부분
         */
        Cartesian cartesian = AnyChart.column();

        List<DataEntry> seriesData = new ArrayList<>();

        //상관계수
        double correlation = 0.0;

        int count = 0;
        for(int i=0;i<dailyList.size();i++){
            double dailyResult = dailyList.get(i).getClose() - dailyList.get(i).getOpen();

            double dailyResultNasdaq = dailyListNasdaq.get(i).getClose() - dailyListNasdaq.get(i).getOpen();

            if( dailyResult >= 0 && dailyResultNasdaq >= 0){
                count ++;
            }else if (dailyResult <= 0 && dailyResultNasdaq <= 0){
                count++;
            }

        }

        correlation = (double) (count) / dailyList.size() * 100;


        /**
         * set에 데이터를 집어 넣어 그래프를 그릴 준비
         */
        seriesData.add(new AlgorithmDataEntry("day", Helper.round(correlation, 2)));

        Column column = cartesian.column(seriesData); //데이터 자리

        column.tooltip()
                .title(strSelectedTicker)
                .titleFormat("{%X}")
                .position(Position.CENTER_BOTTOM)
                .anchor(Anchor.CENTER_BOTTOM)
                .offsetX(0d)
                .offsetY(5d)
                .format("{%Value}{groupsSeparator: }%");

        cartesian.animation(false);

        cartesian.title(strSelectedAlgorithm);

        cartesian.padding(10d, 20d, 5d, 20d);

        cartesian.yAxis(0).labels().format("{%Value}{groupsSeparator: }%");

        cartesian.tooltip().positionMode(TooltipPositionMode.POINT);
        cartesian.interactivity().hoverMode(HoverMode.BY_X);

        cartesian.yAxis(0).title("probility");
        cartesian.xAxis(0).labels().padding(5d, 5d, 5d, 5d);

        anyChartView.setChart(cartesian);

        //알고리즘 계산 결과를 이제 직접 필요한 view를 만들고 세팅해 도식화 하는 부분
        TextView textViewOccurrence = new TextView(viewGroup.getContext());
        textViewOccurrence.setText("알고리즘 매칭 횟수 : " + dailyList.size());
        linearLayout.addView(textViewOccurrence);

    }//end of NADAQ_JOIN

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
