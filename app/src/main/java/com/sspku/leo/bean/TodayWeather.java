package com.sspku.leo.bean;
import org.xmlpull.v1.*;
import android.util.Log;

import java.io.IOException;
import java.io.StringReader;

public class TodayWeather {
    private String city;
    private String updatetime;
    private String temperate;
    private String humidity;
    private String pm25;
    private String air_quality;
    private String wind_direction;
    private String wind_power;
    private String date;
    private String bottom_temp;
    private String top_temp;
    private String type;

    public String getAir_quality() {
        return air_quality;
    }

    public String getCity() {
        return city;
    }

    public String getHumidity() {
        return humidity;
    }

    public String getWind_direction() {
        return wind_direction;
    }

    public String getWind_power() {
        return wind_power;
    }

    public String getBottom_temp() {
        return bottom_temp;
    }

    public String getDate() {
        return date;
    }

    public String getPm25() {
        return pm25;
    }

    public String getTemperate() {
        return temperate;
    }

    public String getTop_temp() {
        return top_temp;
    }

    public String getType() {
        return type;
    }

    public String getUpdatetime() {
        return updatetime;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setBottom_temp(String bottom_temp) {
        this.bottom_temp = bottom_temp;
    }

    public void setTop_temp(String top_temp) {
        this.top_temp = top_temp;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setAir_quality(String air_quality) {
        this.air_quality = air_quality;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public void setPm25(String pm25) {
        this.pm25 = pm25;
    }

    public void setTemperate(String temperate) {
        this.temperate = temperate;
    }

    public void setUpdatetime(String updatetime) {
        this.updatetime = updatetime;
    }

    public void setWind_direction(String wind_direction) {
        this.wind_direction = wind_direction;
    }

    public void setWind_power(String wind_power) {
        this.wind_power = wind_power;
    }

    @Override
    public String toString(){
        return "TodayWeather{" +
                "City='" + city + '\'' +
                ",updatetime=" +updatetime + '\'' +
                ",wendu=" + temperate + '\'' +
                ",shidu=" + humidity + '\'' +
                ",pm25=" + pm25 + '\'' +
                ",quality=" + air_quality + '\'' +
                ",fengxiang=" + wind_direction + '\'' +
                ",fengli=" + wind_power + '\'' +
                ",date=" + date + '\'' +
                ",high=" + top_temp + '\'' +
                ",low=" + bottom_temp + '\'' +
                ",type=" + type + '\'' +
                '}';
    }

    public static TodayWeather parseXML(String xmldata){
        TodayWeather todayWeather = null;
        int fengxiangCount=0;
        int fengliCount =0;
        int dateCount=0;
        int highCount =0;
        int lowCount=0;
        int typeCount =0;
        try {
            XmlPullParserFactory fac = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = fac.newPullParser();
            xmlPullParser.setInput(new StringReader(xmldata));
            int eventType = xmlPullParser.getEventType();
            Log.d("myWeather", "parseXML");
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        if(xmlPullParser.getName().equals("resp"
                        )){
                            todayWeather= new TodayWeather();
                        }
                        if (todayWeather != null) {
                            if (xmlPullParser.getName().equals("city")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setCity(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("updatetime")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setUpdatetime(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("shidu")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setHumidity(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("wendu")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setTemperate(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("pm25")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setPm25(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("quality")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setAir_quality(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("fengxiang") && fengxiangCount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setWind_direction(xmlPullParser.getText());
                                fengxiangCount++;
                            } else if (xmlPullParser.getName().equals("fengli") && fengliCount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setWind_power(xmlPullParser.getText());
                                fengliCount++;
                            } else if (xmlPullParser.getName().equals("date") && dateCount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setDate(xmlPullParser.getText());
                                dateCount++;
                             } else if (xmlPullParser.getName().equals("high") && highCount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setTop_temp(xmlPullParser.getText().substring(2).trim());
                                highCount++;
                            } else if (xmlPullParser.getName().equals("low") && lowCount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setBottom_temp(xmlPullParser.getText().substring(2).trim());
                                lowCount++;
                            } else if (xmlPullParser.getName().equals("type") && typeCount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setType(xmlPullParser.getText());
                                typeCount++;
                            }
                        }
                    break;
                    case XmlPullParser.END_TAG:
                        break;
                }
                eventType = xmlPullParser.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return todayWeather;
    }
}
