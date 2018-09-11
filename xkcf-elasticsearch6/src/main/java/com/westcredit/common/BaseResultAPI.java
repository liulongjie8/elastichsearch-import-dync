/**
 * Copyright (DigitalChina) 2016-2020, DigitalChina.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.westcredit.common;

import java.util.List;
import java.util.Map;

/**
 * 基础API返回结果
 * 
 * @author zuoyue
 * @version V1.0 创建时间：2015年6月16日 下午5:18:33 Copyright 2015 by DigitalChina
 */
public class BaseResultAPI {
	/**
	 * 接口调用异常状态码
	 */
	public static final String ERROR_CODE = "-1";

	/**
	 * 处理异常提示信息
	 */
	public static final String ERROR_MSG = "处理异常";

	/**
	 * 接口调用成功状态码
	 */
	public static final String SUCCESS_CODE = "0";

	/**
	 * 接口调用成功信息
	 */
	public static final String SUCCESS_MSG = "处理成功";

	/**
	 * 接口调用无权限状态码
	 */
	public static final String FORBIDDEN_CODE = "403";

	/**
	 * 返回码信息，默认值为:无权限获取数据
	 */
	public static final String FORBIDDEN_MSG = "无权限获取数据";

	/**
	 * 返回码：0：成功；非0：失败；默认值为0
	 */
	private String resultCode = SUCCESS_CODE;
	/**
	 * 返回码信息，默认值为:查询成功
	 */
	private String resultInfo = SUCCESS_MSG;
	/**
	 * 数据
	 */
	private List<Map<String, Object>> datas;
	/**
	 * 总条数
	 */
	private Integer count;

	public BaseResultAPI() {

	}

	public BaseResultAPI(String resultCode, String resultInfo) {
		this.setResultCode(resultCode);
		this.setResultInfo(resultInfo);
	}

	public List<Map<String, Object>> getDatas() {
		return datas;
	}

	public void setDatas(List<Map<String, Object>> list) {
		this.datas = list;
	}

	public String getResultCode() {
		return resultCode;
	}

	public void setResultCode(String resultCode) {
		this.resultCode = resultCode;
	}

	public String getResultInfo() {
		return resultInfo;
	}

	public void setResultInfo(String resultInfo) {
		this.resultInfo = resultInfo;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

}
