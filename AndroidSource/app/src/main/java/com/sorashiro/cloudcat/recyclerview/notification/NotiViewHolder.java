package com.sorashiro.cloudcat.recyclerview.notification;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sorashiro.cloudcat.R;


public class NotiViewHolder extends RecyclerView.ViewHolder {

    public View           rootView;
    public RelativeLayout layoutRoot;
    public ImageView      imgNotiType;
    public TextView       textNotiType;

    public NotiViewHolder(View rootView) {
        super(rootView);
        this.rootView = rootView;
        this.layoutRoot = (RelativeLayout) rootView.findViewById(R.id.layoutNoti);
        this.imgNotiType = (ImageView) rootView.findViewById(R.id.imgNotiType);
        this.textNotiType = (TextView) rootView.findViewById(R.id.textNotiType);
    }
}
