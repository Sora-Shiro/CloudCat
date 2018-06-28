package com.sorashiro.cloudcat.recyclerview.home;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sorashiro.cloudcat.MyService;
import com.sorashiro.cloudcat.PdiActivity;
import com.sorashiro.cloudcat.PhotoActivity;
import com.sorashiro.cloudcat.PosterDetailActivity;
import com.sorashiro.cloudcat.R;
import com.sorashiro.cloudcat.UserDetailActivity;
import com.sorashiro.cloudcat.bean.PosterBean;
import com.sorashiro.cloudcat.bean.ResMsgBean;
import com.sorashiro.cloudcat.data.Constant;
import com.sorashiro.cloudcat.data.UserData;
import com.sorashiro.cloudcat.recyclerview.RecyclerViewClickListener;
import com.sorashiro.cloudcat.recyclerview.poster.detail.PDRecyclerViewAdapter;
import com.sorashiro.cloudcat.tool.LogAndToastUtil;
import com.sorashiro.cloudcat.viewhelper.ImageInfo;
import com.squareup.picasso.Picasso;

import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeRecyclerViewAdapter extends RecyclerView.Adapter<HomeBaseVH> {

    private Context                                       mContext;
    private List<PosterBean>                              mList;
    private RecyclerViewClickListener.OnItemClickListener mOnItemClickListener;

    public HomeRecyclerViewAdapter(Context context, List<PosterBean> lists) {
        this.mContext = context;
        this.mList = lists;
    }

    public void setOnItemClickListener(RecyclerViewClickListener.OnItemClickListener listener) {//对外提供的一个监听方法
        this.mOnItemClickListener = listener;
    }

    @Override
    public HomeBaseVH onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case PosterBean.TYPE_PHOTO:
                View photoView = LayoutInflater.from(mContext).inflate(R.layout.item_poster, parent, false);//填充这个item布局
                HomeViewHolder photoVH = new HomeViewHolder(photoView);
                return photoVH;
            case PosterBean.TYPE_VIDEO:
                return null;
            case PosterBean.TYPE_FORWARD:
                View forwardView = LayoutInflater.from(mContext).inflate(R.layout.item_poster_forward, parent, false);//填充这个item布局
                HomeForwardViewHolder forwardVH = new HomeForwardViewHolder(forwardView);
                return forwardVH;
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final HomeBaseVH holder, int position) {
        final PosterBean posterBean = mList.get(position);
        int p_type = posterBean.getP_type();
        String name = posterBean.getName();
        String time = posterBean.getTime();
        String text = posterBean.getText();
        int f_num = posterBean.getForward_num();
        int c_num = posterBean.getComment_num();
        final boolean[] if_like = {posterBean.isIf_like()};
        switch (p_type) {
            case PosterBean.TYPE_PHOTO:
                final HomeViewHolder homeViewHolder = (HomeViewHolder) holder;
                homeViewHolder.textName.setText(name);
                homeViewHolder.textTime.setText(time);
                homeViewHolder.textContent.setText(text);
                homeViewHolder.btnForward.setText("转发 " + f_num);
                homeViewHolder.btnComment.setText("评论 " + c_num);
                homeViewHolder.btnLike.setText("赞");
                homeViewHolder.btnLike.setTextColor(
                        if_like[0] ?
                                mContext.getResources().getColor(R.color.colorPrimary) :
                                mContext.getResources().getColor(R.color.colorSecondaryText));

                homeViewHolder.layoutHome.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, PosterDetailActivity.class);
                        intent.putExtra("poster_bean", posterBean);
                        mContext.startActivity(intent);
                    }
                });
                homeViewHolder.btnForward.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent fI = new Intent(mContext, PdiActivity.class);
                        fI.putExtra("pdi_type", PdiActivity.PDI_FORWARD);
                        fI.putExtra("poster_id", posterBean.getId());
                        mContext.startActivity(fI);
                    }
                });
                homeViewHolder.btnComment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent fI = new Intent(mContext, PdiActivity.class);
                        fI.putExtra("pdi_type", PdiActivity.PDI_COMMENT);
                        fI.putExtra("poster_id", posterBean.getId());
                        mContext.startActivity(fI);
                    }
                });
                homeViewHolder.btnLike.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Retrofit retrofit2 = new Retrofit.Builder()
                                .baseUrl(Constant.BASE_URL)
                                .addConverterFactory(GsonConverterFactory.create())
                                .client(new OkHttpClient())
                                .build();
                        MyService service = retrofit2.create(MyService.class);
                        Call<ResMsgBean> call = service.sendLike(
                                UserData.getCurrentUsername(),
                                UserData.getCurrentPassword(),
                                posterBean.getId()
                        );
                        call.enqueue(new Callback<ResMsgBean>() {
                            @Override
                            public void onResponse(Call<ResMsgBean> call, Response<ResMsgBean> response) {
                                if (response.isSuccessful()) {
                                    if_like[0] = !if_like[0];
                                    posterBean.setIf_like(if_like[0]);
                                    homeViewHolder.btnLike.setTextColor(
                                            if_like[0] ?
                                                    mContext.getResources().getColor(R.color.colorPrimary) :
                                                    mContext.getResources().getColor(R.color.colorSecondaryText));
                                    LogAndToastUtil.ToastOut(
                                            mContext, response.body().getResMsg());
                                } else {
                                    String error = response.code() + ": " + response.message();
                                    LogAndToastUtil.LogV(error);
                                    LogAndToastUtil.ToastOut(mContext,
                                            "啊哦，服务器开小差了，请稍候再试");
                                }
                            }

                            @Override
                            public void onFailure(Call<ResMsgBean> call, Throwable t) {
                                LogAndToastUtil.LogV("Error" + t.toString());
                                LogAndToastUtil.ToastOut(mContext,
                                        "啊哦，服务器没有返回数据，请稍候再试");
                            }
                        });
                    }
                });
                homeViewHolder.imgHead.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startUserDetailActivity(posterBean.getName());
                    }
                });
                Picasso.with(mContext)
                        .load(Constant.BASE_STATIC_PHOTO_URL + posterBean.getPhoto_id())
                        .config(Bitmap.Config.RGB_565)
                        .into(homeViewHolder.imgContent, new com.squareup.picasso.Callback() {
                            @Override
                            public void onSuccess() {
                                homeViewHolder.imgContent.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(mContext, PhotoActivity.class);
                                        ImageInfo imageInfo = new ImageInfo();
                                        imageInfo.setPath(Constant.BASE_STATIC_PHOTO_URL + posterBean.getPhoto_id());
                                        intent.putExtra("image_info", imageInfo);
                                        mContext.startActivity(intent);
                                    }
                                });
                            }

                            @Override
                            public void onError() {

                            }
                        });
                Picasso.with(mContext)
                        .load(Constant.BASE_STATIC_AVATAR_URL + posterBean.getName())
                        .placeholder(Constant.AVATAR_DEFAULT)
                        .error(Constant.AVATAR_DEFAULT)
                        .into(homeViewHolder.imgHead);
                break;
            case PosterBean.TYPE_VIDEO:
                break;
            case PosterBean.TYPE_FORWARD:
                final HomeForwardViewHolder homeFVH = (HomeForwardViewHolder) holder;
                int poster_id = posterBean.getOrigin_poster_id();
                Retrofit retrofit2 = new Retrofit.Builder()
                        .baseUrl(Constant.BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .client(new OkHttpClient())
                        .build();
                MyService service = retrofit2.create(MyService.class);
                Call<PosterBean> call = service.getPosterById(
                        UserData.getCurrentUsername(),
                        poster_id
                );
                call.enqueue(new Callback<PosterBean>() {
                    @Override
                    public void onResponse(Call<PosterBean> call, Response<PosterBean> response) {
                        if (response.isSuccessful()) {
                            final PosterBean bean = response.body();
                            if (bean.getResMsg().length() != 0) {
                                String show = bean.getResMsg();
                                LogAndToastUtil.ToastOut(mContext, show);
                            } else {
                                homeFVH.textAuthorContent.setText(bean.getText());
                                homeFVH.textAuthorName.setText(bean.getName() + " ：");
                                homeFVH.textAuthorName.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        startUserDetailActivity(bean.getName());
                                    }
                                });
                                homeFVH.layoutAuthor.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(mContext, PosterDetailActivity.class);
                                        intent.putExtra("poster_bean", bean);
                                        intent.putExtra("pdi_type", PDRecyclerViewAdapter.TYPE_COMMENT);
                                        mContext.startActivity(intent);
                                    }
                                });
                                homeFVH.layoutHome.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(mContext, PosterDetailActivity.class);
                                        intent.putExtra("poster_bean", posterBean);
                                        intent.putExtra("origin_bean", bean);
                                        intent.putExtra("pdi_type", PDRecyclerViewAdapter.TYPE_COMMENT);
                                        mContext.startActivity(intent);
                                    }
                                });
                            }
                        } else {
                            String error = response.code() + ": " + response.message();
                            LogAndToastUtil.LogV(error);
                            LogAndToastUtil.ToastOut(mContext,
                                    "啊哦，服务器开小差了，请稍候再试");
                        }
                    }

                    @Override
                    public void onFailure(Call<PosterBean> call, Throwable t) {
                        LogAndToastUtil.LogV("Error" + t.toString());
                        LogAndToastUtil.ToastOut(mContext,
                                "啊哦，服务器开小差了，请稍候再试");
                    }
                });
                homeFVH.textName.setText(name);
                homeFVH.textTime.setText(time);
                homeFVH.textContent.setText(text);
                homeFVH.btnForward.setText("转发 " + f_num);
                homeFVH.btnComment.setText("评论 " + c_num);
                homeFVH.btnLike.setText("赞");
                homeFVH.btnLike.setTextColor(
                        if_like[0] ?
                                mContext.getResources().getColor(R.color.colorPrimary) :
                                mContext.getResources().getColor(R.color.colorSecondaryText));

                homeFVH.btnForward.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent fI = new Intent(mContext, PdiActivity.class);
                        fI.putExtra("pdi_type", PdiActivity.PDI_FORWARD);
                        fI.putExtra("poster_id", posterBean.getOrigin_poster_id());
                        mContext.startActivity(fI);
                    }
                });
                homeFVH.btnComment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent fI = new Intent(mContext, PdiActivity.class);
                        fI.putExtra("pdi_type", PdiActivity.PDI_COMMENT);
                        fI.putExtra("poster_id", posterBean.getId());
                        mContext.startActivity(fI);
                    }
                });
                homeFVH.btnLike.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Retrofit retrofit2 = new Retrofit.Builder()
                                .baseUrl(Constant.BASE_URL)
                                .addConverterFactory(GsonConverterFactory.create())
                                .client(new OkHttpClient())
                                .build();
                        MyService service = retrofit2.create(MyService.class);
                        Call<ResMsgBean> call = service.sendLike(
                                UserData.getCurrentUsername(),
                                UserData.getCurrentPassword(),
                                posterBean.getId()
                        );
                        call.enqueue(new Callback<ResMsgBean>() {
                            @Override
                            public void onResponse(Call<ResMsgBean> call, Response<ResMsgBean> response) {
                                if (response.isSuccessful()) {
                                    if_like[0] = !if_like[0];
                                    posterBean.setIf_like(if_like[0]);
                                    homeFVH.btnLike.setTextColor(
                                            if_like[0] ?
                                                    mContext.getResources().getColor(R.color.colorPrimary) :
                                                    mContext.getResources().getColor(R.color.colorSecondaryText));
                                    LogAndToastUtil.ToastOut(
                                            mContext, response.body().getResMsg());
                                } else {
                                    String error = response.code() + ": " + response.message();
                                    LogAndToastUtil.LogV(error);
                                    LogAndToastUtil.ToastOut(mContext,
                                            "啊哦，服务器开小差了，请稍候再试");
                                }
                            }

                            @Override
                            public void onFailure(Call<ResMsgBean> call, Throwable t) {
                                LogAndToastUtil.LogV("Error" + t.toString());
                                LogAndToastUtil.ToastOut(mContext,
                                        "啊哦，服务器没有返回数据，请稍候再试");
                            }
                        });
                    }
                });
                homeFVH.imgHead.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startUserDetailActivity(posterBean.getName());
                    }
                });
                Picasso.with(mContext)
                        .load(Constant.BASE_STATIC_PHOTO_URL + posterBean.getPhoto_id())
                        .config(Bitmap.Config.RGB_565)
                        .into(homeFVH.imgAuthorContent, new com.squareup.picasso.Callback() {
                            @Override
                            public void onSuccess() {
                                homeFVH.imgAuthorContent.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(mContext, PhotoActivity.class);
                                        ImageInfo imageInfo = new ImageInfo();
                                        imageInfo.setPath(Constant.BASE_STATIC_PHOTO_URL + posterBean.getPhoto_id());
                                        intent.putExtra("image_info", imageInfo);
                                        mContext.startActivity(intent);
                                    }
                                });
                            }

                            @Override
                            public void onError() {

                            }
                        });
                Picasso.with(mContext)
                        .load(Constant.BASE_STATIC_AVATAR_URL + posterBean.getName())
                        .placeholder(Constant.AVATAR_DEFAULT)
                        .error(Constant.AVATAR_DEFAULT)
                        .into(homeFVH.imgHead);
                break;

        }

    }

    private void startUserDetailActivity(String name) {
        Intent intent = new Intent(mContext, UserDetailActivity.class);
        intent.putExtra("username", name);
        mContext.startActivity(intent);
    }

    @Override
    public int getItemViewType(int position) {
        return mList.get(position).getP_type();
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public List getList() {
        return mList;
    }

    public void setList(List<PosterBean> list) {
        mList = list;
        notifyItemRangeChanged(0, mList.size());
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);
    }

}
