from rest_framework import views, status
from rest_framework.response import Response
from rest_framework.permissions import IsAuthenticated, AllowAny
from django.utils import timezone
from datetime import timedelta

from ..models import SubscriptionPlan
from ..serializers import SubscriptionPlanSerializer, UserSerializer

class SubscriptionPlanListView(views.APIView):
    permission_classes = [AllowAny]

    def get(self, request):
        plans = SubscriptionPlan.objects.filter(is_active=True).order_by('price')
        serializer = SubscriptionPlanSerializer(plans, many=True)
        return Response(serializer.data)

class SubscribeView(views.APIView):
    permission_classes = [IsAuthenticated]

    def post(self, request):
        plan_id = request.data.get('plan_id')
        if not plan_id:
            return Response({"error": "plan_id is required"}, status=status.HTTP_400_BAD_REQUEST)
        try:
            plan = SubscriptionPlan.objects.get(id=plan_id, is_active=True)
            user = request.user
            user.is_premium = True
            user.premium_plan = plan.name
            user.premium_expiry = timezone.now() + timedelta(days=plan.duration_days)
            user.save()
            return Response({"message": f"Successfully subscribed to {plan.name} plan!", "user": UserSerializer(user).data})
        except SubscriptionPlan.DoesNotExist:
            return Response({"error": "Invalid or inactive subscription plan"}, status=status.HTTP_400_BAD_REQUEST)
