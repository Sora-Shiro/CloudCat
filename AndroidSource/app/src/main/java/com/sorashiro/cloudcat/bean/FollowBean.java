package com.sorashiro.cloudcat.bean;

public class FollowBean {

    private int id;

    private String u1;

    private String u2;

    // 是否互相关注
    private boolean if_mutual;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getU1() {
        return u1;
    }

    public void setU1(String u1) {
        this.u1 = u1;
    }

    public String getU2() {
        return u2;
    }

    public void setU2(String u2) {
        this.u2 = u2;
    }

    public boolean isIf_mutual() {
        return if_mutual;
    }

    public void setIf_mutual(boolean if_mutual) {
        this.if_mutual = if_mutual;
    }
}
