from django.conf.urls import url
from django.contrib import admin
from rest_framework.urlpatterns import format_suffix_patterns

import views

admin.autodiscover()

urlpatterns = [
    url(r'^index/$', views.index),
    url(r'^login/$', views.login),
    url(r'^register/$', views.register),
    url(r'^get_follow/$', views.get_follow),
    url(r'^get_other_follow/$', views.get_other_follow),
    url(r'^check_follow/$', views.check_follow),
    url(r'^find_users/$', views.find_users),
    url(r'^change_follow/$', views.change_follow),
    url(r'^get_user_detail/$', views.get_user_detail),
    url(r'^update_user_avatar/$', views.update_user_avatar),
]

urlpatterns = format_suffix_patterns(urlpatterns)
