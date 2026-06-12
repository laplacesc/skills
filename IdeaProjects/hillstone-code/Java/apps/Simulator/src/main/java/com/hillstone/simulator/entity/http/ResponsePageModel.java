package com.hillstone.simulator.entity.http;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * @author: xjhuang
 * @date: create in 16:46 2021/11/19
 * @description:
 */
public class ResponsePageModel<T> {
    /**
     * 每页的限定条数
     */
    private int limit;

    /**
     * 当前第几页
     */
    private int page;
    /**
     * 数据的总数
     */
    private int totalCount;

    @JsonProperty(value = "data")
    private List<T> list;

    public ResponsePageModel(){

    }

    public ResponsePageModel(int limit, int page, int totalCount, List<T> list){
        this.limit = limit;
        this.page = page;
        this.totalCount = totalCount;
        this.list = list;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }
}
