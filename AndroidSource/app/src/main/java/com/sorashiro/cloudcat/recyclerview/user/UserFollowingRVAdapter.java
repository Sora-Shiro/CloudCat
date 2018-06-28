package com.sorashiro.cloudcat.recyclerview.user;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sorashiro.cloudcat.MyService;
import com.sorashiro.cloudcat.R;
import com.sorashiro.cloudcat.UserDetailActivity;
import com.sorashiro.cloudcat.bean.FollowBean;
import com.sorashiro.cloudcat.bean.ResMsgBean;
import com.sorashiro.cloudcat.data.Constant;
import com.sorashiro.cloudcat.data.UserData;
import com.sorashiro.cloudcat.recyclerview.RecyclerViewClickListener;
import com.sorashiro.cloudcat.tool.LogAndToastUtil;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.sorashiro.cloudcat.data.Constant.BASE_URL;

public class UserFollowingRVAdapter extends RecyclerView.Adapter<UserFollowingVH> {

    private Context                                       mContext;
    private ArrayList<FollowBean>                         mList;
    private RecyclerViewClickListener.OnItemClickListener mOnItemClickListener;

    public UserFollowingRVAdapter(Context context, ArrayList<FollowBean> lists) {
        this.mContext = context;
        this.mList = lists;
    }

    public void setOnItemClickListener(RecyclerViewClickListener.OnItemClickListener listener) {//对外提供的一个监听方法
        this.mOnItemClickListener = listener;
    }

    @Override
    public UserFollowingVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_following, parent, false);//填充这个item布局
        UserFollowingVH UserFollowingVH = new UserFollowingVH(view);
        return UserFollowingVH;
    }

    @Override
    public void onBindViewHolder(final UserFollowingVH holder, final int position) {
        final FollowBean followBean = mList.get(position);
        holder.textName.setText(followBean.getU2());
        if (followBean.isIf_mutual()) {
            holder.btnChangeFollow.setText("相互关注");
        } else {
            holder.btnChangeFollow.setText("关注");
        }
        holder.btnChangeFollow.setOnClickListener(new View.OnClickListener() {
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
                        holder.textName.getText().toString(),
                        1
                );
                call.enqueue(new Callback<ResMsgBean>() {
                    @Override
                    public void onResponse(Call<ResMsgBean> call, Response<ResMsgBean> response) {
                        if (response.isSuccessful()) {
                            LogAndToastUtil.ToastOut(
                                    mContext, response.body().getResMsg());
                            boolean reverse = !followBean.isIf_mutual();
                            followBean.setIf_mutual(reverse);
                            if (reverse) {
                                holder.btnChangeFollow.setText("互相关注");
                            } else {
                                holder.btnChangeFollow.setText("关注");
                            }
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
        holder.layoutHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = holder.textName.getText().toString();
                Intent intent = new Intent(mContext, UserDetailActivity.class);
                intent.putExtra("username", name);
                mContext.startActivity(intent);
            }
        });
        Picasso.with(mContext)
                .load(Constant.BASE_STATIC_AVATAR_URL + followBean.getU2())
                .placeholder(Constant.AVATAR_DEFAULT)
                .error(Constant.AVATAR_DEFAULT)
                .into(holder.imgHead);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public List getList() {
        return mList;
    }

    public void setList(ArrayList<FollowBean> list) {
        mList = list;
        notifyItemRangeChanged(0, mList.size());
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);
    }

}
