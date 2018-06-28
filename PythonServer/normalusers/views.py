# coding=utf-8
import os

from django.shortcuts import render_to_response
from rest_framework.decorators import api_view
from rest_framework.response import Response

from cloudcatBn.settings import STATICFILES_DIRS
from models import User, Follower
# Create your views here.
from normalusers.forms import UserLoginForm, UserRegisterForm, TYPE_FOLLOW, TYPE_FOLLOWING, \
    ChangeFollowForm
from normalusers.serializers import UserSerializer, FollowerSerializer
from poster.models import Poster
from tools.re_sorashiro import re_emoji


@api_view(['POST'])
def register(request, format=None):
    if request.method == 'POST':
        userForm = UserRegisterForm(request.POST)
        if userForm.is_valid():
            username = userForm.cleaned_data['username']
            password = userForm.cleaned_data['password']
            email = userForm.cleaned_data['email']
            if re_emoji.search(username) or re_emoji.search(password) or re_emoji.search(email):
                return Response({'resMsg': '暂时不支持Emoji哦'})
            username_set = User.objects.all().values('username')
            for item in username_set:
                if username == item['username']:
                    return Response({'resMsg': '已经有这个用户名了'})
            # client do the password encrypt
            print username
            User.objects.create(username=username, password=password, email=email)
            return Response({'resMsg': '注册成功'})
        else:
            return Response({'resMsg': '注册失败'})


@api_view(['POST'])
def login(request, format=None):
    if request.method == 'POST':
        userForm = UserLoginForm(request.POST)
        if userForm.is_valid():
            username = userForm.cleaned_data['username']
            password = userForm.cleaned_data['password']
            user = User.objects.filter(username__exact=username, password__exact=password)
            if user:
                # serializer = UserSerializer(user[0])
                return Response({'resMsg': '登录成功'})
            else:
                return Response({'resMsg': '用户名或密码错误,请重新登录'})
        else:
            return Response({'resMsg': '登录失败'})


def index(request):
    userform = UserLoginForm(request.POST)
    return render_to_response('normal_index.html', {'userform': userform})


@api_view(['GET'])
def get_follow(request, format=None):
    if request.method == 'GET':
        username = request.GET.get('username')
        s_type = int(request.GET.get('s_type'))
        user = User.objects.filter(username__exact=username)
        if user:
            serializer = UserSerializer(user[0])
            pk = serializer.data['id']
            # 针对自己的关注
            if s_type == TYPE_FOLLOW:
                f = Follower.objects.filter(u2_id__exact=pk).order_by('-time')
                user_name = User.objects.filter(id__exact=pk)[0].username
                f_list = []
                for item in f:
                    serializer = FollowerSerializer(item)
                    target_id = serializer.data['u1']
                    target_name = User.objects.filter(id__exact=target_id)[0].username
                    res = {'u1': target_name, 'u2': user_name}
                    check_following = Follower.objects.filter(u1_id__exact=pk, u2_id__exact=target_id)
                    if check_following:
                        res['if_mutual'] = True
                    else:
                        res['if_mutual'] = False
                    f_list.append(res)
                return Response(f_list)
            # 针对自己的粉丝
            elif s_type == TYPE_FOLLOWING:
                f = Follower.objects.filter(u1_id__exact=pk).order_by('-time')
                f_list = []
                for item in f:
                    serializer = FollowerSerializer(item)
                    target_id = serializer.data['u2']
                    user_id = serializer.data['u1']
                    target_name = User.objects.filter(id__exact=target_id)[0].username
                    user_name = User.objects.filter(id__exact=user_id)[0].username
                    res = {'u1': user_name, 'u2': target_name}
                    check_following = Follower.objects.filter(u2_id__exact=pk, u1_id__exact=target_id)
                    if check_following:
                        res['if_mutual'] = True
                    else:
                        res['if_mutual'] = False
                    f_list.append(res)
                return Response(f_list)
            else:
                return Response({'resMsg': 'sType参数错误'})
        else:
            return Response({'resMsg': '请求用户或密码错误'})


