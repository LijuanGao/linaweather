package com.linaweather.app.util;

import android.text.TextUtils;

import com.linaweather.app.db.LinaWeatherDB;
import com.linaweather.app.model.City;
import com.linaweather.app.model.Country;
import com.linaweather.app.model.Province;

public class Utility {
	public synchronized static boolean handleProvincesResponse(LinaWeatherDB linaWeatherDB, String response){
		if (!TextUtils.isEmpty(response)) {
			String[] allProvinces = response.split(",");
			if(allProvinces != null && allProvinces.length>0){
				for(String p : allProvinces){
					String [] array = p.split("\\|");
					Province province = new Province();
					province.setProvinceCode(array[0]);
					province.setProvinceName(array[1]);
					//解析的数据存在Province表中
					linaWeatherDB.saveProvince(province);
					
				}
				return true;
			}
		}
		return false;
	}
	
	public synchronized static boolean handleCitiesResponse(LinaWeatherDB linaWeatherDB, String response, int provinceId){
		if(!TextUtils.isEmpty(response)){
			String[] allCities = response.split(",");
			if(allCities !=null && allCities.length>0){
				for(String c: allCities){
					String[] array = c.split("\\|");
					City city = new City();
					city.setCityCode(array[0]);
					city.setCityName(array[1]);
					city.setProvinceId(provinceId);
					//解析的数据保存在City表中
					linaWeatherDB.saveCity(city);
				}
				return true;
			}
		}
		return false;
	}
	
	public synchronized static boolean handleCountriesResponse(LinaWeatherDB linaWeatherDB,String response, int cityId){
		if(!TextUtils.isEmpty(response)){
			String[] allCountries = response.split(",");
			if(allCountries != null && allCountries.length>0){
				for(String c2 : allCountries){
					String[] array = c2.split("\\|");
					Country country = new Country();
					country.setCountryCode(array[0]);
					country.setCountryName(array[1]);
					country.setCityId(cityId);
					//解析的数据存在Country中
					linaWeatherDB.saveCountry(country);
				}
				return true;
			}
	
		}
		return false;
	}

}
