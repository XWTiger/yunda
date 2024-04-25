package com.tiger.yunda.utils;

import java.util.Collection;
import java.util.Objects;

public class CollectionUtil {

    public static boolean isEmpty(Collection collection) {
        if (Objects.isNull(collection) || collection.isEmpty()) {
            return true;
        }
        return false;
    }
}
