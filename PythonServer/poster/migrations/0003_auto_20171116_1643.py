# -*- coding: utf-8 -*-
# Generated by Django 1.11.6 on 2017-11-16 08:43
from __future__ import unicode_literals

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('poster', '0002_poster_photo_id'),
    ]

    operations = [
        migrations.AlterField(
            model_name='poster',
            name='photo_id',
            field=models.CharField(max_length=30),
        ),
    ]
