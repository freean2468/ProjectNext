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

public class FragmentMain extends Fragment {
    private static FragmentMain instance = new FragmentMain();

    private LinearLayout linearLayout;
    private TextView textViewHost;
    private Switch switchChangeIp;
    private Spinner spinnerDropdownMenu;
    private Spinner spinnerTickers;
    private Button buttonSelect;
    private TextView selectAlgorithmView;
    private String selectedTicker;

    private FragmentMain(){

    }

    public static FragmentMain getInstance() { return instance; }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        /**
         * 각종 View들과 Button, Spinner 초기화
         */
        String[] selectArray = getResources().getStringArray(R.array.algoRithm);
        textViewHost = view.findViewById(R.id.textViewHost);
        textViewHost.setText(ObjectVolley.getInstance(getContext()).getHostName());
        spinnerDropdownMenu = view.findViewById(R.id.spinnerDropdownMenu);
        spinnerTickers = view.findViewById(R.id.spinnerTickers);
        buttonSelect = view.findViewById(R.id.buttonSelect);
        switchChangeIp = view.findViewById(R.id.switchChangeIp);
        selectAlgorithmView = view.findViewById(R.id.selectAlgorithmView);
        spinnerDropdownMenu.setAdapter(new ArrayAdapter<>(this.getContext(), android.R.layout.simple_spinner_dropdown_item, selectArray));
        linearLayout = view.findViewById(R.id.linearLayout);

        ObjectVolley objectVolley = ObjectVolley.getInstance(getContext());

        /**
         * sy
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

        switchChangeIp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                ObjectVolley objectVolley = ObjectVolley.getInstance(getContext());
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
                selectedTicker = (String)parent.getItemAtPosition(position);

                objectVolley.requestDaily(selectedTicker, new ObjectVolley.RequestDailyListener() {
                    @Override
                    public void jobToDo() {
                        ObjectAnyChart.getInstance().drawOHLCChart(linearLayout, selectedTicker);
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
            selectAlgorithmView.setText(buttonSelect.getText());

            ((ActivityMain)getActivity()).getViewPager2().setCurrentItem(ActivityMain.PAGE_ALGORITHM_RESULT);

            ObjectAnyChart objectAnyChart = ObjectAnyChart.getInstance();
            ObjectAlgorithm objectAlgorithm = ObjectAlgorithm.getInstance();
            ViewGroup viewGroup = FragmentAlgorithmResult.getInstance().getViewGroup();
            int day1 = 1;
            int day2 = 7;
            int day3 = 30;
            int day4 = 180;

            LinkedList<ArrayList<ModelTicker.Daily>> resultList = objectAlgorithm.algorithmTest(selectedTicker, day1, day2, day3, day4);
            objectAnyChart.drawAlgorithmTestResult(viewGroup, resultList, day1, day2, day3, day4);
        });

        return view;
    }

    public LinearLayout getViewGroup() { return linearLayout; }

}
