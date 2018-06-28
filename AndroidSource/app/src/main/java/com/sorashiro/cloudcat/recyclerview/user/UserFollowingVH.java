package com.sorashiro.cloudcat.recyclerview.user;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sorashiro.cloudcat.R;


public class UserFollowingVH extends RecyclerView.ViewHolder {
    public View           rootView;
    public TextView       homeHeadDivider;
    public ImageView      imgHead;
    public TextView       textName;
    public Button         btnChangeFollow;
    public RelativeLayout layoutHome;
    public TextView       homeBottomDivider;

    public UserFollowingVH(View rootView) {
        super(rootView);
        this.rootView = rootView;
        this.homeHeadDivider = (TextView) rootView.findViewById(R.id.homeHeadDivider);
        this.imgHead = (ImageView) rootView.findViewById(R.id.imgHead);
        this.textName = (TextView) rootView.findViewById(R.id.textName);
        this.btnChangeFollow = (Button) rootView.findViewById(R.id.btnChangeFollow);
        this.layoutHome = (RelativeLayout) rootView.findViewById(R.id.layoutHome);
        this.homeBottomDivider = (TextView) rootView.findViewById(R.id.homeBottomDivider);
    }

}
