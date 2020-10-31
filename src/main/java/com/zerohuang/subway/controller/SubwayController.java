package com.zerohuang.subway.controller;

import com.zerohuang.subway.models.Line;
import com.zerohuang.subway.models.MyArrayList;
import com.zerohuang.subway.models.RequestStation;
import com.zerohuang.subway.models.Result;
import com.zerohuang.subway.utils.DataBuild;
import com.zerohuang.subway.utils.DijkstraUtil;
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

    @RequestMapping("/get")
    private MyArrayList<Line> getLineResult(){
        return DataBuild.LINES;
    }

    @RequestMapping("/cal")
    public Result getCalculateResult(@RequestBody RequestStation station){
        return dijkstraUtil.calResult(station.getStartStation(), station.getEndStation());
    }

   @PostConstruct
    private void dataBuild(){
       DataBuild.init();
   }
}
