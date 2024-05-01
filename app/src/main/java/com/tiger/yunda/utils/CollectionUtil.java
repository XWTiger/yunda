package com.tiger.yunda.utils;

import com.tiger.yunda.data.BreakDownType;
import com.tiger.yunda.data.model.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class CollectionUtil {

    public static boolean isEmpty(Collection collection) {
        if (Objects.isNull(collection) || collection.isEmpty()) {
            return true;
        }
        return false;
    }


    public static List<BreakDownType> covertUserToSpinnerObj(List<User> users) {
        if (Objects.isNull(users) || users.size() == 0) {
            return Collections.EMPTY_LIST;
        }
        List<BreakDownType> types = new ArrayList<>();
        users.forEach(user ->  {
            BreakDownType breakDownType = new BreakDownType();
            breakDownType.setName(user.getText());
            breakDownType.setType(user.getValue());
            types.add(breakDownType);
        });
        return types;
    }
}
