package com.simats.rankforgeai;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class UserLoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private android.widget.ImageView ivTogglePassword;
    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_login);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        ivTogglePassword = findViewById(R.id.iv_toggle_password);
        Button btnSubmit = findViewById(R.id.btn_submit);
        android.widget.TextView tvForgotPassword = findViewById(R.id.tv_forgot_password);

        ivTogglePassword.setOnClickListener(v -> {
            if (isPasswordVisible) {
                // Hide Password
                etPassword.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
                ivTogglePassword.setImageResource(R.drawable.ic_eye_open);
                ivTogglePassword.setAlpha(0.6f);
            } else {
                // Show Password
                etPassword.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                ivTogglePassword.setImageResource(R.drawable.ic_eye_closed);
                ivTogglePassword.setAlpha(1.0f);
            }
            etPassword.setSelection(etPassword.length());
            isPasswordVisible = !isPasswordVisible;
        });

        tvForgotPassword.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            Intent intent = new Intent(UserLoginActivity.this, OtpVerificationActivity.class);
            if (!TextUtils.isEmpty(email)) {
                intent.putExtra("USER_EMAIL", email);
            }
            startActivity(intent);
        });

        android.widget.TextView tvSignup = findViewById(R.id.tv_signup);
        tvSignup.setOnClickListener(v -> {
            Intent intent = new Intent(UserLoginActivity.this, UserSignupActivity.class);
            startActivity(intent);
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                
                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                    Toast.makeText(UserLoginActivity.this, "Please enter email and password", Toast.LENGTH_SHORT).show();
                    return;
                }

                btnSubmit.setEnabled(false);
                btnSubmit.setText("HOLD...");

                com.simats.rankforgeai.core.network.internal.api.ApiService apiService = com.simats.rankforgeai.core.network.internal.api.ApiClient.getClient().create(com.simats.rankforgeai.core.network.internal.api.ApiService.class);
                com.simats.rankforgeai.models.LoginRequest request = new com.simats.rankforgeai.models.LoginRequest(email, password);

                apiService.loginUser(request).enqueue(new retrofit2.Callback<com.simats.rankforgeai.models.AuthResponse>() {
                    @Override
                    public void onResponse(retrofit2.Call<com.simats.rankforgeai.models.AuthResponse> call, retrofit2.Response<com.simats.rankforgeai.models.AuthResponse> response) {
                        btnSubmit.setEnabled(true);
                        btnSubmit.setText("Submit");
                        if (response.isSuccessful() && response.body() != null) {
                            Toast.makeText(UserLoginActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();
                            boolean hasGoal = response.body().getUser() != null && response.body().getUser().getTargetExam() != null && !response.body().getUser().getTargetExam().isEmpty();
                            boolean hasName = response.body().getUser() != null && response.body().getUser().getFullName() != null && !response.body().getUser().getFullName().isEmpty();
                            
                            // Save login state and JWT Tokens
                            android.content.SharedPreferences prefs = getSharedPreferences("RankForgePrefs", MODE_PRIVATE);
                            prefs.edit()
                                 .putBoolean("isLoggedIn", true)
                                 .putBoolean("hasSelectedGoal", hasGoal)
                                 .putBoolean("isPremium", response.body().getUser().isPremium())
                                 .putString("accessToken", response.body().getAccess())
                                 .putString("refreshToken", response.body().getRefresh())
                                 .putString("userEmail", email)
                                 .putString("userName", hasName ? response.body().getUser().getFullName() : null)
                                 .apply();

                            Intent intent;
                            boolean isPremium = response.body().getUser() != null && response.body().getUser().isPremium();

                            if (hasGoal && hasName) {
                                // Full profile registered - Bypass Setup completely
                                intent = new Intent(UserLoginActivity.this, HomeActivity.class);
                            } else if (isPremium || (hasGoal && !hasName)) {
                                // Premium users or users with a goal but no name - Send to Home 
                                // We show the popup only if name is missing AND it's not a premium bypass
                                intent = new Intent(UserLoginActivity.this, HomeActivity.class);
                                if (!hasName && !isPremium) {
                                    intent.putExtra("SHOW_PROFILE_POPUP", true);
                                }
                            } else {
                                // Incomplete registration - Force Goal Selection Flow
                                intent = new Intent(UserLoginActivity.this, SelectGoalActivity.class);
                            }
                            startActivity(intent);
                            finish();
                        } else {
                            String errorMessage = "Login failed";
                            try {
                                if (response.errorBody() != null) {
                                    String errorJson = response.errorBody().string();
                                    if (errorJson.contains("error")) {
                                        // Simple extraction of error message from JSON
                                        errorMessage = errorJson.split("\"error\":\"")[1].split("\"")[0];
                                    } else {
                                        // Show truncated HTML error (helpful for debugging 500s)
                                        errorMessage = "500 Err: " + (errorJson.length() > 100 ? errorJson.substring(0, 100) : errorJson);
                                    }
                                } else {
                                    errorMessage = "Status: " + response.code();
                                }
                            } catch (Exception e) {
                                errorMessage = "Network Error (" + response.code() + ")";
                            }
                            Toast.makeText(UserLoginActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(retrofit2.Call<com.simats.rankforgeai.models.AuthResponse> call, Throwable t) {
                        btnSubmit.setEnabled(true);
                        btnSubmit.setText("Submit");
                        Toast.makeText(UserLoginActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
