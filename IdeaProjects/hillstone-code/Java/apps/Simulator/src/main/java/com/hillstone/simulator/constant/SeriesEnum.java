package com.hillstone.simulator.constant;

import java.util.ArrayList;
import java.util.List;

/**
 * @author bohuachen
 * @date 2025/4/2 9:47
 * @description
 */
public enum SeriesEnum {

    /**
     * 设备类型
     */
    IPS("IPS"),
    IDS("IDS"),
    BDS("BDS"),
    WAF("WAF"),
    ADC("ADC"),
    VM("VM"),
    ISOURCE("ISOURCE"),
    UES("UES"),
    FW_T("FW"),
    OTHER("OTHER");;

    private String series;

    SeriesEnum(String series) {
        this.series = series;
    }

    public String getSeries() {
        return series;
    }


    private static final List<String> seriesList = new ArrayList<>();

    static {
        for (SeriesEnum seriesEnum : SeriesEnum.values()) {
            seriesList.add(seriesEnum.getSeries());
        }
    }

    public static List<String> getSeriesList() {
        return seriesList;
    }

}
