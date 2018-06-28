package com.sorashiro.cloudcat.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.gson.Gson;
import com.sorashiro.cloudcat.FindActivity;
import com.sorashiro.cloudcat.MyService;
import com.sorashiro.cloudcat.NewPosterActivity;
import com.sorashiro.cloudcat.R;
import com.sorashiro.cloudcat.bean.PosterBean;
import com.sorashiro.cloudcat.bean.ResMsgBean;
import com.sorashiro.cloudcat.data.Constant;
import com.sorashiro.cloudcat.data.UserData;
import com.sorashiro.cloudcat.recyclerview.Divider;
import com.sorashiro.cloudcat.recyclerview.MyRecyclerView;
import com.sorashiro.cloudcat.recyclerview.WrapContentLinearLayoutManager;
import com.sorashiro.cloudcat.recyclerview.home.HomeRecyclerViewAdapter;
import com.sorashiro.cloudcat.tool.DPUtil;
import com.sorashiro.cloudcat.tool.LogAndToastUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FragmentHome extends Fragment {

    private MyRecyclerView          listPoster;
    private HomeRecyclerViewAdapter mAdapter;
    private Button                  btnNew;
    private Button                  btnFind;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        listPoster = (MyRecyclerView) view.findViewById(R.id.listPoster);
        listPoster.setEmptyView(inflater.inflate(R.layout.view_empty, container, false));
        listPoster.setLayoutManager(new WrapContentLinearLayoutManager(inflater.getContext(),
                LinearLayoutManager.VERTICAL, false));
        listPoster.setItemAnimator(new DefaultItemAnimator());
        listPoster.addItemDecoration(new Divider(
                inflater.getContext(),
                LinearLayoutManager.HORIZONTAL,
                DPUtil.dip2px(getActivity(), 10),
                getResources().getColor(R.color.colorLightDivider)
        ));
        listPoster.addOnScrollListener(new RecyclerView.OnScrollListener() {

            private float mScrollThreshold = 100;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                boolean ifCheck = Math.abs(dy) > mScrollThreshold;
                if (ifCheck) {
                    if (dy > 0) {
                        onScrollUp();
                    } else {
                        onScrollDown();
                    }
                }
            }
        });

        ArrayList<PosterBean> list = new ArrayList<>();
        mAdapter = new HomeRecyclerViewAdapter(inflater.getContext(), list);

        listPoster.setAdapter(mAdapter);

        btnNew = (Button) view.findViewById(R.id.btnNew);
        btnNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), NewPosterActivity.class);
                startActivity(intent);
            }
        });

        btnFind = (Button) view.findViewById(R.id.btnFind);
        btnFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), FindActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

    private void onScrollUp() {
        btnNew.setVisibility(View.GONE);
    }

    private void onScrollDown() {
        btnNew.setVisibility(View.VISIBLE);
    }

    @Override
    public void onStart() {
        super.onStart();
        getDataFromServer(false);
    }

    public void getDataFromServer(final boolean isOuter) {
        if (isOuter) {
            LogAndToastUtil.ToastOut(getActivity(), "刷新中");
        }
        Retrofit retrofit2 = new Retrofit.Builder()
                .baseUrl(Constant.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(new OkHttpClient())
                .build();
        MyService service = retrofit2.create(MyService.class);
        Call<ResponseBody> call = service.getHome(
                UserData.getCurrentUsername()
        );
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String body = response.body().string();
                        Gson gson = new Gson();
                        if (body.contains("resMsg")) {
                            String show = gson.fromJson(body, ResMsgBean.class).getResMsg();
                            LogAndToastUtil.ToastOut(getContext(), show);
                            return;
                        }
                        PosterBean[] beans = gson.fromJson(body, PosterBean[].class);
                        List<PosterBean> list = Arrays.asList(beans);
                        mAdapter = (HomeRecyclerViewAdapter) listPoster.getAdapter();
                        mAdapter.setList(list);
                        if (isOuter) {
                            LogAndToastUtil.ToastOut(getActivity(), "已刷新");
                        }
                    } catch (IOException e) {
                        LogAndToastUtil.ToastOut(getContext(), e.getMessage());
                    }
                } else {
                    String error = response.code() + ": " + response.message();
                    LogAndToastUtil.LogV(error);
                    LogAndToastUtil.ToastOut(getContext(),
                            "啊哦，服务器开小差了，请稍候再试");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                LogAndToastUtil.LogV("Error" + t.toString());
                LogAndToastUtil.ToastOut(getContext(),
                        "啊哦，服务器开小差了，请稍候再试");
            }
        });
    }

}