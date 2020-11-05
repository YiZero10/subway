package com.zerohuang.subway.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author ZeroHuang
 * @date 2020/10/26 10:00 上午
 */
@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Station {
    /**
     * 站点id
     */
    private Long id;

    /**
     * 站点名字
     */
    private String name;


    /**
     * 站点拼音
     */
    private String sPin;

    /**
     * 所属线路的id
     */
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

}
