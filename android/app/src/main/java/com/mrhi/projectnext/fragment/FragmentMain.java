package com.mrhi.projectnext.fragment;

import android.os.Bundle;
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

import static com.mrhi.projectnext.object.ObjectAlgorithm.ALGORITHM_BUY_LOW_CASE;
import static com.mrhi.projectnext.object.ObjectAlgorithm.ALGORITHM_BUY_OPEN_CASE;
import static com.mrhi.projectnext.object.ObjectAlgorithm.ALGORITHM_FOURTEEN_DAYS_CASE;
import static com.mrhi.projectnext.object.ObjectAlgorithm.ALGORITHM_FOURTEEN_DAYS_VOLUME_CASE;
import static com.mrhi.projectnext.object.ObjectAlgorithm.ALGORITHM_OHLC_CASE;
import static com.mrhi.projectnext.object.ObjectAlgorithm.ALGORITHM_SURGE_DAYS_CASE;
import static com.mrhi.projectnext.object.ObjectAlgorithm.ALGORITHM_VOLUME_100PER_INCREASED_CASE;
import static com.mrhi.projectnext.object.ObjectAlgorithm.ALGORITHM_VOLUNE_PRICE_DECREASE_3DAYS_CASE;

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

    private FragmentMain() {

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
        buttonExecute.setOnClickListener(v -> {
            ((ActivityMain) getActivity()).getViewPager2().setCurrentItem(ActivityMain.PAGE_ALGORITHM_RESULT);

            ObjectAnyChart objectAnyChart = ObjectAnyChart.getInstance();
            ObjectAlgorithm objectAlgorithm = ObjectAlgorithm.getInstance();
            ViewGroup viewGroup = FragmentAlgorithmResult.getInstance().getViewGroup();

            switch (nSelectedAlgorithm) {
                case ALGORITHM_VOLUME_100PER_INCREASED_CASE:
                    int day1 = 1;
                    int day2 = 7;
                    int day3 = 30;
                    int day4 = 180;

                {
                    LinkedList<ArrayList<ModelTicker.Daily>> resultList = objectAlgorithm.algorithmVolume100PerIncreatedCase(strSelectedTicker, day1, day2, day3, day4);
                    objectAnyChart.drawAlgorithmVolumeDoubleTimedCase(strSelectedAlgorithm, viewGroup, resultList, day1, day2, day3, day4);
                }
                    break;
                case ALGORITHM_FOURTEEN_DAYS_CASE: {
                    LinkedList<ArrayList<ModelTicker.Daily>> resultList = objectAlgorithm.algorithmFourteenDays(strSelectedTicker);
                    objectAnyChart.drawAlgorithmFourteenDaysResult(strSelectedAlgorithm, viewGroup, resultList);
                }
                    break;
                case ALGORITHM_SURGE_DAYS_CASE: {
                    LinkedList<ArrayList<ModelTicker.Daily>> resultList = objectAlgorithm.algorithmSwitchPrice(strSelectedTicker);
                    objectAnyChart.drawAlgorithmSurgeDaysResult(strSelectedAlgorithm, viewGroup, resultList);
                }
                    break;
                case ALGORITHM_BUY_OPEN_CASE: {
                    ArrayList<ModelTicker.Daily> dailyList = objectAlgorithm.algorithmBuyOpenCase(strSelectedTicker);
                    objectAnyChart.drawAlgorithmBuyOpenCase(strSelectedAlgorithm, viewGroup, dailyList);
                }
                    break;
                case ALGORITHM_FOURTEEN_DAYS_VOLUME_CASE: {
                    LinkedList<ArrayList<ModelTicker.Daily>> resultList = objectAlgorithm.algorithmFourteenDaysVolume(strSelectedTicker);
                    objectAnyChart.drawAlgorithmFourteenDaysVolumeResult(strSelectedAlgorithm, viewGroup, resultList);
                }
                    break;
                case ALGORITHM_BUY_LOW_CASE:{
                    int day = 0;
                    int nextDay = 1;
                    int oneWeek = 7;
                    int oneMonth = 30;
                    int sixMonth = 180;

                    LinkedList<ArrayList<ModelTicker.Daily>> resultList = objectAlgorithm.algorithmBuyLow(strSelectedTicker, day, nextDay, oneWeek, oneMonth, sixMonth);
                    objectAnyChart.drawAlgorithmBuyLowResult(strSelectedAlgorithm, viewGroup, resultList, day, nextDay, oneWeek, oneMonth, sixMonth);
                }
                    break;
                case ALGORITHM_VOLUNE_PRICE_DECREASE_3DAYS_CASE:{
                    int nextDay = 1;
                    int oneWeek = 7;
                    int oneMonth = 30;
                    int sixMonth = 180;

                    int decreaseDay = 3;

                    LinkedList<ArrayList<ModelTicker.Daily>> resultList = objectAlgorithm.algorithmVolumeAndPriceDecrease5daysCase(strSelectedTicker, nextDay, oneWeek, oneMonth, sixMonth, decreaseDay);
                    objectAnyChart.drawAlgorithmVolumeAndPriceDecrease5daysResult(strSelectedAlgorithm, viewGroup, resultList, nextDay, oneWeek, oneMonth, sixMonth, decreaseDay);
                }
                break;
                case ALGORITHM_OHLC_CASE:{
                    List<ModelTicker.Daily> dailyList = objectAlgorithm.algorithmOHLCcase(strSelectedTicker);
                    objectAnyChart.drawAlgorithmOHLCResult(strSelectedAlgorithm, strSelectedTicker, viewGroup, dailyList);
                }
                break;
            }
        });

        return view;
    }

    public LinearLayout getViewGroup() { return linearLayout; }
}
