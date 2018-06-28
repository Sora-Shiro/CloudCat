# coding=utf-8
from django import forms

P_TYPE_CHOICES = (
    (1, u'Photo'),
    (2, u'Video')
)


class PosterLoginForm(forms.Form):
    username = forms.CharField(label='用户名', max_length=120)
    password = forms.CharField(label='密码', widget=forms.PasswordInput())


class CreatePosterForm(forms.Form):
    username = forms.CharField(label='用户名', max_length=120)
    password = forms.CharField(label='密码', widget=forms.PasswordInput())
    p_type = forms.IntegerField()
    text = forms.CharField(max_length=140)


class DeletePosterForm(forms.Form):
    username = forms.CharField(label='用户名', max_length=120)
    password = forms.CharField(label='密码', widget=forms.PasswordInput())
    poster_id = forms.IntegerField()


class SendLikeForm(forms.Form):
    username = forms.CharField(label='用户名', max_length=120)
    password = forms.CharField(label='密码', max_length=128, widget=forms.PasswordInput())
    poster_id = forms.IntegerField()


class SendCommentForm(forms.Form):
    username = forms.CharField(label='用户名', max_length=120)
    password = forms.CharField(label='密码', max_length=128, widget=forms.PasswordInput())
    poster_id = forms.IntegerField()
    text = forms.CharField(max_length=140)


class SendForwardForm(forms.Form):
    username = forms.CharField(label='用户名', max_length=120)
    password = forms.CharField(label='密码', max_length=128, widget=forms.PasswordInput())
    poster_id = forms.IntegerField()
    text = forms.CharField(max_length=140)


class BeForm(forms.Form):
    username = forms.CharField(label='用户名', max_length=120)
    password = forms.CharField(label='密码', max_length=128, widget=forms.PasswordInput())


class PhotoForm(forms.Form):
    username = forms.CharField(label='用户名', max_length=120)
    password = forms.CharField(label='密码', max_length=128, widget=forms.PasswordInput())
    poster_id = forms.IntegerField()
