package com.sorashiro.cloudcat.bean;


import android.os.Parcel;
import android.os.Parcelable;

import com.sorashiro.cloudcat.tool.StringConvertUtil;

public class PosterBean implements Parcelable {

    public static final int TYPE_PHOTO   = 1;
    public static final int TYPE_VIDEO   = 2;
    public static final int TYPE_FORWARD = 3;

    public static final Creator<PosterBean> CREATOR = new Creator<PosterBean>() {
        @Override
        public PosterBean createFromParcel(Parcel in) {
            return new PosterBean(in);
        }

        @Override
        public PosterBean[] newArray(int size) {
            return new PosterBean[size];
        }
    };
    /**
     * forward_num : 0
     * name : 10L
     * comment_num : 0
     * time : 2017-11-05T10:58:21+08:00
     * p_type : 1
     * like_num : 0
     * text : u'12'
     * collect_num : 0
     * u'id' : 1
     */

    private int     id;
    private String  name;
    // 1: 图片, 2: 视频, 3: 转发
    private int     p_type;
    private String  text;
    private int     forward_num;
    private int     comment_num;
    private int     like_num;
    private int     collect_num;
    private String  time;
    private int     origin_poster_id;
    private boolean if_like;
    private String photo_id;
    private String  resMsg;

    public PosterBean(String time, String name, String text) {
        this.id = 1;
        this.name = name;
        this.p_type = TYPE_PHOTO;
        this.text = text;
        this.forward_num = 1;
        this.collect_num = 2;
        this.comment_num = 3;
        this.like_num = 4;
        this.time = time;
        this.origin_poster_id = 0;
        this.if_like = false;
    }

    public PosterBean(Parcel in) {
        String[] strings = new String[4];
        in.readStringArray(strings);
        int[] ints = new int[7];
        in.readIntArray(ints);
        boolean[] booleans = new boolean[1];
        in.readBooleanArray(booleans);
        this.id = ints[0];
        this.name = strings[0];
        this.p_type = ints[1];
        this.text = strings[1];
        this.forward_num = ints[2];
        this.collect_num = ints[3];
        this.comment_num = ints[4];
        this.like_num = ints[5];
        this.time = strings[2];
        this.origin_poster_id = ints[6];
        this.photo_id = strings[3];
        this.if_like = booleans[0];
    }

    public int getForward_num() {
        return forward_num;
    }

    public void setForward_num(int forward_num) {
        this.forward_num = forward_num;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getComment_num() {
        return comment_num;
    }

    public void setComment_num(int comment_num) {
        this.comment_num = comment_num;
    }

    public String getTime() {
        return StringConvertUtil.convertFromSqlDateTime(time);
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getP_type() {
        return p_type;
    }

    public void setP_type(int p_type) {
        this.p_type = p_type;
    }

    public int getLike_num() {
        return like_num;
    }

    public void setLike_num(int like_num) {
        this.like_num = like_num;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getCollect_num() {
        return collect_num;
    }

    public void setCollect_num(int collect_num) {
        this.collect_num = collect_num;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOrigin_poster_id() {
        return origin_poster_id;
    }

    public void setOrigin_poster_id(int origin_poster_id) {
        this.origin_poster_id = origin_poster_id;
    }

    public boolean isIf_like() {
        return if_like;
    }

    public void setIf_like(boolean if_like) {
        this.if_like = if_like;
    }

    public String getPhoto_id() {
        return photo_id;
    }

    public void setPhoto_id(String photo_id) {
        this.photo_id = photo_id;
    }

    public String getResMsg() {
        return resMsg;
    }

    public void setResMsg(String resMsg) {
        this.resMsg = resMsg;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[]{
                name, text, time, photo_id});
        dest.writeIntArray(new int[]{
                id, p_type, forward_num, comment_num, like_num, collect_num, origin_poster_id});
        dest.writeBooleanArray(new boolean[]{
                if_like
        });
    }
}
