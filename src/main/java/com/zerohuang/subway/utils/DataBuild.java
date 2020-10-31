package com.zerohuang.subway.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zerohuang.subway.models.Line;
import com.zerohuang.subway.models.MyArrayList;
import com.zerohuang.subway.models.Station;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
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
                if (jsonLine.get("ln").equals("S9号线(宁高线)")){
                    continue;
                }
                line.setLid(index++);
                line.setLname(jsonLine.getString("ln"));
                setStation(line, jsonStations);
                LINES.add(line);
            }
        }
    }

    public static void setStation(Line line, JSONArray array){
        MyArrayList<Long> stationList = new MyArrayList<>();
        MyArrayList<Station> stations = new MyArrayList<>();

        HashMap<Long, String> stationMap = new HashMap<>();

        for (Object o:array) {
            Station station = new Station();
            JSONObject jsonStation = new JSONObject((Map<String, Object>) o);
            Long stationId = Long.parseLong(jsonStation.getString("sid"));
            String stationName = jsonStation.getString("n");

            station.setSid(stationId);
            station.setSname(stationName);
            station.setSPin(jsonStation.getString("sp"));
            station.setLId(line.getLid());
            station.setLine(line.getLname());
            station.setIsTransferStation(TransferStation.isContain(stationId));
            checkIsTransfer(station);
            stations.add(station);
            stationList.add(stationId);
            stationMap.put(stationId,stationName);
        }
        line.setStations(stations);
        line.setStationMap(stationMap);
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
            if (item.getLname().equals(station.getLine())){
                continue;
            }
            MyArrayList<Long> stationIds = item.getStationList();

            if (stationIds.contains(station.getSid()) && !station.getTransferLines().contains(item.getLid())){
                station.getTransferLines().add(item.getLid());
                for (Station s:item.getStations()) {
                    if (s.getSid().equals(station.getSid()) && !s.getTransferLines().contains(station.getLId())){
                        s.getTransferLines().add(station.getLId());
                        break;
                    }
                }
            }
        }
        return true;
    }


}
