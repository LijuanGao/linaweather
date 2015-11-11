package com.linaweather.app.model;

public class Country {
	private int id;
	private String countryName;
	private String countryCode;
	public int  cityId;
	
	public int  getId(){
		return id;
	}
	public void setId(int id){
		this.id=id;
	}
	
	public String getCountyrName(){
		return countryName;
	}
	public void setCountryName(String countryName){
		this.countryName = countryName;
	}
	
	public String getCountryCode(){
		return countryCode;
	}
	public void setCountryCode(String countryCode){
		this.countryCode = countryCode;
	}
	
	public int  getCityId(){
		return cityId;
	}
	public void setCityId(int cityId){
		this.cityId=cityId;
	}

}
