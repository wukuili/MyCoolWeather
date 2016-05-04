package com.liyj.coolweather.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.liyj.coolweather.R;
import com.liyj.coolweather.db.CoolWeatherDB;
import com.liyj.coolweather.model.City;
import com.liyj.coolweather.model.County;
import com.liyj.coolweather.model.Province;
import com.liyj.coolweather.util.HttpCallbackListener;
import com.liyj.coolweather.util.HttpUtil;
import com.liyj.coolweather.util.Utility;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/5/4.
 */
public class ChooseAreaActivity extends Activity {
    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;
    private ListView listView;
    private TextView titleText;
    private ArrayAdapter<String> adapter;
    private List<String> dataList = new ArrayList<String> ();
    private CoolWeatherDB coolWeatherDB;
    /**
     * 省列表
     */
    private List<Province> provinceList;
    /**
     * 市列表
     */
    private List<City> cityList;
    /**
     * 县列表
     */
    private List<County> countyList;
    /**
     * 选中的省份
     */
    private Province selectedProvince;
    /**
     * 选中的城市
     */
    private City selectedCity;
    /**
     * 当前选中的级别
     */
    private int currentLevel;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        requestWindowFeature (Window.FEATURE_NO_TITLE);
        setContentView (R.layout.choose_area);
        listView = (ListView) findViewById (R.id.list_view);
        titleText = (TextView) findViewById (R.id.title_text);
        adapter = new ArrayAdapter<String> (this, android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter (adapter);
        coolWeatherDB = CoolWeatherDB.getInstance (this);
        listView.setOnItemClickListener (new AdapterView.OnItemClickListener () {
            @Override
            public void onItemClick (AdapterView<?> parent, View view, int position, long id) {
                if (currentLevel == LEVEL_PROVINCE) {
                    selectedProvince = provinceList.get (position);
                    Log.e ("city",""+selectedProvince+"sddas"+position);
                    queryCities ();


                } else if (currentLevel == LEVEL_CITY) {
                    selectedCity = cityList.get (position);

                    queryCounties ();


                }
            }
        });
        queryProvinces ();


    }

    private void queryProvinces () {
        provinceList = coolWeatherDB.loadProvinces ();
        if (provinceList.size () > 0) {
            dataList.clear ();
            for (Province province : provinceList
                    ) {
                dataList.add (province.getProvinceName ());
            }
            adapter.notifyDataSetChanged ();
            listView.setSelection (0);
            titleText.setText ("中国");
            currentLevel = LEVEL_PROVINCE;
        } else {
            queryFromServer (null, "province");
        }
    }


    private void queryCounties () {
        countyList = coolWeatherDB.loadCounties (selectedCity.getId ());
        if (countyList.size () > 0) {
            dataList.clear ();
            for (County county : countyList
                    ) {
                dataList.add (county.getCountyName ());

            }
            adapter.notifyDataSetChanged ();
            listView.setSelection (0);
            titleText.setText (selectedCity.getCityName ());
            currentLevel = LEVEL_COUNTY;
        } else {
            queryFromServer (selectedCity.getCityCode (), "county");
        }
    }

    private void queryCities () {
        cityList = coolWeatherDB.loadCities (selectedProvince.getId ());
        Log.e ("city", "city" + cityList);
        if (cityList.size () > 0) {
            dataList.clear ();
            for (City city :
                    cityList) {
                dataList.add (city.getCityName ());
            }
            adapter.notifyDataSetChanged ();
            listView.setSelection (0);
            titleText.setText (selectedProvince.getProvinceName ());
            currentLevel = LEVEL_CITY;
        } else {
            queryFromServer (selectedProvince.getProvinceCode (), "city");
        }
    }

    private void queryFromServer (final String code, final String type) {
        String address;
        if (!TextUtils.isEmpty (code)) {
            address = "http://www.weather.com.cn/data/list3/city" + code + ".xml";
        } else {
            address = "http://www.weather.com.cn/data/list3/city.xml";
        }
        showProgressDialog ();

        HttpUtil.sendHttpRequest (address, new HttpCallbackListener () {
            @Override
            public void onFinish (String response) {
                boolean result = false;
                if ("province".equals (type)) {
                    result = Utility.handleProvincesResponse (coolWeatherDB, response);
                } else if ("city".equals (type)) {
                    result = Utility.handleCitiesResponse (coolWeatherDB, response, selectedProvince.getId ());
                    Log.e ("city", "" + result);
                } else if ("county".equals (type)) {
                    result = Utility.handleCountiesResponse (coolWeatherDB, response, selectedCity.getId ());
                }
                if (result) {
                    //通过runOnUiThread（）方法回到主线程处理逻辑
                    runOnUiThread (new Runnable () {
                        @Override
                        public void run () {
                            closeProgressDialog ();
                            if ("province".equals (type)) {
                                queryProvinces ();
                            } else if ("city".equals (type)) {
                                queryCities ();
                            } else if ("county".equals (type)) {
                                queryCounties ();
                            }
                        }
                    });
                }
            }

            @Override
            public void onError (final Exception e) {
//通过runOnUiThread（）方法回到主线程处理逻辑
                runOnUiThread (new Runnable () {
                    @Override
                    public void run () {
                        closeProgressDialog ();
                        Toast.makeText (ChooseAreaActivity.this, "加载失败" + e, Toast.LENGTH_LONG).show ();
                        Log.e ("weather", "失败了" + e);
                    }
                });
            }
        });
    }

    private void closeProgressDialog () {
        if (progressDialog != null) {
            progressDialog.dismiss ();
        }
    }

    /**
     * 显示进度对话框
     */
    private void showProgressDialog () {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog (this);
            progressDialog.setMessage ("正在加载...");
            progressDialog.setCanceledOnTouchOutside (false);
        }
        progressDialog.show ();
    }

    @Override
    public void onBackPressed () {
        if (currentLevel == LEVEL_COUNTY) {
            queryCities ();
        } else if (currentLevel == LEVEL_CITY) {
            queryProvinces ();
        } else {
            finish ();
        }
    }
}
