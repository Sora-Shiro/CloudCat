package com.sorashiro.cloudcat;

import android.content.Intent;
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


public class SignInActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView textHomeHead;
    private EditText editUserName;
    private EditText editPassWord;
    private Button   btnSignIn;
    private Button   btnSignUp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(UserData.getCurrentUsername().length() != 0) {
            Intent intent = new Intent(SignInActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        setContentView(R.layout.activity_signin);
        initView();

    }


    private void initView() {
        textHomeHead = (TextView) findViewById(R.id.textHomeHead);
        editUserName = (EditText) findViewById(R.id.editUserName);
        editPassWord = (EditText) findViewById(R.id.editPassWord);
        btnSignIn = (Button) findViewById(R.id.btnSignIn);
        btnSignUp = (Button) findViewById(R.id.btnSignUp);

        btnSignIn.setOnClickListener(this);
        btnSignUp.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSignIn:
                signIn();
                break;
            case R.id.btnSignUp:
                Intent signUpIntent = new Intent(this, SignUpActivity.class);
                startActivity(signUpIntent);
                break;
        }
    }

    private void signIn() {
        final String username = editUserName.getText().toString();
        final String password = editPassWord.getText().toString();
        if (username.trim().length() == 0 || password.length() == 0) {
            LogAndToastUtil.ToastOut(SignInActivity.this, "用户名和密码不能为空");
            return;
        }
        Retrofit retrofit2 = new Retrofit.Builder()
                .baseUrl(Constant.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(new OkHttpClient())
                .build();
        MyService service = retrofit2.create(MyService.class);
        Call<ResMsgBean> call = service.login(
                username,
                password
        );
        call.enqueue(new Callback<ResMsgBean>() {
            @Override
            public void onResponse(Call<ResMsgBean> call, Response<ResMsgBean> response) {
                if (response.isSuccessful()) {
                    ResMsgBean bean = response.body();
                    String resultStr = bean.getResMsg();
                    LogAndToastUtil.ToastOut(SignInActivity.this, resultStr);
                    if (resultStr.equals("登录成功")) {
                        UserData.setCurrentUserName(username);
                        UserData.setCurrentPassword(password);
                        Intent intent = new Intent(
                                SignInActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                } else {
                    String error = response.code() + ": " + response.message();
                    LogAndToastUtil.LogV(error);
                    LogAndToastUtil.ToastOut(SignInActivity.this,
                            "啊哦，服务器开小差了，请稍候再试");
                }
            }

            @Override
            public void onFailure(Call<ResMsgBean> call, Throwable t) {
                LogAndToastUtil.LogV("Error" + t.toString());
                LogAndToastUtil.ToastOut(SignInActivity.this,
                        "啊哦，服务器开小差了，请稍候再试");
            }
        });
    }

}
