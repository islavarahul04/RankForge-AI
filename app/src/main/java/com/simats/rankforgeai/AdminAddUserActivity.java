package com.simats.rankforgeai;

import android.os.Bundle;
import android.widget.Button;
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
import com.simats.rankforgeai.models.RegisterRequest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminAddUserActivity extends AppCompatActivity {

    private EditText etNewEmail, etNewPassword, etConfirmPassword;
    private Button btnCreateUser;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_add_user);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize API Service
        apiService = ApiClient.getClient().create(ApiService.class);

        // Bind Views
        etNewEmail = findViewById(R.id.et_new_email);
        etNewPassword = findViewById(R.id.et_new_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        btnCreateUser = findViewById(R.id.btn_create_user);

        // Back Button
        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        // Create User Action
        btnCreateUser.setOnClickListener(v -> handleCreateUser());
    }

    private void handleCreateUser() {
        String email = etNewEmail.getText().toString().trim();
        String password = etNewPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        btnCreateUser.setEnabled(false);
        btnCreateUser.setText("Creating...");

        // Leverage the public registration endpoint. We don't need a custom name,
        // so we'll just pass "Platform User" or let the name populate blankly.
        RegisterRequest request = new RegisterRequest(email, password, "Admin Authored User");

        apiService.registerUser(request).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                btnCreateUser.setEnabled(true);
                btnCreateUser.setText("Create Account");
                
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(AdminAddUserActivity.this, "User created successfully!", Toast.LENGTH_SHORT).show();
                    finish(); // Return to the Admin Users menu
                } else {
                    Toast.makeText(AdminAddUserActivity.this, "Creation failed. Email might exist.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                btnCreateUser.setEnabled(true);
                btnCreateUser.setText("Create Account");
                Toast.makeText(AdminAddUserActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
