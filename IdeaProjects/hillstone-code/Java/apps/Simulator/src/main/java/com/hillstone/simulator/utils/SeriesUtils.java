package com.hillstone.simulator.utils;

import com.hillstone.simulator.constant.FWSeries;
import com.hillstone.simulator.constant.SeriesRegexConstant;
import com.hillstone.simulator.entity.RegexSeriesRelationModel;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author: bohuachen
 * @date: 2023/6/20 15:33
 * @description: some desc
 */
public class SeriesUtils {
    private SeriesUtils() {
    }

    private static final RegexSeriesRelationModel[] list = new RegexSeriesRelationModel[]{
            new RegexSeriesRelationModel(FWSeries.IPS, SeriesRegexConstant.IPS_DEVICE_REGEX),
            new RegexSeriesRelationModel(FWSeries.IDS, SeriesRegexConstant.IDS_DEVICE_REGEX),
            new RegexSeriesRelationModel(FWSeries.BDS, SeriesRegexConstant.BDS_DEVICE_REGEX),
            new RegexSeriesRelationModel(FWSeries.WAF, SeriesRegexConstant.WAF_DEVICE_REGEX),
            new RegexSeriesRelationModel(FWSeries.ADC, SeriesRegexConstant.ADC_DEVICE_REGEX),
            new RegexSeriesRelationModel(FWSeries.VM, SeriesRegexConstant.VM_DEVICE_REGEX),
            new RegexSeriesRelationModel(FWSeries.ISOURCE, SeriesRegexConstant.ISOURCE_DEVICE_REGEX),
            new RegexSeriesRelationModel(FWSeries.UES, SeriesRegexConstant.UES_DEVICE_REGEX),
            new RegexSeriesRelationModel(FWSeries.FW_T, SeriesRegexConstant.FW_T_DEVICE_REGEX),
            new RegexSeriesRelationModel(FWSeries.FW_E, SeriesRegexConstant.FW_E_DEVICE_REGEX),
            new RegexSeriesRelationModel(FWSeries.FW_C, SeriesRegexConstant.FW_C_DEVICE_REGEX),
            new RegexSeriesRelationModel(FWSeries.FW_X, SeriesRegexConstant.FW_X_DEVICE_REGEX),
            new RegexSeriesRelationModel(FWSeries.FW_A, SeriesRegexConstant.FW_A_DEVICE_REGEX),
            new RegexSeriesRelationModel(FWSeries.FW_B, SeriesRegexConstant.FW_B_DEVICE_REGEX),
            new RegexSeriesRelationModel(FWSeries.FW_K, SeriesRegexConstant.FW_K_DEVICE_REGEX),
            new RegexSeriesRelationModel(FWSeries.FW_M, SeriesRegexConstant.FW_M_DEVICE_REGEX),
            new RegexSeriesRelationModel(FWSeries.FW_P, SeriesRegexConstant.FW_P_DEVICE_REGEX),
            new RegexSeriesRelationModel(FWSeries.FW_Z, SeriesRegexConstant.FW_Z_DEVICE_REGEX),
            new RegexSeriesRelationModel(FWSeries.FW_G, SeriesRegexConstant.FW_G_DEVICE_REGEX),
            new RegexSeriesRelationModel(FWSeries.FW_F, SeriesRegexConstant.FW_F_DEVICE_REGEX)
    };

    public static FWSeries getPlatformSeries(String platform) {
        for (RegexSeriesRelationModel model : SeriesUtils.list) {
            Pattern pattern = Pattern.compile(model.getRegex());
            Matcher matcher = pattern.matcher(platform);
            if (matcher.matches()) {
                return model.getSeries();
            }
        }
        return FWSeries.OTHER;
    }
}
