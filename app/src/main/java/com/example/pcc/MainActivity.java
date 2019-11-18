package com.example.pcc;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private List<JsonBean>  beans;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        beans = new ArrayList<>();

        String province = getJson(this, "province.json");
        String city = getJson(this, "city.json");
        String county = getJson(this, "county.json");


        try {
            List<Province> provinces = parseData(province);
            for (int index = 0; index < provinces.size(); index++) {

                JsonBean bean = new JsonBean();

                Province province1 = provinces.get(index);
                bean.setName(province1.getName());
                beans.add(bean);

                //省份下的城市
                JSONObject cityObject = new JSONObject(city);
                JSONArray cityArray = cityObject.getJSONArray(province1.getId());
                List<City> cityList = new ArrayList<>();
                List<JsonBean.CityBean> cityBeans = new ArrayList<>();
                //将省份转换成City对象
                for(int cityIndex = 0; cityIndex < cityArray.length(); cityIndex ++)
                {
                    JSONObject tmpCity = cityArray.getJSONObject(cityIndex);
                    City city1 = new City();
                    city1.setProvince(tmpCity.getString("province"));
                    city1.setName(tmpCity.getString("name"));
                    city1.setId(tmpCity.getString("id"));
                    cityList.add(city1);

                    JsonBean.CityBean cityBean = new JsonBean.CityBean();
                    cityBean.setName(city1.getName());
                    cityBeans.add(cityBean);
                }
                bean.setCityList(cityBeans);

                //城市下面的区
                for(int cityIndex = 0; cityIndex < cityList.size(); cityIndex++)
                {
                    City city1 = cityList.get(cityIndex);
                    JsonBean.CityBean cityBean = cityBeans.get(cityIndex);
                    //城市下面的JSON
                    JSONObject countyObject = new JSONObject(county);
                    List<String> areas = new ArrayList<>();
                    if(countyObject.has(city1.getId())) {
                        JSONArray countyArray = countyObject.getJSONArray(city1.getId());
                        //遍历JSONArray,转换成County
                        List<County> countyList = new ArrayList<>();

                        for (int countyIndex = 0; countyIndex < countyArray.length(); countyIndex++) {
                            JSONObject object = countyArray.getJSONObject(countyIndex);
                            County county1 = new County();
                            county1.setCity(object.getString("city"));
                            county1.setName(object.getString("name"));
                            county1.setId(object.getString("id"));
                            countyList.add(county1);

                            areas.add(county1.getName());
                        }
                    }else
                    {
                        areas.add(city1.getName());
                    }

                    cityBean.setArea(areas);
                }
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }


        Gson gson = new Gson();
        String jsonString = gson.toJson(beans);
        Log.i("MainActivity", "palldata cityBean = " + jsonString);
    }

    public String getJson(Context context, String fileName) {

        StringBuilder stringBuilder = new StringBuilder();
        try {
            AssetManager assetManager = context.getAssets();
            BufferedReader bf = new BufferedReader(new InputStreamReader(
                    assetManager.open(fileName)));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    public ArrayList<Province> parseData(String result) {//Gson 解析
        ArrayList<Province> detail = new ArrayList<>();
        try {
            JSONArray data = new JSONArray(result);
            Gson gson = new Gson();
            for (int i = 0; i < data.length(); i++) {
                Province entity = gson.fromJson(data.optJSONObject(i).toString(), Province.class);
                detail.add(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return detail;
    }


}
