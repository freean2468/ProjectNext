package com.mrhi.projectnext.object;

import android.content.Context;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.mrhi.projectnext.R;
import com.mrhi.projectnext.model.ModelTicker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * 웹서버와 통신을 담당하는 Volley Manager class
 * Singleton pattern 적용 
 *
 * @author 송훈일(freean2468@gmail.com)
 */
public class ObjectVolley {
    private static ObjectVolley instance;
    private RequestQueue requestQueue;
    private static Context ctx;
    private String hostName;
    private final String hostNameForService;
    private final String hostNameForDevelopment;
    private UrlFactory urlFactory;

    private ObjectVolley(Context context) {
        ctx = context;
        requestQueue = getRequestQueue();
        urlFactory = new UrlFactory();

        /** 서비스 중인 웹서버 hostname */
        hostNameForService = ctx.getString(R.string.host_name_for_service);
        /** 개발 중인 local 서버 hostname */
        hostNameForDevelopment = ctx.getString(R.string.host_name_for_development);

        hostName = hostNameForDevelopment;
    }

    public static synchronized ObjectVolley getInstance(Context context) {
        if (instance == null) {
            instance = new ObjectVolley(context);
        }
        return instance;
    }

    /**
     * @return 현재 선택된 웹서버 hostname
     */
    public String getHostName() { return hostName; }

    /**
     * 연결할 웹서버 전환
     */
    public void toggleUseCase() {
        if (hostName.equals(hostNameForService)){
            hostName = hostNameForDevelopment;
        } else {
            hostName = hostNameForService;
        }
    }

    private class UrlFactory {
        private String route;

        public UrlFactory() {}

        public String getTickers() {
            return hostName + ctx.getString(R.string.url_tickers_all);
        }
        public String getDailies() {
            return hostName + ctx.getString(R.string.url_dailies_all);
        }
        public String getDaily(String ticker) throws UnsupportedEncodingException {
            return hostName + ctx.getString(R.string.url_daily) + "ticker=" + URLEncoder.encode(ticker, "utf-8");
        }
    }

    private RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            requestQueue = Volley.newRequestQueue(ctx.getApplicationContext());
        }
        return requestQueue;
    }

    private <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    public void requestTickers(RequestTickersListener listener, StandardErrorListener errorListener) {
        String url = urlFactory.getTickers();
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, listener, errorListener);
        addToRequestQueue(request);
    }

    /**
     * jobToDo 내용만 구현하고, 필드가 null인지 아닌지만 확인해서 사용
     */
    abstract public static class RequestTickersListener implements Response.Listener<JSONArray> {
        private ArrayList<String> tickerList = new ArrayList<>();

        @Override
        public void onResponse(JSONArray response) {
            try {
                for (int i = 0; i < response.length(); ++i) {
                    JSONObject jsonObject = response.getJSONObject(i);

                    if (jsonObject == null) {
                        Log.d("debug", "tickers are null!");
                    }

                    tickerList.add(jsonObject.get("ticker").toString().trim());
                }
            } catch (JSONException je) {
                je.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            jobToDo();
        }

        public abstract void jobToDo();
        public ArrayList<String> getTickerList() { return tickerList; }
    }

    public void requestDaily(String ticker, RequestDailyListener listener, StandardErrorListener errorListener) {
        try {
            String url = urlFactory.getDaily(ticker);
            Log.d("debug", url);
            JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, listener, errorListener);
            addToRequestQueue(request);
        } catch(UnsupportedEncodingException uee) {
            uee.printStackTrace();
        }
    }

    /**
     * jobToDo 내용만 구현하고, 필드가 null인지 아닌지만 확인해서 사용.
     */
    abstract public static class RequestDailyListener implements Response.Listener<JSONArray> {
        @Override
        public void onResponse(JSONArray response) {
            try {
                ModelTicker modelTicker = null;

                for (int i = 0; i < response.length(); ++i) {
                    JSONObject jsonObject = response.getJSONObject(i);

                    if (jsonObject == null) {
                        Log.d("debug", "tickers are null!");
                    }

                    if (modelTicker == null) {
                        String name = jsonObject.get("ticker").toString().trim();
                        modelTicker = new ModelTicker(name);
                    }

                    Date date = new SimpleDateFormat("yyyy-MM-dd").parse(jsonObject.get("date").toString().trim());
                    double open = Double.parseDouble(jsonObject.get("open").toString());
                    double high = Double.parseDouble(jsonObject.get("high").toString());
                    double low = Double.parseDouble(jsonObject.get("low").toString());
                    double close = Double.parseDouble(jsonObject.get("close").toString());
                    long volume = Long.parseLong(jsonObject.get("volume").toString());

                    modelTicker.add(date, open, high, low, close, volume);
                }

                ObjectAlgorithm.getInstance().add(modelTicker);
            } catch (JSONException je) {
                je.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            jobToDo();
        }

        public abstract void jobToDo();
    }

    /**
     *  에러 시 서버 응답 코드를 자동으로 알려주는 class
     */
    abstract public static class StandardErrorListener implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.i("debug", error.toString() + ", STATUS_CODE : " + volleyResponseStatusCode(error));
            jobToDo();
        }

        public static int volleyResponseStatusCode(VolleyError error)
        {
            NetworkResponse networkResponse = error.networkResponse;
            if (networkResponse != null) {
                return networkResponse.statusCode;
            }
            else{
                return 0;
            }
        }

        public abstract void jobToDo();
    }
}