@api_view(['GET'])
def get_other_follow(request, format=None):
    if request.method == 'GET':
        username = request.GET.get('username')
        check_name = request.GET.get('check_name')
        s_type = int(request.GET.get('s_type'))
        user = User.objects.filter(username__exact=username)
        checker = User.objects.filter(username__exact=check_name)
        if user:
            pk = UserSerializer(user[0]).data['id']
            pk_check = UserSerializer(checker[0]).data['id']
            # 针对自己的关注
            if s_type == TYPE_FOLLOW:
                f = Follower.objects.filter(u2_id__exact=pk).order_by('-time')
                user_name = User.objects.filter(id__exact=pk)[0].username
                f_list = []
                for item in f:
                    serializer = FollowerSerializer(item)
                    target_id = serializer.data['u1']
                    target_name = User.objects.filter(id__exact=target_id)[0].username
                    res = {'u1': target_name, 'u2': user_name}
                    if_other_fan_follow_checker = Follower.objects.filter(u1_id__exact=pk_check, u2_id__exact=target_id)
                    if_other_checker_follow_fan = Follower.objects.filter(u1_id__exact=target_id, u2_id__exact=pk_check)
                    if if_other_fan_follow_checker:
                        res['fan2checker'] = True
                    else:
                        res['fan2checker'] = False
                    if if_other_checker_follow_fan:
                        res['checker2fan'] = True
                    else:
                        res['checker2fan'] = False
                    f_list.append(res)
                return Response(f_list)
            # 针对自己的粉丝
            elif s_type == TYPE_FOLLOWING:
                f = Follower.objects.filter(u1_id__exact=pk).order_by('-time')
                f_list = []
                for item in f:
                    serializer = FollowerSerializer(item)
                    target_id = serializer.data['u2']
                    user_id = serializer.data['u1']
                    target_name = User.objects.filter(id__exact=target_id)[0].username
                    user_name = User.objects.filter(id__exact=user_id)[0].username
                    res = {'u1': user_name, 'u2': target_name}
                    if_other_fan_follow_checker = Follower.objects.filter(u1_id__exact=pk_check, u2_id__exact=target_id)
                    if_other_checker_follow_fan = Follower.objects.filter(u1_id__exact=target_id, u2_id__exact=pk_check)
                    if if_other_fan_follow_checker:
                        res['fan2checker'] = True
                    else:
                        res['fan2checker'] = False
                    if if_other_checker_follow_fan:
                        res['checker2fan'] = True
                    else:
                        res['checker2fan'] = False
                    f_list.append(res)
                return Response(f_list)
            else:
                return Response({'resMsg': 'sType参数错误'})
        else:
            return Response({'resMsg': '请求用户或密码错误'})


@api_view(['GET'])
def check_follow(request, format=None):
    # Check if username FOLLOW check_name and other relationships
    if request.method == 'GET':
        username = request.GET.get('username')
        check_name = request.GET.get('check_name')
        user = User.objects.filter(username__exact=username)
        checker = User.objects.filter(username__exact=check_name)
        if user and checker:
            pk_user = UserSerializer(user[0]).data['id']
            pk_check = UserSerializer(checker[0]).data['id']
            if_user_follow_checker = Follower.objects.filter(u1_id__exact=pk_check, u2_id__exact=pk_user)
            if_checker_follow_user = Follower.objects.filter(u1_id__exact=pk_user, u2_id__exact=pk_check)
            res = {}
            if if_user_follow_checker:
                res['user2checker'] = True
            else:
                res['user2checker'] = False
            if if_checker_follow_user:
                res['checker2user'] = True
            else:
                res['checker2user'] = False
            return Response(res)
        else:
            return Response({'resMsg': '请求用户错误'})


