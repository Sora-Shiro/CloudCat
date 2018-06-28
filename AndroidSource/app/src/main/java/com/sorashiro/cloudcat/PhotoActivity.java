package com.sorashiro.cloudcat;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.chrisbanes.photoview.PhotoView;
import com.sorashiro.cloudcat.tool.FileUtil;
import com.sorashiro.cloudcat.tool.LogAndToastUtil;
import com.sorashiro.cloudcat.viewhelper.ImageInfo;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;


public class PhotoActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageInfo mImageInfo;

    private Button         btnBack;
    private Button         btnSave;
    private TextView       textPhotoHead;
    private RelativeLayout layoutPhotoHead;
    private TextView       photoHeadDivider;
    private PhotoView      imgPhoto;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        initView();

        mImageInfo = getIntent().getParcelableExtra("image_info");

        Picasso.with(PhotoActivity.this)
                .load(mImageInfo.getPath())
                .config(Bitmap.Config.RGB_565)
                .into(imgPhoto);
    }

    private void initView() {
        btnBack = (Button) findViewById(R.id.btnBack);
        btnSave = (Button) findViewById(R.id.btnSave);
        textPhotoHead = (TextView) findViewById(R.id.textPhotoHead);
        layoutPhotoHead = (RelativeLayout) findViewById(R.id.layoutPhotoHead);
        photoHeadDivider = (TextView) findViewById(R.id.photoHeadDivider);
        imgPhoto = (PhotoView) findViewById(R.id.imgPhoto);

        btnBack.setOnClickListener(this);
        imgPhoto.setOnClickListener(this);
        btnSave.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnBack:
                finish();
                break;
            case R.id.imgPhoto:
                finish();
                break;
            case R.id.btnSave:
                download(mImageInfo.getPath());
                break;
        }
    }

    private void download(String url) {

        Target target = new Target() {

            @Override
            public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                LogAndToastUtil.ToastOut(PhotoActivity.this, "图片较大时存储较久，请耐心等待");

                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        String imageName = System.currentTimeMillis() + ".png";

                        File dcimFile = FileUtil
                                .getDCIMFile(FileUtil.PATH_PHOTOGRAPH, imageName);

                        FileOutputStream ostream = null;
                        try {
                            ostream = new FileOutputStream(dcimFile);
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, ostream);
                            ostream.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        LogAndToastUtil.LogV(dcimFile.toString());


                        LogAndToastUtil.ToastOut(PhotoActivity.this, "存储成功！图片下载至:" + imageName);
                    }
                };

                Thread thread = new Thread(runnable);
                thread.start();


            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };

        Picasso.with(this).load(url).into(target);

    }
}
