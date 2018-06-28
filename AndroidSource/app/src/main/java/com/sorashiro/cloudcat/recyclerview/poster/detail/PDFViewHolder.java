package com.sorashiro.cloudcat.recyclerview.poster.detail;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sorashiro.cloudcat.R;


public class PDFViewHolder extends PDBaseViewHolder {

    public View           rootView;
    public TextView       homeHeadDivider;
    public ImageView      imgHead;
    public TextView       textName;
    public TextView       textTime;
    public TextView       textContent;
    public RelativeLayout layoutContent;
    public TextView       textAuthorName;
    public TextView       textAuthorContent;
    public RelativeLayout layoutAuthor;
    public TextView       contentBtnDivider;
    public Button         btnForward;
    public Button         btnComment;
    public Button         btnLike;
    public LinearLayout   layoutBtn;
    public RelativeLayout layoutHome;
    public TextView       homeBottomDivider;
    public ImageView imgAuthorContent;

    public PDFViewHolder(View rootView) {
        super(rootView);
        this.rootView = rootView;
        this.homeHeadDivider = (TextView) rootView.findViewById(R.id.homeHeadDivider);
        this.imgHead = (ImageView) rootView.findViewById(R.id.imgHead);
        this.textName = (TextView) rootView.findViewById(R.id.textName);
        this.textTime = (TextView) rootView.findViewById(R.id.textTime);
        this.textContent = (TextView) rootView.findViewById(R.id.textContent);
        this.layoutContent = (RelativeLayout) rootView.findViewById(R.id.layoutContent);
        this.textAuthorName = (TextView) rootView.findViewById(R.id.textAuthorName);
        this.textAuthorContent = (TextView) rootView.findViewById(R.id.textAuthorContent);
        this.layoutAuthor = (RelativeLayout) rootView.findViewById(R.id.layoutAuthor);
        this.contentBtnDivider = (TextView) rootView.findViewById(R.id.contentBtnDivider);
        this.btnForward = (Button) rootView.findViewById(R.id.btnForward);
        this.btnComment = (Button) rootView.findViewById(R.id.btnComment);
        this.btnLike = (Button) rootView.findViewById(R.id.btnLike);
        this.layoutBtn = (LinearLayout) rootView.findViewById(R.id.layoutBtn);
        this.layoutHome = (RelativeLayout) rootView.findViewById(R.id.layoutHome);
        this.homeBottomDivider = (TextView) rootView.findViewById(R.id.homeBottomDivider);
        this.imgAuthorContent = (ImageView) rootView.findViewById(R.id.imgAuthorContent);
    }
}
