package com.sorashiro.cloudcat.recyclerview.notification;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sorashiro.cloudcat.MyService;
import com.sorashiro.cloudcat.PosterDetailActivity;
import com.sorashiro.cloudcat.R;
import com.sorashiro.cloudcat.UserDetailActivity;
import com.sorashiro.cloudcat.bean.BeAtBean;
import com.sorashiro.cloudcat.bean.PosterBean;
import com.sorashiro.cloudcat.data.Constant;
import com.sorashiro.cloudcat.data.UserData;
import com.sorashiro.cloudcat.recyclerview.RecyclerViewClickListener;
import com.sorashiro.cloudcat.recyclerview.poster.detail.PDRecyclerViewAdapter;
import com.sorashiro.cloudcat.tool.LogAndToastUtil;
import com.squareup.picasso.Picasso;

import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class BeAtRecyclerViewAdapter extends RecyclerView.Adapter<BeAtViewHolder> {

    private Context                                       mContext;
    private List<BeAtBean>                                mList;
    private RecyclerViewClickListener.OnItemClickListener mOnItemClickListener;

    public BeAtRecyclerViewAdapter(Context context, List<BeAtBean> lists) {
        this.mContext = context;
        this.mList = lists;
    }

    public void setOnItemClickListener(RecyclerViewClickListener.OnItemClickListener listener) {//对外提供的一个监听方法
        this.mOnItemClickListener = listener;
    }

    @Override
    public BeAtViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_be_ated, parent, false);//填充这个item布局
        BeAtViewHolder viewHolder = new BeAtViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final BeAtViewHolder holder, int position) {
        final PosterBean[] innerBean = {null};
        final BeAtBean beAtBean = mList.get(position);
        holder.textName.setText(beAtBean.getName());
        holder.textTime.setText(beAtBean.getTime());
        holder.textContent.setText(beAtBean.getText());
        holder.textAuthorName.setText(beAtBean.getAuthor());
        holder.textAuthorPoster.setText(beAtBean.getOri_text());
        // 因为是转发类微博，先得到内部的 bean， 然后再请求外部的 bean
        holder.layoutAuthorContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (innerBean[0] != null) {
                    Intent intent = new Intent(mContext, PosterDetailActivity.class);
                    intent.putExtra("poster_bean", innerBean[0]);
                    intent.putExtra("pdi_type", PDRecyclerViewAdapter.TYPE_FORWARD);
                    mContext.startActivity(intent);
                } else {
                    getInBeanThanStartActivity(innerBean, beAtBean.getPoster(), null);
                }
            }
        });
        // 外部 bean 请求
        holder.layoutHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int poster_id = beAtBean.getF_poster();
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
                            PosterBean outBean = response.body();
                            if (outBean.getResMsg().length() != 0) {
                                String show = outBean.getResMsg();
                                LogAndToastUtil.ToastOut(mContext, show);
                            } else {
                                Intent intent = new Intent(mContext, PosterDetailActivity.class);
                                if (innerBean[0] != null) {
                                    intent.putExtra("poster_bean", outBean);
                                    intent.putExtra("origin_bean", innerBean[0]);
                                    intent.putExtra("pdi_type", PDRecyclerViewAdapter.TYPE_COMMENT);
                                    mContext.startActivity(intent);
                                } else {
                                    getInBeanThanStartActivity(innerBean, beAtBean.getPoster(), outBean);
                                }
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
            }
        });
        holder.imgHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startUserDetailActivity(beAtBean.getName());
            }
        });
        holder.imgAuthorHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startUserDetailActivity(beAtBean.getAuthor());
            }
        });
        Picasso.with(mContext)
                .load(Constant.BASE_STATIC_AVATAR_URL + beAtBean.getName())
                .placeholder(Constant.AVATAR_DEFAULT)
                .error(Constant.AVATAR_DEFAULT)
                .into(holder.imgHead);
        Picasso.with(mContext)
                .load(Constant.BASE_STATIC_AVATAR_URL + beAtBean.getAuthor())
                .placeholder(Constant.AVATAR_DEFAULT)
                .error(Constant.AVATAR_DEFAULT)
                .into(holder.imgAuthorHead);
    }

    private void getInBeanThanStartActivity(final PosterBean[] innerBean, int poster_id, @Nullable final PosterBean outBean) {
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
                    PosterBean inBean = response.body();
                    if (inBean.getResMsg().length() != 0) {
                        String show = inBean.getResMsg();
                        LogAndToastUtil.ToastOut(mContext, show);
                    } else {
                        innerBean[0] = inBean;
                        Intent intent = new Intent(mContext, PosterDetailActivity.class);
                        intent.putExtra("poster_bean", inBean);
                        if (outBean != null) {
                            intent.putExtra("origin_bean", outBean);
                            intent.putExtra("pdi_type", PDRecyclerViewAdapter.TYPE_COMMENT);
                        } else {
                            intent.putExtra("pdi_type", PDRecyclerViewAdapter.TYPE_FORWARD);
                        }
                        mContext.startActivity(intent);
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
    }

    private void startUserDetailActivity(String name) {
        Intent intent = new Intent(mContext, UserDetailActivity.class);
        intent.putExtra("username", name);
        mContext.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public List getList() {
        return mList;
    }

    public void setList(List<BeAtBean> list) {
        mList = list;
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);
    }

}
