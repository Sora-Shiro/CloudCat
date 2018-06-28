package com.sorashiro.cloudcat.tool;


public class StringConvertUtil {

    // 2017-11-07T21:22:10+08:00 => 2017-11-07 21:22:10
    public static String convertFromSqlDateTime(String dateTimeStr) {
        return dateTimeStr.substring(0, 10) + " " +
                dateTimeStr.substring(11, 19);
    }

}
