package com.zerohuang.subway.controller;

import com.zerohuang.subway.models.*;
import com.zerohuang.subway.utils.DataBuild;
import com.zerohuang.subway.utils.DijkstraUtil;
import com.zerohuang.subway.utils.LeastUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import javax.annotation.PostConstruct;


/**
 * @author ZeroHuang
 * @date 2020/10/26 9:41 上午
 */
@RestController
public class SubwayController {

    @Autowired
    private DijkstraUtil dijkstraUtil;

    @Autowired
    private LeastUtil leastUtil;

    @RequestMapping("/get")
    private MyArrayList<Line> getLineResult(){
        return DataBuild.LINES;
    }

    @RequestMapping("/cal")
    public Result getCalculateResult(@RequestBody RequestStation station){
        return dijkstraUtil.calResult(DataBuild.getStation(station.getStartStation()), DataBuild.getStation(station.getEndStation()), DataBuild.LINES);
    }

    @RequestMapping("/dfs")
    public Result getDFSResult(@RequestBody RequestStation station){
        Station start = DataBuild.getStation(station.getStartStation());
        Station end = DataBuild.getStation(station.getEndStation());
        return leastUtil.getLeast(start, end);
    }
   @PostConstruct
    private void dataBuild(){
       DataBuild.init();
   }
}
