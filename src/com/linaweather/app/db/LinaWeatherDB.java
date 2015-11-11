package com.linaweather.app.db;


import java.util.ArrayList;
import java.util.List;

import com.linaweather.app.model.City;
import com.linaweather.app.model.Country;
import com.linaweather.app.model.Province;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class LinaWeatherDB {
	
	//数据库名字
	public static final String DB_NAME = "lina_weather";
	//数据库版本
	public static final int VERSION = 1;
	private static LinaWeatherDB linaWeatherDB;
	private SQLiteDatabase db;
	private  LinaWeatherDB(Context context) {
		LinaWeatherOpenHelper dbHelper = new LinaWeatherOpenHelper(context, DB_NAME, null, VERSION);
		db = dbHelper.getWritableDatabase();	
	}
	
	//获取数据库的实例
	public synchronized static LinaWeatherDB getInstance(Context context){
		if(linaWeatherDB == null){
			linaWeatherDB = new LinaWeatherDB(context);
		}
		return linaWeatherDB;
	}
	
	//将Province实例存储到数据库中
	public void saveProvince(Province province){
		if(province != null){
			ContentValues values = new ContentValues();
			values.put("provinceName", province.getProvinceName());
			values.put("provinceCode", province.getProvinceCode());
			db.insert("Province", null, values);
		}
	}
	
	//从数据库中读取省份信息
	public List<Province> loadProvinces(){
		List<Province> list = new ArrayList<Province>();
		Cursor cursor = db.query("Province", null, null, null, null, null, null);
		if(cursor.moveToFirst()){
			Province province = new Province();
			province.setId(cursor.getInt(cursor.getColumnIndex("id")));
			province.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
			province.setProvinceCode(cursor.getString(cursor.getColumnIndex("province_code")));
			list.add(province);
		}while(cursor.moveToNext());
		if(cursor != null){
			cursor.close();
		}
		return list;
	}
	
	//将City实例存储到数据库中
	public void  saveCity(City city){
		if(city != null){
			ContentValues values = new ContentValues();
			values.put("cityName", city.getCityName());
			values.put("cityCode", city.getCityCode());
			values.put("provinceId", city.getProvinceId());
			db.insert("City", null, values);
		}
	}
	
	//从数据库中读取City信息
	public List<City> loadCities(){
		List<City> list = new ArrayList<City>();
		Cursor cursor = db.query("City", null, "province_id=?", null, null, null, null);
		if(cursor.moveToFirst()){
			do{
				City city = new City();
				city.setId(cursor.getInt(cursor.getColumnIndex("id")));
				city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
				city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
				city.setProvinceId(cursor.getInt(cursor.getColumnIndex("procince_id")));
				list.add(city);
			}while(cursor.moveToNext());
		}
		if(cursor != null){
			cursor.close();
		}
		return list;
	}
	
	//将Country实例存储到数据库中
	public void  saveCountry(Country country){
		if(country != null){
			ContentValues values = new ContentValues();
			values.put("countryName", country.getCountyrName());
			values.put("countryCode", country.getCountryCode());
			values.put("cityId", country.getCityId());
			db.insert("Country", null, values);
		}
	}
	
	//从数据库中读取Country信息
	public List<Country> loadCountries(){
		List<Country> list = new ArrayList<Country>();
		Cursor cursor = db.query("Country", null, "city_id = ?", null, null, null, null);
		if(cursor.moveToFirst()){
			do{
				Country country = new Country();
				country.setId(cursor.getInt(cursor.getColumnIndex("id")));
				country.setCountryName(cursor.getString(cursor.getColumnIndex("country_name")));
				country.setCountryCode(cursor.getString(cursor.getColumnIndex("country_code")));
				country.setCityId(cursor.getInt(cursor.getColumnIndex("city_id")));
				list.add(country);
			}while(cursor.moveToNext());
		}
		if(cursor != null){
			cursor.close();
		}
		return list;
	}

}
