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
import com.mrhi.projectnext.object.ObjectAnyChart;
import com.mrhi.projectnext.object.ObjectVolley;

public class FragmentMain extends Fragment {
    private LinearLayout linearLayout;
    private TextView textViewHost;
    private Switch switchChangeIp;
    private Spinner spinnerDropdownMenu;
    private Spinner spinnerTickers;
    private Button buttonSelect;
    private TextView selectAlgorithmView;

    public FragmentMain(){

    }

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
//                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
//                Fragment fragment = new FragmentAlgorithm();
//                fragmentTransaction.re
//                fragmentTransaction.replace(R.id.frameLayout, fragment);
//                fragmentTransaction.commit();

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

                objectVolley.requestDaily(selectedTicker, new ObjectVolley.RequestDailyListener() {
                    @Override
                    public void jobToDo() {
                        ObjectAnyChart.getInstance().drawMainChart(linearLayout, selectedTicker);
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

        return view;
    }

}
