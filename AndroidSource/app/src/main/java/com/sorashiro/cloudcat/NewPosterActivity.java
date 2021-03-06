package com.sorashiro.cloudcat;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sorashiro.cloudcat.bean.ResMsgBean;
import com.sorashiro.cloudcat.data.Constant;
import com.sorashiro.cloudcat.data.UserData;
import com.sorashiro.cloudcat.picasso.setting.ScaleWithViewWidthT;
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


public class NewPosterActivity extends AppCompatActivity implements View.OnClickListener {

    private Button         btnBack;
    private TextView       textDetailHead;
    private Button         btnSend;
    private RelativeLayout layoutPdiHead;
    private TextView       pdiHeadDivider;
    private EditText       editSend;
    private ImageView      imgNew;

    private ImageInfo mImageInfo;
    private AlbumFile mAlbumFile;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_poster);
        initView();
    }

    private void initView() {
        btnBack = (Button) findViewById(R.id.btnBack);
        textDetailHead = (TextView) findViewById(R.id.textDetailHead);
        btnSend = (Button) findViewById(R.id.btnSend);
        layoutPdiHead = (RelativeLayout) findViewById(R.id.layoutPdiHead);
        pdiHeadDivider = (TextView) findViewById(R.id.pdiHeadDivider);
        editSend = (EditText) findViewById(R.id.editSend);
        imgNew = (ImageView) findViewById(R.id.imgNew);

        btnBack.setOnClickListener(this);
        btnSend.setOnClickListener(this);
        imgNew.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnBack:
                finish();
                break;
            case R.id.btnSend:
                btnSend.setClickable(false);
                sendNewPoster();
                break;
            case R.id.imgNew:
                callAlbum();
                break;
        }
    }

    private void sendNewPoster() {
        if(mImageInfo == null) {
            LogAndToastUtil.ToastOut(this, "请选择图片");
            return;
        }
        File file = new File(mAlbumFile.getPath());
        int index = mAlbumFile.getName().lastIndexOf('.');
        String suffix = mAlbumFile.getName().substring(index+1, mAlbumFile.getName().length());
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
                LogAndToastUtil.ToastOut(this, "不支持的图片格式");
                return;
        }
        LogAndToastUtil.ToastOut(this, "发布中");
        Retrofit retrofit2 = new Retrofit.Builder()
                .baseUrl(Constant.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(new OkHttpClient())
                .build();
        MyService service = retrofit2.create(MyService.class);
        File compressFile;
        try {
            compressFile = new Compressor(this).compressToFile(file);
        } catch (IOException e) {
            LogAndToastUtil.ToastOut(this, "图片压缩失败，即将上传原图");
            compressFile = file;
        }
        RequestBody photoRequestBody = RequestBody.create(MediaType.parse(parseString), compressFile);
        MultipartBody.Part photo = MultipartBody.Part.createFormData(
                "photo", file.getName(), photoRequestBody);
        RequestBody usernameBody = RequestBody.create(MediaType.parse("text/plain"), UserData.getCurrentUsername());
        RequestBody passwordBody = RequestBody.create(MediaType.parse("text/plain"), UserData.getCurrentPassword());
        RequestBody textBody = RequestBody.create(MediaType.parse("text/plain"), editSend.getText().toString());
        RequestBody p_TypeBody = RequestBody.create(MediaType.parse("text/plain"), "1");
        Call<ResMsgBean> call = service.createPosterWithPhoto(
                usernameBody, passwordBody, textBody, p_TypeBody, photo
        );
        call.enqueue(new retrofit2.Callback<ResMsgBean>() {
            @Override
            public void onResponse(Call<ResMsgBean> call, Response<ResMsgBean> response) {
                if (response.isSuccessful()) {
                    ResMsgBean bean = response.body();
                    String resultStr = bean.getResMsg();
                    LogAndToastUtil.ToastOut(NewPosterActivity.this, resultStr);
                    if (resultStr.equals("发布成功")) {
                        finish();
                    }
                } else {
                    String error = response.code() + ": " + response.message();
                    LogAndToastUtil.LogV(error);
                    LogAndToastUtil.ToastOut(NewPosterActivity.this,
                            "啊哦，服务器开小差了，请稍候再试");
                    btnSend.setClickable(true);
                }
            }

            @Override
            public void onFailure(Call<ResMsgBean> call, Throwable t) {
                LogAndToastUtil.LogV("Error" + t.toString());
                LogAndToastUtil.ToastOut(NewPosterActivity.this,
                        "啊哦，服务器出问题了，请稍候再试");
                btnSend.setClickable(true);
            }
        });
    }

    private void callAlbum() {
        Album.image(this)
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
                        for (AlbumFile albumFile : result) {
                            if(albumFile.getSize() > MAX_PHOTO_SIZE) {
                                LogAndToastUtil.ToastOut(NewPosterActivity.this, "图片过大，请选择小于10M的图片");
                                return;
                            }
                            mImageInfo = new ImageInfo();
                            mImageInfo.setPath("file://" + albumFile.getPath());
                            mImageInfo.setThumbPath("file://" + albumFile.getThumbPath());
                            mAlbumFile = albumFile;
                        }
                        Picasso.with(NewPosterActivity.this)
                                .load(mImageInfo.getThumbPath())
                                .config(Bitmap.Config.RGB_565)
                                .transform(new ScaleWithViewWidthT(imgNew))
                                .into(imgNew, new com.squareup.picasso.Callback() {
                                    @Override
                                    public void onSuccess() {

                                    }

                                    @Override
                                    public void onError() {

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

}
