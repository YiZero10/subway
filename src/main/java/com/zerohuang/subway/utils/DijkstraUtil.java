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

    /**
     * Dijkstra算法
     * @param startStation
     * @param endStation
     * @return
     */
    public Result calculate(Station startStation, Station endStation) {
        if (!analysisList.contains(startStation)) {
            analysisList.add(startStation);                                  //将起始站点标记成已分析
        }
        if (startStation.equals(endStation)) {                              //判断是否已经找到终点，找到了将结果加入到结果集中
            Result result = new Result();
            result.setDistance(DEFAULT_DISTANCE);
            result.setEnd(startStation);
            result.setStart(startStation);
            resultMap.put(startStation, result);
            return resultMap.get(startStation);
        }
        if (resultMap.isEmpty()) {                                          // 初始化结果集，将起始站点的邻接点都加入到集合中
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
        Station parent = getNextStation();                                 //获取下一个要分析的站点
        if (parent == null) {                                              //没有下一个分析的点 加入到结果集 并返回
            Result result = new Result();
            result.setStart(startStation);
            result.setEnd(endStation);
            return resultMap.put(endStation, result);
        }
        if (parent.equals(endStation)) {
            return resultMap.get(parent);
        }
        MyArrayList<Station> childLinkStations = getLinkStations(parent);  //获取下一个要分析的站点的所有邻接点
        for (Station child : childLinkStations) {
            if (analysisList.contains(child)) {
                continue;
            }

            int distance = DEFAULT_DISTANCE;
            if (parent.getName().equals(child.getName())) {
                distance = 0;
            }

            int parentDistance = resultMap.get(parent).getDistance();
            distance = distance + parentDistance;                          //判断邻接点加入后的最短路径的点
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
            resultMap.put(child, childResult);                              //将最短路径的那个邻接点加入到结果集中
        }
        analysisList.add(parent);
        calculate(startStation, endStation);                                //开始递归
        return resultMap.get(endStation);                                   //返回结果
    }

    /**
     * 求解的入口
     * @param start
     * @param end
     * @param lines
     * @return
     */
    public Result calResult(Station start, Station end, MyArrayList<Line> lines){
        this.lines = lines;
        //调用Dijkstra算法函数calculate
        calculate(start, end);
        analysisList.clear();
        //对所得到的解集合进行分析
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

    /**
     * 获取站点相邻的所有站点，包括换乘路线上的
     * @param station
     * @return
     */
    public MyArrayList<Station> getLinkStations(Station station) {
        MyArrayList<Station> linkedStations = new MyArrayList<>();
        for (Line line : lines) {
            MyArrayList<Station> stations = line.getStations();
            for (int i = 0; i < stations.size(); i++) {
                if (station.equals(stations.get(i))) {
                    if (i == 0) {                                           //判断是否为头结点
                        linkedStations.add(stations.get(i + 1));
                    } else if (i == (stations.size() - 1)) {                //判断是否为尾结点
                        linkedStations.add(stations.get(i - 1));
                    } else {
                        linkedStations.add(stations.get(i + 1));
                        linkedStations.add(stations.get(i - 1));
                    }
                    if (station.getIsTransferStation()) {                  //判断是否需要换乘
                        getTransfer(linkedStations, station);
                    }
                }
            }
        }
        return linkedStations;
    }

    /**
     * 检查是否换乘，并将换乘路线上的相邻站点加到邻接站点集合中
     * @param linkedStations
     * @param s
     */
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
