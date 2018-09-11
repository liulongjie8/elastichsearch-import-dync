package com.westcredit.modules.config;

import java.io.Serializable;
import java.util.List;

public class SyncEntity implements Serializable {

    private String sql;

    private String syncType;

    private List<String> sortFile;

    private String syncFiledName;


    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public String getSyncType() {
        return syncType;
    }

    public void setSyncType(String syncType) {
        this.syncType = syncType;
    }

    public List<String> getSortFile() {
        return sortFile;
    }

    public void setSortFile(List<String> sortFile) {
        this.sortFile = sortFile;
    }


    public String getSyncFiledName() {
        return syncFiledName;
    }

    public void setSyncFiledName(String syncFiledName) {
        this.syncFiledName = syncFiledName;
    }
}
