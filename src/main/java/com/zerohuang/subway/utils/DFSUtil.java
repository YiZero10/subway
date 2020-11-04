package com.zerohuang.subway.utils;

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
public class DFSUtil {

    @Autowired
    private DijkstraUtil dijkstraUtil;

    private static final int DEFAULT_DISTANCE = 1;

    private static final int MAX = 11110;
    /**
     * 结果集.
     */
    private static MyHashMap<Integer, LineResult> resultMap = new MyHashMap<>();

    /**
     * 存储分析过的站点集合
     */
    private MyArrayList<Integer> analysisList = new MyArrayList<>();

    private MyArrayList<Station> visited = new MyArrayList<>();

    private MyArrayList<Station> passStations = new MyArrayList<>();

    MyArrayList<Integer> transLine = new MyArrayList<>();

    MyArrayList<Line> relatedLines = new MyArrayList<>();

    private Integer[][] LineG;

    private int pathNum = 0;
    private boolean endFlag = false;

    public Result getDFS(Station start, Station end) {

        if (relatedLines.size() > 0)
            relatedLines.clear();

        lineGBuild();
        dijkstraLine(start.getLId());
        relatedLines.add(DataBuild.LINES.get(start.getLId()));
        transLine = resultMap.get(end.getLId()).getPassLines();

        for (int i = 0; i < transLine.size(); i++) {
            relatedLines.add(DataBuild.LINES.get(start.getLId()));
        }
        relatedLines.add(DataBuild.LINES.get(end.getLId()));
        Station target = bfs(start, end, null);

        return new Result();
    }


    private Station bfs(Station start, Station end, Station parent){
        if (start.equals(end)){
            return start;
        }
        if (start.getName().equals("金马路")){
            System.out.println("jinmalu");
        }
        MyArrayList<Station> linkStation = getLinkStations(start, relatedLines);

        visited.add(start);
        for (Station temp:linkStation) {
            int size = linkStation.size();
            if (linkStation.size() <= 0){
                return null;
            }
            if (!visited.contains(temp)){
                Station  station =  bfs(temp, end, start);
                if (station != null)
                  passStations.add(station);
                else
                    return null;
            }else {
                break;
            }
        }
        return start;
    }

    public void dijkstraLine(int startLine){

        if (!resultMap.isEmpty()){
           resultMap.clear();
           analysisList.clear();
        }

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
            Integer nearest = getNextLine(startLine);
            if (nearest == null){
                return ;
            }
            analysisList.add(nearest);
            for (int j = 0; j < LineG[startLine].length; j++) {
                int dis = resultMap.get(nearest).getDistance() + LineG[nearest][j];
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
