package com.zerohuang.subway.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author ZeroHuang
 * @date 2020/10/26 10:00 上午
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Line {
    private int id;

    private String name;

    private MyArrayList<Station> stations;

    private MyArrayList<Long> stationList;
}
