package com.example.weather;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;


import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import interfaces.heweather.com.interfacesmodule.bean.Lang;
import interfaces.heweather.com.interfacesmodule.bean.Unit;
import interfaces.heweather.com.interfacesmodule.bean.weather.now.Now;
import interfaces.heweather.com.interfacesmodule.view.HeConfig;
import interfaces.heweather.com.interfacesmodule.view.HeWeather;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
private ImageView UpdateBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        UpdateBtn=(ImageView)findViewById(R.id.title_city_update);
        UpdateBtn.setOnClickListener(this);
        Window window = getWindow();
        HeConfig.init("HE2001301154211846", "31f7619d0f934f58ac2d4d9b35ea5075");
        HeConfig.switchToFreeServerNode();

    }
    public void onClick(View v){
        if(v.getId()==R.id.title_city_update){
            getWeatherDatafromNet("CN101010100");
        }
    }
    private void getWeatherDatafromNet(String cityCode)
    {
        final String address = "https://api.heweather.net/s6/weather/now?location="+cityCode+"&key=7413862dc62147888c6ec28aceb615af";

        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection urlConnection = null;
                try {
                    URL url = new URL(address);
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.setConnectTimeout(8000);
                    urlConnection.setReadTimeout(8000);
                    InputStream in = urlConnection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuffer sb = new StringBuffer();
                    String str;
                    while((str=reader.readLine())!=null)
                    {
                        sb.append(str);
                        Log.d("date from url",str);
                    }
                    String response = sb.toString();
                    Log.d("response",response);
                }catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }).start();
    }
private void getWeather(){
    HeWeather.getWeatherNow(MainActivity.this, "CN101010100",  Lang.CHINESE_SIMPLIFIED , Unit.METRIC , new HeWeather.OnResultWeatherNowBeanListener() {
        public static final String TAG="he_feng_now";
        @Override
        public void onError(Throwable e) {
            Log.i(TAG, "onError: ", e);
            System.out.println("Weather Now Error:"+new Gson());
        }

        @Override
        public void onSuccess(Now dataObject) {
            Log.i(TAG, " Weather Now onSuccess: " + new Gson().toJson(dataObject));
            String jsonData = new Gson().toJson(dataObject);
            String tianqi = null,wendu = null, tianqicode = null;
            if (dataObject.getStatus().equals("ok")){
                String JsonNow = new Gson().toJson(dataObject.getNow());
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(JsonNow);
                    tianqi = jsonObject.getString("cond_txt");
                    wendu = jsonObject.getString("tmp");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else {
                Toast.makeText(MainActivity.this,"有错误",Toast.LENGTH_SHORT).show();
                return;
            }
            String wendu2 = wendu +"℃";

        }
    });
}
}
