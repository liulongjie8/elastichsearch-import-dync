package com.westcredit.modules.xkzf.service;

/**
 * 区县级别
 * 
 * @author LLS
 * 
 */
enum AreaLevel {

	COUNTRY("1"), // 国家
	PROVINCE("2"), // 省级
	CITY("3"), // 市级
	COUNTY("4");// 区县

	private String value = "";

	private AreaLevel(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public static AreaLevel levelOf(String value){

		switch (Integer.parseInt(value)) {
			case 1:
				return COUNTRY;
			case 2:
				return PROVINCE;
			case 3:
				return CITY;
			case 4:
				return COUNTY;
			default:
				return null;
		}
	}
}
