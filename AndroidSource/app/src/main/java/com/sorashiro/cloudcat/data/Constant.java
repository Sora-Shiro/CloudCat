package com.sorashiro.cloudcat.data;


import com.sorashiro.cloudcat.R;

public class Constant {

       public static final String BASE_URL = "http://23.83.226.192:6000";
    // public static final String BASE_URL = "http://192.168.155.1:8000";

    public static final String BASE_STATIC_PHOTO_URL = BASE_URL + "/static/file/photo/";
    public static final String BASE_STATIC_AVATAR_URL = BASE_URL + "/static/file/avatar/";

    // 100MB
    public static final long MAX_PHOTO_SIZE = 104857600;

    public static final int AVATAR_DEFAULT = R.drawable.account;

}
