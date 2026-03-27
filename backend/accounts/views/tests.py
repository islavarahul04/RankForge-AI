from rest_framework import views, status
from rest_framework.response import Response
from rest_framework.permissions import IsAuthenticated

from ..models import MockTest, UserMockTestResult
from ..serializers import MockTestSerializer, MockTestListSerializer, UserMockTestResultSerializer

class MockTestListView(views.APIView):
    permission_classes = [IsAuthenticated]

    def get(self, request):
        tests = MockTest.objects.all().order_by('-id')
        serializer = MockTestListSerializer(tests, many=True, context={'request': request})
        return Response(serializer.data)

class MockTestDetailView(views.APIView):
    permission_classes = [IsAuthenticated]

    def get(self, request, test_id):
        try:
            test = MockTest.objects.prefetch_related('questions').get(id=test_id)
            serializer = MockTestSerializer(test, context={'request': request})
            return Response(serializer.data)
        except MockTest.DoesNotExist:
            return Response({"error": "Test not found"}, status=status.HTTP_404_NOT_FOUND)

class SubmitMockTestResultView(views.APIView):
    permission_classes = [IsAuthenticated]

    def post(self, request):
        test_id = request.data.get('test_id')
        score = request.data.get('score', 0)

        total_questions = request.data.get('total_questions', 100)
        correct_count = request.data.get('correct_count', 0)
        incorrect_count = request.data.get('incorrect_count', 0)
        eng_score = request.data.get('eng_score', 0)
        quant_score = request.data.get('quant_score', 0)
        reason_score = request.data.get('reason_score', 0)
        gk_score = request.data.get('gk_score', 0)
        selected_answers = request.data.get('selected_answers', [])

        if not test_id:
            return Response({"error": "test_id is required"}, status=status.HTTP_400_BAD_REQUEST)

        try:
            test_obj = MockTest.objects.get(id=test_id)
        except MockTest.DoesNotExist:
            return Response({"error": "MockTest not found"}, status=status.HTTP_404_NOT_FOUND)

        result = UserMockTestResult.objects.create(
            user=request.user,
            test=test_obj,
            score=score,
            total_questions=total_questions,
            correct_count=correct_count,
            incorrect_count=incorrect_count,
            eng_score=eng_score,
            quant_score=quant_score,
            reason_score=reason_score,
            gk_score=gk_score,
            selected_answers=selected_answers,
            is_completed=True
        )

        serializer = UserMockTestResultSerializer(result)
        return Response(serializer.data)

class TestHistoryView(views.APIView):
    permission_classes = [IsAuthenticated]

    def get(self, request):
        results = UserMockTestResult.objects.filter(user=request.user).order_by('-created_at')
        serializer = UserMockTestResultSerializer(results, many=True)
        return Response(serializer.data)
