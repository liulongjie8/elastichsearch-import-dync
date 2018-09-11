package com.westcredit.modules.xkzf.entity;

import java.io.Serializable;

/**
 * 全文检索 查询类
 * 
 * @author LLS
 * 
 */
public class Query implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String keyWords; // 查询关键字

	private String baseCreditCorpusCode; // 信用主体业务

	private String searchType; // 查询类型

	private String tableId;

	private String tableName;

	public String getKeyWords() {
		return keyWords;
	}

	public void setKeyWords(String keyWords) {
		this.keyWords = keyWords;
	}

	public String getBaseCreditCorpusCode() {
		return baseCreditCorpusCode;
	}

	public void setBaseCreditCorpusCode(String baseCreditCorpusCode) {
		this.baseCreditCorpusCode = baseCreditCorpusCode;
	}

	public String getSearchType() {
		return searchType;
	}

	public void setSearchType(String searchType) {
		this.searchType = searchType;
	}

	public String getTableId() {
		return tableId;
	}

	public void setTableId(String tableId) {
		this.tableId = tableId;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

}
