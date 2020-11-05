package com.zerohuang.subway.utils;

import com.alibaba.fastjson.JSON;
import com.zerohuang.subway.models.*;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @author ZeroHuang
 * @date 2020/11/2 10:46 上午
 */
@Data
@Component
public class LeastUtil {

    @Autowired
    private DijkstraUtil dijkstraUtil;

    private static final int DEFAULT_DISTANCE = 1;

    private static final int MAX = 11110;

    /**
     * 路线之间的图结构
     */
    private Integer[][] LineG;

    /**
     * 结果集.
     */
    private static MyHashMap<Integer, LineResult> resultMap = new MyHashMap<>();

    /**
     * dijkstra存储分析过的站点集合
     */
    private MyArrayList<Integer> analysisList = new MyArrayList<>();

    /**
     * BFS存储分析过的站点集合
     */
    private MyArrayList<Station> visited = new MyArrayList<>();

    /**
     * 存储最少换乘所用到的路线id
     */
    MyArrayList<Integer> transLine = new MyArrayList<>();

    /**
     * 存储最少换乘所用到的路线
     */
    MyArrayList<Line> relatedLines = new MyArrayList<>();

    /**
     * 存储最少换乘的结果
     */
    Map<Integer, Object[]> res = new HashMap<>();

    /**
     * 获取最少换乘路径的最终结果
     * @param start
     * @param end
     * @return
     */
    public Result getLeast(Station start, Station end){
        lineGBuild();
        MyArrayList<Result> results = new MyArrayList<>();
        results.add(getBFS(start, end));
        if (end.getIsTransferStation()){
            for (Integer temp : end.getTransferLines()) {
                transLine.clear();
                relatedLines.clear();
                Station newEnd = new Station();
                Line line = DataBuild.LINES.get(temp);
                for (Station s:line.getStations()) {
                    if (s.getName().equals(end.getName()))
                        newEnd = s;
                }
                Result result = getBFS(start, newEnd);
                if (result != null)
                    results.add(result);
            }
        }

        int min = Integer.MAX_VALUE;
        int index = Integer.MAX_VALUE;
        for (Result temp:results) {
            if (temp.getPassStations().getArrayList().length < min) {
                min = temp.getPassStations().getArrayList().length;
                index = results.indexOf(temp);
            }
        }

        return results.get(index);
    }

    /**
     * 通过广度优先遍历找到最少换乘的路线
     * @param start
     * @param end
     * @return
     */
    public Result getBFS(Station start, Station end) {

        if (relatedLines.size() > 0)
            relatedLines.clear();

        relatedLines.add(DataBuild.LINES.get(start.getLId()));
        dijkstraLine(start.getLId());   //通过Dijkstra求出最少换乘所需要的路线集合
        MyArrayList<Integer> trans = end.getTransferLines();  //判断终点是否是可以换乘的
        for (int i = 0; i < trans.size(); i++) {              //如果可以换乘需要再求一次最少换乘所需要的路线集合，从而更加完整
            dijkstraLine(trans.get(i));
        }
        transLine = resultMap.get(end.getLId()).getPassLines();
        for (int i = 0; i < transLine.size(); i++) {
            if (transLine.get(0) == -1)
                continue;
            Line line = DataBuild.LINES.get(transLine.get(i));
            if (! relatedLines.contains(line)){
                relatedLines.add(line);
            }
        }
        relatedLines.add(DataBuild.LINES.get(end.getLId()));
        MyArrayList<Station>  res = getAllPaths(start, end);  //通过BFS算法将到终点的最短路径求出
        if (res == null){
            return null;
        }
        Result result = new Result();
        result.setEnd(end);
        result.setPassStations(res);
        return result;
    }

    /**
     * 已知起始站点所涉及到最少换乘的路线，获取起始站点的所有路径
     * @param s
     * @param d
     * @return
     */
    public MyArrayList<Station> getAllPaths(Station s, Station d)
    {
        if ( visited.size() > 0){
            visited.clear();
        }

        if (res.size() > 0){
            res.clear();
        }

        List<Station> pathList = new ArrayList<>();

        // 将源结点加入到分析的路径中去
        pathList.add(s);

        // 通过BFS求路径
        getAllPathsUtil(s, d, pathList);

        Integer min = Integer.MAX_VALUE;

        Set<Integer> keys = res.keySet();

        Map<Integer, Object[]> results = new HashMap<>();

        for (Integer i:keys) {
            if (calTransCount(res.get(i)) <= min ){
                min = calTransCount(res.get(i));
                results.put(i, res.get(i));
            }
        }

        min = 999999;
        keys = results.keySet();
        for (Integer i:keys) {
            if ( i < min)
                min = i;
        }
        if (results.size() == 0){
            return null;
        }
        MyArrayList<Station> result = new MyArrayList<>();
        result.setArrayList(results.get(min));
        result.setSize(results.get(min).length);
        return result;
    }

    /**
     * 计算换乘次数
     * @param objects
     * @return
     */
    private int calTransCount (Object[] objects){
        int count = 0;
        MyArrayList<Station> stations = new MyArrayList<>();
        stations.setArrayList(objects);
        stations.setSize(stations.getArrayList().length);
        for (int i = 1; i < stations.getArrayList().length - 1; i++) {
            if (! stations.get(i).getLine().equals(stations.get( i -1).getLine()))
                count++;
        }
        return count;
    }

