package com.tiger.yunda.ui.login;

import java.io.Serializable;

/**
 * Class exposing authenticated user details to the UI.
 */
class LoggedInUserView implements Serializable {
    private String displayName;
    //... other data fields that may be accessible to the UI

    LoggedInUserView(String displayName) {
        this.displayName = displayName;
    }

    String getDisplayName() {
        return displayName;
    }
}