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
//路线
public class Line {
    /**
     * 路径id
     */
    private int id;

    /**
     * 路径名字
     */
    private String name;

    /**
     * 路径上所有站点信息
     */
    private MyArrayList<Station> stations;

    /**
     * 路径上所有站点的id集合
     */
    private MyArrayList<Long> stationList;
}
