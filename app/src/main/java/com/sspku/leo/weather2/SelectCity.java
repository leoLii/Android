package com.sspku.leo.weather2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sspku.leo.bean.City;
import com.sspku.leo.db.CityDB;

import com.sspku.leo.bean.City;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

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


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.select_city);

        mCityDB = openCityDB();

        initCityList();

        initViews();

    }

    @Override
    public void onClick(View v){
        switch (v.getId()) {
            case R.id.title_back:
                Intent i = new Intent();
                if(returnCityCode != null)
                    i.putExtra("cityCode", returnCityCode);
                else
                    i.putExtra("cityCode", "101010100");
                setResult(RESULT_OK, i);
                finish();
                break;
            default:
                break;
        }
    }

    private void initCityList(){
        mCityList = new ArrayList<City>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                prepareCityArray();
            }
        }).start();



    }

    private boolean prepareCityArray() {
        mCityList = mCityDB.getAllCity();

        for (City city : mCityList) {
            cityName.add(city.getCity()) ;
            cityNum.add(city.getNumber());

        }
        return true;
    }

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

    public void initViews(){
        cityNameTop = (TextView)findViewById(R.id.title_name);
        mListView = (ListView)findViewById(R.id.city_list);
        mBackBtn = (ImageView)findViewById(R.id.title_back);
        mBackBtn.setOnClickListener(this);

        ArrayAdapter<String>mAdapter = new ArrayAdapter<String>(SelectCity.this, android.R.layout.simple_list_item_1, cityName);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                returnCityName = (String)cityName.get(i);
                returnCityCode = (String)cityNum.get(i);
                cityNameTop.setText("当前城市：" + returnCityName);
                Toast.makeText(SelectCity.this, "城市名称："+cityName.get(i)+"城市编号："+cityNum.get(i),Toast.LENGTH_LONG).show();
            }
        });
    }


}
