package com.sorashiro.cloudcat.recyclerview.poster.detail;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sorashiro.cloudcat.PhotoActivity;
import com.sorashiro.cloudcat.PosterDetailActivity;
import com.sorashiro.cloudcat.R;
import com.sorashiro.cloudcat.UserDetailActivity;
import com.sorashiro.cloudcat.bean.BasePosterDetailBean;
import com.sorashiro.cloudcat.bean.CommentBean;
import com.sorashiro.cloudcat.bean.ForwardBean;
import com.sorashiro.cloudcat.bean.LikeBean;
import com.sorashiro.cloudcat.bean.PosterBean;
import com.sorashiro.cloudcat.data.Constant;
import com.sorashiro.cloudcat.recyclerview.RecyclerViewClickListener;
import com.sorashiro.cloudcat.viewhelper.ImageInfo;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class PDRecyclerViewAdapter extends RecyclerView.Adapter<PDBaseViewHolder> {

    public static final int TYPE_POSTER_DETAIL = 0x0000_0101;
    public static final int TYPE_FORWARD       = 0x0000_0001;
    public static final int TYPE_COMMENT       = 0x0000_0002;
    public static final int TYPE_LIKE          = 0x0000_0003;
    public static final int TYPE_FORWARD_EMPTY = 0x0000_0011;
    public static final int TYPE_COMMENT_EMPTY = 0x0000_0012;
    public static final int TYPE_LIKE_EMPTY    = 0x0000_0013;

    private static final int PRE_ITEM_COUNT = 1;

    private int mType = TYPE_COMMENT;
    private List<? extends BasePosterDetailBean> mBeans;

    private boolean mFirst = true;

    private Context                                       mContext;
    private PosterBean                                    mPosterBean;
    private PosterBean                                    mOriginBean;
    private RecyclerViewClickListener.OnItemClickListener mOnItemClickListener;

    public PDRecyclerViewAdapter(Context context, PosterBean bean, List<BasePosterDetailBean> basePosterDetailBeans) {
        this.mContext = context;
        this.mPosterBean = bean;
        this.mOriginBean = null;
        this.mBeans = basePosterDetailBeans;
    }

    public PDRecyclerViewAdapter(Context context, PosterBean bean, PosterBean ori, List<BasePosterDetailBean> basePosterDetailBeans) {
        this.mContext = context;
        this.mPosterBean = bean;
        this.mOriginBean = ori;
        this.mBeans = basePosterDetailBeans;
    }

    public void setOnItemClickListener(RecyclerViewClickListener.OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    @Override
    public PDBaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_POSTER_DETAIL:
                if (mOriginBean != null) {
                    View itemView1 = LayoutInflater.from(mContext).inflate(R.layout.item_pdi_head_forward, parent, false);
                    PDFViewHolder viewHolder1 = new PDFViewHolder(itemView1);
                    return viewHolder1;
                } else {
                    View itemView1 = LayoutInflater.from(mContext).inflate(R.layout.item_pdi_head, parent, false);
                    PDViewHolder viewHolder1 = new PDViewHolder(itemView1);
                    return viewHolder1;
                }
            case TYPE_FORWARD:
                View itemView2 = LayoutInflater.from(mContext).inflate(R.layout.item_pdi_forward, parent, false);
                PdiForwardVH viewHolder2 = new PdiForwardVH(itemView2);
                return viewHolder2;
            case TYPE_COMMENT:
                View itemView3 = LayoutInflater.from(mContext).inflate(R.layout.item_pdi_comment, parent, false);
                PdiCommentVH viewHolder3 = new PdiCommentVH(itemView3);
                return viewHolder3;
            case TYPE_LIKE:
                View itemView4 = LayoutInflater.from(mContext).inflate(R.layout.item_pdi_like, parent, false);
                PdiLikeVH viewHolder4 = new PdiLikeVH(itemView4);
                return viewHolder4;
            case TYPE_FORWARD_EMPTY:
            case TYPE_COMMENT_EMPTY:
            case TYPE_LIKE_EMPTY:
                View emptyView = LayoutInflater.from(mContext).inflate(R.layout.item_empty_pdi, parent, false);
                PdiEmptyVH emptyVH = new PdiEmptyVH(emptyView);
                return emptyVH;
            default:
                return null;
        }

    }

    @Override
    public void onBindViewHolder(final PDBaseViewHolder holder, int position) {
        if (position == 0) {
            if (mOriginBean == null) {
                final PDViewHolder pdViewHolder = (PDViewHolder) holder;
                String userName = mPosterBean.getName();
                String posterTime = mPosterBean.getTime();
                String posterText = mPosterBean.getText();
                pdViewHolder.textName.setText(userName);
                pdViewHolder.textTime.setText(posterTime);
                pdViewHolder.textContent.setText(posterText);

                Picasso.with(mContext)
                        .load(Constant.BASE_STATIC_PHOTO_URL + mPosterBean.getPhoto_id())
                        .config(Bitmap.Config.RGB_565)
                        .into(pdViewHolder.imgContent, new Callback() {
                            @Override
                            public void onSuccess() {
                                pdViewHolder.imgContent.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(mContext, PhotoActivity.class);
                                        ImageInfo imageInfo = new ImageInfo();
                                        imageInfo.setPath(Constant.BASE_STATIC_PHOTO_URL + mPosterBean.getPhoto_id());
                                        intent.putExtra("image_info", imageInfo);
                                        mContext.startActivity(intent);
                                    }
                                });
                            }

                            @Override
                            public void onError() {

                            }
                        });

                pdViewHolder.imgHead.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startUserDetailActivity(mPosterBean.getName());
                    }
                });
                Picasso.with(mContext)
                        .load(Constant.BASE_STATIC_AVATAR_URL + mPosterBean.getName())
                        .placeholder(Constant.AVATAR_DEFAULT)
                        .error(Constant.AVATAR_DEFAULT)
                        .into(pdViewHolder.imgHead);
            } else {
                final PDFViewHolder pdViewHolder = (PDFViewHolder) holder;
                String userName = mPosterBean.getName();
                String posterTime = mPosterBean.getTime();
                String posterText = mPosterBean.getText();
                pdViewHolder.textName.setText(userName);
                pdViewHolder.textTime.setText(posterTime);
                pdViewHolder.textContent.setText(posterText);
                pdViewHolder.textAuthorName.setText(mOriginBean.getName() + " ：");
                pdViewHolder.textAuthorName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startUserDetailActivity(mOriginBean.getName());
                    }
                });
                pdViewHolder.textAuthorContent.setText(mOriginBean.getText());
                pdViewHolder.layoutAuthor.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, PosterDetailActivity.class);
                        intent.putExtra("poster_bean", mOriginBean);
                        intent.putExtra("pdi_type", PDRecyclerViewAdapter.TYPE_COMMENT);
                        mContext.startActivity(intent);
                    }
                });

                Picasso.with(mContext)
                        .load(Constant.BASE_STATIC_PHOTO_URL + mPosterBean.getPhoto_id())
                        .config(Bitmap.Config.RGB_565)
                        .into(pdViewHolder.imgAuthorContent, new Callback() {
                            @Override
                            public void onSuccess() {
                                pdViewHolder.imgAuthorContent.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(mContext, PhotoActivity.class);
                                        ImageInfo imageInfo = new ImageInfo();
                                        imageInfo.setPath(Constant.BASE_STATIC_PHOTO_URL + mPosterBean.getPhoto_id());
                                        intent.putExtra("image_info", imageInfo);
                                        mContext.startActivity(intent);
                                    }
                                });
                            }

                            @Override
                            public void onError() {

                            }
                        });

                pdViewHolder.imgHead.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startUserDetailActivity(mPosterBean.getName());
                    }
                });
                pdViewHolder.imgAuthorContent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startUserDetailActivity(mOriginBean.getName());
                    }
                });
                Picasso.with(mContext)
                        .load(Constant.BASE_STATIC_AVATAR_URL + mPosterBean.getName())
                        .placeholder(Constant.AVATAR_DEFAULT)
                        .error(Constant.AVATAR_DEFAULT)
                        .into(pdViewHolder.imgHead);
            }

        } else {
            switch (mType) {
                case TYPE_COMMENT:
                    final PdiCommentVH pdiCommentVH = (PdiCommentVH) holder;
                    CommentBean commentBean = (CommentBean) mBeans.get(position - PRE_ITEM_COUNT);
                    String commentName = commentBean.getName();
                    String commentText = commentBean.getText();
                    String commentTime = commentBean.getTime();
                    pdiCommentVH.textName.setText(commentName);
                    pdiCommentVH.textContent.setText(commentText);
                    pdiCommentVH.textTime.setText(commentTime);
                    pdiCommentVH.imgHead.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startUserDetailActivity(pdiCommentVH.textName.getText().toString());
                        }
                    });
                    Picasso.with(mContext)
                            .load(Constant.BASE_STATIC_AVATAR_URL + commentBean.getName())
                            .placeholder(Constant.AVATAR_DEFAULT)
                            .error(Constant.AVATAR_DEFAULT)
                            .into(pdiCommentVH.imgHead);
                    break;
                case TYPE_FORWARD:
                    final PdiForwardVH pdiForwardVH = (PdiForwardVH) holder;
                    ForwardBean forwardBean = (ForwardBean) mBeans.get(position - PRE_ITEM_COUNT);
                    String forwardName = forwardBean.getName();
                    String forwardText = forwardBean.getText();
                    String forwardTime = forwardBean.getTime();
                    pdiForwardVH.textName.setText(forwardName);
                    pdiForwardVH.textContent.setText(forwardText);
                    pdiForwardVH.textTime.setText(forwardTime);
                    pdiForwardVH.imgHead.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startUserDetailActivity(pdiForwardVH.textName.getText().toString());
                        }
                    });
                    Picasso.with(mContext)
                            .load(Constant.BASE_STATIC_AVATAR_URL + forwardBean.getName())
                            .placeholder(Constant.AVATAR_DEFAULT)
                            .error(Constant.AVATAR_DEFAULT)
                            .into(pdiForwardVH.imgHead);
                    break;
                case TYPE_LIKE:
                    final PdiLikeVH pdiLikeVH = (PdiLikeVH) holder;
                    LikeBean likeBean = (LikeBean) mBeans.get(position - PRE_ITEM_COUNT);
                    String likeName = likeBean.getName();
                    String likeTime = likeBean.getTime();
                    pdiLikeVH.textName.setText(likeName);
                    pdiLikeVH.textTime.setText(likeTime);
                    pdiLikeVH.imgHead.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startUserDetailActivity(pdiLikeVH.textName.getText().toString());
                        }
                    });
                    Picasso.with(mContext)
                            .load(Constant.BASE_STATIC_AVATAR_URL + likeBean.getName())
                            .placeholder(Constant.AVATAR_DEFAULT)
                            .error(Constant.AVATAR_DEFAULT)
                            .into(pdiLikeVH.imgHead);
                    break;
                case TYPE_FORWARD_EMPTY:
                    PdiEmptyVH pdiEmptyFVH = (PdiEmptyVH) holder;
                    pdiEmptyFVH.textEmpty.setText(mContext.getResources().getString(R.string.nobody_forward));
                    break;
                case TYPE_COMMENT_EMPTY:
                    PdiEmptyVH pdiEmptyCVH = (PdiEmptyVH) holder;
                    pdiEmptyCVH.textEmpty.setText(mContext.getResources().getString(R.string.nobody_comment));
                    break;
                case TYPE_LIKE_EMPTY:
                    PdiEmptyVH pdiEmptyLVH = (PdiEmptyVH) holder;
                    pdiEmptyLVH.textEmpty.setText(mContext.getResources().getString(R.string.nobody_like));
                    break;
            }
        }
    }

    private void startUserDetailActivity(String name) {
        Intent intent = new Intent(mContext, UserDetailActivity.class);
        intent.putExtra("username", name);
        mContext.startActivity(intent);
    }

    @Override
    public int getItemViewType(int position) {
        switch (position) {
            case 0:
                return TYPE_POSTER_DETAIL;
            default:
                return mType;
        }
    }

    @Override
    public int getItemCount() {
        if ((mType & 0x0000_00F0) == 0x0000_0010) {
            return 1 + PRE_ITEM_COUNT;
        } else {
            return mBeans.size() + PRE_ITEM_COUNT;
        }
    }

    public int getType() {
        return mType;
    }

    public void setType(int type) {
        mType = type;
    }

    public PosterBean getPosterBean() {
        return mPosterBean;
    }

    public void setPosterBean(PosterBean posterBean) {
        mPosterBean = posterBean;
        notifyItemChanged(0);
    }

    public List<? extends BasePosterDetailBean> getBeans() {
        return mBeans;
    }

    public void setBeans(List<? extends BasePosterDetailBean> beans) {
        if (beans.size() == 0) {
            switch (mType) {
                case TYPE_FORWARD:
                    mType = TYPE_FORWARD_EMPTY;
                    break;
                case TYPE_COMMENT:
                    mType = TYPE_COMMENT_EMPTY;
                    break;
                case TYPE_LIKE:
                    mType = TYPE_LIKE_EMPTY;
                    break;
            }
        }
        int size = mBeans.size();
        mBeans = new ArrayList<>();
        notifyItemRangeRemoved(1, size == 0 ? 1 : size);
        mBeans = beans;
        notifyItemRangeInserted(1, mBeans.size() == 0 ? 1 : mBeans.size());
        //        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);
    }

    public interface OnTabSelectedListener {
        void onTabSelected(int position);

        void onTabUnselected(int position);

        void onTabReselected(int position);
    }

}
