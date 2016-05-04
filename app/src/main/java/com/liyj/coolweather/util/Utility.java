package com.liyj.coolweather.util;

import android.text.TextUtils;

import com.liyj.coolweather.db.CoolWeatherDB;
import com.liyj.coolweather.model.City;
import com.liyj.coolweather.model.County;
import com.liyj.coolweather.model.Province;

/**
 * Created by Administrator on 2016/5/4.
 */
public class Utility {
    /**
     * 解析和处理服务器返回的省级数据
     * */
    public synchronized static boolean handleProvincesResponse(CoolWeatherDB coolWeatherDB,String response){
        if (!TextUtils.isEmpty(response)){
            String[] allProvinces=response.split(",");
            if (allProvinces!=null&&allProvinces.length>0){
                for (String p:allProvinces){
                    String[] array=p.split("\\|");
                    Province province=new Province();
                    province.setProvinceName(array[1]);
                    province.setProvinceCode(array[0]);
                    coolWeatherDB.saveProvince(province);
                }
                return true;
            }
        }
        return false;
    }
    /**
     * 解析和处理服务器返回的市级数据
     * */
    public synchronized static boolean handleCitiesResponse(CoolWeatherDB coolWeatherDB,String response,int provinceId){
        if (!TextUtils.isEmpty(response)){
            String[] allCitie=response.split(",");
            if (allCitie!=null&&allCitie.length>0){
                for (String p:allCitie){
                    String[] array=p.split("\\|");//\\|是一个正则表达式  正则表达式中 要匹配|需要用\|
//                    String[] array=p.split("|");
                    City city=new City();

                    city.setCityName(array[1]);
                    city.setCityCode(array[0]);
                    city.setProvinceId(provinceId);
                    coolWeatherDB.saveCity(city);
                }
                return true;
            }
        }
        return false;
    }
    /**
     * 解析和处理服务器返回的县级数据
     * */
    public synchronized static boolean handleCountiesResponse(CoolWeatherDB coolWeatherDB,String response,int cityId){
        if (!TextUtils.isEmpty(response)){
            String[] allCounties=response.split(",");
            if (allCounties!=null&&allCounties.length>0){
                for (String p:allCounties){
                    String[] array=p.split("\\|");
//                    String[] array=p.split("|");
                    County county=new County();

                    county.setCountyCode(array[0]);
                    county.setCountyName(array[1]);
                    county.setCityId(cityId);
                    coolWeatherDB.saveCounty(county);
                }
                return true;
            }
        }
        return false;
    }
}