@api_view(['GET'])
def find_users(request, format=None):
    if request.method == 'GET':
        find_name = request.GET.get('find_name')
        finds = User.objects.filter(username__contains=find_name)
        f_list = []
        for item in finds:
            item_data = UserSerializer(item).data
            re_data = {'username': item_data['username']}
            f_list.append(re_data)
            print re_data
        if len(f_list) == 0:
            return Response({'resMsg': '没有符合条件的用户'})
        return Response(f_list)


@api_view(['POST'])
def change_follow(request, format=None):
    if request.method == 'POST':
        followForm = ChangeFollowForm(request.POST)
        if followForm.is_valid():
            username = followForm.cleaned_data['username']
            password = followForm.cleaned_data['password']
            target_user = followForm.cleaned_data['target_user']
            if username == target_user:
                return Response({'resMsg': '不能关注自己'})
            s_type = followForm.cleaned_data['s_type']
            user = User.objects.filter(username__exact=username, password__exact=password)
            target = User.objects.filter(username__exact=target_user)
            if user:
                if target:
                    serializer_user = UserSerializer(user[0])
                    pk_user = serializer_user.data['id']
                    serializer_target = UserSerializer(target[0])
                    pk_target = serializer_target.data['id']
                    # 针对自己的关注
                    if s_type == TYPE_FOLLOW:
                        # 如果有关注就取关，没有就关注，相当于异或运算
                        f = Follower.objects.filter(u1_id__exact=pk_target, u2_id__exact=pk_user)
                        if f:
                            f.delete()
                            return Response({'resMsg': '取消关注成功'})
                        else:
                            new_f = Follower(u1_id=pk_target, u2_id=pk_user)
                            new_f.save()
                            return Response({'resMsg': '关注成功'})
                    # 针对自己的粉丝
                    elif s_type == TYPE_FOLLOWING:
                        # 移除粉丝
                        f = Follower.objects.filter(u1_id__exact=pk_user, u2_id__exact=pk_target)
                        if f:
                            f.delete()
                            return Response({'resMsg': '移除成功'})
                        else:
                            return Response({'resMsg': 'TA没有关注你'})
                    else:
                        return Response({'resMsg': 'sType参数错误'})
                else:
                    return Response({'resMsg': '没有这个用户'})
            else:
                return Response({'resMsg': '请求用户或密码错误'})
        else:
            return Response({'resMsg': '获取失败'})


@api_view(['GET'])
def get_user_detail(request, format=None):
    if request.method == 'GET':
        username = request.GET.get('username')
        user = User.objects.filter(username__exact=username)
        if user:
            serializer = UserSerializer(user[0])
            pk = serializer.data['id']
            poster_num = Poster.objects.filter(name_id__exact=pk).count()
            follow_num = Follower.objects.filter(u2_id__exact=pk).count()
            following_num = Follower.objects.filter(u1_id__exact=pk).count()
            result = {}
            result['poster_num'] = poster_num
            result['follow_num'] = follow_num
            result['following_num'] = following_num
            result['resMsg'] = ''
            return Response(result)
        else:
            return Response({'resMsg': '该用户不存在'})


@api_view(['POST'])
def update_user_avatar(request, format=None):
    if request.method == 'POST':
        form = UserLoginForm(request.POST)
        if form.is_valid():
            username = form.cleaned_data['username']
            password = form.cleaned_data['password']
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
                serializer = UserSerializer(user[0])
                username = serializer.data['username']
                file_name = STATICFILES_DIRS[0] + os.path.sep + 'file' + \
                            os.path.sep + 'avatar' + os.path.sep + username + '.' + suffix
                with open(file_name, str('wb+')) as destination:
                    for chunk in photo.chunks():
                        destination.write(chunk)
                # Result
                return Response({'resMsg': '上传成功'})
            else:
                return Response({'resMsg': '请求用户密码错误'})
        else:
            return Response({'resMsg': '请求格式错误'})