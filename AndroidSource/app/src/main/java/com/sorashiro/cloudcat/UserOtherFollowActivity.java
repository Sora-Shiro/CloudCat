package com.sorashiro.cloudcat;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.sorashiro.cloudcat.bean.OtherFollowBean;
import com.sorashiro.cloudcat.bean.ResMsgBean;
import com.sorashiro.cloudcat.data.Constant;
import com.sorashiro.cloudcat.data.UserData;
import com.sorashiro.cloudcat.recyclerview.Divider;
import com.sorashiro.cloudcat.recyclerview.MyRecyclerView;
import com.sorashiro.cloudcat.recyclerview.WrapContentLinearLayoutManager;
import com.sorashiro.cloudcat.recyclerview.user.UserOtherFollowRVAdapter;
import com.sorashiro.cloudcat.tool.LogAndToastUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class UserOtherFollowActivity extends AppCompatActivity implements View.OnClickListener {

    private String mUserName;

    private UserOtherFollowRVAdapter mAdapter;
    private Button                   btnBack;
    private TextView                 textFollowHead;
    private RelativeLayout           layoutFollowHead;
    private TextView                 followHeadDivider;
    private MyRecyclerView           listUserFollow;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_follow);
        initView();

        mUserName = getIntent().getStringExtra("username");

        listUserFollow = (MyRecyclerView) findViewById(R.id.listUserFollow);
        listUserFollow.setEmptyView(View.inflate(this, R.layout.view_empty, null));
        listUserFollow.setLayoutManager(new WrapContentLinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));
        listUserFollow.setItemAnimator(new DefaultItemAnimator());
        listUserFollow.addItemDecoration(new Divider(
                this,
                LinearLayoutManager.HORIZONTAL,
                1,
                getResources().getColor(R.color.colorLightDivider)
        ));

        ArrayList<OtherFollowBean> list = new ArrayList<>();
        mAdapter = new UserOtherFollowRVAdapter(this, list);

        listUserFollow.setAdapter(mAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        getDataFromServer();
    }

    private void getDataFromServer() {
        Retrofit retrofit2 = new Retrofit.Builder()
                .baseUrl(Constant.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(new OkHttpClient())
                .build();
        MyService service = retrofit2.create(MyService.class);
        Call<ResponseBody> call = service.getOtherFollow(
                mUserName,
                UserData.getCurrentUsername(),
                1
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
                            LogAndToastUtil.ToastOut(UserOtherFollowActivity.this, show);
                            return;
                        }
                        OtherFollowBean[] beans = gson.fromJson(body, OtherFollowBean[].class);
                        ArrayList<OtherFollowBean> list = new ArrayList<>();
                        list.addAll(Arrays.asList(beans));
                        mAdapter = (UserOtherFollowRVAdapter) listUserFollow.getAdapter();
                        mAdapter.setList(list);
                    } catch (IOException e) {
                        LogAndToastUtil.ToastOut(UserOtherFollowActivity.this, e.getMessage());
                    }
                } else {
                    String error = response.code() + ": " + response.message();
                    LogAndToastUtil.LogV(error);
                    LogAndToastUtil.ToastOut(UserOtherFollowActivity.this,
                            "啊哦，服务器开小差了，请稍候再试");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                LogAndToastUtil.LogV("Error" + t.toString());
                LogAndToastUtil.ToastOut(UserOtherFollowActivity.this,
                        "啊哦，服务器开小差了，请稍候再试");
            }
        });
    }

    private void initView() {
        btnBack = (Button) findViewById(R.id.btnBack);
        textFollowHead = (TextView) findViewById(R.id.textFollowHead);
        layoutFollowHead = (RelativeLayout) findViewById(R.id.layoutFollowHead);
        followHeadDivider = (TextView) findViewById(R.id.followHeadDivider);
        listUserFollow = (MyRecyclerView) findViewById(R.id.listUserFollow);

        btnBack.setOnClickListener(this);
        textFollowHead.setOnClickListener(this);
        layoutFollowHead.setOnClickListener(this);
        followHeadDivider.setOnClickListener(this);
        listUserFollow.setOnClickListener(this);
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
