package com.mrhi.projectnext;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

import static android.widget.CompoundButton.*;

public class MainActivity extends AppCompatActivity {

    private TextView ipView;
    private Switch switchChangeIp;
    private Spinner spinnerDropdownMenu;
    private Button buttonSelect;
    private String url;
    private TextView selectAlgorithmView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /**
         * 각종 View들과 Button, Spinner 초기화
        */
        String[] selectArray = getResources().getStringArray(R.array.algoRithm);
        ipView = (TextView)findViewById(R.id.ipView);
        spinnerDropdownMenu = findViewById(R.id.spinnerDropdownMenu);
        buttonSelect = findViewById(R.id.buttonSelect);
        switchChangeIp = findViewById(R.id.switchChangeIp);
        selectAlgorithmView = findViewById(R.id.selectAlgorithmView);
        spinnerDropdownMenu.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,selectArray));

        /**
         * 임시적으로 스위치로 IP가 바뀌는걸 테스트 해보았다
         * 실제 통신 메서드 sendRequest 를 호출해도 문제 없다.
         */

        switchChangeIp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked)
                {
                    ipView.setText(R.string.localhost);
                    url = ipView.getText().toString();
                }
                else
                {
                    ipView.setText(R.string.test_server);
                    url = ipView.getText().toString();
                }
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

        buttonSelect.setOnClickListener(v->{
            selectAlgorithmView.setText(buttonSelect.getText());

        });




        /**
         * 응답이 넘어오는지 아닌지를 확인하는 코드이다
         * 응답이 넘어온 경우 새로운 리퀘스트 큐를 생성한다.
         */
        if(AppHelper.requestQueue!=null)
        {
            AppHelper.requestQueue = Volley.newRequestQueue(getApplicationContext());
        }
    }
    /**
     * 요청을 보내고 응답을 받는 메서드이다.
     * 응답이던 에러이던 둘중 하나는 돌아온다.
     * 이전 결과 있더라도 새로 요청하여 응답을 보여준다.
     * 아이피가 바뀌는지 확인하는 테스트 코드를 추가하였다.
     */

    public void sendRequest()
    {
        StringRequest request = new StringRequest(
                Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //println("응답 -> " + response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //println("에러 -> " + error.getMessage());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String,String>();

                return params;
            }
        };

        request.setShouldCache(false);
        AppHelper.requestQueue = Volley.newRequestQueue(this); // requestQueue 초기화 필수
        AppHelper.requestQueue.add(request);

    }
}

