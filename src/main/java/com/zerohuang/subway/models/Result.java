package com.zerohuang.subway.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

/**
 * @author ZeroHuang
 * @date 2020/10/28 9:00 上午
 */
@Data
public class Result {
    private Station start;

    private Station end;

    private int distance;

    private MyArrayList<Station> passStations = new MyArrayList<>();
}
