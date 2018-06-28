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
import com.sorashiro.cloudcat.bean.PosterBean;
import com.sorashiro.cloudcat.bean.ResMsgBean;
import com.sorashiro.cloudcat.data.Constant;
import com.sorashiro.cloudcat.recyclerview.Divider;
import com.sorashiro.cloudcat.recyclerview.MyRecyclerView;
import com.sorashiro.cloudcat.recyclerview.WrapContentLinearLayoutManager;
import com.sorashiro.cloudcat.recyclerview.user.UserPosterRVAdapter;
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


public class UserPosterActivity extends AppCompatActivity implements View.OnClickListener {

    private Button         btnBack;
    private TextView       textPosterHead;
    private TextView       posterHeadDivider;
    private MyRecyclerView listUserPoster;

    private String mUserName;

    private UserPosterRVAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_poster);
        initView();

        mUserName = getIntent().getStringExtra("username");

        listUserPoster = (MyRecyclerView) findViewById(R.id.listUserPoster);
        listUserPoster.setEmptyView(View.inflate(this, R.layout.view_empty, null));
        listUserPoster.setLayoutManager(new WrapContentLinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));
        listUserPoster.setItemAnimator(new DefaultItemAnimator());
        listUserPoster.addItemDecoration(new Divider(
                this,
                LinearLayoutManager.HORIZONTAL,
                DPUtil.dip2px(this, 10),
                getResources().getColor(R.color.colorLightDivider)
        ));

        ArrayList<PosterBean> list = new ArrayList<>();
        mAdapter = new UserPosterRVAdapter(this, list);

        listUserPoster.setAdapter(mAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        getDataFromServer(false);
    }

    public void getDataFromServer(final boolean isOuter) {
        if (isOuter) {
            LogAndToastUtil.ToastOut(this, "刷新中");
        }
        Retrofit retrofit2 = new Retrofit.Builder()
                .baseUrl(Constant.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(new OkHttpClient())
                .build();
        MyService service = retrofit2.create(MyService.class);
        Call<ResponseBody> call = service.getPosterList(
                mUserName
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
                            LogAndToastUtil.ToastOut(UserPosterActivity.this, show);
                            return;
                        }
                        PosterBean[] beans = gson.fromJson(body, PosterBean[].class);
                        List<PosterBean> list = Arrays.asList(beans);
                        mAdapter = (UserPosterRVAdapter) listUserPoster.getAdapter();
                        mAdapter.setList(list);
                        if (isOuter) {
                            LogAndToastUtil.ToastOut(UserPosterActivity.this, "已刷新");
                        }
                    } catch (IOException e) {
                        LogAndToastUtil.ToastOut(UserPosterActivity.this, e.getMessage());
                    }
                } else {
                    String error = response.code() + ": " + response.message();
                    LogAndToastUtil.LogV(error);
                    LogAndToastUtil.ToastOut(UserPosterActivity.this,
                            "啊哦，服务器开小差了，请稍候再试");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                LogAndToastUtil.LogV("Error" + t.toString());
                LogAndToastUtil.ToastOut(UserPosterActivity.this,
                        "啊哦，服务器开小差了，请稍候再试");
            }
        });
    }

    private void initView() {
        btnBack = (Button) findViewById(R.id.btnBack);
        textPosterHead = (TextView) findViewById(R.id.textPosterHead);
        posterHeadDivider = (TextView) findViewById(R.id.posterHeadDivider);
        listUserPoster = (MyRecyclerView) findViewById(R.id.listUserPoster);

        btnBack.setOnClickListener(this);
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
