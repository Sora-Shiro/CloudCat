# coding=utf-8
from __future__ import unicode_literals

import os

from rest_framework.decorators import api_view
from rest_framework.response import Response

from cloudcatBn.settings import STATICFILES_DIRS
from normalusers.models import User, Follower
from normalusers.serializers import FollowerSerializer
from poster.forms import CreatePosterForm, SendCommentForm, SendForwardForm, SendLikeForm, BeForm
from poster.models import Poster, CommentP, ForwardP, LikeP
from poster.serializers import PosterSerializer, CommentPSerializer, ForwardPSerializer, LikePSerializer
from tools.re_sorashiro import re_emoji

@api_view(['GET'])
def home(request, format=None):
    if request.method == 'GET':
        username = request.GET.get('username')
        follows = Follower.objects.filter(u2__username__exact=username)
        p_list = []
        if follows:
            follow_list = []
            for item in follows:
                follow = FollowerSerializer(item).data
                u1 = follow['u1']
                follow_list.append(u1)
            posters = Poster.objects.filter(name_id__in=follow_list).order_by('-time')
            for item in posters:
                poster = PosterSerializer(item).data
                pk_user = poster['name']
                user_name = User.objects.filter(id__exact=pk_user)[0].username
                poster = dict(poster)
                poster['name'] = user_name
                check_like = LikeP.objects.filter(poster_id__exact=poster['id'],
                                                  name__username__exact=username)
                if check_like:
                    poster['if_like'] = True
                else:
                    poster['if_like'] = False
                p_list.append(poster)
        return Response(p_list)


@api_view(['GET'])
def poster_list(request, format=None):
    if request.method == 'GET':
        username = request.GET.get('username')
        user = User.objects.filter(username__exact=username)
        if user:
            posters = Poster.objects.filter(name__username__exact=username).order_by('-time')
            p_list = []
            for item in posters:
                poster = PosterSerializer(item).data
                pk_user = poster['name']
                user_name = User.objects.filter(id__exact=pk_user)[0].username
                poster = dict(poster)
                poster['name'] = user_name
                check_like = LikeP.objects.filter(poster_id__exact=poster['id'], name__username__exact=username)
                if check_like:
                    poster['if_like'] = True
                else:
                    poster['if_like'] = False
                p_list.append(poster)
            return Response(p_list)
        else:
            return Response({'resMsg': '没有这个用户'})


@api_view(['GET'])
def get_poster_by_id(request, format=None):
    if request.method == 'GET':
        username = request.GET.get('username')
        user = User.objects.filter(username__exact=username)
        if user:
            poster_id = request.GET.get('poster_id')
            poster = Poster.objects.filter(id__exact=poster_id)
            if poster:
                poster = PosterSerializer(poster[0]).data
                pk_user = poster['name']
                user_name = User.objects.filter(id__exact=pk_user)[0].username
                poster = dict(poster)
                poster['name'] = user_name
                check_like = LikeP.objects.filter(poster_id__exact=poster['id'], name__username__exact=username)
                if check_like:
                    poster['if_like'] = True
                else:
                    poster['if_like'] = False
                poster['resMsg'] = ''
                return Response(poster)
            else:
                return Response({'resMsg': '没有这条动态'})
        else:
            return Response({'resMsg': '没有这个用户'})


@api_view(['POST'])
def create_poster(request, format=None):
    if request.method == 'POST':
        form = CreatePosterForm(request.POST)
        if form.is_valid():
            username = form.cleaned_data['username']
            password = form.cleaned_data['password']
            p_type = form.cleaned_data['p_type']
            text = form.cleaned_data['text']
            # Emoji 处理
            if re_emoji.search(text):
                return Response({'resMsg': '暂时不支持Emoji哦'})
            user = User.objects.filter(username__exact=username, password__exact=password)
            if user:
                new_poster = Poster()
                new_poster.name = user[0]
                new_poster.p_type = p_type
                new_poster.text = text
                new_poster.save()
                return Response({'resMsg': '发布成功'})
            else:
                return Response({'resMsg': '请求用户密码错误'})


