package com.liyj.coolweather.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.liyj.coolweather.model.City;
import com.liyj.coolweather.model.County;
import com.liyj.coolweather.model.Province;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/5/4.
 */
public class CoolWeatherDB {
    /**
     *数据库名
     * */
    public static final String DB_NAME="cool_weather";
    /**
     *数据库版本
     * */
    public static final int DB_VERSION= 1;

    private static CoolWeatherDB coolWeatherDB;
    private SQLiteDatabase db;
    /**
     * 将构造方法私有化
     * */
    private CoolWeatherDB(Context context){
        CoolWeatherOpenHelper dbHelper=new CoolWeatherOpenHelper(context,DB_NAME,null,DB_VERSION);
        db=dbHelper.getWritableDatabase();
    }
    /**
     * 获取CoolWeatherDB的实例
     * */
    public synchronized static CoolWeatherDB getInstance(Context context){
        if (coolWeatherDB==null){
            coolWeatherDB=new CoolWeatherDB(context);
        }
        return coolWeatherDB;
    }
    /**
     * 将Province实例保存到数据库
     * */
    public void saveProvince(Province province){
        if (province!=null){
            ContentValues contentValues=new ContentValues();
            contentValues.put("province_name",province.getProvinceName());
            contentValues.put("province_code",province.getProvinceCode());
            db.insert("Province",null,contentValues);
        }

    }
    /**
     * 从数据库读取全国所有省份信息
     * */
    public List<Province> loadProvinces(){
        List<Province> list=new ArrayList<Province>();
        Cursor cursor=db.query("Province",null,null,null,null,null,null);
        if (cursor.moveToFirst()){
            do {
                Province province1=new Province();
                province1.setId(cursor.getInt(cursor.getColumnIndex("id")));
                province1.setProvinceCode(cursor.getString(cursor.getColumnIndex("province_code")));
                province1.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
                list.add(province1);
            }while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }
    /**
     * 将City实例存储到数据库
     * */
    public void saveCity(City city){
        if (city!=null){
            ContentValues contentValues=new ContentValues();
            contentValues.put("city_name",city.getCityName());
            contentValues.put("city_code",city.getCityCode());
            contentValues.put("province_id",city.getProvinceId());
            db.insert("City",null,contentValues);
        }
    }
    /**
     * 从数据库读取某省所有城市信息
     * */
    public List<City> loadCities(int provinceId){
        List<City> list=new ArrayList<City>();
        Cursor cursor=db.query("City",null,null,null,null,null,null);
        if (cursor.moveToFirst()){
            do {
                City city=new City();
                city.setId(cursor.getInt(cursor.getColumnIndex("id")));
                city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
                city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
                city.setProvinceId(provinceId);
                list.add(city);
            }while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }
    /**
     * 将County实例存储到数据库
     * */
    public void saveCounty(County county){
        if (county!=null){
            ContentValues contentValues=new ContentValues();
            contentValues.put("county_name",county.getCountyName());
            contentValues.put("county_code",county.getCountyCode());
            contentValues.put("city_id",county.getCityId());
            db.insert("County",null,contentValues);
        }
    }
    /**
     * 从数据库读取某市所有县信息
     * */
    public List<County> loadCounties(int cityId){
        List<County> list=new ArrayList<County>();
        Cursor cursor=db.query("County",null,null,null,null,null,null);
        if (cursor.moveToFirst()){
            do {
                County county=new County();
                county.setId(cursor.getInt(cursor.getColumnIndex("id")));
                county.setCountyCode(cursor.getString(cursor.getColumnIndex("county_code")));
                county.setCountyName(cursor.getString(cursor.getColumnIndex("county_name")));
                county.setCityId(cityId);
                list.add(county);
            }while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }
}
