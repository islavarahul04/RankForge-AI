package com.simats.rankforgeai;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.simats.rankforgeai.core.network.internal.api.ApiClient;
import com.simats.rankforgeai.core.network.internal.api.ApiService;
import com.simats.rankforgeai.models.AuthResponse;
import com.simats.rankforgeai.models.RegisterRequest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserSignupActivity extends AppCompatActivity {

    private EditText etEmail, etPassword, etConfirmPassword;
    private Button btnSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_signup);

        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        btnSignup = findViewById(R.id.btn_signup);

        setupPasswordValidation();

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                String confirmPassword = etConfirmPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
                    Toast.makeText(UserSignupActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!password.equals(confirmPassword)) {
                    Toast.makeText(UserSignupActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!isValidPassword(password)) {
                    Toast.makeText(UserSignupActivity.this, "Password must be at least 8 characters and contain at least one number and one special character", Toast.LENGTH_LONG).show();
                    return;
                }

                btnSignup.setEnabled(false);
                btnSignup.setText("Loading...");

                ApiService apiService = ApiClient.getClient().create(ApiService.class);
                // Passing empty full name as requested by UI design
                RegisterRequest request = new RegisterRequest(email, password, "");

                apiService.registerUser(request).enqueue(new Callback<AuthResponse>() {
                    @Override
                    public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                        btnSignup.setEnabled(true);
                        btnSignup.setText("Sign Up");

                        if (response.isSuccessful() && response.body() != null) {
                            Toast.makeText(UserSignupActivity.this, "Signup Successful! Logging in...", Toast.LENGTH_SHORT).show();
                            
                            // Save login state and JWT Tokens
                            android.content.SharedPreferences prefs = getSharedPreferences("RankForgePrefs", MODE_PRIVATE);
                            prefs.edit()
                                 .putBoolean("isLoggedIn", true)
                                 .putString("accessToken", response.body().getAccess())
                                 .putString("refreshToken", response.body().getRefresh())
                                 .putString("userEmail", email)
                                 .putString("userName", email.split("@")[0]) // Initial fallback for signup
                                 .apply();

                            // Redirect to Goal Selection or Login Provider as appropriate
                            Intent intent = new Intent(UserSignupActivity.this, SelectGoalActivity.class);
                            startActivity(intent);
                            finishAffinity(); // Clear stack
                        } else {
                            String errorMessage = "Signup failed (Code: " + response.code() + ")";
                            try {
                                if (response.errorBody() != null) {
                                    String errorJson = response.errorBody().string();
                                    if (errorJson.contains("email")) {
                                        errorMessage = "Email already exists! Please use a different email.";
                                    } else if (errorJson.contains("password")) {
                                        errorMessage = "Password too weak! Needs 8+ chars, Number, Special & Upper case.";
                                    } else if (errorJson.contains("error")) {
                                        // Handle structured error { "error": "message" }
                                        errorMessage = errorJson;
                                    } else {
                                        errorMessage += ": " + errorJson;
                                    }
                                }
                            } catch (Exception e) {
                                errorMessage += " Parse error.";
                            }
                            Toast.makeText(UserSignupActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<AuthResponse> call, Throwable t) {
                        btnSignup.setEnabled(true);
                        btnSignup.setText("Sign Up");
                        Toast.makeText(UserSignupActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private boolean isValidPassword(String password) {
        if (password.length() < 8) return false;
        boolean hasDigit = false;
        boolean hasSpecial = false;
        boolean hasUpper = false;
        String specialChars = "!@#$%^&*(),.?\":{}|<>";
        
        for (char c : password.toCharArray()) {
            if (Character.isDigit(c)) hasDigit = true;
            if (Character.isUpperCase(c)) hasUpper = true;
            if (specialChars.contains(String.valueOf(c))) hasSpecial = true;
        }
        return hasDigit && hasSpecial && hasUpper;
    }

    private void setupPasswordValidation() {
        android.widget.ImageView ivReqLength = findViewById(R.id.iv_req_length);
        android.widget.TextView tvReqLength = findViewById(R.id.tv_req_length);
        android.widget.ImageView ivReqNumber = findViewById(R.id.iv_req_number);
        android.widget.TextView tvReqNumber = findViewById(R.id.tv_req_number);
        android.widget.ImageView ivReqSpecial = findViewById(R.id.iv_req_special);
        android.widget.TextView tvReqSpecial = findViewById(R.id.tv_req_special);
        android.widget.ImageView ivReqUpper = findViewById(R.id.iv_req_upper);
        android.widget.TextView tvReqUpper = findViewById(R.id.tv_req_upper);

        etPassword.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String password = s.toString();
                
                // Length check
                updateRequirementUI(password.length() >= 8, ivReqLength, tvReqLength);
                
                // Number check
                boolean hasDigit = false;
                for (char c : password.toCharArray()) { if (Character.isDigit(c)) { hasDigit = true; break; } }
                updateRequirementUI(hasDigit, ivReqNumber, tvReqNumber);
                
                // Special check
                boolean hasSpecial = false;
                String specialChars = "!@#$%^&*(),.?\":{}|<>";
                for (char c : password.toCharArray()) { if (specialChars.contains(String.valueOf(c))) { hasSpecial = true; break; } }
                updateRequirementUI(hasSpecial, ivReqSpecial, tvReqSpecial);
                
                // Upper check
                boolean hasUpper = false;
                for (char c : password.toCharArray()) { if (Character.isUpperCase(c)) { hasUpper = true; break; } }
                updateRequirementUI(hasUpper, ivReqUpper, tvReqUpper);
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });
    }

    private void updateRequirementUI(boolean val, android.widget.ImageView iv, android.widget.TextView tv) {
        if (val) {
            iv.setImageResource(R.drawable.ic_check_tick);
            iv.setColorFilter(android.graphics.Color.parseColor("#4CAF50"));
            tv.setTextColor(android.graphics.Color.parseColor("#4CAF50"));
        } else {
            iv.setImageResource(R.drawable.ic_close_x);
            iv.setColorFilter(android.graphics.Color.parseColor("#FF4444"));
            tv.setTextColor(android.graphics.Color.parseColor("#FF4444"));
        }
    }
}
