# coding=utf-8
from django import forms


class UserRegisterForm(forms.Form):
    username = forms.CharField(label='用户名', max_length=120)
    password = forms.CharField(label='密码', max_length=128, widget=forms.PasswordInput())
    email = forms.EmailField(label='邮箱')


class UserLoginForm(forms.Form):
    username = forms.CharField(label='用户名', max_length=120)
    password = forms.CharField(label='密码', max_length=128, widget=forms.PasswordInput())


class UserForm(forms.Form):
    username = forms.CharField(label='用户名', max_length=120)


TYPE_FOLLOW = 1
TYPE_FOLLOWING = 2


class GetFollowForm(forms.Form):
    username = forms.CharField(label='用户名', max_length=120)
    # 1: 查询该用户的关注  2: 查询该用户的被关注
    s_type = forms.IntegerField(label='查询类型')


# 查询是否有关注/被关注记录，如果有删除，如果没有添加
class ChangeFollowForm(forms.Form):
    username = forms.CharField(label='用户名', max_length=120)
    password = forms.CharField(label='密码', max_length=128, widget=forms.PasswordInput())
    target_user = forms.CharField(label='用户名', max_length=120)
    s_type = forms.IntegerField(label='查询类型')



