package com.sspku.leo.weather2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sspku.leo.ViewsOverride.ClearEditText;
import com.sspku.leo.bean.City;
import com.sspku.leo.db.CityDB;

import com.sspku.leo.bean.City;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class SelectCity extends Activity implements View.OnClickListener {


    private ImageView mBackBtn;
    private List<City> mCityList;
    private EditText mClearEditCity;
    private ListView mListView;
    private ArrayList cityName = new ArrayList();
    private ArrayList cityNum = new ArrayList();
    //private String cityName[]={"test"};
    //private String cityNum[]={"test"};
    private CityDB mCityDB;
    private String returnCityName;
    private String returnCityCode;
    private TextView cityNameTop;
    private static String currentCityName = "北京";
    private static String currentCityCode = "101010100";
    private ClearEditText mClearEditText;
    ArrayAdapter<String> mAdapter;



    //开始函数
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.select_city);

        mCityDB = openCityDB();

        initCityList();

        initViews();

    }

    //返回按钮响应时间
    @Override
    public void onClick(View v){
        switch (v.getId()) {
            case R.id.title_back:
                finish();
                break;
            default:
                break;
        }
    }

    //初始化城市列表，获取信息
    private void initCityList(){
        mCityList = new ArrayList<City>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                prepareCityArray();
            }
        }).start();



    }

    //利用cityDB类获取城市信息
    private boolean prepareCityArray() {
        mCityList = mCityDB.getAllCity();

        for (City city : mCityList) {
            cityName.add(city.getCity()) ;
            cityNum.add(city.getNumber());

        }
        return true;
    }

    //下面几个函数是直接从MyApplication复制过来的
    private CityDB openCityDB() {
        String path = "/data"
                + Environment.getDataDirectory().getAbsolutePath()
                + File.separator + getPackageName()
                + File.separator + "databases1"
                + File.separator
                + CityDB.CITY_DB_NAME;
        File db = new File(path);
        Log.d("cityDB",path);
        if (!db.exists()) {
            String pathfolder = "/data"
                    + Environment.getDataDirectory().getAbsolutePath()
                    + File.separator + getPackageName()
                    + File.separator + "databases1"
                    + File.separator;

            File dirFirstFolder = new File(pathfolder);
            if(!dirFirstFolder.exists()){
                dirFirstFolder.mkdirs();
                Log.i("MyApp","mkdirs");
            }
            Log.i("MyApp","db is not exists");
            try {
                InputStream is = getAssets().open("city.db");
                FileOutputStream fos = new FileOutputStream(db);
                int len = -1;
                byte[] buffer = new byte[1024];
                while ((len = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, len);
                    fos.flush();
                }
                fos.close();
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(0);
            }
        }
        return new CityDB(this, path);
    }

    //初始化界面，设置Adapter并显示ListView
    public void initViews(){
        Intent i = getIntent();
        i.setClass(this, MyActivity.class);
        //currentCityCode = i.getStringExtra("currentCityCode");
        //currentCityName = i.getStringExtra("currentCityName");
        //Log.d("weather", "currentcity:"+currentCityName+"code:"+currentCityCode);
        cityNameTop = (TextView)findViewById(R.id.title_name);
        cityNameTop.setText("当前城市：" + currentCityName);
        mListView = (ListView)findViewById(R.id.city_list);
        mBackBtn = (ImageView)findViewById(R.id.title_back);
        mBackBtn.setOnClickListener(this);

        mClearEditCity = (ClearEditText)findViewById(R.id.searchCity);
        mClearEditCity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence!=null){

                    ArrayList filterNameList = new ArrayList<String>();
                    ArrayList filterNumList = new ArrayList<String>();

                    if(TextUtils.isEmpty(charSequence)){
                        for(City city:mCityList){
                            filterNameList.add(city.getCity());
                            filterNumList.add(city.getNumber());
                        }
                    }
                    else{
                        filterNameList.clear();
                        filterNumList.clear();
                        for(City city:mCityList){
                            if(city.getCity().indexOf(charSequence.toString())!=-1){
                                filterNameList.add(city.getCity());
                                filterNumList.add(city.getNumber());
                            }
                        }
                    }

                    cityName = filterNameList;
                    cityNum = filterNumList;
                    mAdapter = new ArrayAdapter<String>(SelectCity.this, android.R.layout.simple_list_item_1, cityName);

                    mListView.setAdapter(mAdapter);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        mAdapter = new ArrayAdapter<String>(SelectCity.this, android.R.layout.simple_list_item_1, cityName);

        mListView.setAdapter(mAdapter);
        //点击城市之后将数据传输到主界面，并更新CurrentCity，使得标题栏显示当前城市
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                returnCityName = (String)cityName.get(i);
                returnCityCode = (String)cityNum.get(i);
                cityNameTop.setText("当前城市：" + returnCityName);
                Intent intent = new Intent();
                if(returnCityCode != null)
                    intent.putExtra("cityCode", returnCityCode);
                else
                    intent.putExtra("cityCode", currentCityCode);
                setResult(RESULT_OK, intent);
                currentCityCode = returnCityCode;
                currentCityName = returnCityName;
                finish();
            }
        });

    }

    private void filterData(String filterstr){
        ArrayList filterDataList = new ArrayList<City>();

        if(TextUtils.isEmpty(filterstr)){
            for(City city:mCityList){
                filterDataList.add(city);
            }
        }
        else{
            filterDataList.clear();
            for(City city:mCityList){
                if(city.getCity().indexOf(filterstr.toString())!=-1){
                    filterDataList.add(city);
                }
            }
        }

    }



}
