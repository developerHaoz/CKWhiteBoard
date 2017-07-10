package com.example.developerhaoz.ckwhiteboard.common.util;

import java.util.Collection;
import java.util.Map;

/**
 * 用于判空的辅助类
 *
 * Created by developerHaoz on 2017/7/10.
 */

public class Check {

    public static boolean isEmpty(CharSequence str) {
        return isNull(str) || str.length() == 0;
    }

    public static boolean isEmpty(Object[] objects) {
        return isNull(objects) || objects.length == 0;
    }

    public static boolean isEmpty(Collection<?> collection) {
        return isNull(collection) || collection.size() == 0;
    }

    public static boolean isEmpty(Map<?, ?> map) {
        return isNull(map) || map.size() == 0;
    }


    private static boolean isNull(Object o) {
        return o == null;
    }

}