@api_view(['POST'])
def create_poster_with_photo(request, format=None):
    if request.method == 'POST':
        form = CreatePosterForm(request.POST)
        if form.is_valid():
            username = form.cleaned_data['username']
            password = form.cleaned_data['password']
            p_type = form.cleaned_data['p_type']
            text = form.cleaned_data['text']
            # Emoji 处理
            if re_emoji.search(text):
                return Response({'resMsg': '暂时不支持Emoji哦'})
            user = User.objects.filter(username__exact=username, password__exact=password)
            if user:
                # Handle photo
                photo = request.FILES.get('photo')
                if not photo:
                    return Response({'resMsg': '请上传图片'})
                photo_name = photo.name
                point_index = photo_name.rfind('.')
                if point_index == -1:
                    return Response({'resMsg': '文件格式错误'})
                suffix = photo_name[point_index:]
                # size (Byte)
                photo_size = photo.size
                # Max size: 10MB
                if photo_size > 10485760:
                    return Response({'resMsg': '文件过大，超过10M'})
                # Basic save
                new_poster = Poster()
                new_poster.name = user[0]
                new_poster.p_type = p_type
                new_poster.text = text
                new_poster.save()
                new_poster_id = new_poster.id
                # Name: {poster_id} + '_0'(which is index) + suffix
                file_name = STATICFILES_DIRS[0] + os.path.sep + 'file' + \
                            os.path.sep + 'photo' + os.path.sep + str(new_poster_id) + '_0' + suffix
                with open(file_name, str('wb+')) as destination:
                    for chunk in photo.chunks():
                        destination.write(chunk)
                new_poster.photo_id = str(new_poster_id) + '_0' + suffix
                new_poster.save()
                # Result
                return Response({'resMsg': '发布成功'})
            else:
                return Response({'resMsg': '请求用户密码错误'})
        else:
            return Response({'resMsg': '请求格式错误'})


@api_view(['GET'])
def get_comments(request, format=None):
    if request.method == 'GET':
        poster_id = request.GET.get('poster_id')
        comments = CommentP.objects.filter(poster_id__exact=poster_id).order_by('-time')
        c_list = []
        for item in comments:
            comment = CommentPSerializer(item).data
            pk_author = comment['author']
            pk_comment_user = comment['name']
            author_name = User.objects.filter(id__exact=pk_author)[0].username
            comment_name = User.objects.filter(id__exact=pk_comment_user)[0].username
            comment = dict(comment)
            comment['author'] = author_name
            comment['name'] = comment_name
            c_list.append(comment)
        return Response(c_list)


@api_view(['POST'])
def send_comment(request, format=None):
    if request.method == 'POST':
        form = SendCommentForm(request.POST)
        if form.is_valid():
            username = form.cleaned_data['username']
            password = form.cleaned_data['password']
            poster_id = form.cleaned_data['poster_id']
            text = form.cleaned_data['text']
            # Emoji 处理
            if re_emoji.search(text):
                return Response({'resMsg': '暂时不支持Emoji哦'})
            user = User.objects.filter(username__exact=username, password__exact=password)
            ori_poster = Poster.objects.filter(id__exact=poster_id)
            if user and ori_poster:
                new_comment = CommentP()
                new_comment.name = user[0]
                new_comment.text = text
                new_comment.poster_id = poster_id
                new_comment.author = ori_poster[0].name
                new_comment.save()
                ori_poster = ori_poster[0]
                ori_poster.comment_num = ori_poster.comment_num + 1
                ori_poster.save()
                return Response({'resMsg': '评论成功'})
            else:
                return Response({'resMsg': '请求用户密码错误或动态不存在'})


@api_view(['GET'])
def get_forwards(request, format=None):
    if request.method == 'GET':
        poster_id = request.GET.get('poster_id')
        forwards = ForwardP.objects.filter(poster_id__exact=poster_id).order_by('-time')
        f_list = []
        for item in forwards:
            forward = ForwardPSerializer(item).data
            pk_author = forward['author']
            pk_comment_user = forward['name']
            author_name = User.objects.filter(id__exact=pk_author)[0].username
            forward_name = User.objects.filter(id__exact=pk_comment_user)[0].username
            forward = dict(forward)
            forward['author'] = author_name
            forward['name'] = forward_name
            f_list.append(forward)
        return Response(f_list)


