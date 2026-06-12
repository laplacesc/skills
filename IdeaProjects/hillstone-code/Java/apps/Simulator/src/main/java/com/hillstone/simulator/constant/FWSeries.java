package com.hillstone.simulator.constant;

/**
 * @author: bohuachen
 * @date: 2023/6/20 15:31
 * @description: some desc
 */
public enum FWSeries {
    /**
     * 设备类型
     */
    IPS("IPS", "IPS"),
    IDS("IDS", "IDS"),
    BDS("BDS", "BDS"),
    WAF("WAF", "WAF"),
    ADC("ADC", "ADC"),
    VM("VM", "VM"),
    ISOURCE("ISOURCE", "ISOURCE"),
    UES("UES", "UES"),
    FW_T("FW", "T"),
    FW_E("FW", "E"),
    FW_C("FW", "C"),
    FW_X("FW", "X"),
    FW_A("FW", "A"),
    FW_B("FW", "B"),
    FW_K("FW", "K"),
    FW_M("FW", "M"),
    FW_P("FW", "P"),
    FW_Z("FW", "Z"),
    FW_G("FW", "G"),
    FW_F("FW", "F"),
    OTHER("OTHER", "OTHER");

    FWSeries(String series, String subSeries) {
        this.series = series;
        this.subSeries = subSeries;
    }

    private String series;
    private String subSeries;


    public String getSeries() {
        return series;
    }


    public String getSubSeries() {
        return subSeries;
    }


    @Override
    public String toString() {
        return "FWSeries{" +
                "series='" + series + '\'' +
                ", subSeries='" + subSeries + '\'' +
                '}';
    }
}
