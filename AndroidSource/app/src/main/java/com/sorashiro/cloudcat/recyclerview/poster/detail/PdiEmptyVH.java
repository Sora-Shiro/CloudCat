package com.sorashiro.cloudcat.recyclerview.poster.detail;

import android.view.View;
import android.widget.TextView;

import com.sorashiro.cloudcat.R;


public class PdiEmptyVH extends PDBaseViewHolder {

    public View     rootView;
    public TextView textEmpty;

    public PdiEmptyVH(View rootView) {
        super(rootView);
        this.rootView = rootView;
        this.textEmpty = (TextView) rootView.findViewById(R.id.textEmpty);
    }
}
