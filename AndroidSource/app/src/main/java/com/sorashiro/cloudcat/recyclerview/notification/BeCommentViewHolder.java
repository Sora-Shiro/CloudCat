package com.sorashiro.cloudcat.recyclerview.notification;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sorashiro.cloudcat.R;


public class BeCommentViewHolder extends RecyclerView.ViewHolder {
    public View           rootView;
    public TextView       homeHeadDivider;
    public ImageView      imgHead;
    public TextView       textName;
    public TextView       textTime;
    public TextView       textContent;
    public RelativeLayout layoutContent;
    public ImageView      imgAuthorHead;
    public TextView       textAuthorName;
    public RelativeLayout layoutAuthorContent;
    public TextView       textAuthorPoster;
    public RelativeLayout layoutAuthor;
    public TextView       contentBtnDivider;
    public RelativeLayout layoutHome;
    public TextView       homeBottomDivider;

    public BeCommentViewHolder(View rootView) {
        super(rootView);
        this.rootView = rootView;
        this.homeHeadDivider = (TextView) rootView.findViewById(R.id.homeHeadDivider);
        this.imgHead = (ImageView) rootView.findViewById(R.id.imgHead);
        this.textName = (TextView) rootView.findViewById(R.id.textName);
        this.textTime = (TextView) rootView.findViewById(R.id.textTime);
        this.textContent = (TextView) rootView.findViewById(R.id.textContent);
        this.layoutContent = (RelativeLayout) rootView.findViewById(R.id.layoutContent);
        this.imgAuthorHead = (ImageView) rootView.findViewById(R.id.imgAuthorHead);
        this.textAuthorName = (TextView) rootView.findViewById(R.id.textAuthorName);
        this.layoutAuthorContent = (RelativeLayout) rootView.findViewById(R.id.layoutAuthorContent);
        this.textAuthorPoster = (TextView) rootView.findViewById(R.id.textAuthorPoster);
        this.layoutAuthor = (RelativeLayout) rootView.findViewById(R.id.layoutAuthor);
        this.contentBtnDivider = (TextView) rootView.findViewById(R.id.contentBtnDivider);
        this.layoutHome = (RelativeLayout) rootView.findViewById(R.id.layoutHome);
        this.homeBottomDivider = (TextView) rootView.findViewById(R.id.homeBottomDivider);
    }
}
