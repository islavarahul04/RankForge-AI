from rest_framework import views, viewsets, status
from rest_framework.response import Response
from rest_framework.permissions import IsAuthenticated, AllowAny

from ..models import ExamCategory, StudySubject, StudyTopic, UserTopicProgress
from ..serializers import ExamCategorySerializer, StudySubjectSerializer, StudyTopicSerializer
from .permissions import IsPlatformAdmin

class ExamCategoryListView(views.APIView):
    permission_classes = [AllowAny]

    def get(self, request):
        categories = ExamCategory.objects.all().order_by('order')
        serializer = ExamCategorySerializer(categories, many=True)
        return Response(serializer.data)

class StudySubjectListView(views.APIView):
    permission_classes = [IsAuthenticated]

    def get(self, request):
        subjects = StudySubject.objects.all().order_by('order')
        serializer = StudySubjectSerializer(subjects, many=True, context={'request': request})
        return Response(serializer.data)

class TopicProgressUpdateView(views.APIView):
    permission_classes = [IsAuthenticated]

    def post(self, request):
        topic_id = request.data.get('topic_id')
        is_completed = request.data.get('is_completed', False)

        if not topic_id:
            return Response({"error": "topic_id is required"}, status=400)

        try:
            topic = StudyTopic.objects.get(id=topic_id)
        except StudyTopic.DoesNotExist:
            return Response({"error": "Topic not found"}, status=404)

        progress, created = UserTopicProgress.objects.get_or_create(user=request.user, topic=topic)
        progress.is_completed = is_completed
        progress.save()

        return Response({"message": "Progress updated successfully", "is_completed": progress.is_completed})

class AdminExamCategoryViewSet(viewsets.ModelViewSet):
    permission_classes = [IsPlatformAdmin]
    queryset = ExamCategory.objects.all().order_by('order')
    serializer_class = ExamCategorySerializer

class AdminStudySubjectViewSet(viewsets.ModelViewSet):
    permission_classes = [IsPlatformAdmin]
    queryset = StudySubject.objects.all().order_by('order')
    serializer_class = StudySubjectSerializer

    def get_queryset(self):
        category_id = self.request.query_params.get('category_id')
        if category_id:
            return self.queryset.filter(category_id=category_id)
        return self.queryset

class AdminStudyTopicViewSet(viewsets.ModelViewSet):
    permission_classes = [IsPlatformAdmin]
    queryset = StudyTopic.objects.all().order_by('order')
    serializer_class = StudyTopicSerializer

    def get_queryset(self):
        subject_id = self.request.query_params.get('subject_id')
        if subject_id:
            return self.queryset.filter(subject_id=subject_id)
        return self.queryset
