package com.zerohuang.subway.models;

import lombok.Data;
import lombok.NonNull;

/**
 * @author ZeroHuang
 * @date 2020/10/29 2:54 下午
 */
@Data
public class RequestStation {
    @NonNull
    private SimpleStation startStation;

    @NonNull
    private SimpleStation endStation;
}
