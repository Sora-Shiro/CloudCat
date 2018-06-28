# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.contrib import admin
from django.db import models
# Create your models here.
from django.utils import timezone


class User(models.Model):
    username = models.CharField(max_length=15)
    password = models.CharField(max_length=128)
    email = models.EmailField()


class UserAdmin(admin.ModelAdmin):
    list_display = ('username', 'password', 'email')


class Follower(models.Model):
    u1 = models.ForeignKey('normalusers.User', related_name='u1', on_delete=models.CASCADE)
    u2 = models.ForeignKey('normalusers.User', related_name='u2', on_delete=models.CASCADE)
    time = models.DateTimeField(u'保存日期', default=timezone.now)


class FollowerAdmin(admin.ModelAdmin):
    list_display = ('u1', 'u2')


admin.site.register(User, UserAdmin)
admin.site.register(Follower, FollowerAdmin)