@api_view(['POST'])
def send_forward(request, format=None):
    if request.method == 'POST':
        form = SendForwardForm(request.POST)
        if form.is_valid():
            username = form.cleaned_data['username']
            password = form.cleaned_data['password']
            poster_id = form.cleaned_data['poster_id']
            text = form.cleaned_data['text']
            # Emoji 处理
            if re_emoji.search(text):
                return Response({'resMsg': '暂时不支持Emoji哦'})
            user = User.objects.filter(username__exact=username, password__exact=password)
            ori_poster = Poster.objects.filter(id__exact=poster_id)
            if user and ori_poster:
                new_poster = Poster()
                new_poster.name = user[0]
                new_poster.text = text
                new_poster.p_type = 3
                new_poster.origin_poster_id = ori_poster[0].id
                new_poster.photo_id = ori_poster[0].photo_id
                new_poster.save()
                new_p_id = new_poster.id
                new_time = new_poster.time

                new_forward = ForwardP()
                new_forward.time = new_time
                new_forward.name = user[0]
                new_forward.text = text
                new_forward.poster_id = poster_id
                new_forward.f_poster_id = new_p_id
                new_forward.author = ori_poster[0].name
                new_forward.save()

                ori_poster = ori_poster[0]
                ori_poster.forward_num = ori_poster.forward_num + 1
                ori_poster.save()
                return Response({'resMsg': '转发成功'})
            else:
                return Response({'resMsg': '请求用户密码错误或动态不存在'})


@api_view(['GET'])
def get_likes(request, format=None):
    if request.method == 'GET':
        poster_id = request.GET.get('poster_id')
        likes = LikeP.objects.filter(poster_id__exact=poster_id).order_by('-time')
        l_list = []
        for item in likes:
            like = LikePSerializer(item).data
            pk_author = like['author']
            pk_comment_user = like['name']
            author_name = User.objects.filter(id__exact=pk_author)[0].username
            like_name = User.objects.filter(id__exact=pk_comment_user)[0].username
            like = dict(like)
            like['author'] = author_name
            like['name'] = like_name
            l_list.append(like)
        return Response(l_list)


@api_view(['POST'])
def send_like(request, format=None):
    if request.method == 'POST':
        form = SendLikeForm(request.POST)
        if form.is_valid():
            username = form.cleaned_data['username']
            password = form.cleaned_data['password']
            poster_id = form.cleaned_data['poster_id']
            user = User.objects.filter(username__exact=username, password__exact=password)
            ori_poster = Poster.objects.filter(id__exact=poster_id)
            if user and ori_poster:
                check_like = LikeP.objects.filter(poster_id__exact=poster_id, name__username__exact=username)
                if check_like:
                    old_like = check_like[0]
                    old_like.delete()
                    ori_poster = ori_poster[0]
                    ori_poster.like_num = ori_poster.like_num - 1
                    ori_poster.save()
                    return Response({'resMsg': '已取消赞'})
                else:
                    new_like = LikeP()
                    new_like.name = user[0]
                    new_like.poster_id = poster_id
                    new_like.author = ori_poster[0].name
                    new_like.save()
                    ori_poster = ori_poster[0]
                    ori_poster.like_num = ori_poster.like_num + 1
                    ori_poster.save()
                    return Response({'resMsg': '已赞'})
            else:
                return Response({'resMsg': '请求用户密码错误或动态不存在'})


