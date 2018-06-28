package com.sorashiro.cloudcat.bean;

import com.sorashiro.cloudcat.tool.StringConvertUtil;

/**
 * Created by Administrator on 2017/11/7.
 */

public class LikeBean extends BasePosterDetailBean {


    /**
     * name : c
     * author : b
     * text : Comment from C
     * time : 2017-11-07T22:22:59+08:00
     * id : 2
     * poster : 1
     */

    private String name;
    private String author;
    private String time;
    private int    id;
    private int    poster;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTime() {
        return StringConvertUtil.convertFromSqlDateTime(time);
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPoster() {
        return poster;
    }

    public void setPoster(int poster) {
        this.poster = poster;
    }
}
