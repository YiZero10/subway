package com.zerohuang.subway.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;

/**
 * @author ZeroHuang
 * @date 2020/10/26 10:00 上午
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Line {
    private int lid;

    private String lname;

    private MyArrayList<Station> stations;

    private MyArrayList<Long> stationList;

    private HashMap<Long, String> stationMap;

}
