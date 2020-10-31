package com.zerohuang.subway.models;

import lombok.Data;

/**
 * @author ZeroHuang
 * @date 2020/10/29 2:54 下午
 */
@Data
public class RequestStation {
    private Station startStation;

    private Station endStation;
}
