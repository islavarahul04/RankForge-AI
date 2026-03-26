package com.simats.rankforgeai;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.simats.rankforgeai.core.network.internal.api.ApiClient;
import com.simats.rankforgeai.core.network.internal.api.ApiService;
import com.simats.rankforgeai.models.AuthResponse;
import com.simats.rankforgeai.models.LoginRequest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminLoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private android.widget.ImageView ivTogglePassword;
    private boolean isPasswordVisible = false;
    private ApiService apiService;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
                
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_login);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        ivTogglePassword = findViewById(R.id.iv_toggle_password);

        ivTogglePassword.setAlpha(0.6f);
        ivTogglePassword.setOnClickListener(v -> {
            if (isPasswordVisible) {
                etPassword.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
                ivTogglePassword.setImageResource(R.drawable.ic_eye_open);
                ivTogglePassword.setAlpha(0.6f);
            } else {
                etPassword.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                ivTogglePassword.setImageResource(R.drawable.ic_eye_closed);
                ivTogglePassword.setAlpha(1.0f);
            }
            etPassword.setSelection(etPassword.length());
            isPasswordVisible = !isPasswordVisible;
        });

        apiService = ApiClient.getClient().create(ApiService.class);
        sharedPreferences = getSharedPreferences("RankForgePrefs", Context.MODE_PRIVATE);

        findViewById(R.id.btn_sign_in).setOnClickListener(v -> performAdminLogin());

        findViewById(R.id.tv_forgot_password).setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            Intent intent = new Intent(AdminLoginActivity.this, OtpVerificationActivity.class);
            if (!android.text.TextUtils.isEmpty(email)) {
                intent.putExtra("USER_EMAIL", email);
            }
            startActivity(intent);
        });
    }

    private void performAdminLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        LoginRequest request = new LoginRequest(email, password);
        apiService.adminLoginUser(request).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AuthResponse authResponse = response.body();
                    
                    // Save admin access token securely
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("accessToken", authResponse.getAccess());
                    editor.putBoolean("isAdminLoggedIn", true);
                    editor.apply();

                    Toast.makeText(AdminLoginActivity.this, "Admin Login Successful!", Toast.LENGTH_SHORT).show();
                    
                    startActivity(new Intent(AdminLoginActivity.this, AdminDashboardActivity.class));
                    finish();
                } else {
                    Toast.makeText(AdminLoginActivity.this, "Access Denied: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                Toast.makeText(AdminLoginActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
