package com.sorashiro.cloudcat;


import com.sorashiro.cloudcat.bean.CheckFollowBean;
import com.sorashiro.cloudcat.bean.PosterBean;
import com.sorashiro.cloudcat.bean.ResMsgBean;
import com.sorashiro.cloudcat.bean.UserDetailBean;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface MyService {

    public static final int TYPE_FOLLOW    = 1;
    public static final int TYPE_FOLLOWING = 2;
    public static final int TYPE_PHOTO     = 1;
    public static final int TYPE_VIDEO     = 2;

    @FormUrlEncoded
    @POST("/normalusers/login/")
    Call<ResMsgBean> login(@Field("username") String name,
                           @Field("password") String pw);

    @FormUrlEncoded
    @POST("/normalusers/register/")
    Call<ResMsgBean> register(@Field("username") String username,
                              @Field("password") String pw,
                              @Field("email") String email);

    @GET("/normalusers/get_follow/")
        // 1: 查询该用户的关注  2: 查询该用户的粉丝
    Call<ResponseBody> getFollow(@Query("username") String username,
                                 @Query("s_type") int sType);

    @GET("/normalusers/get_other_follow/")
        // 1: 查询该用户的关注  2: 查询该用户的粉丝
    Call<ResponseBody> getOtherFollow(@Query("username") String username,
                                      @Query("check_name") String check_name,
                                      @Query("s_type") int sType);

    @GET("/normalusers/check_follow/")
        // 1: 查询该用户的关注  2: 查询该用户的粉丝
    Call<CheckFollowBean> checkFollow(@Query("username") String username,
                                      @Query("check_name") String check_name);

    @FormUrlEncoded
    @POST("/normalusers/change_follow/")
        // 1: 修改该用户的关注  2: 移除该用户的粉丝
    Call<ResMsgBean> changeFollow(@Field("username") String username,
                                  @Field("password") String pw,
                                  @Field("target_user") String targetName,
                                  @Field("s_type") int sType);

    @GET("/normalusers/get_user_detail/")
        // 查询该用户的详细信息，如动态、关注、粉丝数量等
    Call<UserDetailBean> getUserDetail(@Query("username") String username);

    @GET("/normalusers/find_users/")
        // 查询名字包含 find_name 字符串的用户群
    Call<ResponseBody> findUsers(@Query("find_name") String find_name);

    @Multipart
    @POST("/normalusers/update_user_avatar/")
    Call<ResMsgBean> updateUserAvatar(@Part("username") RequestBody username,
                                           @Part("password") RequestBody pw,
                                           @Part MultipartBody.Part photo);

    @FormUrlEncoded
    @POST("/poster/create_poster/")
        // 1: 图片  2: 视频
    Call<ResMsgBean> createPoster(@Field("username") String username,
                                  @Field("password") String pw,
                                  @Field("text") String text,
                                  @Field("p_type") int p_type);

    @GET("/poster/poster_list/")
        // 查询该用户的动态列表
    Call<ResponseBody> getPosterList(@Query("username") String username);

    @GET("/poster/get_poster_by_id/")
        // 查询某一动态具体信息
    Call<PosterBean> getPosterById(@Query("username") String username,
                                   @Query("poster_id") int poster_id);

    @GET("/poster/home/")
        // 查询该用户的关注的人的动态列表
    Call<ResponseBody> getHome(@Query("username") String username);

    @GET("/poster/get_comments/")
        // 查询某一动态的评论列表
    Call<ResponseBody> getComments(@Query("poster_id") int poster_id);

    @GET("/poster/get_forwards/")
        // 查询某一动态的评论列表
    Call<ResponseBody> getForwards(@Query("poster_id") int poster_id);

    @GET("/poster/get_likes/")
        // 查询某一动态的评论列表
    Call<ResponseBody> getLikes(@Query("poster_id") int poster_id);

    @FormUrlEncoded
    @POST("/poster/send_forward/")
    Call<ResMsgBean> sendForward(@Field("username") String username,
                                 @Field("password") String pw,
                                 @Field("text") String text,
                                 @Field("poster_id") int poster_id);

    @FormUrlEncoded
    @POST("/poster/send_comment/")
    Call<ResMsgBean> sendComment(@Field("username") String username,
                                 @Field("password") String pw,
                                 @Field("text") String text,
                                 @Field("poster_id") int poster_id);

    @FormUrlEncoded
    @POST("/poster/send_like/")
    Call<ResMsgBean> sendLike(@Field("username") String username,
                              @Field("password") String pw,
                              @Field("poster_id") int poster_id);

    @FormUrlEncoded
    @POST("/poster/get_be_ated_list/")
    Call<ResponseBody> getBeAtList(@Field("username") String username,
                                   @Field("password") String pw);

    @FormUrlEncoded
    @POST("/poster/get_be_commented_list/")
    Call<ResponseBody> getBeCommentList(@Field("username") String username,
                                        @Field("password") String pw);

    @FormUrlEncoded
    @POST("/poster/get_be_liked_list/")
    Call<ResponseBody> getBeLikeList(@Field("username") String username,
                                     @Field("password") String pw);

    @Multipart
    @POST("/poster/create_poster_with_photo/")
    Call<ResMsgBean> createPosterWithPhoto(@Part("username") RequestBody username,
                                           @Part("password") RequestBody pw,
                                           @Part("text") RequestBody text,
                                           @Part("p_type") RequestBody p_type,
                                           @Part MultipartBody.Part photo);


}
