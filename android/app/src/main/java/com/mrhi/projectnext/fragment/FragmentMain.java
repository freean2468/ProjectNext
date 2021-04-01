package com.mrhi.projectnext.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.mrhi.projectnext.R;
import com.mrhi.projectnext.activity.ActivityMain;
import com.mrhi.projectnext.model.ModelTicker;
import com.mrhi.projectnext.object.ObjectAlgorithm;
import com.mrhi.projectnext.object.ObjectAnyChart;
import com.mrhi.projectnext.object.ObjectVolley;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static com.mrhi.projectnext.object.ObjectAlgorithm.BUY_CLOSE_WHEN_DECREASED_SEVERAL_DAYS;
import static com.mrhi.projectnext.object.ObjectAlgorithm.BUY_LOW;
import static com.mrhi.projectnext.object.ObjectAlgorithm.BUY_OPEN_SELL_CLOSE;
import static com.mrhi.projectnext.object.ObjectAlgorithm.CLOSE_OPEN;
import static com.mrhi.projectnext.object.ObjectAlgorithm.FLUCTUATION_RATE_ONE_DAY;
import static com.mrhi.projectnext.object.ObjectAlgorithm.FLUCTUATION_RATE_SEVERAL_DAYS;
import static com.mrhi.projectnext.object.ObjectAlgorithm.HIGH_LOW;
import static com.mrhi.projectnext.object.ObjectAlgorithm.NASDAQ_CORRELATION;
import static com.mrhi.projectnext.object.ObjectAlgorithm.PROBABILITY_CONTINUITY_2DAYS_LOSE;
import static com.mrhi.projectnext.object.ObjectAlgorithm.RECOVERING;
import static com.mrhi.projectnext.object.ObjectAlgorithm.SEVERAL_DAYS_INCREASE_OUT_OF_2_WEEKS;
import static com.mrhi.projectnext.object.ObjectAlgorithm.SEVERAL_DAYS_INCREASE_OUT_OF_2_WEEKS_AS_WELL_AS_VOLUME;
import static com.mrhi.projectnext.object.ObjectAlgorithm.VOLUME_2_TIMES_DECREASED_MORE_THAN_YESTERDAY;
import static com.mrhi.projectnext.object.ObjectAlgorithm.VOLUME_2_TIMES_INCREASED_MORE_THAN_YESTERDAY;

/**
 * 첫 화면, 메인 화면을 담당한 FragmentMain
 * ticker 데이터 도식화 및 알고리즘 선택 및 실행을 위한 화면
 *
 * @author 송훈일(freean2468 @ gmail.com)
 */
public class FragmentMain extends Fragment {
    private static FragmentMain instance = new FragmentMain();

    private LinearLayout linearLayout;
    private String strSelectedTicker;
    private int nSelectedAlgorithm;
    private String strSelectedAlgorithm;

    private FragmentMain(){

    }

