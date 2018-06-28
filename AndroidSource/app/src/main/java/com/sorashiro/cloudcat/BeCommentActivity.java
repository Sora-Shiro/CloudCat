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
import com.sorashiro.cloudcat.bean.BeCommentBean;
import com.sorashiro.cloudcat.bean.ResMsgBean;
import com.sorashiro.cloudcat.data.Constant;
import com.sorashiro.cloudcat.data.UserData;
import com.sorashiro.cloudcat.recyclerview.Divider;
import com.sorashiro.cloudcat.recyclerview.MyRecyclerView;
import com.sorashiro.cloudcat.recyclerview.WrapContentLinearLayoutManager;
import com.sorashiro.cloudcat.recyclerview.notification.BeCommentRecyclerViewAdapter;
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


public class BeCommentActivity extends AppCompatActivity implements View.OnClickListener {

    private Button                       btnBack;
    private TextView                     textDetailHead;
    private TextView                     atHeadDivider;
    private MyRecyclerView               listBeComment;
    private BeCommentRecyclerViewAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_be_comment);
        initView();

        listBeComment = (MyRecyclerView) findViewById(R.id.listBeComment);
        listBeComment.setEmptyView(View.inflate(this, R.layout.view_empty, null));
        listBeComment.setLayoutManager(new WrapContentLinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));
        listBeComment.setItemAnimator(new DefaultItemAnimator());
        listBeComment.addItemDecoration(new Divider(
                this,
                LinearLayoutManager.HORIZONTAL,
                DPUtil.dip2px(this, 10),
                getResources().getColor(R.color.colorLightDivider)
        ));
        mAdapter = new BeCommentRecyclerViewAdapter(
                this,
                new ArrayList<BeCommentBean>());
        listBeComment.setAdapter(mAdapter);

        getDataFromServer();
    }

    private void initView() {
        btnBack = (Button) findViewById(R.id.btnBack);
        textDetailHead = (TextView) findViewById(R.id.textAtHead);
        atHeadDivider = (TextView) findViewById(R.id.atHeadDivider);
        listBeComment = (MyRecyclerView) findViewById(R.id.listBeAt);

        btnBack.setOnClickListener(this);
    }

    private void getDataFromServer() {
        Retrofit retrofit2 = new Retrofit.Builder()
                .baseUrl(Constant.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(new OkHttpClient())
                .build();
        MyService service = retrofit2.create(MyService.class);
        Call<ResponseBody> call = service.getBeCommentList(
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
                            LogAndToastUtil.ToastOut(BeCommentActivity.this, show);
                            return;
                        }
                        BeCommentBean[] beans = gson.fromJson(body, BeCommentBean[].class);
                        List<BeCommentBean> list = Arrays.asList(beans);
                        BeCommentRecyclerViewAdapter adapter = (BeCommentRecyclerViewAdapter) listBeComment.getAdapter();
                        adapter.setList(list);
                    } catch (IOException e) {
                        LogAndToastUtil.ToastOut(
                                BeCommentActivity.this, e.getMessage());
                    }
                } else {
                    String error = response.code() + ": " + response.message();
                    LogAndToastUtil.LogV(error);
                    LogAndToastUtil.ToastOut(BeCommentActivity.this,
                            "啊哦，服务器开小差了，请稍候再试");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                LogAndToastUtil.LogV("Error" + t.toString());
                LogAndToastUtil.ToastOut(BeCommentActivity.this,
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
