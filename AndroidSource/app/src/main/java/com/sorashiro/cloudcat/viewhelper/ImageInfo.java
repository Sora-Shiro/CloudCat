package com.sorashiro.cloudcat.viewhelper;


import android.os.Parcel;
import android.os.Parcelable;

public class ImageInfo implements Parcelable {

    private String path;
    private String thumbPath;

    public static final Creator<ImageInfo> CREATOR = new Creator<ImageInfo>() {
        @Override
        public ImageInfo createFromParcel(Parcel in) {
            return new ImageInfo(in);
        }

        @Override
        public ImageInfo[] newArray(int size) {
            return new ImageInfo[size];
        }
    };


    public ImageInfo() {

    }

    public ImageInfo(Parcel in) {
        String[] strings = new String[2];
        in.readStringArray(strings);
        path = strings[0];
        thumbPath = strings[1];
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getThumbPath() {
        return thumbPath;
    }

    public void setThumbPath(String thumbPath) {
        this.thumbPath = thumbPath;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[]{
                path, thumbPath});
    }
}
