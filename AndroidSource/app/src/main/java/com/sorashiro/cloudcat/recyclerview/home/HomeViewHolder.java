package com.sorashiro.cloudcat.recyclerview.home;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sorashiro.cloudcat.R;


public class HomeViewHolder extends HomeBaseVH {

    public View           rootView;
    public RelativeLayout layoutHome;
    public ImageView      imgHead;
    public TextView       textName;
    public TextView       textTime;
    public TextView       textContent;
    public RelativeLayout layoutContent;
    public TextView       contentBtnDivider;
    public Button         btnForward;
    public Button         btnComment;
    public Button         btnLike;
    public LinearLayout   layoutBtn;
    public ImageView imgContent;

    public HomeViewHolder(View rootView) {
        super(rootView);
        this.rootView = rootView;
        this.layoutHome = (RelativeLayout) rootView.findViewById(R.id.layoutHome);
        this.imgHead = (ImageView) rootView.findViewById(R.id.imgHead);
        this.textName = (TextView) rootView.findViewById(R.id.textName);
        this.textTime = (TextView) rootView.findViewById(R.id.textTime);
        this.textContent = (TextView) rootView.findViewById(R.id.textContent);
        this.layoutContent = (RelativeLayout) rootView.findViewById(R.id.layoutContent);
        this.contentBtnDivider = (TextView) rootView.findViewById(R.id.contentBtnDivider);
        this.btnForward = (Button) rootView.findViewById(R.id.btnForward);
        this.btnComment = (Button) rootView.findViewById(R.id.btnComment);
        this.btnLike = (Button) rootView.findViewById(R.id.btnLike);
        this.layoutBtn = (LinearLayout) rootView.findViewById(R.id.layoutBtn);
        this.imgContent = (ImageView) rootView.findViewById(R.id.imgContent);
    }
}
