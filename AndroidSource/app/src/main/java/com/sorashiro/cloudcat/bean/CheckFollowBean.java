package com.sorashiro.cloudcat.bean;

public class CheckFollowBean {

    private boolean user2checker;

    private boolean checker2user;

    private String resMsg;

    public boolean isUser2checker() {
        return user2checker;
    }

    public void setUser2checker(boolean user2checker) {
        this.user2checker = user2checker;
    }

    public boolean isChecker2user() {
        return checker2user;
    }

    public void setChecker2user(boolean checker2user) {
        this.checker2user = checker2user;
    }

    public String getResMsg() {
        return resMsg;
    }

    public void setResMsg(String resMsg) {
        this.resMsg = resMsg;
    }
}
