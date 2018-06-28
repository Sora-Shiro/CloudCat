from django.conf.urls import url
from django.contrib import admin
from rest_framework.urlpatterns import format_suffix_patterns

from poster import views

admin.autodiscover()

urlpatterns = [
    url(r'^home/$', views.home),
    url(r'^poster_list/$', views.poster_list),
    url(r'^get_poster_by_id/$', views.get_poster_by_id),
    url(r'^create_poster/$', views.create_poster),
    url(r'^create_poster_with_photo/$', views.create_poster_with_photo),
    url(r'^get_comments/$', views.get_comments),
    url(r'^send_comment/$', views.send_comment),
    url(r'^get_forwards/$', views.get_forwards),
    url(r'^send_forward/$', views.send_forward),
    url(r'^get_likes/$', views.get_likes),
    url(r'^send_like/$', views.send_like),
    url(r'^get_be_ated_list/$', views.get_be_ated_list),
    url(r'^get_be_commented_list/$', views.get_be_commented_list),
    url(r'^get_be_liked_list/$', views.get_be_liked_list),
]

urlpatterns = format_suffix_patterns(urlpatterns)
