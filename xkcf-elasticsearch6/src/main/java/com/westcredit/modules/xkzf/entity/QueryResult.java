package com.westcredit.modules.xkzf.entity;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 查询结果返回封装类
 * 
 * @author LLS
 */
public class QueryResult implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4606730662764226232L;

	/**
	 * 成功
	 */
	public static String SUCCESS = "0";

	/**
	 * 失败
	 */
	public static String FAILURE = "1";

	/**
	 * 连接超时
	 */
	public static String TIMEOUT = "2";

	/**
	 * 返回结果代码
	 */
	private String code;

	/**
	 * 查询数据结果集
	 */
	private List<Map<String, Object>> result;

	/**
	 * 查询结果总条数
	 */
	private Double count;

	/**
	 * 消耗时间
	 */
	private Double time;

	/**
	 * 失败结果说明
	 */
	private String message;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public List<Map<String, Object>> getResult() {
		return result;
	}

	public void setResult(List<Map<String, Object>> result) {
		this.result = result;
	}

	public Double getCount() {
		return count;
	}

	public void setCount(Double count) {
		this.count = count;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Double getTime() {
		return time;
	}

	public void setTime(Double time) {
		this.time = time;
	}
}
