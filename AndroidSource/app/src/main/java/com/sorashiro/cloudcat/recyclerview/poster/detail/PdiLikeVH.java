package com.sorashiro.cloudcat.recyclerview.poster.detail;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sorashiro.cloudcat.R;


public class PdiLikeVH extends PDBaseViewHolder {

    public View      rootView;
    public ImageView imgHead;
    public TextView  textName;
    public TextView  textTime;

    public PdiLikeVH(View rootView) {
        super(rootView);
        this.rootView = rootView;
        this.imgHead = (ImageView) rootView.findViewById(R.id.imgHead);
        this.textName = (TextView) rootView.findViewById(R.id.textName);
        this.textTime = (TextView) rootView.findViewById(R.id.textTime);
    }
}
