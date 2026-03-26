package com.simats.rankforgeai.core.network.internal.api;

import com.simats.rankforgeai.models.AdminDashboardStats;
import com.simats.rankforgeai.models.AdminMockTestReport;
import com.simats.rankforgeai.models.AdminNotificationRequest;
import com.simats.rankforgeai.models.AdminTestDetailsResponse;
import com.simats.rankforgeai.models.AdminUser;
import com.simats.rankforgeai.models.AiChatModels;
import com.simats.rankforgeai.models.AuthResponse;
import com.simats.rankforgeai.models.ChangePasswordRequest;
import com.simats.rankforgeai.models.ExamCategory;
import com.simats.rankforgeai.models.ForgotPasswordRequest;
import com.simats.rankforgeai.models.LoginRequest;
import com.simats.rankforgeai.models.MessageResponse;
import com.simats.rankforgeai.models.MockTest;
import com.simats.rankforgeai.models.Notification;
import com.simats.rankforgeai.models.OtpVerificationRequest;
import com.simats.rankforgeai.models.RegisterRequest;
import com.simats.rankforgeai.models.ResetPasswordRequest;
import com.simats.rankforgeai.models.StreakResponse;
import com.simats.rankforgeai.models.StudySubject;
import com.simats.rankforgeai.models.SubmitMockTestRequest;
import com.simats.rankforgeai.models.SubmitMockTestResponse;
import com.simats.rankforgeai.models.SubscribeRequest;
import com.simats.rankforgeai.models.SupportTicketRequest;
import com.simats.rankforgeai.models.TestHistoryResult;
import com.simats.rankforgeai.models.UpdateProgressRequest;
import com.simats.rankforgeai.models.UserProfile;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ApiService {

    // --- Authentication ---
    @POST("auth/register/")
    Call<AuthResponse> registerUser(@Body RegisterRequest request);

    @POST("auth/login/")
    Call<AuthResponse> loginUser(@Body LoginRequest request);

    @POST("auth/admin-login/")
    Call<AuthResponse> adminLoginUser(@Body LoginRequest request);

    @POST("auth/forgot-password/")
    Call<MessageResponse> forgotPassword(@Body ForgotPasswordRequest request);

    @POST("auth/verify-otp/")
    Call<AuthResponse> verifyOtp(@Body OtpVerificationRequest request);

    @POST("auth/reset-password/")
    Call<MessageResponse> resetPassword(@Header("Authorization") String token, @Body ResetPasswordRequest request);

    // --- Profile & Account ---
    @GET("profile/")
    Call<UserProfile> getProfile(@Header("Authorization") String token);

    @PUT("profile/")
    Call<UserProfile> updateProfile(@Header("Authorization") String token, @Body UserProfile profile);

    @PUT("profile/")
    Call<UserProfile> updateProfilePartial(@Header("Authorization") String token, @Body java.util.Map<String, Object> fields);

    @Multipart
    @PUT("profile/")
    Call<UserProfile> updateProfileMultipart(
            @Header("Authorization") String token,
            @Part MultipartBody.Part profile_picture,
            @Part("full_name") RequestBody full_name,
            @Part("phone_number") RequestBody phone_number,
            @Part("dob") RequestBody dob,
            @Part("city") RequestBody city,
            @Part("gender") RequestBody gender,
            @Part("target_exam") RequestBody target_exam
    );

    @POST("profile/password/")
    Call<MessageResponse> changePassword(@Header("Authorization") String token, @Body ChangePasswordRequest request);

    @DELETE("profile/")
    Call<Void> deleteAccount(@Header("Authorization") String token);

    // --- AI Chat ---
    @GET("ai/chat/")
    Call<List<AiChatModels.ChatMessage>> getChatHistory(@Header("Authorization") String token);

    @POST("ai/chat/")
    Call<AiChatModels.ChatMessage> sendChatMessage(@Header("Authorization") String token, @Body AiChatModels.ChatRequest request);

    // --- Dashboard & Gamification ---
    @GET("streak/")
    Call<StreakResponse> getStreakData(@Header("Authorization") String token);

    @POST("streak/")
    Call<Void> checkIn(@Header("Authorization") String token);

    @GET("notifications/")
    Call<List<Notification>> getNotifications(@Header("Authorization") String token);

    @PUT("notifications/") // Mapped to markNotificationsRead based on PUT request in NotificationsActivity.java line 158
    Call<Void> markNotificationsRead(@Header("Authorization") String token);

    // --- Exams & Tests ---
    @GET("exams/categories/")
    Call<List<ExamCategory>> getExamCategories();

    @GET("tests/")
    Call<List<MockTest>> getMockTests(@Header("Authorization") String token);

    @POST("tests/submit/")
    Call<SubmitMockTestResponse> submitMockTest(@Header("Authorization") String token, @Body SubmitMockTestRequest request);

    @GET("tests/history/")
    Call<List<TestHistoryResult>> getTestHistory(@Header("Authorization") String token);

    // --- Study Content ---
    @GET("study/subjects/")
    Call<List<StudySubject>> getStudySubjects(@Header("Authorization") String token);

    @POST("study/progress/")
    Call<MessageResponse> updateTopicProgress(@Header("Authorization") String token, @Body UpdateProgressRequest request);

    // --- Subscriptions & Support ---
    @GET("subscriptions/")
    Call<List<com.simats.rankforgeai.models.SubscriptionPlan>> getSubscriptionPlans();

    @POST("user/subscribe/")
    Call<MessageResponse> subscribePlan(@Header("Authorization") String token, @Body SubscribeRequest request);

    @GET("support/")
    Call<List<SupportTicketRequest>> getSupportTickets(@Header("Authorization") String token);

    @POST("support/")
    Call<MessageResponse> createSupportTicket(@Header("Authorization") String token, @Body SupportTicketRequest request);

    @GET("admin/support/manage/")
    Call<List<com.simats.rankforgeai.models.AdminSupportTicket>> getAdminSupportTickets(@Header("Authorization") String token);

    @POST("admin/support/manage/")
    Call<MessageResponse> replyToSupportTicket(@Header("Authorization") String token, @Body java.util.Map<String, Object> body);

    // --- Admin Functions ---
    @GET("admin/dashboard-stats/")
    Call<AdminDashboardStats> getAdminDashboardStats(@Header("Authorization") String token);

    @GET("admin/users/")
    Call<List<AdminUser>> getAdminUsers(@Header("Authorization") String token);

    @GET("admin/reports/mock-tests/")
    Call<List<AdminMockTestReport>> getAdminMockTestReports(@Header("Authorization") String token);

    @GET("admin/reports/test-details/{test_id}/")
    Call<AdminTestDetailsResponse> getAdminTestResultsDetail(@Header("Authorization") String token, @Path("test_id") int testId);

    @POST("admin/notifications/create/")
    Call<MessageResponse> createAdminNotification(@Header("Authorization") String token, @Body AdminNotificationRequest request);

    @POST("admin/users/unlock-test/")
    Call<MessageResponse> grantUserTestAccess(@Header("Authorization") String token, @Body java.util.Map<String, Object> body);

    @POST("admin/tests/create/")
    Call<MessageResponse> adminCreateMockTest(@Header("Authorization") String token, @Body com.simats.rankforgeai.models.CreateMockTestRequest request);

    @GET("tests/{test_id}/")
    Call<MockTest> getMockTestDetail(@Header("Authorization") String token, @Path("test_id") int testId);

    @GET("admin/tests/list/")
    Call<com.simats.rankforgeai.models.AdminMockTestListResponse> getAdminMockTests(@Header("Authorization") String token);

    @retrofit2.http.PATCH("admin/tests/list/")
    Call<MessageResponse> updateMockTestStatus(@Header("Authorization") String token, @Body java.util.Map<String, Object> body);

    @GET("pyq-papers/")
    Call<List<com.simats.rankforgeai.models.PYQPaper>> getPYQPapers(@Header("Authorization") String token);

    @Multipart
    @POST("pyq-papers/")
    Call<com.simats.rankforgeai.models.PYQPaper> uploadPYQ(
        @Header("Authorization") String token,
        @Part MultipartBody.Part file,
        @Part("title") RequestBody title,
        @Part("year") RequestBody year,
        @Part("exam_category") RequestBody category
    );
    @GET
    @retrofit2.http.Streaming
    Call<okhttp3.ResponseBody> downloadFile(@retrofit2.http.Url String fileUrl);
    @DELETE("pyq-papers/{id}/")
    Call<Void> deletePYQPaper(@retrofit2.http.Path("id") int id);

    @POST("admin/subscriptions/")
    Call<com.simats.rankforgeai.models.SubscriptionPlan> createSubscriptionPlan(@Header("Authorization") String token, @Body com.simats.rankforgeai.models.SubscriptionPlan plan);

    @PUT("admin/subscriptions/{id}/")
    Call<com.simats.rankforgeai.models.SubscriptionPlan> updateSubscriptionPlan(@Header("Authorization") String token, @Path("id") int id, @Body com.simats.rankforgeai.models.SubscriptionPlan plan);

    @DELETE("admin/subscriptions/{id}/")
    Call<Void> deleteSubscriptionPlan(@Header("Authorization") String token, @Path("id") int id);

    // --- Admin Study Management ---
    @GET("admin/study/exams/")
    Call<List<ExamCategory>> getAdminStudyExams(@Header("Authorization") String token);

    @retrofit2.http.PATCH("admin/study/exams/{id}/")
    Call<ExamCategory> updateAdminStudyExam(@Header("Authorization") String token, @Path("id") int id, @Body java.util.Map<String, Object> body);

    @GET("admin/study/subjects/")
    Call<List<StudySubject>> getAdminStudySubjects(@Header("Authorization") String token, @retrofit2.http.Query("category_id") Integer categoryId);

    @POST("admin/study/subjects/")
    Call<StudySubject> adminCreateSubject(@Header("Authorization") String token, @Body StudySubject subject);

    @PUT("admin/study/subjects/{id}/")
    Call<StudySubject> adminUpdateSubject(@Header("Authorization") String token, @Path("id") int id, @Body StudySubject subject);

    @DELETE("admin/study/subjects/{id}/")
    Call<Void> adminDeleteSubject(@Header("Authorization") String token, @Path("id") int id);

    @GET("admin/study/topics/")
    Call<List<com.simats.rankforgeai.models.StudyTopic>> getAdminStudyTopics(@Header("Authorization") String token, @retrofit2.http.Query("subject_id") Integer subjectId);

    @POST("admin/study/topics/")
    Call<com.simats.rankforgeai.models.StudyTopic> adminCreateTopic(@Header("Authorization") String token, @Body com.simats.rankforgeai.models.StudyTopic topic);

    @PUT("admin/study/topics/{id}/")
    Call<com.simats.rankforgeai.models.StudyTopic> adminUpdateTopic(@Header("Authorization") String token, @Path("id") int id, @Body com.simats.rankforgeai.models.StudyTopic topic);

    @DELETE("admin/study/topics/{id}/")
    Call<Void> adminDeleteTopic(@Header("Authorization") String token, @Path("id") int id);
}
