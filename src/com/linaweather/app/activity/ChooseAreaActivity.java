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
	
	//ʡ���У����б�
	private List<Province> provinceList;
	private List<City>  cityList;
	private List<Country> countryList;
	
	//ѡ�е�ʡ�ݺͳ���
	private Province selectedProvince;
	private City selectedCity;
	
	//ѡ�еļ���
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
	
	//��ѯʡ�����ݣ����ȴ����ݿ��в飬�鲻���Ļ�ȥ��������ѯ
	private void queryProvinces() {
		provinceList = linaWeatherDB.loadProvinces();
		if(provinceList.size() > 0){
			datalist.clear();
			for(Province province : provinceList){
				datalist.add(province.getProvinceName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText("�й�");
			currentLevel = LEVEL_PROVINCE;
		}else{
			queryFromServer(null,"province");
		}
	}
	
	//��ѯʡ�ڵ��м����ݣ����ȴ����ݿ��в飬�鲻���Ļ�ȥ��������ѯ
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
	
	//��ѯ���ڵ��ؼ����ݣ����ȴ����ݿ��в飬�鲻���Ļ�ȥ��������ѯ
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
	
	//�ӷ�������ѯ����
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
						Toast.makeText(ChooseAreaActivity.this, "����ʧ��", Toast.LENGTH_LONG).show();
					}
				});
			}
		});
	}
	
	//��ʾ�Ի������
	private void showProgressDialog() {
		if(progressDialog == null){
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("���ڼ���...");
			progressDialog.setCanceledOnTouchOutside(false);
		}
		progressDialog.show();
	}
	
	//�رնԻ���
	private void closeProgressDialog() {
		if(progressDialog != null){
			progressDialog.dismiss();
		}
	}
	
	//back����׽
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
