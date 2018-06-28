from rest_framework import serializers

from poster.models import Poster, LikeP, CommentP, ForwardP, CommentC


class PosterSerializer(serializers.ModelSerializer):
    class Meta:
        model = Poster
        fields = '__all__'


class LikePSerializer(serializers.ModelSerializer):
    class Meta:
        model = LikeP
        fields = '__all__'


class CommentPSerializer(serializers.ModelSerializer):
    class Meta:
        model = CommentP
        fields = '__all__'


class ForwardPSerializer(serializers.ModelSerializer):
    class Meta:
        model = ForwardP
        fields = '__all__'


class CommentCSerializer(serializers.ModelSerializer):
    class Meta:
        model = CommentC
        fields = '__all__'

