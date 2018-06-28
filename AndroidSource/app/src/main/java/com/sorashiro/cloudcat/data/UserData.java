package com.sorashiro.cloudcat.data;


public class UserData {

    public static String getCurrentUsername() {
        return AppSaveDataSPUtil.getInstance().getUserName();
    }

    public static String getCurrentPassword() {
        return AppSaveDataSPUtil.getInstance().getPassWord();
    }

    public static void setCurrentPassword(String password) {
        AppSaveDataSPUtil.getInstance().setPassWord(password);
    }

    public static void setCurrentUserName(String userName) {
        AppSaveDataSPUtil.getInstance().setUserName(userName);
    }

}
