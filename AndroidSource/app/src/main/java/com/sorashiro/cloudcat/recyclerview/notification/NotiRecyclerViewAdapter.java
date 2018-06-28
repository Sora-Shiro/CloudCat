package com.sorashiro.cloudcat.recyclerview.notification;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sorashiro.cloudcat.BeAtActivity;
import com.sorashiro.cloudcat.BeCommentActivity;
import com.sorashiro.cloudcat.BeLikeActivity;
import com.sorashiro.cloudcat.R;
import com.sorashiro.cloudcat.recyclerview.RecyclerViewClickListener;

import java.util.List;


public class NotiRecyclerViewAdapter extends RecyclerView.Adapter<NotiViewHolder> {

    private Context                                       mContext;
    private List<String>                                  mList;
    private RecyclerViewClickListener.OnItemClickListener mOnItemClickListener;

    public NotiRecyclerViewAdapter(Context context, List<String> lists) {
        this.mContext = context;
        this.mList = lists;
    }

    public void setOnItemClickListener(RecyclerViewClickListener.OnItemClickListener listener) {//对外提供的一个监听方法
        this.mOnItemClickListener = listener;
    }

    @Override
    public NotiViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_notification, parent, false);//填充这个item布局
        NotiViewHolder viewHolder = new NotiViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final NotiViewHolder holder, final int position) {

        switch (position) {
            case 0:
                holder.textNotiType.setText(mContext.getResources().getString(R.string.at_me));
                holder.imgNotiType.setBackground(
                        mContext.getResources().getDrawable(R.drawable.at_select));
                break;
            case 1:
                holder.textNotiType.setText(mContext.getResources().getString(R.string.comment));
                holder.imgNotiType.setBackground(
                        mContext.getResources().getDrawable(R.drawable.comment_select));
                break;
            case 2:
                holder.textNotiType.setText(mContext.getResources().getString(R.string.like));
                holder.imgNotiType.setBackground(
                        mContext.getResources().getDrawable(R.drawable.like_select));
                break;
        }

        holder.layoutRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (position) {
                    case 0:
                        Intent atIntent = new Intent(mContext, BeAtActivity.class);
                        mContext.startActivity(atIntent);
                        break;
                    case 1:
                        Intent commentIntent = new Intent(mContext, BeCommentActivity.class);
                        mContext.startActivity(commentIntent);
                        break;
                    case 2:
                        Intent likeIntent = new Intent(mContext, BeLikeActivity.class);
                        mContext.startActivity(likeIntent);
                        break;
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mList.size() + 3;
    }

    public List getList() {
        return mList;
    }

    public void setList(List<String> list) {
        mList = list;
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);
    }

}
