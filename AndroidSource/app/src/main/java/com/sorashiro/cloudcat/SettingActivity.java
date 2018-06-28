package com.sorashiro.cloudcat;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.sorashiro.cloudcat.application.MyApplication;
import com.sorashiro.cloudcat.bean.ResMsgBean;
import com.sorashiro.cloudcat.data.Constant;
import com.sorashiro.cloudcat.data.UserData;
import com.sorashiro.cloudcat.tool.LogAndToastUtil;
import com.sorashiro.cloudcat.viewhelper.ImageInfo;
import com.squareup.picasso.Picasso;
import com.yanzhenjie.album.Action;
import com.yanzhenjie.album.Album;
import com.yanzhenjie.album.AlbumFile;
import com.yanzhenjie.album.Filter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import id.zelory.compressor.Compressor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.sorashiro.cloudcat.data.Constant.MAX_PHOTO_SIZE;


public class SettingActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView textHomeHead;
    private Button btnBack;
    private Button   btnSignOut;
    private Button   btnAvatarSetting;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initView();
    }

    private void initView() {
        textHomeHead = (TextView) findViewById(R.id.textHomeHead);
        btnSignOut = (Button) findViewById(R.id.btnSignOut);
        btnAvatarSetting = (Button) findViewById(R.id.btnAvatarSetting);
        btnBack = (Button) findViewById(R.id.btnBack);

        btnSignOut.setOnClickListener(SettingActivity.this);
        btnAvatarSetting.setOnClickListener(SettingActivity.this);
        btnBack.setOnClickListener(SettingActivity.this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSignOut:
                signOut();
                break;
            case R.id.btnAvatarSetting:
                callAlbum();
                break;
            case R.id.btnBack:
                finish();
                break;
        }
    }

    private void callAlbum() {
        Album.image(SettingActivity.this)
                .multipleChoice()
                .requestCode(200)
                .camera(true)
                .columnCount(3)
                .selectCount(1)
                .checkedList(null)
                .filterSize(new Filter<Long>() {
                    @Override
                    public boolean filter(Long attributes) {
                        return attributes > MAX_PHOTO_SIZE;
                    }
                })
                .filterMimeType(null)
                .afterFilterVisibility(false)
                .onResult(new Action<ArrayList<AlbumFile>>() {
                    @Override
                    public void onAction(int requestCode, @NonNull ArrayList<AlbumFile> result) {
                        ImageInfo imageInfo;
                        AlbumFile mAlbumFile = null;
                        for (AlbumFile albumFile : result) {
                            if (albumFile.getSize() > MAX_PHOTO_SIZE) {
                                LogAndToastUtil.ToastOut(SettingActivity.this, "图片过大，请选择小于10M的图片");
                                return;
                            }
                            imageInfo = new ImageInfo();
                            imageInfo.setPath("file://" + albumFile.getPath());
                            imageInfo.setThumbPath("file://" + albumFile.getThumbPath());
                            mAlbumFile = albumFile;
                        }
                        if (mAlbumFile == null) {
                            LogAndToastUtil.ToastOut(SettingActivity.this, "未选择图片");
                            return;
                        }
                        File file = new File(mAlbumFile.getPath());
                        int index = mAlbumFile.getName().lastIndexOf('.');
                        String suffix = mAlbumFile.getName().substring(index + 1, mAlbumFile.getName().length());
                        String parseString = "";
                        switch (suffix) {
                            case "jpg":
                            case "jpeg":
                                parseString += "image/jpeg";
                                break;
                            case "png":
                                parseString += "image/png";
                                break;
                            default:
                                LogAndToastUtil.ToastOut(SettingActivity.this, "不支持的图片格式");
                                return;
                        }
                        LogAndToastUtil.ToastOut(SettingActivity.this, "上传中");
                        Retrofit retrofit2 = new Retrofit.Builder()
                                .baseUrl(Constant.BASE_URL)
                                .addConverterFactory(GsonConverterFactory.create())
                                .client(new OkHttpClient())
                                .build();
                        MyService service = retrofit2.create(MyService.class);
                        File compressFile;
                        try {
                            compressFile = new Compressor(SettingActivity.this).compressToFile(file);
                        } catch (IOException e) {
                            LogAndToastUtil.ToastOut(SettingActivity.this, "图片压缩失败，即将上传原图");
                            compressFile = file;
                        }
                        RequestBody photoRequestBody = RequestBody.create(MediaType.parse(parseString), compressFile);
                        MultipartBody.Part photo = MultipartBody.Part.createFormData(
                                "photo", file.getName(), photoRequestBody);
                        RequestBody usernameBody = RequestBody.create(MediaType.parse("text/plain"), UserData.getCurrentUsername());
                        RequestBody passwordBody = RequestBody.create(MediaType.parse("text/plain"), UserData.getCurrentPassword());
                        Call<ResMsgBean> call = service.updateUserAvatar(
                                usernameBody, passwordBody, photo
                        );
                        call.enqueue(new retrofit2.Callback<ResMsgBean>() {
                            @Override
                            public void onResponse(Call<ResMsgBean> call, Response<ResMsgBean> response) {
                                if (response.isSuccessful()) {
                                    ResMsgBean bean = response.body();
                                    String resultStr = bean.getResMsg();
                                    LogAndToastUtil.ToastOut(SettingActivity.this, resultStr);
                                    if (resultStr.equals("上传成功")) {
                                        Picasso.with(MyApplication.getInstance()).invalidate(
                                                Constant.BASE_STATIC_AVATAR_URL + UserData.getCurrentUsername());
                                        finish();
                                    }
                                } else {
                                    String error = response.code() + ": " + response.message();
                                    LogAndToastUtil.LogV(error);
                                    LogAndToastUtil.ToastOut(SettingActivity.this,
                                            "啊哦，服务器开小差了，请稍候再试");
                                }
                            }

                            @Override
                            public void onFailure(Call<ResMsgBean> call, Throwable t) {
                                LogAndToastUtil.LogV("Error" + t.toString());
                                LogAndToastUtil.ToastOut(SettingActivity.this,
                                        "啊哦，服务器出问题了，请稍候再试");
                            }
                        });
                    }
                })
                .onCancel(new Action<String>() {
                    @Override
                    public void onAction(int requestCode, @NonNull String result) {

                    }
                })
                .start();
    }

    private void signOut() {
        UserData.setCurrentUserName("");
        UserData.setCurrentPassword("");
        Intent intent = new Intent(SettingActivity.this, SignInActivity.class);
        startActivity(intent);
        finish();
    }
}
