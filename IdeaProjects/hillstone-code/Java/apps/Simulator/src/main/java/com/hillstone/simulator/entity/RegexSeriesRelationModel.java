package com.hillstone.simulator.entity;

import com.hillstone.simulator.constant.FWSeries;

/**
 * @author: bohuachen
 * @date: 2023/6/20 15:33
 * @description: some desc
 */
public class RegexSeriesRelationModel {
    private FWSeries series;
    private String regex;

    public RegexSeriesRelationModel(FWSeries series, String regex){
        this.series = series;
        this.regex = regex;
    }

    public FWSeries getSeries() {
        return series;
    }

    public void setSeries(FWSeries series) {
        this.series = series;
    }

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }
}
