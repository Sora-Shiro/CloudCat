package com.sorashiro.cloudcat.recyclerview.find;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sorashiro.cloudcat.R;
import com.sorashiro.cloudcat.UserDetailActivity;
import com.sorashiro.cloudcat.bean.UserBean;
import com.sorashiro.cloudcat.data.Constant;
import com.sorashiro.cloudcat.recyclerview.RecyclerViewClickListener;
import com.squareup.picasso.Picasso;

import java.util.List;


public class UserFindAdapter extends RecyclerView.Adapter<UserFindViewHolder> {

    private Context                                       mContext;
    private List<UserBean>                                mList;
    private RecyclerViewClickListener.OnItemClickListener mOnItemClickListener;

    public UserFindAdapter(Context context, List<UserBean> lists) {
        this.mContext = context;
        this.mList = lists;
    }


    @Override
    public UserFindViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_find, parent, false);//填充这个item布局
        UserFindViewHolder viewHolder = new UserFindViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final UserFindViewHolder holder, int position) {
        UserBean userBean = mList.get(position);
        holder.textName.setText(userBean.getUsername());
        holder.layoutHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, UserDetailActivity.class);
                intent.putExtra("username", holder.textName.getText().toString());
                mContext.startActivity(intent);
            }
        });
        Picasso.with(mContext)
                .load(Constant.BASE_STATIC_AVATAR_URL + userBean.getUsername())
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

    public void setList(List<UserBean> list) {
        mList = list;
        notifyItemRangeChanged(0, mList.size());
    }
}
