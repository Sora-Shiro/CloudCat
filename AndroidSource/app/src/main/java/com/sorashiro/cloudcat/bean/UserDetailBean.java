package com.sorashiro.cloudcat.bean;


public class UserDetailBean {
    /**
     * poster_num : 1
     * follow_num : 3
     * following_num : 1
     */

    private int    poster_num;
    private int    follow_num;
    private int    following_num;
    private String resMsg;

    public int getPoster_num() {
        return poster_num;
    }

    public void setPoster_num(int poster_num) {
        this.poster_num = poster_num;
    }

    public int getFollow_num() {
        return follow_num;
    }

    public void setFollow_num(int follow_num) {
        this.follow_num = follow_num;
    }

    public int getFollowing_num() {
        return following_num;
    }

    public void setFollowing_num(int following_num) {
        this.following_num = following_num;
    }

    public String getResMsg() {
        return resMsg;
    }

    public void setResMsg(String resMsg) {
        this.resMsg = resMsg;
    }
}
