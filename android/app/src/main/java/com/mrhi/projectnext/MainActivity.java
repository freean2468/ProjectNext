package com.mrhi.projectnext;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    TextView respond_View;
    Button request_Button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        respond_View = (TextView)findViewById(R.id.respond_View);
        request_Button = (Button)findViewById(R.id.request_Button);

        request_Button.setOnClickListener(v->{
            sendRequest();
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
     */
    public void sendRequest()
    {
        String url = "https://www.google.co.kr";
        StringRequest request = new StringRequest(
                Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        println("응답 -> " + response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        println("에러 -> " + error.getMessage());
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
        println("요청 보냄.");
    }

    public void println(String data)
    {
        respond_View.setText(data + "\n");
    }



}

