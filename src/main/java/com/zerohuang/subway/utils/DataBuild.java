package com.zerohuang.subway.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zerohuang.subway.models.Line;
import com.zerohuang.subway.models.MyArrayList;
import com.zerohuang.subway.models.SimpleStation;
import com.zerohuang.subway.models.Station;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * @author ZeroHuang
 * @date 2020/10/29 2:32 下午
 */
public class DataBuild {
    private static final String URL = "http://map.amap.com/service/subway?_1603675241452&srhdata=3201_drw_nanjing.json";

    public static final MyArrayList<Line> LINES = new MyArrayList<>();

    public static void init(){
        RestTemplate restTemplate = new RestTemplate();
        JSONObject result = restTemplate.getForObject(URL, JSONObject.class);
        if (result != null) {
            int index = 0;
            JSONArray jsonLines = result.getJSONArray("l");
            for (Object o:jsonLines) {
                Line line = new Line();
                JSONObject jsonLine = new JSONObject((Map<String, Object>) o);
                JSONArray jsonStations = jsonLine.getJSONArray("st");
                String ln = jsonLine.getString("ln");
                if (ln.equals("S9号线(宁高线)") || ln.equals("S1号线(机场线)") || ln.equals("S7号线(宁溧线)")){
                    continue;
                }
                line.setId(index++);
                line.setName(ln);
                setStation(line, jsonStations);
                line.getStations().trimToSize();
                LINES.add(line);
            }
        }
    }

    public static void setStation(Line line, JSONArray array){
        MyArrayList<Long> stationList = new MyArrayList<>();
        MyArrayList<Station> stations = new MyArrayList<>();

        for (Object o:array) {
            Station station = new Station();
            JSONObject jsonStation = new JSONObject((Map<String, Object>) o);
            Long stationId = Long.parseLong(jsonStation.getString("sid"));
            String stationName = jsonStation.getString("n");
            station.setId(stationId);
            station.setName(stationName);
            station.setSPin(jsonStation.getString("sp"));
            station.setLId(line.getId());
            station.setLine(line.getName());
            station.setIsTransferStation(TransferStation.isContain(stationId));
            checkIsTransfer(station);
            stations.add(station);
            stationList.add(stationId);
        }
        line.setStations(stations);
        line.setStationList(stationList);
    }

    /**
     * 检查是否需要换乘 并标记换乘的line id
     * @param station
     * @return
     */
    public static boolean checkIsTransfer(Station station){
        for (int i = 0; i < LINES.size(); i++) {
            Line item = LINES.get(i);
            if (item.getName().equals(station.getLine())){
                continue;
            }
            MyArrayList<Long> stationIds = item.getStationList();

            if (stationIds.contains(station.getId()) && !station.getTransferLines().contains(item.getId())){
                station.getTransferLines().add(item.getId());
                for (Station s:item.getStations()) {
                    if (s.getId().equals(station.getId()) && !s.getTransferLines().contains(station.getLId())){
                        s.getTransferLines().add(station.getLId());
                        break;
                    }
                }
            }
        }
        return true;
    }

    public static Station getStation(SimpleStation simpleStation){
        for (Station s : LINES.get(simpleStation.getLid()).getStations()) {
            if (s.getId().equals(simpleStation.getSid())){
                return s;
            }
        }
        return null;
    }
}
