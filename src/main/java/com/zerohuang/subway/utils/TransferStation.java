package com.zerohuang.subway.utils;

import com.zerohuang.subway.models.MyArrayList;

/**
 * @author ZeroHuang
 * @date 2020/10/28 10:43 上午
 */
public  class TransferStation {
    private static final MyArrayList<Long> TRANSFER_STATIONS = new MyArrayList<>();

    static{
        TRANSFER_STATIONS.add(320100021307016L);
        TRANSFER_STATIONS.add(320100021293010L);
        TRANSFER_STATIONS.add(320100021495012L);
        TRANSFER_STATIONS.add(320100022330017L);
        TRANSFER_STATIONS.add(320100021495004L);
        TRANSFER_STATIONS.add(320100021307011L);
        TRANSFER_STATIONS.add(320100021573021L);
        TRANSFER_STATIONS.add(320100021293017L);
        TRANSFER_STATIONS.add(320100021573027L);
        TRANSFER_STATIONS.add(320100021293011L);
        TRANSFER_STATIONS.add(320100021293011L);
        TRANSFER_STATIONS.add(320100021495013L);
    }

    public static boolean isContain(Long id){
        if (TRANSFER_STATIONS.contains(id)){
            return true;
        }
        return false;
    }

}
