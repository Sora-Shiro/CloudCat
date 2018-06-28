package com.sorashiro.cloudcat.bean;

import com.sorashiro.cloudcat.tool.StringConvertUtil;

public class BeLikeBean {

    private String  name;
    private String  author;
    private String  time;
    private int     id;
    private int     poster;
    private boolean if_ori;
    private String  ori_text;
    private boolean if_like;

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

    public boolean isIf_ori() {
        return if_ori;
    }

    public void setIf_ori(boolean if_ori) {
        this.if_ori = if_ori;
    }

    public String getOri_text() {
        return ori_text;
    }

    public void setOri_text(String ori_text) {
        this.ori_text = ori_text;
    }

    public boolean isIf_like() {
        return if_like;
    }

    public void setIf_like(boolean if_like) {
        this.if_like = if_like;
    }
}
