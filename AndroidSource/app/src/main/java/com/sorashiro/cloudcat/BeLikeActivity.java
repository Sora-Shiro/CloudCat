package com.sorashiro.cloudcat;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.sorashiro.cloudcat.bean.BeLikeBean;
import com.sorashiro.cloudcat.bean.ResMsgBean;
import com.sorashiro.cloudcat.data.Constant;
import com.sorashiro.cloudcat.data.UserData;
import com.sorashiro.cloudcat.recyclerview.Divider;
import com.sorashiro.cloudcat.recyclerview.MyRecyclerView;
import com.sorashiro.cloudcat.recyclerview.WrapContentLinearLayoutManager;
import com.sorashiro.cloudcat.recyclerview.notification.BeLikeRecyclerViewAdapter;
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


public class BeLikeActivity extends AppCompatActivity implements View.OnClickListener {

    private Button                    btnBack;
    private TextView                  textDetailHead;
    private TextView                  atHeadDivider;
    private MyRecyclerView            listBeLike;
    private BeLikeRecyclerViewAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_be_like);
        initView();

        listBeLike = (MyRecyclerView) findViewById(R.id.listBeLike);
        listBeLike.setEmptyView(View.inflate(this, R.layout.view_empty, null));
        listBeLike.setLayoutManager(new WrapContentLinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));
        listBeLike.setItemAnimator(new DefaultItemAnimator());
        listBeLike.addItemDecoration(new Divider(
                this,
                LinearLayoutManager.HORIZONTAL,
                DPUtil.dip2px(this, 10),
                getResources().getColor(R.color.colorLightDivider)
        ));
        mAdapter = new BeLikeRecyclerViewAdapter(
                this,
                new ArrayList<BeLikeBean>());
        listBeLike.setAdapter(mAdapter);

        getDataFromServer();
    }

    private void initView() {
        btnBack = (Button) findViewById(R.id.btnBack);
        textDetailHead = (TextView) findViewById(R.id.textAtHead);
        atHeadDivider = (TextView) findViewById(R.id.atHeadDivider);
        listBeLike = (MyRecyclerView) findViewById(R.id.listBeLike);

        btnBack.setOnClickListener(this);
    }

    private void getDataFromServer() {
        Retrofit retrofit2 = new Retrofit.Builder()
                .baseUrl(Constant.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(new OkHttpClient())
                .build();
        MyService service = retrofit2.create(MyService.class);
        Call<ResponseBody> call = service.getBeLikeList(
                UserData.getCurrentUsername(),
                UserData.getCurrentPassword()
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
                            LogAndToastUtil.ToastOut(BeLikeActivity.this, show);
                            return;
                        }
                        BeLikeBean[] beans = gson.fromJson(body, BeLikeBean[].class);
                        List<BeLikeBean> list = Arrays.asList(beans);
                        BeLikeRecyclerViewAdapter adapter = (BeLikeRecyclerViewAdapter) listBeLike.getAdapter();
                        adapter.setList(list);
                    } catch (IOException e) {
                        LogAndToastUtil.ToastOut(
                                BeLikeActivity.this, e.getMessage());
                    }
                } else {
                    String error = response.code() + ": " + response.message();
                    LogAndToastUtil.LogV(error);
                    LogAndToastUtil.ToastOut(BeLikeActivity.this,
                            "啊哦，服务器开小差了，请稍候再试");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                LogAndToastUtil.LogV("Error" + t.toString());
                LogAndToastUtil.ToastOut(BeLikeActivity.this,
                        "啊哦，服务器开小差了，请稍候再试");
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnBack:
                finish();
                break;
        }
    }
}
