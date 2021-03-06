package com.sspku.leo.app;

import com.sspku.leo.bean.City;
import com.sspku.leo.db.CityDB;
import android.app.Application;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MyApplication extends Application{
    private static final String TAG = "MyAPP";
    private static MyApplication myApplication;
    private List<City> myCityList;
    private CityDB mycitydb;
    @Override
    public void onCreate(){
        super.onCreate();
        Log.d(TAG,"MyApplictioan->onCreate");

        myApplication = this;
        mycitydb = openCityDB();
        initCityList();
    }

    //初始化城市列表
    private void initCityList(){
        myCityList = new ArrayList<City>();
        //新建线程处理城市列表
        new Thread(new Runnable() {
            @Override
            public void run() {
                prepareCityList();
            }
        }).start();
    }
    //处理城市列表函数，取出城市名和城市序号
    private boolean prepareCityList() {
        myCityList = mycitydb.getAllCity();
        int i=0;
        for (City city : myCityList) {
            i++;
            String cityName = city.getCity();
            String cityCode = city.getNumber();
            //Log.d(TAG,cityCode+":"+cityName);
        }
        //Log.d(TAG,"i="+i);
        return true;
    }
    public List<City> getCityList() {
        return myCityList;
    }

    public static MyApplication getInstance(){
        return myApplication;
    }

    //处理城市数据库的函数
    private CityDB openCityDB() {
        //声明路径名称
        String path = "/data"
                + Environment.getDataDirectory().getAbsolutePath()
                + File.separator + getPackageName()
                + File.separator + "databases1"
                + File.separator
                + CityDB.CITY_DB_NAME;
        File db = new File(path);
        Log.d(TAG,path);
        //异常处理（数据库不存在）
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
}
