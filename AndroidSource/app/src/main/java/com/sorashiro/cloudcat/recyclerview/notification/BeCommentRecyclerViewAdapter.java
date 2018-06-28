package com.sorashiro.cloudcat.recyclerview.notification;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sorashiro.cloudcat.MyService;
import com.sorashiro.cloudcat.PosterDetailActivity;
import com.sorashiro.cloudcat.R;
import com.sorashiro.cloudcat.UserDetailActivity;
import com.sorashiro.cloudcat.bean.BeCommentBean;
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


public class BeCommentRecyclerViewAdapter extends RecyclerView.Adapter<BeCommentViewHolder> {

    private Context                                       mContext;
    private List<BeCommentBean>                           mList;
    private RecyclerViewClickListener.OnItemClickListener mOnItemClickListener;

    public BeCommentRecyclerViewAdapter(Context context, List<BeCommentBean> lists) {
        this.mContext = context;
        this.mList = lists;
    }

    public void setOnItemClickListener(RecyclerViewClickListener.OnItemClickListener listener) {//对外提供的一个监听方法
        this.mOnItemClickListener = listener;
    }

    @Override
    public BeCommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_be_comment, parent, false);//填充这个item布局
        BeCommentViewHolder viewHolder = new BeCommentViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final BeCommentViewHolder holder, int position) {
        final PosterBean[] innerBean = {null};
        final BeCommentBean beCommentBean = mList.get(position);
        holder.textName.setText(beCommentBean.getName());
        holder.textTime.setText(beCommentBean.getTime());
        holder.textContent.setText(beCommentBean.getText());
        holder.textAuthorName.setText(beCommentBean.getAuthor());
        holder.textAuthorPoster.setText(beCommentBean.getOri_text());
        holder.layoutAuthorContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (innerBean[0] != null) {
                    Intent intent = new Intent(mContext, PosterDetailActivity.class);
                    intent.putExtra("poster_bean", innerBean[0]);
                    intent.putExtra("pdi_type", PDRecyclerViewAdapter.TYPE_COMMENT);
                    mContext.startActivity(intent);
                } else {
                    getInBeanThanStartActivity(innerBean, beCommentBean.getPoster());
                }
            }
        });
        holder.layoutHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (innerBean[0] != null) {
                    Intent intent = new Intent(mContext, PosterDetailActivity.class);
                    intent.putExtra("poster_bean", innerBean[0]);
                    intent.putExtra("pdi_type", PDRecyclerViewAdapter.TYPE_COMMENT);
                    mContext.startActivity(intent);
                } else {
                    getInBeanThanStartActivity(innerBean, beCommentBean.getPoster());
                }
            }
        });
        holder.imgHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startUserDetailActivity(beCommentBean.getName());
            }
        });
        holder.imgAuthorHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startUserDetailActivity(beCommentBean.getAuthor());
            }
        });
        Picasso.with(mContext)
                .load(Constant.BASE_STATIC_AVATAR_URL + beCommentBean.getName())
                .placeholder(Constant.AVATAR_DEFAULT)
                .error(Constant.AVATAR_DEFAULT)
                .into(holder.imgHead);
        Picasso.with(mContext)
                .load(Constant.BASE_STATIC_AVATAR_URL + beCommentBean.getAuthor())
                .placeholder(Constant.AVATAR_DEFAULT)
                .error(Constant.AVATAR_DEFAULT)
                .into(holder.imgAuthorHead);
    }

    private void getInBeanThanStartActivity(final PosterBean[] innerBean, int poster_id) {
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
                        intent.putExtra("pdi_type", PDRecyclerViewAdapter.TYPE_COMMENT);
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

    public void setList(List<BeCommentBean> list) {
        mList = list;
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);
    }

}
