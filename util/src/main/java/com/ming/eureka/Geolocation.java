package com.ming.eureka;

/**
 * 地理位置
 */
public class Geolocation {
	
	/**
	 * 地址
	 */
	private String address;
	
	/**
	 * 省份
	 */
	private String province;
	
	/**
	 * 城市 
	 */
	private String city;
	
	/**
	 * 县 区
	 */
	private String district;
	
	/**
	 * 街道
	 */
	private String street;
	
	/**
	 * 门牌号
	 */
	private String streetNumber;
	
	/**
	 * ip
	 */
	private String ip;
	
	/**
	 * 经度
	 */
	private Double longitude;
	
	/**
	 * 纬度
	 */
	private Double latitude;
	

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getStreetNumber() {
		return streetNumber;
	}

	public void setStreetNumber(String streetNumber) {
		this.streetNumber = streetNumber;
	}
}
