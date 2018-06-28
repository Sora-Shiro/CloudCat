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
import com.sorashiro.cloudcat.tool.LogAndToastUtil;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView textHomeHead;
    private EditText editUserName;
    private EditText editPassWord;
    private EditText editPassWordConfirm;
    private EditText editEmail;
    private Button   btnRegister;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        initView();

    }


    private void initView() {
        textHomeHead = (TextView) findViewById(R.id.textHomeHead);
        editUserName = (EditText) findViewById(R.id.editUserName);
        editPassWord = (EditText) findViewById(R.id.editPassWord);
        editPassWordConfirm = (EditText) findViewById(R.id.editPassWordConfirm);
        editEmail = (EditText) findViewById(R.id.editEmail);
        btnRegister = (Button) findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnRegister:
                signUp();
                break;
        }
    }

    private void signUp() {
        String username = editUserName.getText().toString();
        String password = editPassWord.getText().toString();
        String passwordConfirm = editPassWordConfirm.getText().toString();
        String email = editEmail.getText().toString();
        if (username.trim().length() == 0 || password.length() == 0 || email.trim().length() == 0) {
            LogAndToastUtil.ToastOut(SignUpActivity.this, "请填上所有字段");
            return;
        }
        if (!password.equals(passwordConfirm)) {
            LogAndToastUtil.ToastOut(SignUpActivity.this, "两次填写的密码不一致");
        }
        Retrofit retrofit2 = new Retrofit.Builder()
                .baseUrl(Constant.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(new OkHttpClient())
                .build();
        MyService service = retrofit2.create(MyService.class);
        Call<ResMsgBean> call = service.register(
                username,
                password,
                email
        );
        call.enqueue(new Callback<ResMsgBean>() {
            @Override
            public void onResponse(Call<ResMsgBean> call, Response<ResMsgBean> response) {
                if (response.isSuccessful()) {
                    ResMsgBean bean = response.body();
                    String resultStr = bean.getResMsg();
                    LogAndToastUtil.ToastOut(SignUpActivity.this, resultStr);
                    if (resultStr.equals("注册成功")) {
                        finish();
                    }
                } else {
                    String error = response.code() + ": " + response.message();
                    LogAndToastUtil.LogV(error);
                    LogAndToastUtil.ToastOut(SignUpActivity.this,
                            "啊哦，服务器开小差了，请稍候再试");
                }
            }

            @Override
            public void onFailure(Call<ResMsgBean> call, Throwable t) {
                LogAndToastUtil.LogV("Error" + t.toString());
                LogAndToastUtil.ToastOut(SignUpActivity.this,
                        "啊哦，服务器开小差了，请稍候再试");
            }
        });
    }

}