    /**
     * 广度优先遍历
     * @param u
     * @param d
     * @param localPathList
     */
    private void getAllPathsUtil(Station u, Station d,
                                   List<Station> localPathList)
    {

        if (u.getName().equals(d.getName())) {
            res.put(localPathList.size(), localPathList.toArray());
            // 如果找到匹配项，则无需遍历深度
            return;
        }

        // 将当前起始点标记已经查看
        visited.add(u);

        //获取站点的所有链接站点
        MyArrayList<Station> linkStation = getLinkStations(u,relatedLines);

        // 判断站点是否查看过
        for (Station i : linkStation) {
            if (!visited.contains(i)) {
                // 未查看过存储当前站点 保存到路径，开始递归
                localPathList.add(i);
                getAllPathsUtil(i, d, localPathList);

                // 将当前站点remove掉
                localPathList.remove(i);
            }
        }

        // 将当前站点取消查看标记
        visited.remove(u);
    }


    /**
     * 通过Dijkstra算法，求出起始路线到其余路线的最短路径，即可知道最少换乘所需要涉及到的路线
     * @param startLine
     */
    public void dijkstraLine(int startLine){
        if (!resultMap.isEmpty()){
           resultMap.clear();
           analysisList.clear();
        }

        //通过路线图，初始化结果集合
        for (int i = 0; i < LineG[startLine].length; i++) {
            LineResult lineResult = new LineResult();
            lineResult.setStartLine(startLine);
            lineResult.setEndLine(i);
            lineResult.setDistance(LineG[startLine][i]);
            if (LineG[startLine][i] == MAX){
                lineResult.getPassLines().add(-1);
            }
            resultMap.put(i, lineResult);
        }

        // start放入到已分析集合中去
        analysisList.add(startLine);

        for (int i = 0; i < LineG[startLine].length; i++) {
            if (i == startLine){
                continue;
            }
            //获取下一个要分析的路线
            Integer nearest = getNextLine(startLine);
            if (nearest == null){
                return ;
            }
            analysisList.add(nearest);

            //遍历起始路线所有的邻接路线
            for (int j = 0; j < LineG[startLine].length; j++) {
                int dis = resultMap.get(nearest).getDistance() + LineG[nearest][j];
                //如果邻接路线没有被分析，并且加入下一个要分析的路线使得到达起始的路线变短，就将分析的路线点加入到结果中去
                if (! analysisList.contains(j) && dis < resultMap.get(j).getDistance()){
                    resultMap.get(j).setDistance(dis);
                    if (resultMap.get(j).getPassLines().size() > 0){
                        resultMap.get(j).getPassLines().remove(0);
                    }
                    resultMap.get(j).getPassLines().add(nearest);
                }
            }
        }
    }

    /**
     * 获取邻接站点
     * @param station
     * @param lines
     * @return
     */
    private MyArrayList<Station>  getLinkStations(Station station, MyArrayList<Line> lines){
        MyArrayList<Station> linkedStations = new MyArrayList<>();
        for (Line line : lines) {
            MyArrayList<Station> stations = line.getStations();
            for (int i = 0; i < stations.size(); i++) {
                if (station.getName().equals(stations.get(i).getName())) {
                    if (i == 0) {
                        linkedStations.add(stations.get(i + 1));
                    } else if (i == (stations.size() - 1)) {
                        linkedStations.add(stations.get(i - 1));
                    } else {
                        linkedStations.add(stations.get(i + 1));
                        linkedStations.add(stations.get(i - 1));
                    }
                    if (station.getIsTransferStation() && transLine.size() > 0) {
                        getTransfer(linkedStations, station);
                    }
                }
            }
        }
        return linkedStations;
    }

    /**
     * 获取站点可换乘的邻接站点
     * @param linkedStations
     * @param station
     */
    private void getTransfer(MyArrayList<Station> linkedStations, Station station){
        MyArrayList<Integer> oldTransfer = station.getTransferLines();
        for (Integer temp:oldTransfer) {
            if (!transLine.contains(temp))
                continue;
            MyArrayList<Station> stations = DataBuild.LINES.get(temp).getStations();
            for (int i = 0; i < stations.size(); i++) {
                if (station.getName().equals(stations.get(i).getName())) {
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

    /**
     * 通过计算最小权值 计算下一个需要分析的点
     *
     * @return the next station
     */
    public Integer getNextLine(int start) {
        int min = MAX;
        Integer rets = null;
        for (int i = 0; i < LineG[start].length; i++) {
            if (resultMap.get(i).getDistance() < min && !analysisList.contains(i)){
                min = resultMap.get(i).getDistance();
                rets = i;
            }
        }
        return rets;
    }

    /**
     * 获取线路可以换乘的邻接线路
     * @param lid
     * @return
     */
    private MyArrayList<Integer> getTransferLines(int lid) {
        MyArrayList<Integer> linkLines = new MyArrayList<>();
        for (Station s: DataBuild.LINES.get(lid).getStations()) {
            if (s.getIsTransferStation()){
                MyArrayList<Integer> stationTransfer = s.getTransferLines();
                for (Integer i: stationTransfer) {
                    if ( ! linkLines.contains(i)){
                        linkLines.add(i);
                    }
                }
            }
        }
        return linkLines;
    }


    /**
     * 构建线路之间的关系图
     */
    private void lineGBuild(){
        LineG = new Integer[DataBuild.LINES.size()][DataBuild.LINES.size()];
        for (int i = 0; i < LineG.length; i++) {
            Arrays.fill(LineG[i], MAX);
        }

        MyArrayList<Integer> linkLines ;
        for (int i = 0; i < DataBuild.LINES.size(); i++) {
            linkLines = getTransferLines(i);
            for (Integer integer:linkLines) {
                LineG[i][integer] = DEFAULT_DISTANCE;
            }
        }
    }
}