    public static FragmentMain getInstance() {
        return instance;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        String[] strAlgorithmList = getResources().getStringArray(R.array.algorithmList);
        TextView textViewHost = view.findViewById(R.id.textViewHost);
        textViewHost.setText(ObjectVolley.getInstance(getContext()).getHostName());
        ;
        Switch switchChangeIp = view.findViewById(R.id.switchChangeIp);
        Spinner spinnerAlgorithmList = view.findViewById(R.id.spinnerAlgorithmList);
        ;
        Spinner spinnerTickers = view.findViewById(R.id.spinnerTickers);
        ;
        Button buttonExecute = view.findViewById(R.id.buttonExecute);
        ;

        spinnerAlgorithmList.setAdapter(new ArrayAdapter<>(this.getContext(), R.layout.spinner_item, strAlgorithmList));
        linearLayout = view.findViewById(R.id.linearLayout);

        ObjectVolley objectVolley = ObjectVolley.getInstance(getContext());

        /**
         * 최초 실행 시 서버에 현재 ticker 목록을 요청해 초기화.
         */
        objectVolley.requestTickers(new ObjectVolley.RequestTickersListener() {
            @Override
            public void jobToDo() {
                String[] tickerList = this.getTickerList().toArray(new String[this.getTickerList().size()]);

                spinnerTickers.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, tickerList));
            }
        }, new ObjectVolley.StandardErrorListener() {
            @Override
            public void jobToDo() {

            }
        });

        /**
         * 나중에 AWS에 서버 deploy 시 연결할 수 있도록 toggle 스위치
         */
        switchChangeIp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                ObjectVolley objectVolley = ObjectVolley.getInstance(getContext());
                objectVolley.toggleUseCase();
                textViewHost.setText(objectVolley.getHostName());
            }
        });

        /**
         *  알고리즘 선택
         */
        spinnerAlgorithmList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int selectedNum, long l) {
                nSelectedAlgorithm = selectedNum;
                strSelectedAlgorithm = strAlgorithmList[selectedNum];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        /**
         * 종목 선택
         */
        spinnerTickers.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                strSelectedTicker = (String) parent.getItemAtPosition(position);

                objectVolley.requestDaily(strSelectedTicker, new ObjectVolley.RequestDailyListener() {
                    @Override
                    public void jobToDo() {
                        ObjectAnyChart.getInstance().drawOHLCChart(linearLayout, strSelectedTicker);
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

        /**
         * 알고리즘 선택
         */
        buttonExecute.setOnClickListener(v->{
            ((ActivityMain)getActivity()).getViewPager2().setCurrentItem(ActivityMain.PAGE_ALGORITHM_RESULT);

            ObjectAnyChart objectAnyChart = ObjectAnyChart.getInstance();
            ObjectAlgorithm objectAlgorithm = ObjectAlgorithm.getInstance();
            ViewGroup viewGroup = FragmentAlgorithmResult.getInstance().getViewGroup();

            switch (nSelectedAlgorithm) {
                case VOLUME_2_TIMES_INCREASED_MORE_THAN_YESTERDAY: {
                    int day1 = 1;
                    int day2 = 7;
                    int day3 = 30;
                    int day4 = 180;

                    LinkedList<ArrayList<ModelTicker.Daily>> resultList = objectAlgorithm.algorithm_VOLUME_2_TIMES_INCREASED_MORE_THAN_YESTERDAY(strSelectedTicker, day1, day2, day3, day4);
                    objectAnyChart.draw_VOLUME_2_TIMES_INCREASED_MORE_THAN_YESTERDAY(strSelectedAlgorithm, viewGroup, resultList, day1, day2, day3, day4);
                }
                    break;
                case VOLUME_2_TIMES_DECREASED_MORE_THAN_YESTERDAY: {
                    int day1 = 1;
                    int day2 = 7;
                    int day3 = 30;
                    int day4 = 180;
                    LinkedList<ArrayList<ModelTicker.Daily>> mResultList = objectAlgorithm.algorithm_VOLUME_2_TIMES_DECREASED_MORE_THAN_YESTERDAY(strSelectedTicker, day1, day2, day3, day4);
                    objectAnyChart.draw_VOLUME_2_TIMES_INCREASED_MORE_THAN_YESTERDAY(strSelectedAlgorithm, viewGroup, mResultList, day1, day2, day3, day4);
                }
                    break;
                case SEVERAL_DAYS_INCREASE_OUT_OF_2_WEEKS: {
                    LinkedList<ArrayList<ModelTicker.Daily>> resultList = objectAlgorithm.algorithm_SEVERAL_DAYS_INCREASE_OUT_OF_2_WEEKS(strSelectedTicker);
                    objectAnyChart.draw_SEVERAL_DAYS_INCREASE_OUT_OF_2_WEEKS(strSelectedAlgorithm, viewGroup, resultList);
                }
                    break;
                case SEVERAL_DAYS_INCREASE_OUT_OF_2_WEEKS_AS_WELL_AS_VOLUME: {
                    LinkedList<ArrayList<ModelTicker.Daily>> resultList = objectAlgorithm.algorithm_SEVERAL_DAYS_INCREASE_OUT_OF_2_WEEKS_AS_WELL_AS_VOLUME(strSelectedTicker);
                    objectAnyChart.draw_SEVERAL_DAYS_INCREASE_OUT_OF_2_WEEKS_AS_WELL_AS_VOLUME(strSelectedAlgorithm, viewGroup, resultList);
                }
                    break;
                case HIGH_LOW:
                    LinkedList<Double> mAverage = objectAlgorithm.algorithm_HIGH_LOW(strSelectedTicker);
                    objectAnyChart.draw_HIGH_LOW(strSelectedAlgorithm, viewGroup, mAverage);
                    break;
                case CLOSE_OPEN :
                    LinkedList<Double> mGap = objectAlgorithm.algorithm_CLOSE_OPEN(strSelectedTicker);
                    objectAnyChart.draw_CLOSE_OPEN(strSelectedAlgorithm,viewGroup,mGap);
                    break;
                case BUY_LOW:{
                    int day0 = 0;
                    int day1 = 1;
                    int day2 = 7;
                    int day3 = 30;
                    int day4 = 180;

                    LinkedList<ArrayList<ModelTicker.Daily>> resultList = objectAlgorithm.algorithm_BUY_LOW(strSelectedTicker, day0, day1, day2, day3, day4);
                    objectAnyChart.draw_BUY_LOW(strSelectedAlgorithm, viewGroup, resultList, day0, day1, day2, day3, day4);
                }
                    break;
                case BUY_CLOSE_WHEN_DECREASED_SEVERAL_DAYS:{
                    int day1 = 1;
                    int day2 = 7;
                    int day3 = 30;
                    int day4 = 180;

                    int decreaseDay = 3;

                    LinkedList<ArrayList<ModelTicker.Daily>> resultList = objectAlgorithm.algorithm_BUY_CLOSE_WHEN_DECREASED_SEVERAL_DAYS(strSelectedTicker, day1, day2, day3, day4, decreaseDay);
                    objectAnyChart.draw_BUY_CLOSE_WHEN_DECREASED_SEVERAL_DAYS(strSelectedAlgorithm, viewGroup, resultList, day1, day2, day3, day4, decreaseDay);
                }
                break;
                case RECOVERING: {
                    LinkedList<ArrayList<ModelTicker.Daily>> resultList = objectAlgorithm.algorithm_RECOVERING(strSelectedTicker);
                    objectAnyChart.draw_RECOVERING(strSelectedAlgorithm, viewGroup, resultList);
                }
                    break;
                case BUY_OPEN_SELL_CLOSE: {
                    ArrayList<ModelTicker.Daily> dailyList = objectAlgorithm.algorithm_BUY_OPEN_SELL_CLOSE(strSelectedTicker);
                    objectAnyChart.draw_BUY_OPEN_SELL_CLOSE(strSelectedAlgorithm, viewGroup, dailyList);
                }
                    break;
                case FLUCTUATION_RATE_ONE_DAY:{
                    List<ModelTicker.Daily> dailyList = objectAlgorithm.algorithm_FLUCTUATION_RATE_ONE_DAY(strSelectedTicker);
                    objectAnyChart.draw_FLUCTUATION_RATE_ONE_DAY(strSelectedAlgorithm, strSelectedTicker, viewGroup, dailyList);
                }
                    break;
                case FLUCTUATION_RATE_SEVERAL_DAYS:{
                    int day1 = 1;
                    int day2 = 7;
                    int day3 = 14;
                    int day4 = 30;

                    LinkedList<ArrayList<ModelTicker.Daily>> resultList = objectAlgorithm.algorithm_FLUCTUATION_RATE_SEVERAL_DAYS(strSelectedTicker, day1, day2, day3, day4);
                    objectAnyChart.draw_FLUCTUATION_RATE_SEVERAL_DAYS(strSelectedAlgorithm, strSelectedTicker, viewGroup, resultList, day1, day2, day3, day4);
                }
                case PROBABILITY_CONTINUITY_2DAYS_LOSE:{
                    int CELL_DATE = 2;

                    LinkedList<ArrayList<ModelTicker.Daily>> resultList = objectAlgorithm.algorithm_PROBABILITY_CONTINUITY_2DAYS_LOSE(strSelectedTicker, CELL_DATE);
                    objectAnyChart.draw_PROBABILITY_CONTINUITY_2DAYS_LOSE(strSelectedAlgorithm, strSelectedTicker, viewGroup, resultList, CELL_DATE);
                }
                    break;
                case NASDAQ_CORRELATION:{
                    List<ModelTicker.Daily> dailyList = objectAlgorithm.algorithm_NASDAQ_CORRELATION(strSelectedTicker);
                    objectAnyChart.draw_NADAQ_CORRELATION(strSelectedAlgorithm, strSelectedTicker, viewGroup, dailyList);
                }
                default:
                    break;
            }
        });

        return view;
    }

    public LinearLayout getViewGroup() { return linearLayout; }
}
