package com.sorashiro.cloudcat;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.sorashiro.cloudcat.bean.ResMsgBean;
import com.sorashiro.cloudcat.data.Constant;
import com.sorashiro.cloudcat.data.UserData;
import com.sorashiro.cloudcat.tool.LogAndToastUtil;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class PdiActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int PDI_FORWARD = 1;
    public static final int PDI_COMMENT = 2;

    private Button   btnBack;
    private TextView textDetailHead;
    private Button   btnSend;
    private EditText editSend;

    private int mPdiType;
    private int mPosterId;

    private Retrofit  retrofit2 = new Retrofit.Builder()
            .baseUrl(Constant.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(new OkHttpClient())
            .build();
    private MyService service   = retrofit2.create(MyService.class);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdi);
        initView();

        mPdiType = getIntent().getIntExtra("pdi_type", 0);
        mPosterId = getIntent().getIntExtra("poster_id", -1);
        if (mPdiType == 0 || mPosterId == -1) {
            LogAndToastUtil.ToastOut(PdiActivity.this, "该动态不存在，无法转发或评论");
            finish();
        }

        switch (mPdiType) {
            case PDI_FORWARD:
                textDetailHead.setText(getResources().getString(R.string.forward_poster));
                editSend.setHint(getResources().getString(R.string.forward_poster_hint));
                break;
            case PDI_COMMENT:
                textDetailHead.setText(getResources().getString(R.string.comment_poster));
                editSend.setHint(getResources().getString(R.string.comment_poster_hint));
                break;
        }
    }

    private void initView() {
        btnBack = (Button) findViewById(R.id.btnBack);
        textDetailHead = (TextView) findViewById(R.id.textDetailHead);
        btnSend = (Button) findViewById(R.id.btnSend);
        editSend = (EditText) findViewById(R.id.editSend);

        btnBack.setOnClickListener(this);
        btnSend.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnBack:
                finish();
                break;
            case R.id.btnSend:
                btnSend.setClickable(false);
                sendSomething();
                finish();
                break;
        }
    }

    private void sendSomething() {
        String text = editSend.getText().toString();
        if (mPdiType == PDI_FORWARD) {
            sendForward(text);
        } else if (text.length() != 0) {
            if (text.length() > 140) {
                LogAndToastUtil.ToastOut(PdiActivity.this, "不能超过140字哦~");
            } else {
                sendComment(text);
            }
        } else {
            LogAndToastUtil.ToastOut(PdiActivity.this, "不能留空哦~");
        }
    }

    private void sendForward(String text) {
        Call<ResMsgBean> call = service.sendForward(
                UserData.getCurrentUsername(),
                UserData.getCurrentPassword(),
                text,
                mPosterId
        );
        call.enqueue(new Callback<ResMsgBean>() {
            @Override
            public void onResponse(Call<ResMsgBean> call, Response<ResMsgBean> response) {
                if (response.isSuccessful()) {
                    LogAndToastUtil.ToastOut(
                            PdiActivity.this, response.body().getResMsg());
                } else {
                    String error = response.code() + ": " + response.message();
                    LogAndToastUtil.LogV(error);
                    LogAndToastUtil.ToastOut(PdiActivity.this,
                            "啊哦，服务器开小差了，请稍候再试");
                }
            }

            @Override
            public void onFailure(Call<ResMsgBean> call, Throwable t) {
                LogAndToastUtil.LogV("Error" + t.toString());
                LogAndToastUtil.ToastOut(PdiActivity.this,
                        "啊哦，服务器没有返回数据，请稍候再试");
            }
        });

    }

    private void sendComment(String text) {
        Call<ResMsgBean> call = service.sendComment(
                UserData.getCurrentUsername(),
                UserData.getCurrentPassword(),
                text,
                mPosterId
        );
        call.enqueue(new Callback<ResMsgBean>() {
            @Override
            public void onResponse(Call<ResMsgBean> call, Response<ResMsgBean> response) {
                if (response.isSuccessful()) {
                    LogAndToastUtil.ToastOut(
                            PdiActivity.this, response.body().getResMsg());
                } else {
                    String error = response.code() + ": " + response.message();
                    LogAndToastUtil.LogV(error);
                    LogAndToastUtil.ToastOut(PdiActivity.this,
                            "啊哦，服务器开小差了，请稍候再试");
                }
            }

            @Override
            public void onFailure(Call<ResMsgBean> call, Throwable t) {
                LogAndToastUtil.LogV("Error" + t.toString());
                LogAndToastUtil.ToastOut(PdiActivity.this,
                        "啊哦，服务器没有返回数据，请稍候再试");
            }
        });

    }
}
