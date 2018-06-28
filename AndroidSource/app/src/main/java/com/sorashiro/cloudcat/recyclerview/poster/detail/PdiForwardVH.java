package com.sorashiro.cloudcat.recyclerview.poster.detail;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sorashiro.cloudcat.R;


public class PdiForwardVH extends PDBaseViewHolder {

    public View           rootView;
    public ImageView      imgHead;
    public TextView       textName;
    public TextView       textContent;
    public RelativeLayout layoutContent;
    public TextView       textTime;
    public RelativeLayout layoutHome;

    public PdiForwardVH(View rootView) {
        super(rootView);
        this.rootView = rootView;
        this.imgHead = (ImageView) rootView.findViewById(R.id.imgHead);
        this.textName = (TextView) rootView.findViewById(R.id.textName);
        this.textContent = (TextView) rootView.findViewById(R.id.textContent);
        this.layoutContent = (RelativeLayout) rootView.findViewById(R.id.layoutContent);
        this.textTime = (TextView) rootView.findViewById(R.id.textTime);
        this.layoutHome = (RelativeLayout) rootView.findViewById(R.id.layoutHome);
    }
}
