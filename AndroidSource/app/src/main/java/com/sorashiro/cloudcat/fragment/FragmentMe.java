package com.sorashiro.cloudcat.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sorashiro.cloudcat.AboutActivity;
import com.sorashiro.cloudcat.MyService;
import com.sorashiro.cloudcat.R;
import com.sorashiro.cloudcat.SettingActivity;
import com.sorashiro.cloudcat.UserFollowActivity;
import com.sorashiro.cloudcat.UserFollowingActivity;
import com.sorashiro.cloudcat.UserPosterActivity;
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

public class FragmentMe extends Fragment implements View.OnClickListener {

    private TextView       textMeHead;
    private ImageView      imgMeHead;
    private TextView       textMeName;
    private RelativeLayout layoutMeThumb;
    private TextView       contentBtnDivider;
    private TextView       textPoster;
    private RelativeLayout layoutPoster;
    private TextView       textFollower;
    private RelativeLayout layoutFollow;
    private TextView       textFollowing;
    private RelativeLayout layoutFollowing;
    private LinearLayout   layoutMeBtn;
    private TextView       thumbDivider;
    private ImageView      imgCollect;
    private TextView       textCollect;
    private RelativeLayout layoutCollection;
    private TextView       collectDivider;
    private ImageView      imgSetting;
    private TextView       textSetting;
    private RelativeLayout layoutSetting;
    private TextView       settingDivider;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_me, container, false);

        initView(view);

        textMeName.setText(UserData.getCurrentUsername());
        return view;
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
        Call<UserDetailBean> call = service.getUserDetail(
                UserData.getCurrentUsername()
        );
        call.enqueue(new Callback<UserDetailBean>() {
            @Override
            public void onResponse(Call<UserDetailBean> call, Response<UserDetailBean> response) {
                if (response.isSuccessful()) {
                    UserDetailBean bean = response.body();
                    if (bean.getResMsg().length() != 0) {
                        LogAndToastUtil.ToastOut(getContext(), bean.getResMsg());
                        return;
                    }
                    String posterNum = bean.getPoster_num() + "";
                    String followNum = bean.getFollow_num() + "";
                    String followingNum = bean.getFollowing_num() + "";
                    textPoster.setText(posterNum);
                    textFollower.setText(followNum);
                    textFollowing.setText(followingNum);
                    Picasso.with(getActivity())
                            .load(Constant.BASE_STATIC_AVATAR_URL + UserData.getCurrentUsername())
                            .placeholder(Constant.AVATAR_DEFAULT)
                            .error(Constant.AVATAR_DEFAULT)
                            .into(imgMeHead);
                    if (isOuter) {
                        LogAndToastUtil.ToastOut(getActivity(), "已刷新");
                    }
                } else {
                    String error = response.code() + ": " + response.message();
                    LogAndToastUtil.LogV(error);
                    LogAndToastUtil.ToastOut(getContext(),
                            "啊哦，服务器开小差了，请稍候再试");
                }
            }

            @Override
            public void onFailure(Call<UserDetailBean> call, Throwable t) {
                LogAndToastUtil.LogV("Error" + t.toString());
                LogAndToastUtil.ToastOut(getContext(),
                        "啊哦，服务器开小差了，请稍候再试");
            }
        });
    }

    private void initView(View view) {
        textMeHead = (TextView) view.findViewById(R.id.textMeHead);
        imgMeHead = (ImageView) view.findViewById(R.id.imgMeHead);
        textMeName = (TextView) view.findViewById(R.id.textMeName);
        layoutMeThumb = (RelativeLayout) view.findViewById(R.id.layoutMeThumb);
        contentBtnDivider = (TextView) view.findViewById(R.id.contentBtnDivider);
        textPoster = (TextView) view.findViewById(R.id.textPoster);
        layoutPoster = (RelativeLayout) view.findViewById(R.id.layoutPoster);
        textFollower = (TextView) view.findViewById(R.id.textFollow);
        layoutFollow = (RelativeLayout) view.findViewById(R.id.layoutFollow);
        textFollowing = (TextView) view.findViewById(R.id.textFollowing);
        layoutFollowing = (RelativeLayout) view.findViewById(R.id.layoutFollowing);
        layoutMeBtn = (LinearLayout) view.findViewById(R.id.layoutMeBtn);
        thumbDivider = (TextView) view.findViewById(R.id.thumbDivider);
        imgCollect = (ImageView) view.findViewById(R.id.imgAbout);
        textCollect = (TextView) view.findViewById(R.id.textAbout);
        layoutCollection = (RelativeLayout) view.findViewById(R.id.layoutAbout);
        collectDivider = (TextView) view.findViewById(R.id.collectDivider);
        imgSetting = (ImageView) view.findViewById(R.id.imgSetting);
        textSetting = (TextView) view.findViewById(R.id.textSetting);
        layoutSetting = (RelativeLayout) view.findViewById(R.id.layoutSetting);
        settingDivider = (TextView) view.findViewById(R.id.settingDivider);

        layoutMeThumb.setOnClickListener(this);
        layoutPoster.setOnClickListener(this);
        layoutFollow.setOnClickListener(this);
        layoutFollowing.setOnClickListener(this);
        layoutCollection.setOnClickListener(this);
        layoutSetting.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layoutMeThumb:

                break;
            case R.id.layoutPoster:
                Intent posterIntent = new Intent(getContext(), UserPosterActivity.class);
                posterIntent.putExtra("username", UserData.getCurrentUsername());
                startActivity(posterIntent);
                break;
            case R.id.layoutFollow:
                Intent followIntent = new Intent(getContext(), UserFollowActivity.class);
                followIntent.putExtra("username", UserData.getCurrentUsername());
                startActivity(followIntent);
                break;
            case R.id.layoutFollowing:
                Intent followingIntent = new Intent(getContext(), UserFollowingActivity.class);
                followingIntent.putExtra("username", UserData.getCurrentUsername());
                startActivity(followingIntent);
                break;
            case R.id.layoutAbout:
                Intent aboutIntent = new Intent(getContext(), AboutActivity.class);
                startActivity(aboutIntent);
                break;
            case R.id.layoutSetting:
                Intent settingIntent =  new Intent(getContext(), SettingActivity.class);
                startActivity(settingIntent);
                break;
        }
    }
}