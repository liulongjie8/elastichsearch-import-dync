package com.westcredit.modules.thread;

/**
 * 线程共享分页数据
 */
public class Page {

    private Integer pageNo =1;

    public synchronized  void add(){
        pageNo++;
    }

    public Integer getPageNo() {
        return pageNo;
    }
}
