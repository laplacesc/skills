package com.hillstone.simulator.mocker.avroimpl;

import com.hillstone.simulator.config.DeviceInfoConfig;
import com.hillstone.simulator.constant.AvroConstant;
import com.hillstone.simulator.entity.avro.model.statistics_set_dynamic.traffic_rank.traffic_rank;
import com.hillstone.simulator.mocker.IAvroMocker;
import com.hillstone.simulator.utils.AvroUtil;
import com.hillstone.simulator.utils.NameMocker;
import com.hillstone.simulator.utils.NumberMocker;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: bohuachen
 * @date: 2023/6/19 5:51
 * @description: some desc
 */
public class TrafficRankMocker extends IAvroMocker {


    public TrafficRankMocker() {
        super();
    }

    @Override
    public String getCategory() {
        return AvroConstant.STATISTICS_SET_DYNAMIC_CATEGORY;
    }

    @Override
    public String getType() {
        return AvroConstant.TRAFFIC_RANK_TYPE;
    }

    @Override
    public String getAvroFileName() {
        return AvroConstant.TRAFFIC_RANK_FILENAME;
    }

    @Override
    public String getAvroMd5() {
        return AvroConstant.TRAFFIC_RANK_MD5;
    }

    @Override
    public Integer getTaskInterval() {
        return 600;
    }

    @Override
    public String getAvroFilePath() {
        return AvroConstant.BASE_AVSC_PATH + "/" + this.getCategory() + "/" + this.getType() + "/" + this.getAvroFileName();
    }

    public traffic_rank getNoDataAvro() {
        //字段很多，这些字段目前都没有用所以随便写点值
        traffic_rank trafficRank = new traffic_rank();
        trafficRank.setType("123");
        trafficRank.setAppGroup("");
        trafficRank.setAddressBook("");
        trafficRank.setSortBy(1L);
        trafficRank.setTimeRange(1L);
        trafficRank.setWithOther(1);
        trafficRank.setVr("");
        trafficRank.setAaaServer("");
        trafficRank.setPhystatus(1);
        trafficRank.setAdminstatus(1);
        trafficRank.setLinkstatus(1);
        trafficRank.setProtostatus(1);
        trafficRank.setIpOfIf("");
        trafficRank.setIpmaskOfIf(1);
        trafficRank.setZoneOfIf("");
        trafficRank.setZoneIfId(1);
        trafficRank.setUpStream(1L);
        trafficRank.setDownStream(1L);
        trafficRank.setAllUpStream(1L);
        trafficRank.setAllDownStream(1L);
        trafficRank.setSessions(1L);

        trafficRank.setUpPct(1L);
        trafficRank.setDownPct(1L);
        trafficRank.setTotalPct(1L);
        trafficRank.setSessionsPct(1L);
        return trafficRank;
    }

