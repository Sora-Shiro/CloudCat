package com.sorashiro.cloudcat;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.sorashiro.cloudcat.bean.BasePosterDetailBean;
import com.sorashiro.cloudcat.bean.CommentBean;
import com.sorashiro.cloudcat.bean.ForwardBean;
import com.sorashiro.cloudcat.bean.LikeBean;
import com.sorashiro.cloudcat.bean.PosterBean;
import com.sorashiro.cloudcat.bean.ResMsgBean;
import com.sorashiro.cloudcat.data.Constant;
import com.sorashiro.cloudcat.data.UserData;
import com.sorashiro.cloudcat.recyclerview.Divider;
import com.sorashiro.cloudcat.recyclerview.MyRecyclerView;
import com.sorashiro.cloudcat.recyclerview.WrapContentLinearLayoutManager;
import com.sorashiro.cloudcat.recyclerview.poster.detail.PDRecyclerViewAdapter;
import com.sorashiro.cloudcat.tool.DPUtil;
import com.sorashiro.cloudcat.tool.LogAndToastUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PosterDetailActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {

    private Button         btnBack;
    private TextView       textDetailHead;
    private Button         btnForward;
    private Button         btnComment;
    private Button         btnLike;
    private TextView       btnDivider;
    private MyRecyclerView listPosterDetail;

    private List<CommentBean> mCommentBeans;
    private List<ForwardBean> mForwardBeans;
    private List<LikeBean>    mLikeBeans;

    private int mType;

    private PosterBean mPosterBean;
    private PosterBean mOriginBean;
    private boolean    mIfLike;

    private PDRecyclerViewAdapter mAdapter;

    private Retrofit  retrofit2 = new Retrofit.Builder()
            .baseUrl(Constant.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(new OkHttpClient())
            .build();
    private MyService service   = retrofit2.create(MyService.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPosterBean = getIntent().getParcelableExtra("poster_bean");
        mOriginBean = getIntent().getParcelableExtra("origin_bean");
        mType = getIntent().getIntExtra("pdi_type", PDRecyclerViewAdapter.TYPE_COMMENT);

        setContentView(R.layout.activity_poster_detail);

        initView();


        mIfLike = mPosterBean.isIf_like();
        btnLike.setTextColor(
                mIfLike ?
                        getResources().getColor(R.color.colorPrimary) :
                        getResources().getColor(R.color.colorSecondaryText));

        //        View emptyView = View.inflate(this, R.layout.item_empty_pdi, null);
        //        TextView textEmpty = (TextView) emptyView.findViewById(R.id.textEmpty);
        //        textEmpty.setText(getResources().getString(R.string.nobody_comment));
        //        listPosterDetail.setEmptyView(emptyView);

        listPosterDetail.setLayoutManager(new WrapContentLinearLayoutManager(
                PosterDetailActivity.this,
                LinearLayoutManager.VERTICAL, false));
        listPosterDetail.setItemAnimator(new DefaultItemAnimator());
        listPosterDetail.addItemDecoration(new Divider(
                PosterDetailActivity.this,
                LinearLayoutManager.HORIZONTAL,
                DPUtil.dip2px(PosterDetailActivity.this, 1),
                getResources().getColor(R.color.colorLightDivider)
        ));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            listPosterDetail.setNestedScrollingEnabled(false);
        }

        mAdapter = new PDRecyclerViewAdapter(
                PosterDetailActivity.this,
                mPosterBean,
                mOriginBean,
                new ArrayList<BasePosterDetailBean>());
        listPosterDetail.setAdapter(mAdapter);

        switch (mType) {
            case PDRecyclerViewAdapter.TYPE_FORWARD:
                getForwardsFromServer();
                textDetailHead.setText(R.string.pdi_forward);
                break;
            case PDRecyclerViewAdapter.TYPE_COMMENT:
                getCommentsFromServer();
                textDetailHead.setText(R.string.pdi_comment);
                break;
            case PDRecyclerViewAdapter.TYPE_LIKE:
                getLikesFromServer();
                textDetailHead.setText(R.string.pdi_like);
                break;
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        //        switch (mType) {
        //            case PDRecyclerViewAdapter.TYPE_FORWARD:
        //                getForwardsFromServer();
        //                break;
        //            case PDRecyclerViewAdapter.TYPE_COMMENT:
        //                getCommentsFromServer();
        //                break;
        //            case PDRecyclerViewAdapter.TYPE_LIKE:
        //                getLikesFromServer();
        //                break;
        //        }
    }

    private void initView() {
        btnBack = (Button) findViewById(R.id.btnBack);
        textDetailHead = (TextView) findViewById(R.id.textDetailHead);
        btnForward = (Button) findViewById(R.id.btnForward);
        btnComment = (Button) findViewById(R.id.btnComment);
        btnLike = (Button) findViewById(R.id.btnLike);
        btnDivider = (TextView) findViewById(R.id.btnDivider);
        listPosterDetail = (MyRecyclerView) findViewById(R.id.listPosterDetail);

        btnBack.setOnClickListener(this);
        btnForward.setOnClickListener(this);
        btnComment.setOnClickListener(this);
        btnLike.setOnClickListener(this);
        textDetailHead.setOnClickListener(this);

        btnForward.setOnLongClickListener(this);
        btnComment.setOnLongClickListener(this);
        btnLike.setOnLongClickListener(this);
    }

    private void getForwardsFromServer() {
        Call<ResponseBody> call = service.getForwards(
                mPosterBean.getId()
        );
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String body = response.body().string();
                        Gson gson = new Gson();
                        if (body.contains("resMsg")) {
                            String show = gson.fromJson(body, ResMsgBean.class).getResMsg();
                            LogAndToastUtil.ToastOut(PosterDetailActivity.this, show);
                            return;
                        }
                        ForwardBean[] beans = gson.fromJson(body, ForwardBean[].class);
                        List<ForwardBean> list = Arrays.asList(beans);
                        mForwardBeans = list;
                        PDRecyclerViewAdapter adapter = (PDRecyclerViewAdapter) listPosterDetail.getAdapter();
                        adapter.setType(PDRecyclerViewAdapter.TYPE_FORWARD);
                        adapter.setBeans(mForwardBeans);
                    } catch (IOException e) {
                        LogAndToastUtil.ToastOut(
                                PosterDetailActivity.this, e.getMessage());
                    }
                } else {
                    String error = response.code() + ": " + response.message();
                    LogAndToastUtil.LogV(error);
                    LogAndToastUtil.ToastOut(PosterDetailActivity.this,
                            "啊哦，服务器开小差了，请稍候再试");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                LogAndToastUtil.LogV("Error" + t.toString());
                LogAndToastUtil.ToastOut(PosterDetailActivity.this,
                        "啊哦，服务器开小差了，请稍候再试");
            }
        });
    }

    private void getCommentsFromServer() {
        Call<ResponseBody> call = service.getComments(
                mPosterBean.getId()
        );
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String body = response.body().string();
                        Gson gson = new Gson();
                        if (body.contains("resMsg")) {
                            String show = gson.fromJson(body, ResMsgBean.class).getResMsg();
                            LogAndToastUtil.ToastOut(PosterDetailActivity.this, show);
                            return;
                        }
                        CommentBean[] beans = gson.fromJson(body, CommentBean[].class);
                        List<CommentBean> list = Arrays.asList(beans);
                        mCommentBeans = list;
                        PDRecyclerViewAdapter adapter = (PDRecyclerViewAdapter) listPosterDetail.getAdapter();
                        adapter.setType(PDRecyclerViewAdapter.TYPE_COMMENT);
                        adapter.setBeans(mCommentBeans);
                    } catch (IOException e) {
                        LogAndToastUtil.ToastOut(
                                PosterDetailActivity.this, e.getMessage());
                    }
                } else {
                    String error = response.code() + ": " + response.message();
                    LogAndToastUtil.LogV(error);
                    LogAndToastUtil.ToastOut(PosterDetailActivity.this,
                            "啊哦，服务器开小差了，请稍候再试");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                LogAndToastUtil.LogV("Error" + t.toString());
                LogAndToastUtil.ToastOut(PosterDetailActivity.this,
                        "啊哦，服务器开小差了，请稍候再试");
            }
        });
    }

    private void getLikesFromServer() {
        Call<ResponseBody> call = service.getLikes(
                mPosterBean.getId()
        );
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String body = response.body().string();
                        Gson gson = new Gson();
                        if (body.contains("resMsg")) {
                            String show = gson.fromJson(body, ResMsgBean.class).getResMsg();
                            LogAndToastUtil.ToastOut(PosterDetailActivity.this, show);
                            return;
                        }
                        LikeBean[] beans = gson.fromJson(body, LikeBean[].class);
                        List<LikeBean> list = Arrays.asList(beans);
                        mLikeBeans = list;
                        PDRecyclerViewAdapter adapter = (PDRecyclerViewAdapter) listPosterDetail.getAdapter();
                        adapter.setType(PDRecyclerViewAdapter.TYPE_LIKE);
                        adapter.setBeans(mLikeBeans);
                    } catch (IOException e) {
                        LogAndToastUtil.ToastOut(
                                PosterDetailActivity.this, e.getMessage());
                    }
                } else {
                    String error = response.code() + ": " + response.message();
                    LogAndToastUtil.LogV(error);
                    LogAndToastUtil.ToastOut(PosterDetailActivity.this,
                            "啊哦，服务器开小差了，请稍候再试");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                LogAndToastUtil.LogV("Error" + t.toString());
                LogAndToastUtil.ToastOut(PosterDetailActivity.this,
                        "啊哦，服务器开小差了，请稍候再试");
            }
        });
    }

    @Override
    public void onClick(View v) {
        PDRecyclerViewAdapter adapter = (PDRecyclerViewAdapter) listPosterDetail.getAdapter();
        int type = adapter.getType() & 0x0000_000F;
        switch (v.getId()) {
            case R.id.btnBack:
                finish();
                break;
            case R.id.btnForward:
                mType = PDRecyclerViewAdapter.TYPE_FORWARD;
                textDetailHead.setText(R.string.pdi_forward);
                if (mForwardBeans == null || type == PDRecyclerViewAdapter.TYPE_FORWARD) {
                    getForwardsFromServer();
                } else {
                    adapter.setType(PDRecyclerViewAdapter.TYPE_FORWARD);
                    adapter.setBeans(mForwardBeans);
                }
                break;
            case R.id.btnComment:
                mType = PDRecyclerViewAdapter.TYPE_COMMENT;
                textDetailHead.setText(R.string.pdi_comment);
                if (mCommentBeans == null || type == PDRecyclerViewAdapter.TYPE_COMMENT) {
                    getCommentsFromServer();
                } else {
                    adapter.setType(PDRecyclerViewAdapter.TYPE_COMMENT);
                    adapter.setBeans(mCommentBeans);
                }
                break;
            case R.id.btnLike:
                mType = PDRecyclerViewAdapter.TYPE_LIKE;
                textDetailHead.setText(R.string.pdi_like);
                if (mLikeBeans == null || type == PDRecyclerViewAdapter.TYPE_LIKE) {
                    getLikesFromServer();
                } else {
                    adapter.setType(PDRecyclerViewAdapter.TYPE_LIKE);
                    adapter.setBeans(mLikeBeans);
                }
                break;
            case R.id.textDetailHead:
                listPosterDetail.smoothScrollToPosition(0);
                break;
        }
    }

    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()) {
            case R.id.btnForward:
                Intent fI = new Intent(this, PdiActivity.class);
                fI.putExtra("pdi_type", PdiActivity.PDI_FORWARD);
                if (mOriginBean == null) {
                    fI.putExtra("poster_id", mPosterBean.getId());
                } else {
                    fI.putExtra("poster_id", mPosterBean.getOrigin_poster_id());
                }
                startActivity(fI);
                break;
            case R.id.btnComment:
                Intent cI = new Intent(this, PdiActivity.class);
                cI.putExtra("pdi_type", PdiActivity.PDI_COMMENT);
                cI.putExtra("poster_id", mPosterBean.getId());
                startActivity(cI);
                break;
            case R.id.btnLike:
                sendLike();
                getLikesFromServer();
                break;
        }
        return true;
    }

    private void sendLike() {
        Call<ResMsgBean> call = service.sendLike(
                UserData.getCurrentUsername(),
                UserData.getCurrentPassword(),
                mPosterBean.getId()
        );
        call.enqueue(new Callback<ResMsgBean>() {
            @Override
            public void onResponse(Call<ResMsgBean> call, Response<ResMsgBean> response) {
                if (response.isSuccessful()) {
                    mIfLike = !mIfLike;
                    btnLike.setTextColor(
                            mIfLike ?
                                    getResources().getColor(R.color.colorPrimary) :
                                    getResources().getColor(R.color.colorSecondaryText));
                    LogAndToastUtil.ToastOut(
                            PosterDetailActivity.this, response.body().getResMsg());
                } else {
                    String error = response.code() + ": " + response.message();
                    LogAndToastUtil.LogV(error);
                    LogAndToastUtil.ToastOut(PosterDetailActivity.this,
                            "啊哦，服务器开小差了，请稍候再试");
                }
            }

            @Override
            public void onFailure(Call<ResMsgBean> call, Throwable t) {
                LogAndToastUtil.LogV("Error" + t.toString());
                LogAndToastUtil.ToastOut(PosterDetailActivity.this,
                        "啊哦，服务器没有返回数据，请稍候再试");
            }
        });
    }
}
