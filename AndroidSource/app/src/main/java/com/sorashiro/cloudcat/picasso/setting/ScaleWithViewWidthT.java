package com.sorashiro.cloudcat.picasso.setting;


import android.graphics.Bitmap;
import android.widget.ImageView;

import com.sorashiro.cloudcat.tool.LogAndToastUtil;
import com.squareup.picasso.Transformation;

public class ScaleWithViewWidthT implements Transformation{

    private ImageView mImageView;

    public ScaleWithViewWidthT(ImageView view) {
        mImageView = view;
    }

    @Override
    public Bitmap transform(Bitmap source) {

        int targetWidth = mImageView.getWidth();
        LogAndToastUtil.LogV("source.getHeight()="+source.getHeight()+",source.getWidth()="+source.getWidth()+",targetWidth="+targetWidth);

        if(source.getWidth()==0){
            return source;
        }

        //如果图片小于设置的宽度，则返回原图
        if(source.getWidth()<targetWidth){
            return source;
        }else{
            //如果图片大小大于等于设置的宽度，则按照设置的宽度比例来缩放
            double aspectRatio = (double) source.getHeight() / (double) source.getWidth();
            int targetHeight = (int) (targetWidth * aspectRatio);
            if (targetHeight != 0 && targetWidth != 0) {
                Bitmap result = Bitmap.createScaledBitmap(source, targetWidth, targetHeight, false);
                if (result != source) {
                    // Same bitmap is returned if sizes are the same
                    source.recycle();
                }
                return result;
            } else {
                return source;
            }
        }

    }

    @Override
    public String key() {
        return "transformation" + " desiredWidth";
    }

}