    @Override
    public byte[] createDataFile(DeviceInfoConfig device) throws Exception {


        //统计集数据多少有点复杂，同样的schema数据，当字段不同时代表了不同类型的数据
        List<traffic_rank> list = new ArrayList<>();
        //zone
        for (int i = 0; i < NameMocker.ZONENAMES.length; i++) {
            traffic_rank zoneRank = new traffic_rank();
            zoneRank.setType("");
            zoneRank.setUser("");
            zoneRank.setInterface$("");
            zoneRank.setAppName("");
            zoneRank.setZoneName(NameMocker.ZONENAMES[i]);
            zoneRank.setUpBw(NumberMocker.getInt(1000000000));
            zoneRank.setDownBw(NumberMocker.getInt(1000000000));
            zoneRank.setAppGroup("");
            zoneRank.setAddressBook("");
            zoneRank.setSortBy(1L);
            zoneRank.setTimeRange(System.currentTimeMillis());
            zoneRank.setWithOther(1);
            zoneRank.setVr("");
            zoneRank.setAaaServer("");
            zoneRank.setPhystatus(1);
            zoneRank.setAdminstatus(1);
            zoneRank.setLinkstatus(1);
            zoneRank.setProtostatus(1);
            zoneRank.setIpOfIf("");
            zoneRank.setIpmaskOfIf(1);
            zoneRank.setZoneOfIf("");
            zoneRank.setZoneIfId(1);
            zoneRank.setUpStream(1L);
            zoneRank.setDownStream(1L);

            zoneRank.setAllUpStream(NumberMocker.getInt(1000000000));
            zoneRank.setAllDownStream(NumberMocker.getInt(1000000000));
            zoneRank.setSessions(NumberMocker.getInt(1000000000));
            zoneRank.setUpPct(NumberMocker.getInt(1000000000));
            zoneRank.setDownPct(NumberMocker.getInt(1000000000));
            zoneRank.setTotalPct(NumberMocker.getInt(1000000000));
            zoneRank.setSessionsPct(NumberMocker.getInt(1000000000));
            list.add(zoneRank);
        }
        //interface
        for (int i = 0; i < NameMocker.IFNAMES.length; i++) {
            traffic_rank ifRank = new traffic_rank();
            ifRank.setType("");
            ifRank.setAppGroup("");
            ifRank.setAddressBook("");
            ifRank.setSortBy(1L);
            ifRank.setTimeRange(System.currentTimeMillis());
            ifRank.setWithOther(1);
            ifRank.setVr("");
            ifRank.setAaaServer("");
            ifRank.setPhystatus(1);
            ifRank.setAdminstatus(1);
            ifRank.setLinkstatus(1);
            ifRank.setProtostatus(1);
            ifRank.setIpOfIf("");
            ifRank.setIpmaskOfIf(1);
            ifRank.setZoneOfIf("");
            ifRank.setZoneIfId(1);
            ifRank.setUpStream(1L);
            ifRank.setDownStream(1L);

            ifRank.setAllUpStream(NumberMocker.getInt(1000000000));
            ifRank.setAllDownStream(NumberMocker.getInt(1000000000));
            ifRank.setSessions(NumberMocker.getInt(1000000000));
            ifRank.setUpPct(NumberMocker.getInt(1000000000));
            ifRank.setDownPct(NumberMocker.getInt(1000000000));
            ifRank.setTotalPct(NumberMocker.getInt(1000000000));
            ifRank.setSessionsPct(NumberMocker.getInt(1000000000));
            ifRank.setUser("");
            ifRank.setAppName("");
            ifRank.setZoneName("");
            ifRank.setInterface$(NameMocker.IFNAMES[i]);
            ifRank.setUpBw(NumberMocker.getInt(1000000000));
            ifRank.setDownBw(NumberMocker.getInt(1000000000));
            list.add(ifRank);
        }
        //user(Ip)
        for (int i = 0; i < NameMocker.IPNAMES.length; i++) {
            traffic_rank userIP = new traffic_rank();
            userIP.setType("");
            userIP.setUser(NameMocker.IPNAMES[i]);
            userIP.setAppName("");
            userIP.setZoneName("");
            userIP.setInterface$("");
            userIP.setUpBw(NumberMocker.getInt(1000000000));
            userIP.setDownBw(NumberMocker.getInt(1000000000));
            userIP.setAppGroup("");
            userIP.setAddressBook("");
            userIP.setSortBy(1L);
            userIP.setTimeRange(System.currentTimeMillis());
            userIP.setWithOther(1);
            userIP.setVr("");
            userIP.setAaaServer("");
            userIP.setPhystatus(1);
            userIP.setAdminstatus(1);
            userIP.setLinkstatus(1);
            userIP.setProtostatus(1);
            userIP.setIpOfIf("");
            userIP.setIpmaskOfIf(1);
            userIP.setZoneOfIf("");
            userIP.setZoneIfId(1);
            userIP.setUpStream(1L);
            userIP.setDownStream(1L);

            userIP.setAllUpStream(NumberMocker.getInt(1000000000));
            userIP.setAllDownStream(NumberMocker.getInt(1000000000));
            userIP.setSessions(NumberMocker.getInt(1000000000));
            userIP.setUpPct(NumberMocker.getInt(1000000000));
            userIP.setDownPct(NumberMocker.getInt(1000000000));
            userIP.setTotalPct(NumberMocker.getInt(1000000000));
            userIP.setSessionsPct(NumberMocker.getInt(1000000000));
            list.add(userIP);
        }
        //app
        for (int i = 0; i < NameMocker.APPNAMES.length; i++) {
            traffic_rank app = new traffic_rank();
            app.setType("");
            app.setUser("");
            app.setAppName(NameMocker.APPNAMES[i]);
            app.setZoneName("");
            app.setInterface$("");
            app.setUpBw(NumberMocker.getInt(1000000000));
            app.setDownBw(NumberMocker.getInt(1000000000));
            app.setAppGroup("");
            app.setAddressBook("");
            app.setSortBy(1L);
            app.setTimeRange(System.currentTimeMillis());
            app.setWithOther(1);
            app.setVr("");
            app.setAaaServer("");
            app.setPhystatus(1);
            app.setAdminstatus(1);
            app.setLinkstatus(1);
            app.setProtostatus(1);
            app.setIpOfIf("");
            app.setIpmaskOfIf(1);
            app.setZoneOfIf("");
            app.setZoneIfId(1);
            app.setUpStream(1L);
            app.setDownStream(1L);

            app.setAllUpStream(NumberMocker.getInt(1000000000));
            app.setAllDownStream(NumberMocker.getInt(1000000000));
            app.setSessions(NumberMocker.getInt(1000000000));
            app.setUpPct(NumberMocker.getInt(1000000000));
            app.setDownPct(NumberMocker.getInt(1000000000));
            app.setTotalPct(NumberMocker.getInt(1000000000));
            app.setSessionsPct(NumberMocker.getInt(1000000000));
            list.add(app);
        }
        //app+user
        for (int i = 0; i < NameMocker.APPNAMES.length; i++) {
            for (int j = 0; j < NameMocker.IPNAMES.length; j++) {
                traffic_rank ipAndApp = new traffic_rank();
                ipAndApp.setType("");
                ipAndApp.setUser(NameMocker.IPNAMES[j]);
                ipAndApp.setAppName(NameMocker.APPNAMES[i]);
                ipAndApp.setZoneName("");
                ipAndApp.setInterface$("");
                ipAndApp.setUpBw(NumberMocker.getInt(1000000000));
                ipAndApp.setDownBw(NumberMocker.getInt(1000000000));
                ipAndApp.setAppGroup("");
                ipAndApp.setAddressBook("");
                ipAndApp.setSortBy(1L);
                ipAndApp.setTimeRange(System.currentTimeMillis());
                ipAndApp.setWithOther(1);
                ipAndApp.setVr("");
                ipAndApp.setAaaServer("");
                ipAndApp.setPhystatus(1);
                ipAndApp.setAdminstatus(1);
                ipAndApp.setLinkstatus(1);
                ipAndApp.setProtostatus(1);
                ipAndApp.setIpOfIf("");
                ipAndApp.setIpmaskOfIf(1);
                ipAndApp.setZoneOfIf("");
                ipAndApp.setZoneIfId(1);
                ipAndApp.setUpStream(1L);
                ipAndApp.setDownStream(1L);

                ipAndApp.setAllUpStream(NumberMocker.getInt(1000000000));
                ipAndApp.setAllDownStream(NumberMocker.getInt(1000000000));
                ipAndApp.setSessions(NumberMocker.getInt(1000000000));
                ipAndApp.setUpPct(NumberMocker.getInt(1000000000));
                ipAndApp.setDownPct(NumberMocker.getInt(1000000000));
                ipAndApp.setTotalPct(NumberMocker.getInt(1000000000));
                ipAndApp.setSessionsPct(NumberMocker.getInt(1000000000));
                list.add(ipAndApp);
            }
        }

        return AvroUtil.serializeAvroNoHeaderByObject(traffic_rank.getClassSchema(),list);
    }

}
