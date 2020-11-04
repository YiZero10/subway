package com.zerohuang.subway.models;

import lombok.Data;

/**
 * @author ZeroHuang
 * @date 2020/11/3 9:18 上午
 */
@Data
public class LineResult {
    private Integer startLine;

    private Integer endLine;

    private int distance;

    private MyArrayList<Integer> passLines = new MyArrayList<>();


}
