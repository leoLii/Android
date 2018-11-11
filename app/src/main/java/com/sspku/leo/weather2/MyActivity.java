package com.sspku.leo.weather2;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.sspku.leo.util.NetUtil;
import com.sspku.leo.bean.TodayWeather;

import static android.content.ContentValues.TAG;

public class MyActivity extends Activity implements View.OnClickListener {

    private ImageView mUpdateBtn;
    private ImageView mCitySelect;
    private static final int UPDATE_TODAY_WEATHER = 1;


    private TextView cityTv, timeTv, humidityTv, weekTv, pmDataTv,
            pmQualityTv,
            temperatureTv, climateTv, windTv, city_name_Tv;
    private ImageView weatherImg, pmImg;
    private String returnCityName;
    private String returnCityCode;
    private String newCityCode;
    private String newCityName;

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case UPDATE_TODAY_WEATHER:
                    updateTodayWeather((TodayWeather) msg.obj);
                    break;
                    default:
                        break;
            }
        }
    };



    //创建界面的函数
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_layout);


        mUpdateBtn = (ImageView)findViewById(R.id.title_update_btn);
        mUpdateBtn.setOnClickListener(this);

        mCitySelect = (ImageView)findViewById(R.id.title_city_manager);
        mCitySelect.setOnClickListener(this);



        if (NetUtil.getNetworkState(this) != NetUtil.NETWORK_NONE) {
            Log.d("myWeather", "网络OK");
        }else
        {
            Log.d("myWeather", "请检查网络连接");
            Toast.makeText(MyActivity.this,"请检查网络连接！",Toast.LENGTH_LONG).show();
        }
        initView();
    }

    //城市选择按钮和更新按钮的更新响应事件
    @Override
    public void onClick(View view){
        if(view.getId() == R.id.title_city_manager){
            //Intent i = new Intent();
            //i.putExtra("currentCityCode", newCityCode);
            //i.putExtra("currentCityName",newCityName);
            //setResult(RESULT_OK, i);
            Intent intent = new Intent(this, SelectCity.class);
            startActivityForResult(intent, 1);
        }
        if(view.getId() == R.id.title_update_btn){
            SharedPreferences sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
            String cityCode = sharedPreferences.getString("main_city_code",returnCityCode);
            if (NetUtil.getNetworkState(this) != NetUtil.NETWORK_NONE) {
                Log.d("myWeather", "网络OK");
                queryWeatherCode(cityCode);
            }else
            {
                Log.d("myWeather", "请检查网络连接");
                Toast.makeText(MyActivity.this,"请检查网络连接！",Toast.LENGTH_LONG).show();
            }
        }
    }

    //重写获取SelectCity界面数据的处理函数
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == 1 && resultCode == RESULT_OK) {
            newCityCode = data.getStringExtra("cityCode");
            newCityName = data.getStringExtra("cityName");
            Log.d("myWeather", "城市代码为"+newCityCode);
            returnCityCode = data.getStringExtra("cityCode");
            returnCityName = data.getStringExtra("cityName");

            if (NetUtil.getNetworkState(this) != NetUtil.NETWORK_NONE) {
                Log.d("myWeather", "网络OK");
                queryWeatherCode(newCityCode);
            }else
            {
                Log.d("myWeather", "请检查网络连接");
                Toast.makeText(MyActivity.this,"请检查网络连接！",Toast.LENGTH_LONG).show();
            }

        }

    }


    //从网络获取实时天气数据
    private void queryWeatherCode(String cityCode) {
        final String address = "http://wthrcdn.etouch.cn/WeatherApi?citykey=" + cityCode;
        Log.d("myWeather", address);
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection con=null;
                try{
                    URL url = new URL(address);
                    TodayWeather todayWeather = null;
                    con = (HttpURLConnection)url.openConnection(
                    );
                    con.setRequestMethod("GET");
                    con.setConnectTimeout(8000);
                    con.setReadTimeout(8000);
                    InputStream in = con.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String str;
                    while((str=reader.readLine()) != null){
                        response.append(str);
                        Log.d("myWeather", str);
                    }
                    String responseStr=response.toString();
                    Log.d("myWeather", responseStr);
                    todayWeather = TodayWeather.parseXML(responseStr);
                    if (todayWeather != null) {
                        Log.d("myWeather", todayWeather.toString());
                        Message msg =new Message();
                        msg.what = UPDATE_TODAY_WEATHER;
                        msg.obj=todayWeather;
                        mHandler.sendMessage(msg);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    if(con != null){
                        con.disconnect();
                    }
                }
            }
        }).start();
    }

    //初始化界面，各信息设置为空
    void initView(){
        city_name_Tv = (TextView) findViewById(R.id.title_city_name);
        cityTv = (TextView) findViewById(R.id.content_city);
        timeTv = (TextView) findViewById(R.id.content_releasetime);
        humidityTv = (TextView) findViewById(R.id.content_wet);
        weekTv = (TextView) findViewById(R.id.week_today);
        pmDataTv = (TextView) findViewById(R.id.pmData);
        pmQualityTv = (TextView) findViewById(R.id.pmText);
        pmImg = (ImageView) findViewById(R.id.pmImage);
        temperatureTv = (TextView) findViewById(R.id.temperature);
        climateTv = (TextView) findViewById(R.id.climate);
        windTv = (TextView) findViewById(R.id.wind);
        weatherImg = (ImageView) findViewById(R.id.weather_img);
        city_name_Tv.setText("N/A");
        cityTv.setText("N/A");
        timeTv.setText("N/A");
        humidityTv.setText("N/A");
        pmDataTv.setText("N/A");
        pmQualityTv.setText("N/A");
        weekTv.setText("N/A");
        temperatureTv.setText("N/A");
        climateTv.setText("N/A");
        windTv.setText("N/A");
    }

    //将TodayWeather的数据更新到界面
    void updateTodayWeather(TodayWeather todayWeather){
        city_name_Tv.setText(todayWeather.getCity()+"天气");
        cityTv.setText(todayWeather.getCity());
        timeTv.setText(todayWeather.getUpdatetime()+ "发布");

        if(todayWeather.getHumidity()!=null){
            humidityTv.setText("湿度："+todayWeather.getHumidity());
        }
        else{
            humidityTv.setText("无数据");
        }
        if(todayWeather.getPm25()!=null){
            pmDataTv.setText(todayWeather.getPm25());
        }
        else{
            pmDataTv.setText("无数据");
        }
        if(todayWeather.getAir_quality()!=null){
            pmQualityTv.setText(todayWeather.getAir_quality());
        }
        else{
            pmQualityTv.setText("无数据");
        }
        if(todayWeather.getBottom_temp()!=null&&todayWeather.getTop_temp()!=null){
            temperatureTv.setText(todayWeather.getBottom_temp()+"~"+todayWeather.getTop_temp());
        }
        else{
            temperatureTv.setText("无数据");
        }
        if(todayWeather.getType()!=null){
            climateTv.setText(todayWeather.getType());
        }
        else{
            climateTv.setText("无数据");
        }
        if(todayWeather.getWind_power()!=null){
            windTv.setText("风力:"+todayWeather.getWind_power());
        }
        else{
            windTv.setText("风力:"+"无数据");
        }
        weekTv.setText(todayWeather.getDate());
        String climate=todayWeather.getType();
        if(climate.equals("暴雪"))
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_snowstorm);
        if(climate.equals("暴雨"))
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_rainstorm);
        if(climate.equals("大暴雨"))
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_rainstorm);
        if(climate.equals("大雪"))
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_hugesnow);
        if(climate.equals("大雨"))
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_hugerain);
        if(climate.equals("多云"))
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_clouds);
        if(climate.equals("雷阵雨"))
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_thunder);
        if(climate.equals("雷阵雨冰雹"))
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_ice);
        if(climate.equals("晴"))
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_sun);
        if(climate.equals("沙尘暴"))
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_dust);
        if(climate.equals("特大暴雨"))
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_hugerain);
        if(climate.equals("雾"))
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_dust);
        if(climate.equals("小雪"))
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_smallsnow);
        if(climate.equals("小雨"))
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_smallrain);
        if(climate.equals("阴"))
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_cloud);
        if(climate.equals("雨夹雪"))
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_rainsnow);
        if(climate.equals("阵雨"))
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_smallrain);
        if(climate.equals("阵雪"))
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_smallsnow);
        if(climate.equals("中雪"))
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_middlesnow);
        if(climate.equals("中雨"))
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_middlerain);

        Toast.makeText(MyActivity.this,"更新成功！",Toast.LENGTH_SHORT).show();
    }

}
