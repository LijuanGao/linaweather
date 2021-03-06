package com.linaweather.app.activity;

import java.util.ArrayList;
import java.util.List;

import com.linaweather.app.R;
import com.linaweather.app.db.LinaWeatherDB;
import com.linaweather.app.model.City;
import com.linaweather.app.model.Country;
import com.linaweather.app.model.Province;
import com.linaweather.app.util.HttpCallbackListener;
import com.linaweather.app.util.HttpUtil;
import com.linaweather.app.util.Utility;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ChooseAreaActivity extends Activity {
	public static final  int LEVEL_PROVINCE = 0;
	public static final int LEVEL_CITY = 1;
	public static final int LEVEL_COUNTRY = 2;
	
	private ProgressDialog progressDialog;
	private TextView titleText;
	private ListView listView;
	private ArrayAdapter<String>  adapter;
	private LinaWeatherDB linaWeatherDB;
	private List<String> datalist = new ArrayList<String>();
	
	//省，市，县列表
	private List<Province> provinceList;
	private List<City>  cityList;
	private List<Country> countryList;
	
	//选中的省份和城市
	private Province selectedProvince;
	private City selectedCity;
	
	//选中的级别
	private int currentLevel;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.choose_area);
		listView = (ListView) findViewById(R.id.list_view);
		titleText = (TextView) findViewById(R.id.title_text);
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,datalist);
		listView.setAdapter(adapter);
		linaWeatherDB = LinaWeatherDB.getInstance(this);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
		public void onItemClick(AdapterView<?> arg0, View view, int index,
					long arg3) {
				// TODO Auto-generated method stub
				if(currentLevel == LEVEL_PROVINCE){
					selectedProvince = provinceList.get(index);
					queryCities();
				}else if (currentLevel == LEVEL_CITY) {
					selectedCity = cityList.get(index);
					queryCountries();
				}
				
		}
		});
		queryProvinces();
	}
	
	//查询省级数据，优先从数据库中查，查不到的话去服务器查询
	private void queryProvinces() {
		provinceList = linaWeatherDB.loadProvinces();
		if(provinceList.size() > 0){
			datalist.clear();
			for(Province province : provinceList){
				datalist.add(province.getProvinceName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText("中国");
			currentLevel = LEVEL_PROVINCE;
		}else{
			queryFromServer(null,"province");
		}
	}
	
	//查询省内的市级数据，优先从数据库中查，查不到的话去服务器查询
	private void queryCities() {
		cityList = linaWeatherDB.loadCities(selectedProvince.getId());
		if(cityList.size() > 0){
			datalist.clear();
			for(City city : cityList){
				datalist.add(city.getCityName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText(selectedProvince.getProvinceName());
			currentLevel = LEVEL_CITY;
		}else {
			queryFromServer(selectedProvince.getProvinceCode(), "city");
		}
	}
	
	//查询市内的县级数据，优先从数据库中查，查不到的话去服务器查询
	private void queryCountries() {
		countryList = linaWeatherDB.loadCountries(selectedCity.getId());
		if(countryList.size() > 0){
			datalist.clear();
			for(Country country : countryList){
				datalist.add(country.getCountyrName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText(selectedCity.getCityName());
			currentLevel = LEVEL_COUNTRY;
		}else{
			queryFromServer(selectedCity.getCityCode(), "country");
		}
	}
	
	//从服务器查询数据
	private void queryFromServer(final String code, final String type) {
		String address;
		if(!TextUtils.isEmpty(code)){
			address = "http://www.weather.com.cn/data/list3/city" + "code" + ".xml";
		}else{
			address = "http://www.weather.com.cn/data/list3/city.xml";
		}
		showProgressDialog();
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			
			@Override
			public void onFinish(String response) {
				// TODO Auto-generated method stub
				boolean result = false;
				if("province".equals(type)){
					result = Utility.handleProvincesResponse(linaWeatherDB, response);
				}else if ("city".equals(type)) {
					result = Utility.handleCitiesResponse(linaWeatherDB, response, selectedProvince.getId());
				}else if ("country".equals(type)) {
					result = Utility.handleCountriesResponse(linaWeatherDB, response, selectedCity.getId());
				}
				if(result){
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							closeProgressDialog();
							if("province".equals(type)){
								queryProvinces();
							}else if ("city".equals(type)) {
								queryCities();
							}else if ("country".equals(type)) {
								queryCountries();
							}
						}
					});
				}
			}
			
			@Override
			public void onError(Exception e) {
				// TODO Auto-generated method stub
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						closeProgressDialog();
						Toast.makeText(ChooseAreaActivity.this, "加载失败", Toast.LENGTH_LONG).show();
					}
				});
			}
		});
	}
	
	//显示对话框进度
	private void showProgressDialog() {
		if(progressDialog == null){
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("正在加载...");
			progressDialog.setCanceledOnTouchOutside(false);
		}
		progressDialog.show();
	}
	
	//关闭对话框
	private void closeProgressDialog() {
		if(progressDialog != null){
			progressDialog.dismiss();
		}
	}
	
	//back键捕捉
	public void onBackPressed() {
		if(currentLevel == LEVEL_COUNTRY){
			queryCities();
		}else if (currentLevel == LEVEL_CITY) {
			queryProvinces();
		}else{
			finish();
		}
	}

}
