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

/**
 * 첫 화면, 메인 화면을 담당한 FragmentMain
 * ticker 데이터 도식화 및 알고리즘 선택 및 실행을 위한 화면
 *
 * @author 송훈일(freean2468@gmail.com)
 */
public class FragmentMain extends Fragment {
    private static FragmentMain instance = new FragmentMain();

    private LinearLayout linearLayout;
    private String strSelectedTicker;
    private int nSelectedAlgorithm;
    private String strSelectedAlgorithm;

    private static final int ALGORITHM_VOLUME_100PER_INCREASED_CASE = 0;
    private static final int ALGORITHM_FOURTEEN_DAYS_CASE = 1;

    private FragmentMain(){

    }

    public static FragmentMain getInstance() { return instance; }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        String[] strAlgorithmList = getResources().getStringArray(R.array.algorithmList);
        TextView textViewHost = view.findViewById(R.id.textViewHost);
        textViewHost.setText(ObjectVolley.getInstance(getContext()).getHostName());;
        Switch switchChangeIp = view.findViewById(R.id.switchChangeIp);
        Spinner spinnerAlgorithmList = view.findViewById(R.id.spinnerAlgorithmList);;
        Spinner spinnerTickers = view.findViewById(R.id.spinnerTickers);;
        Button buttonExecute = view.findViewById(R.id.buttonExecute);;

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
                strSelectedTicker = (String)parent.getItemAtPosition(position);

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

            switch(nSelectedAlgorithm) {
                case ALGORITHM_VOLUME_100PER_INCREASED_CASE:
                    int day1 = 1;
                    int day2 = 7;
                    int day3 = 30;
                    int day4 = 180;

                    LinkedList<ArrayList<ModelTicker.Daily>> resultList = objectAlgorithm.algorithmTest(strSelectedTicker, day1, day2, day3, day4);
                    objectAnyChart.drawAlgorithmTestResult(strSelectedAlgorithm, viewGroup, resultList, day1, day2, day3, day4);
                    break;

                case ALGORITHM_FOURTEEN_DAYS_CASE:
                    LinkedList<ArrayList<ModelTicker.Daily>> resultList2 = objectAlgorithm.algorithmFourteenDays(strSelectedTicker);
                    objectAnyChart.drawAlgorithmFourteenDaysResult(strSelectedAlgorithm, viewGroup, resultList2);
                    break;
            }
        });

        return view;
    }

    public LinearLayout getViewGroup() { return linearLayout; }

}
