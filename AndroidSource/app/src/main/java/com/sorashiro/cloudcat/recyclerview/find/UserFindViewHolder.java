package com.sorashiro.cloudcat.recyclerview.find;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sorashiro.cloudcat.R;


public class UserFindViewHolder extends RecyclerView.ViewHolder {
    public View           rootView;
    public ImageView      imgHead;
    public TextView       textName;
    public RelativeLayout layoutHome;

    public UserFindViewHolder(View rootView) {
        super(rootView);
        this.rootView = rootView;
        this.imgHead = (ImageView) rootView.findViewById(R.id.imgHead);
        this.textName = (TextView) rootView.findViewById(R.id.textName);
        this.layoutHome = (RelativeLayout) rootView.findViewById(R.id.layoutHome);
    }
}
