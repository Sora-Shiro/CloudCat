package com.sorashiro.cloudcat.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sorashiro.cloudcat.R;
import com.sorashiro.cloudcat.recyclerview.Divider;
import com.sorashiro.cloudcat.recyclerview.MyRecyclerView;
import com.sorashiro.cloudcat.recyclerview.WrapContentLinearLayoutManager;
import com.sorashiro.cloudcat.recyclerview.notification.NotiRecyclerViewAdapter;
import com.sorashiro.cloudcat.tool.DPUtil;

import java.util.ArrayList;

public class FragmentNotification extends Fragment {

    private MyRecyclerView          listNotification;
    private NotiRecyclerViewAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        listNotification = (MyRecyclerView) view.findViewById(R.id.listNotification);
        listNotification.setEmptyView(inflater.inflate(R.layout.view_empty, container, false));
        listNotification.setLayoutManager(new WrapContentLinearLayoutManager(inflater.getContext(),
                LinearLayoutManager.VERTICAL, false));
        listNotification.setItemAnimator(new DefaultItemAnimator());
        listNotification.addItemDecoration(new Divider(
                inflater.getContext(),
                LinearLayoutManager.HORIZONTAL,
                DPUtil.dip2px(getContext(), 1),
                getResources().getColor(R.color.colorLightDivider)
        ));

        ArrayList<String> list = new ArrayList<>();
        mAdapter = new NotiRecyclerViewAdapter(inflater.getContext(), list);

        listNotification.setAdapter(mAdapter);

        return view;
    }

}  