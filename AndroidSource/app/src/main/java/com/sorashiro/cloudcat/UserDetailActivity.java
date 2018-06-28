package com.sorashiro.cloudcat;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sorashiro.cloudcat.bean.CheckFollowBean;
import com.sorashiro.cloudcat.bean.ResMsgBean;
import com.sorashiro.cloudcat.bean.UserDetailBean;
import com.sorashiro.cloudcat.data.Constant;
import com.sorashiro.cloudcat.data.UserData;
import com.sorashiro.cloudcat.tool.LogAndToastUtil;
import com.squareup.picasso.Picasso;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.sorashiro.cloudcat.data.Constant.BASE_URL;

/**
 * Created by Administrator on 2017/11/13.
 */

public class UserDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private String mUserName;

    private Button         btnBack;
    private RelativeLayout layoutUserDetailHead;
    private TextView       textMeName;
    private TextView       textPosterNum;
    private RelativeLayout layoutPosterNum;
    private TextView       textFollowNum;
    private RelativeLayout layoutFollowNum;
    private TextView       textFollowingNum;
    private RelativeLayout layoutFollowingNum;
    private Button         btnChangeFollow;
    private ImageView imgMeHead;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);

        mUserName = getIntent().getStringExtra("username");

        initView();

        textMeName.setText(mUserName);

        Picasso.with(this)
                .load(Constant.BASE_STATIC_AVATAR_URL + mUserName)
                .placeholder(Constant.AVATAR_DEFAULT)
                .error(Constant.AVATAR_DEFAULT)
                .into(imgMeHead);

    }

    @Override
    protected void onStart() {
        super.onStart();
        getDataFromServer();
        getRelationshipFromServer();
    }

    private void getDataFromServer() {
        Retrofit retrofit2 = new Retrofit.Builder()
                .baseUrl(Constant.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(new OkHttpClient())
                .build();
        MyService service = retrofit2.create(MyService.class);
        Call<UserDetailBean> call = service.getUserDetail(
                mUserName
        );
        call.enqueue(new Callback<UserDetailBean>() {
            @Override
            public void onResponse(Call<UserDetailBean> call, Response<UserDetailBean> response) {
                if (response.isSuccessful()) {
                    UserDetailBean bean = response.body();
                    if (bean.getResMsg().length() != 0) {
                        LogAndToastUtil.ToastOut(UserDetailActivity.this, bean.getResMsg());
                        return;
                    }
                    String posterNum = bean.getPoster_num() + "";
                    String followNum = bean.getFollow_num() + "";
                    String followingNum = bean.getFollowing_num() + "";
                    textPosterNum.setText(posterNum);
                    textFollowNum.setText(followNum);
                    textFollowingNum.setText(followingNum);
                } else {
                    String error = response.code() + ": " + response.message();
                    LogAndToastUtil.LogV(error);
                    LogAndToastUtil.ToastOut(UserDetailActivity.this,
                            "啊哦，服务器开小差了，请稍候再试");
                }
            }

            @Override
            public void onFailure(Call<UserDetailBean> call, Throwable t) {
                LogAndToastUtil.LogV("Error" + t.toString());
                LogAndToastUtil.ToastOut(UserDetailActivity.this,
                        "啊哦，服务器开小差了，请稍候再试");
            }
        });
    }

    private void getRelationshipFromServer() {
        Retrofit retrofit2 = new Retrofit.Builder()
                .baseUrl(Constant.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(new OkHttpClient())
                .build();
        MyService service = retrofit2.create(MyService.class);
        Call<CheckFollowBean> call = service.checkFollow(
                UserData.getCurrentUsername(),
                mUserName
        );
        call.enqueue(new Callback<CheckFollowBean>() {
            @Override
            public void onResponse(Call<CheckFollowBean> call, Response<CheckFollowBean> response) {
                if (response.isSuccessful()) {
                    final CheckFollowBean bean = response.body();
                    if (bean.isChecker2user() && bean.isUser2checker()) {
                        btnChangeFollow.setText("相互关注");
                    } else if (bean.isChecker2user()) {
                        btnChangeFollow.setText("已关注");
                    } else {
                        btnChangeFollow.setText("关注");
                    }
                    btnChangeFollow.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Retrofit retrofit2 = new Retrofit.Builder()
                                    .baseUrl(BASE_URL)
                                    .addConverterFactory(GsonConverterFactory.create())
                                    .client(new OkHttpClient())
                                    .build();
                            MyService service = retrofit2.create(MyService.class);
                            Call<ResMsgBean> call = service.changeFollow(
                                    UserData.getCurrentUsername(),
                                    UserData.getCurrentPassword(),
                                    textMeName.getText().toString(),
                                    1
                            );
                            call.enqueue(new Callback<ResMsgBean>() {
                                @Override
                                public void onResponse(Call<ResMsgBean> call, Response<ResMsgBean> response) {
                                    if (response.isSuccessful()) {
                                        LogAndToastUtil.ToastOut(
                                                UserDetailActivity.this, response.body().getResMsg());
                                        bean.setChecker2user(!bean.isChecker2user());
                                        if (bean.isChecker2user() && bean.isUser2checker()) {
                                            btnChangeFollow.setText("相互关注");
                                        } else if (bean.isChecker2user()) {
                                            btnChangeFollow.setText("已关注");
                                        } else {
                                            btnChangeFollow.setText("关注");
                                        }
                                    } else {
                                        String error = response.code() + ": " + response.message();
                                        LogAndToastUtil.LogV(error);
                                        LogAndToastUtil.ToastOut(UserDetailActivity.this,
                                                "啊哦，服务器开小差了，请稍候再试");
                                    }
                                }

                                @Override
                                public void onFailure(Call<ResMsgBean> call, Throwable t) {
                                    LogAndToastUtil.LogV("Error" + t.toString());
                                    LogAndToastUtil.ToastOut(UserDetailActivity.this,
                                            "啊哦，服务器没有返回数据，请稍候再试");
                                }
                            });
                        }
                    });
                } else {
                    String error = response.code() + ": " + response.message();
                    LogAndToastUtil.LogV(error);
                    LogAndToastUtil.ToastOut(UserDetailActivity.this,
                            "啊哦，服务器开小差了，请稍候再试");
                }
            }

            @Override
            public void onFailure(Call<CheckFollowBean> call, Throwable t) {
                LogAndToastUtil.LogV("Error" + t.toString());
                LogAndToastUtil.ToastOut(UserDetailActivity.this,
                        "啊哦，服务器开小差了，请稍候再试");
            }
        });
    }

    private void initView() {
        btnBack = (Button) findViewById(R.id.btnBack);
        btnChangeFollow = (Button) findViewById(R.id.btnChangeFollow);
        layoutUserDetailHead = (RelativeLayout) findViewById(R.id.layoutUserDetailHead);
        textMeName = (TextView) findViewById(R.id.textMeName);
        textPosterNum = (TextView) findViewById(R.id.textPosterNum);
        layoutPosterNum = (RelativeLayout) findViewById(R.id.layoutPosterNum);
        textFollowNum = (TextView) findViewById(R.id.textFollowNum);
        layoutFollowNum = (RelativeLayout) findViewById(R.id.layoutFollowNum);
        textFollowingNum = (TextView) findViewById(R.id.textFollowingNum);
        layoutFollowingNum = (RelativeLayout) findViewById(R.id.layoutFollowingNum);
        imgMeHead = (ImageView) findViewById(R.id.imgMeHead);

        btnBack.setOnClickListener(this);
        layoutPosterNum.setOnClickListener(this);
        layoutFollowNum.setOnClickListener(this);
        layoutFollowingNum.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnBack:
                finish();
                break;
            case R.id.layoutPosterNum:
                Intent posterIntent = new Intent(UserDetailActivity.this, UserPosterActivity.class);
                posterIntent.putExtra("username", mUserName);
                startActivity(posterIntent);
                break;
            case R.id.layoutFollowNum:
                if (mUserName.equals(UserData.getCurrentUsername())) {
                    Intent followIntent = new Intent(UserDetailActivity.this, UserFollowActivity.class);
                    followIntent.putExtra("username", mUserName);
                    startActivity(followIntent);
                } else {
                    Intent followIntent = new Intent(UserDetailActivity.this, UserOtherFollowActivity.class);
                    followIntent.putExtra("username", mUserName);
                    startActivity(followIntent);
                }
                break;
            case R.id.layoutFollowingNum:
                if (mUserName.equals(UserData.getCurrentUsername())) {
                    Intent followingIntent = new Intent(UserDetailActivity.this, UserFollowingActivity.class);
                    followingIntent.putExtra("username", mUserName);
                    startActivity(followingIntent);
                } else {
                    Intent followingIntent = new Intent(UserDetailActivity.this, UserOtherFollowingActivity.class);
                    followingIntent.putExtra("username", mUserName);
                    startActivity(followingIntent);
                }

                break;
        }
    }
}
