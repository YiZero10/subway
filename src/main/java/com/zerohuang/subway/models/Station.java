package com.zerohuang.subway.models;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author ZeroHuang
 * @date 2020/10/26 10:00 上午
 */
@Data
@NoArgsConstructor
public class Station {
    /**
     * 站点id
     */
    private Long sid;

    /**
     * 站点名字
     */
    private String sname;


    /**
     * 站点拼音
     */
    private String sPin;

    private int lId;

    /**
     * 所属线路
     */
    private String line;

    /**
     * 是否可换乘
     */
    private Boolean isTransferStation;

    /**
     * 可换乘线路
     */
    private MyArrayList<Integer> transferLines = new MyArrayList<>();

    /**
     * 相邻站点
     */
    private MyArrayList<Integer> edges;

}
