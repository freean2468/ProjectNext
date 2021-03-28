package com.mrhi.projectnext.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.charts.Stock;
import com.anychart.core.stock.Plot;
import com.anychart.core.stock.series.Line;
import com.anychart.core.stock.series.OHLC;
import com.anychart.data.Table;
import com.anychart.enums.MovingAverageType;
import com.mrhi.projectnext.R;
import com.mrhi.projectnext.model.ModelTicker;
import com.mrhi.projectnext.object.ObjectAlgorithm;
import com.mrhi.projectnext.object.ObjectVolley;

import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.HighLowDataEntry;
import com.anychart.data.TableMapping;
import com.anychart.enums.StockSeriesType;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private TextView textViewHost;
    private Switch switchChangeIp;
    private Spinner spinnerDropdownMenu;
    private Spinner spinnerTickers;
    private Button buttonSelect;
    private TextView selectAlgorithmView;
    private int anyChartViewId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /**
         * 각종 View들과 Button, Spinner 초기화
        */
        String[] selectArray = getResources().getStringArray(R.array.algoRithm);
        textViewHost = findViewById(R.id.textViewHost);
        textViewHost.setText(ObjectVolley.getInstance(getApplicationContext()).getHostName());
        spinnerDropdownMenu = findViewById(R.id.spinnerDropdownMenu);
        spinnerTickers = findViewById(R.id.spinnerTickers);
        buttonSelect = findViewById(R.id.buttonSelect);
        switchChangeIp = findViewById(R.id.switchChangeIp);
        selectAlgorithmView = findViewById(R.id.selectAlgorithmView);
        spinnerDropdownMenu.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, selectArray));
        LinearLayout linearLayout = findViewById(R.id.linearLayout);

        ObjectVolley objectVolley = ObjectVolley.getInstance(getApplicationContext());

        /**
         * sy
         */
        objectVolley.requestTickers(new ObjectVolley.RequestTickersListener() {
            @Override
            public void jobToDo() {
                String[] tickerList = this.getTickerList().toArray(new String[this.getTickerList().size()]);

                spinnerTickers.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, tickerList));
            }
        }, new ObjectVolley.StandardErrorListener() {
            @Override
            public void jobToDo() {

            }
        });

        switchChangeIp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                ObjectVolley objectVolley = ObjectVolley.getInstance(getApplicationContext());
                objectVolley.toggleUseCase();
                textViewHost.setText(objectVolley.getHostName());
            }
        });

        /**
         *  알고리즘 선택을 위해 콤보박스와 버튼을 만들었다
         *  실제 알고리즘이 나오면 각각의 콤보박스에 적용 예정이다..
         */
        spinnerDropdownMenu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int selectNum, long l) {
                buttonSelect.setText(selectArray[selectNum]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinnerTickers.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedTicker = (String)parent.getItemAtPosition(position);

                Log.d("debug", "selectedTicker : " + selectedTicker);

                objectVolley.requestDaily(selectedTicker, new ObjectVolley.RequestDailyListener() {
                    @Override
                    public void jobToDo() {
                        AnyChartView anyChartView = linearLayout.findViewById(anyChartViewId);
                        if (anyChartView != null) {
                            linearLayout.removeView(anyChartView);
                        }

                        anyChartView = new AnyChartView(getApplicationContext());
                        anyChartView.setLayoutParams(new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.MATCH_PARENT));
                        anyChartView.setPadding(10, 0, 0, 0);

                        anyChartViewId = ViewCompat.generateViewId();
                        anyChartView.setId(anyChartViewId);
                        linearLayout.addView(anyChartView);

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

//                        "step-line"
                        plot2.volumeMa(mapping, 20d, MovingAverageType.EMA, StockSeriesType.COLUMN, StockSeriesType.LINE);

                        stock.scroller().ohlc(mapping);

                        anyChartView.setChart(stock);
                    }
                }, new ObjectVolley.StandardErrorListener() {
                    @Override
                    public void jobToDo() {

                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        buttonSelect.setOnClickListener(v->{
//            ObjectVolley objectVolley = ObjectVolley.getInstance(getApplicationContext());

            selectAlgorithmView.setText(buttonSelect.getText());
        });
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

