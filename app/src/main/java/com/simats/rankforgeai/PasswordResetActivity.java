package com.simats.rankforgeai;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class PasswordResetActivity extends AppCompatActivity {

    private EditText etNewPassword, etConfirmPassword;
    private ImageView ivTogglePassword, ivToggleConfirmPassword;
    private boolean isPasswordVisible = false;
    private boolean isConfirmPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
                
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_password_reset);
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etNewPassword = findViewById(R.id.et_new_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        ivTogglePassword = findViewById(R.id.iv_toggle_password);
        ivToggleConfirmPassword = findViewById(R.id.iv_toggle_confirm_password);
        Button btnSave = findViewById(R.id.btn_save);

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        ivTogglePassword.setOnClickListener(v -> {
            if (isPasswordVisible) {
                etNewPassword.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
                ivTogglePassword.setColorFilter(android.graphics.Color.parseColor("#FFFFFF"));
            } else {
                etNewPassword.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                ivTogglePassword.setColorFilter(android.graphics.Color.parseColor("#FF8C00"));
            }
            etNewPassword.setSelection(etNewPassword.length());
            isPasswordVisible = !isPasswordVisible;
        });

        ivToggleConfirmPassword.setOnClickListener(v -> {
            if (isConfirmPasswordVisible) {
                etConfirmPassword.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
                ivToggleConfirmPassword.setColorFilter(android.graphics.Color.parseColor("#FFFFFF"));
            } else {
                etConfirmPassword.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                ivToggleConfirmPassword.setColorFilter(android.graphics.Color.parseColor("#FF8C00"));
            }
            etConfirmPassword.setSelection(etConfirmPassword.length());
            isConfirmPasswordVisible = !isConfirmPasswordVisible;
        });

        String resetToken = getIntent().getStringExtra("RESET_TOKEN");

        btnSave.setOnClickListener(v -> {
            String password = etNewPassword.getText().toString().trim();
            String confirmPassword = etConfirmPassword.getText().toString().trim();
            
            if (TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }
            
            if (!password.equals(confirmPassword)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!isValidPassword(password)) {
                Toast.makeText(this, "Password must be at least 8 characters and contain at least one number and one special character", Toast.LENGTH_LONG).show();
                return;
            }
            
            if (resetToken == null) {
                Toast.makeText(this, "Invalid session. Please verify OTP again.", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            resetPasswordWithBackend(resetToken, password);
        });
    }

    private void resetPasswordWithBackend(String token, String newPassword) {
        com.simats.rankforgeai.core.network.internal.api.ApiService apiService = com.simats.rankforgeai.core.network.internal.api.ApiClient.getClient().create(com.simats.rankforgeai.core.network.internal.api.ApiService.class);
        com.simats.rankforgeai.models.ResetPasswordRequest request = new com.simats.rankforgeai.models.ResetPasswordRequest(newPassword);

        apiService.resetPassword("Bearer " + token, request).enqueue(new retrofit2.Callback<com.simats.rankforgeai.models.MessageResponse>() {
            @Override
            public void onResponse(retrofit2.Call<com.simats.rankforgeai.models.MessageResponse> call, retrofit2.Response<com.simats.rankforgeai.models.MessageResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(PasswordResetActivity.this, "Password updated successfully!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(PasswordResetActivity.this, UserLoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(PasswordResetActivity.this, "Failed to update password. Try again.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<com.simats.rankforgeai.models.MessageResponse> call, Throwable t) {
                Toast.makeText(PasswordResetActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
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
}
