from rest_framework import views, status
from rest_framework.response import Response
from rest_framework.permissions import IsAuthenticated
from datetime import date, timedelta

from ..models import Notification, DailyCheckIn
from ..serializers import NotificationSerializer

class NotificationListView(views.APIView):
    permission_classes = [IsAuthenticated]

    def get(self, request):
        notifications = Notification.objects.filter(user=request.user).order_by('-created_at')
        serializer = NotificationSerializer(notifications, many=True)
        return Response(serializer.data)

    def put(self, request):
        Notification.objects.filter(user=request.user, is_read=False).update(is_read=True)
        return Response({"message": "All notifications marked as read."})

class StreakView(views.APIView):
    permission_classes = [IsAuthenticated]

    def post(self, request):
        today = date.today()
        created = False
        if not DailyCheckIn.objects.filter(user=request.user, date=today).exists():
            DailyCheckIn.objects.create(user=request.user, date=today)
            created = True
        return Response({"message": "Checked in successfully", "new_check_in": created})

    def get(self, request):
        checkins = DailyCheckIn.objects.filter(user=request.user).order_by('date')
        dates = [c.date for c in checkins]
        current_streak = 0
        today = date.today()
        if dates and (dates[-1] == today or dates[-1] == today - timedelta(days=1)):
            current_date, current_streak = dates[-1], 1
            idx = len(dates) - 2
            while idx >= 0:
                if dates[idx] == current_date - timedelta(days=1):
                    current_streak += 1
                    current_date = dates[idx]
                    idx -= 1
                else: break
        longest_streak = 0
        if dates:
            temp_longest = 1
            for i in range(1, len(dates)):
                if dates[i] == dates[i-1] + timedelta(days=1): temp_longest += 1
                else:
                    longest_streak = max(longest_streak, temp_longest)
                    temp_longest = 1
            longest_streak = max(longest_streak, temp_longest)
        return Response({"current_streak": current_streak, "longest_streak": longest_streak, "checked_in_dates": [d.strftime('%Y-%m-%d') for d in dates]})
