package com.sorashiro.cloudcat;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.sorashiro.cloudcat.bean.ResMsgBean;
import com.sorashiro.cloudcat.bean.UserBean;
import com.sorashiro.cloudcat.data.Constant;
import com.sorashiro.cloudcat.recyclerview.Divider;
import com.sorashiro.cloudcat.recyclerview.MyRecyclerView;
import com.sorashiro.cloudcat.recyclerview.WrapContentLinearLayoutManager;
import com.sorashiro.cloudcat.recyclerview.find.UserFindAdapter;
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


public class FindActivity extends AppCompatActivity implements View.OnClickListener {

    private Button          btnBack;
    private Button          btnFind;
    private TextView        textFindHead;
    private RelativeLayout  layoutFindHead;
    private TextView        findHeadDivider;
    private EditText        editFind;
    private TextView        findEditDivider;
    private MyRecyclerView  listUserFind;
    private UserFindAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find);

        initView();

        listUserFind = (MyRecyclerView) findViewById(R.id.listUserFind);
        listUserFind.setLayoutManager(new WrapContentLinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));
        listUserFind.setItemAnimator(new DefaultItemAnimator());
        listUserFind.addItemDecoration(new Divider(
                this,
                LinearLayoutManager.HORIZONTAL,
                1,
                getResources().getColor(R.color.colorLightDivider)
        ));

        ArrayList<UserBean> list = new ArrayList<>();
        mAdapter = new UserFindAdapter(this, list);

        listUserFind.setAdapter(mAdapter);
    }

    private void initView() {
        btnBack = (Button) findViewById(R.id.btnBack);
        btnFind = (Button) findViewById(R.id.btnFind);
        textFindHead = (TextView) findViewById(R.id.textFindHead);
        layoutFindHead = (RelativeLayout) findViewById(R.id.layoutFindHead);
        findHeadDivider = (TextView) findViewById(R.id.findHeadDivider);
        editFind = (EditText) findViewById(R.id.editFind);
        findEditDivider = (TextView) findViewById(R.id.findEditDivider);
        listUserFind = (MyRecyclerView) findViewById(R.id.listUserFind);

        btnBack.setOnClickListener(this);
        btnFind.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnBack:
                finish();
                break;
            case R.id.btnFind:
                getDataByServer();
                break;
        }
    }

    private void getDataByServer() {
        String findName = editFind.getText().toString();
        if (findName.trim().length() == 0) {
            LogAndToastUtil.ToastOut(this, "请输入你想查找的用户名");
            return;
        }
        Retrofit retrofit2 = new Retrofit.Builder()
                .baseUrl(Constant.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(new OkHttpClient())
                .build();
        MyService service = retrofit2.create(MyService.class);
        Call<ResponseBody> call = service.findUsers(
                findName
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
                            LogAndToastUtil.ToastOut(FindActivity.this, show);
                            return;
                        }
                        UserBean[] beans = gson.fromJson(body, UserBean[].class);
                        List<UserBean> list = Arrays.asList(beans);
                        mAdapter = (UserFindAdapter) listUserFind.getAdapter();
                        mAdapter.setList(list);
                    } catch (IOException e) {
                        LogAndToastUtil.ToastOut(FindActivity.this, e.getMessage());
                    }
                } else {
                    String error = response.code() + ": " + response.message();
                    LogAndToastUtil.LogV(error);
                    LogAndToastUtil.ToastOut(FindActivity.this,
                            "啊哦，服务器开小差了，请稍候再试");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                LogAndToastUtil.LogV("Error" + t.toString());
                LogAndToastUtil.ToastOut(FindActivity.this,
                        "啊哦，服务器开小差了，请稍候再试");
            }
        });
    }

}
