package com.sorashiro.cloudcat.bean;


public class OtherFollowBean {

    private int id;

    private String u1;

    private String u2;

    // 别人的粉丝or关注和自己的关系，2即是to，follow
    private boolean fan2checker;
    private boolean checker2fan;

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

    public boolean isChecker2fan() {
        return checker2fan;
    }

    public void setChecker2fan(boolean checker2fan) {
        this.checker2fan = checker2fan;
    }

    public boolean isFan2checker() {
        return fan2checker;
    }

    public void setFan2checker(boolean fan2checker) {
        this.fan2checker = fan2checker;
    }
}
