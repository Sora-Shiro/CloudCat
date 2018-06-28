# coding=utf-8
from __future__ import unicode_literals

from django.contrib import admin
from django.db import models
# Create your models here.
from django.utils import timezone

P_TYPE_CHOICES = (
    (u'P', u'Photo'),
    (u'V', u'Video'),
    (u'F', u'Forward')
)


class Poster(models.Model):
    name = models.ForeignKey('normalusers.User', related_name='poster', on_delete=models.CASCADE)
    # p_type = models.IntegerField(max_length=2, choices=P_TYPE_CHOICES)
    p_type = models.IntegerField()
    # p_video = models.
    text = models.CharField(max_length=140)
    forward_num = models.IntegerField(default=0)
    comment_num = models.IntegerField(default=0)
    like_num = models.IntegerField(default=0)
    collect_num = models.IntegerField(default=0)
    time = models.DateTimeField(u'保存日期', default=timezone.now)
    origin_poster_id = models.IntegerField(default=0)
    photo_id = models.CharField(max_length=30)


class PosterAdmin(admin.ModelAdmin):
    list_display = ('name', 'p_type', 'text', 'time')


class LikeP(models.Model):
    poster = models.ForeignKey('poster.Poster', related_name='like_p', on_delete=models.CASCADE)
    author = models.ForeignKey('normalusers.User', related_name='author_l_name', on_delete=models.CASCADE)
    name = models.ForeignKey('normalusers.User', related_name='like_name', on_delete=models.CASCADE)
    time = models.DateTimeField(u'保存日期', default=timezone.now)


class CommentP(models.Model):
    poster = models.ForeignKey('poster.Poster', related_name='comment_p', on_delete=models.CASCADE)
    author = models.ForeignKey('normalusers.User', related_name='author_c_name', on_delete=models.CASCADE)
    name = models.ForeignKey('normalusers.User', related_name='comment_name', on_delete=models.CASCADE)
    text = models.CharField(max_length=140)
    time = models.DateTimeField(u'保存日期', default=timezone.now)


class ForwardP(models.Model):
    # 原动态 id
    poster = models.ForeignKey('poster.Poster', related_name='forward_p', on_delete=models.CASCADE)
    # 转发动态 id
    f_poster = models.ForeignKey('poster.Poster', related_name='forward_f_p', on_delete=models.CASCADE)
    author = models.ForeignKey('normalusers.User', related_name='author_f_name', on_delete=models.CASCADE)
    name = models.ForeignKey('normalusers.User', related_name='forward_name', on_delete=models.CASCADE)
    text = models.CharField(max_length=140)
    time = models.DateTimeField(u'保存日期', default=timezone.now)


class CommentC(models.Model):
    originComment = models.ForeignKey('poster.CommentP', related_name='comment_c', on_delete=models.CASCADE)
    author = models.ForeignKey('normalusers.User', related_name='author_cc_name', on_delete=models.CASCADE)
    name = models.ForeignKey('normalusers.User', related_name='comment_c_name', on_delete=models.CASCADE)
    text = models.CharField(max_length=140)
    time = models.DateTimeField(u'保存日期', default=timezone.now)


admin.site.register(Poster, PosterAdmin)