@api_view(['POST'])
def get_be_ated_list(request, format=None):
    if request.method == 'POST':
        form = BeForm(request.POST)
        if form.is_valid():
            username = form.cleaned_data['username']
            password = form.cleaned_data['password']
            user = User.objects.filter(username__exact=username, password__exact=password)
            if user:
                be_ateds = ForwardP.objects.filter(author__username__exact=username).order_by('-time')
                a_list = []
                for item in be_ateds:
                    item = ForwardPSerializer(item).data
                    item = dict(item)
                    poster_id = item['poster']
                    ori_poster = Poster.objects.filter(id__exact=poster_id)
                    if ori_poster:
                        item['if_ori'] = True
                        item['ori_text'] = ori_poster[0].text
                        check_like = LikeP.objects.filter(poster_id__exact=ori_poster[0].id,
                                                          name__username__exact=username)
                        if check_like:
                            item['if_like'] = True
                        else:
                            item['if_like'] = False
                    else:
                        item['if_ori'] = False
                        item['ori_text'] = '该动态不存在'
                        item['if_like'] = False
                    pk_author = item['author']
                    pk_forward_user = item['name']
                    author_name = User.objects.filter(id__exact=pk_author)[0].username
                    forward_name = User.objects.filter(id__exact=pk_forward_user)[0].username
                    item['author'] = author_name
                    item['name'] = forward_name
                    a_list.append(item)
                return Response(a_list)
            else:
                return Response({'resMsg': '请求用户密码错误'})
        else:
            return Response({'resMsg': '请求格式不规范'})


@api_view(['POST'])
def get_be_commented_list(request, format=None):
    if request.method == 'POST':
        form = BeForm(request.POST)
        if form.is_valid():
            username = form.cleaned_data['username']
            password = form.cleaned_data['password']
            user = User.objects.filter(username__exact=username, password__exact=password)
            if user:
                be_comments = CommentP.objects.filter(author__username__exact=username).order_by('-time')
                c_list = []
                for item in be_comments:
                    item = CommentPSerializer(item).data
                    item = dict(item)
                    poster_id = item['poster']
                    ori_poster = Poster.objects.filter(id__exact=poster_id)
                    if ori_poster:
                        item['if_ori'] = True
                        item['ori_text'] = ori_poster[0].text
                        check_like = LikeP.objects.filter(poster_id__exact=ori_poster[0].id,
                                                          name__username__exact=username)
                        if check_like:
                            item['if_like'] = True
                        else:
                            item['if_like'] = False
                    else:
                        item['if_ori'] = False
                        item['ori_text'] = '该动态不存在'
                        item['if_like'] = False
                    pk_author = item['author']
                    pk_comment_user = item['name']
                    author_name = User.objects.filter(id__exact=pk_author)[0].username
                    forward_name = User.objects.filter(id__exact=pk_comment_user)[0].username
                    item['author'] = author_name
                    item['name'] = forward_name
                    c_list.append(item)
                return Response(c_list)
            else:
                return Response({'resMsg': '请求用户密码错误'})
        else:
            return Response({'resMsg': '请求格式不规范'})


@api_view(['POST'])
def get_be_liked_list(request, format=None):
    if request.method == 'POST':
        form = BeForm(request.POST)
        if form.is_valid():
            username = form.cleaned_data['username']
            password = form.cleaned_data['password']
            user = User.objects.filter(username__exact=username, password__exact=password)
            if user:
                be_likes = LikeP.objects.filter(author__username__exact=username).order_by('-time')
                l_list = []
                for item in be_likes:
                    item = LikePSerializer(item).data
                    item = dict(item)
                    poster_id = item['poster']
                    ori_poster = Poster.objects.filter(id__exact=poster_id)
                    if ori_poster:
                        item['if_ori'] = True
                        item['ori_text'] = ori_poster[0].text
                        check_like = LikeP.objects.filter(poster_id__exact=ori_poster[0].id,
                                                          name__username__exact=username)
                        if check_like:
                            item['if_like'] = True
                        else:
                            item['if_like'] = False
                    else:
                        item['if_ori'] = False
                        item['ori_text'] = '该动态不存在'
                        item['if_like'] = False
                    pk_author = item['author']
                    pk_like_user = item['name']
                    author_name = User.objects.filter(id__exact=pk_author)[0].username
                    forward_name = User.objects.filter(id__exact=pk_like_user)[0].username
                    item['author'] = author_name
                    item['name'] = forward_name
                    l_list.append(item)
                return Response(l_list)
            else:
                return Response({'resMsg': '请求用户密码错误'})
        else:
            return Response({'resMsg': '请求格式不规范'})
