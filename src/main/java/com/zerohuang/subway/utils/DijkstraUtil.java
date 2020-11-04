package com.zerohuang.subway.utils;

import com.zerohuang.subway.models.*;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author ZeroHuang
 * @date 2020/10/28 9:00 上午
 */
@Data
@Component
public class DijkstraUtil {

    private static final int DEFAULT_DISTANCE = 1;
    /**
     * 结果集.
     */
    private static Map<Station, Result> resultMap = new HashMap<>();

    /**
     * 存储分析过的站点集合
     */
    private static MyArrayList<Station> analysisList = new MyArrayList<>();

    private MyArrayList<Line> lines = new MyArrayList<>();

    public Result calculate(Station startStation, Station endStation) {
        if (!analysisList.contains(startStation)) {
            analysisList.add(startStation);
        }
        if (startStation.equals(endStation)) {
            Result result = new Result();
            result.setDistance(DEFAULT_DISTANCE);
            result.setEnd(startStation);
            result.setStart(startStation);
            resultMap.put(startStation, result);
            return resultMap.get(startStation);
        }
        if (resultMap.isEmpty()) {
            MyArrayList<Station> linkStations = getLinkStations(startStation);
            for (Station station : linkStations) {
                Result result = new Result();
                result.setStart(startStation);
                result.setEnd(station);
                result.getPassStations().add(station);
                result.setDistance(DEFAULT_DISTANCE);
                resultMap.put(station, result);
            }
        }
        Station parent = getNextStation();
        if (parent == null) {
            Result result = new Result();
            result.setStart(startStation);
            result.setEnd(endStation);
            return resultMap.put(endStation, result);
        }
        if (parent.equals(endStation)) {
            return resultMap.get(parent);
        }
        MyArrayList<Station> childLinkStations = getLinkStations(parent);
        for (Station child : childLinkStations) {
            if (analysisList.contains(child)) {
                continue;
            }

            int distance = DEFAULT_DISTANCE;
            if (parent.getName().equals(child.getName())) {
                distance = 0;
            }

            int parentDistance = resultMap.get(parent).getDistance();
            distance = distance + parentDistance;
            MyArrayList<Station> parentPassStations = resultMap.get(parent).getPassStations();
            Result childResult = resultMap.get(child);
            if (childResult != null) {
                if (childResult.getDistance() > distance) {
                    childResult.setDistance(distance);
                    childResult.getPassStations().clear();
                    childResult.getPassStations().addAll(parentPassStations);
                    childResult.getPassStations().add(child);
                    parentPassStations.clear();
                }
            } else {
                childResult = new Result();
                childResult.setDistance(distance);
                childResult.setStart(startStation);
                childResult.setEnd(child);
                childResult.getPassStations().addAll(parentPassStations);
                childResult.getPassStations().add(child);
            }
            resultMap.put(child, childResult);
        }
        analysisList.add(parent);
        calculate(startStation, endStation);
        return resultMap.get(endStation);
    }

    public Result calResult(Station start, Station end, MyArrayList<Line> lines){
        this.lines = lines;
        calculate(start, end);
        analysisList.clear();
        MyArrayList<Station> list = resultMap.get(end).getPassStations();
        MyArrayList<Station> pass = new MyArrayList<>();
        for ( Station station : list) {
            if (station != null){
                pass.add(station);
                if (station.getName().equals(end.getName()))
                    break;
            }
        }
        list.clear();
        resultMap.get(end).getPassStations().clear();
        resultMap.get(end).setPassStations(pass);
        Result result = resultMap.get(end);
        resultMap.clear();
        return result;
    }

    /**
     * 通过计算最小权值 计算下一个需要分析的点
     *
     * @return the next station
     */
    public Station getNextStation() {
        int min = Integer.MAX_VALUE;
        Station rets = null;
        Set<Station> stations = resultMap.keySet();
        for (Station station : stations) {
            if (analysisList.contains(station)) {
                continue;
            }
            Result result = resultMap.get(station);
            if (result.getDistance() < min) {
                min = result.getDistance();
                rets = result.getEnd();
            }
        }
        return rets;
    }

    public MyArrayList<Station> getLinkStations(Station station) {
        MyArrayList<Station> linkedStations = new MyArrayList<>();
        for (Line line : lines) {
            MyArrayList<Station> stations = line.getStations();
            for (int i = 0; i < stations.size(); i++) {
                if (station.equals(stations.get(i))) {
                    if (i == 0) {
                        linkedStations.add(stations.get(i + 1));
                    } else if (i == (stations.size() - 1)) {
                        linkedStations.add(stations.get(i - 1));
                    } else {
                        linkedStations.add(stations.get(i + 1));
                        linkedStations.add(stations.get(i - 1));
                    }
                    if (station.getIsTransferStation()) {
                        getTransfer(linkedStations, station);
                    }
                }
            }
        }
        return linkedStations;
    }

    private void getTransfer(MyArrayList<Station> linkedStations, Station s) {
        for (int id : s.getTransferLines()) {
            if (lines.size() < id){
                continue;
            }
            MyArrayList<Station> stations = lines.get(id).getStations();
            for (int i = 0; i < stations.size(); i++) {
                if (s.getName().equals(stations.get(i).getName())) {
                    if (i == 0) {
                        linkedStations.add(stations.get(i + 1));
                    } else if (i == (stations.size() - 1)) {
                        linkedStations.add(stations.get(i - 1));
                    } else {
                        linkedStations.add(stations.get(i + 1));
                        linkedStations.add(stations.get(i - 1));
                    }
                }
            }
        }
    }
}
