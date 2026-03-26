package com.simats.rankforgeai;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class OtpVerificationActivity extends AppCompatActivity {

    private EditText otp1, otp2, otp3, otp4;
    private TextView tvEmail, tvResendTimer;
    private CountDownTimer countDownTimer;
    private boolean isTimerRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
                
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_otp_verification);
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvEmail = findViewById(R.id.tv_email);
        tvResendTimer = findViewById(R.id.tv_resend_timer);
        otp1 = findViewById(R.id.otp_1);
        otp2 = findViewById(R.id.otp_2);
        otp3 = findViewById(R.id.otp_3);
        otp4 = findViewById(R.id.otp_4);

        // Get Email
        String email = getIntent().getStringExtra("USER_EMAIL");
        if (email != null && !email.isEmpty()) {
            tvEmail.setText(email);
            triggerOtpEmail(email);
        }

        setupOtpInputs();
        startResendTimer();

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        findViewById(R.id.btn_verify).setOnClickListener(v -> {
            String otp = otp1.getText().toString() + otp2.getText().toString() +
                         otp3.getText().toString() + otp4.getText().toString();
            if (otp.length() == 4) {
                verifyOtpWithBackend(email, otp);
            } else {
                Toast.makeText(this, "Please enter all 4 digits", Toast.LENGTH_SHORT).show();
            }
        });

        tvResendTimer.setOnClickListener(v -> {
            if (!isTimerRunning && email != null && !email.isEmpty()) {
                triggerOtpEmail(email);
                startResendTimer();
            }
        });
    }

    private void verifyOtpWithBackend(String email, String otp) {
        com.simats.rankforgeai.core.network.internal.api.ApiService apiService = com.simats.rankforgeai.core.network.internal.api.ApiClient.getClient().create(com.simats.rankforgeai.core.network.internal.api.ApiService.class);
        com.simats.rankforgeai.models.OtpVerificationRequest request = new com.simats.rankforgeai.models.OtpVerificationRequest(email, otp);

        apiService.verifyOtp(request).enqueue(new retrofit2.Callback<com.simats.rankforgeai.models.AuthResponse>() {
            @Override
            public void onResponse(retrofit2.Call<com.simats.rankforgeai.models.AuthResponse> call, retrofit2.Response<com.simats.rankforgeai.models.AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String accessToken = response.body().getAccess();
                    Toast.makeText(OtpVerificationActivity.this, "OTP Verified!", Toast.LENGTH_SHORT).show();
                    
                    Intent intent = new Intent(OtpVerificationActivity.this, PasswordResetActivity.class);
                    intent.putExtra("RESET_TOKEN", accessToken);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(OtpVerificationActivity.this, "Invalid or expired OTP", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<com.simats.rankforgeai.models.AuthResponse> call, Throwable t) {
                Toast.makeText(OtpVerificationActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void triggerOtpEmail(String email) {
        com.simats.rankforgeai.core.network.internal.api.ApiService apiService = com.simats.rankforgeai.core.network.internal.api.ApiClient.getClient().create(com.simats.rankforgeai.core.network.internal.api.ApiService.class);
        com.simats.rankforgeai.models.ForgotPasswordRequest request = new com.simats.rankforgeai.models.ForgotPasswordRequest(email);

        apiService.forgotPassword(request).enqueue(new retrofit2.Callback<com.simats.rankforgeai.models.MessageResponse>() {
            @Override
            public void onResponse(retrofit2.Call<com.simats.rankforgeai.models.MessageResponse> call, retrofit2.Response<com.simats.rankforgeai.models.MessageResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(OtpVerificationActivity.this, "OTP sent to " + email, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(OtpVerificationActivity.this, "Failed to send OTP", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<com.simats.rankforgeai.models.MessageResponse> call, Throwable t) {
                Toast.makeText(OtpVerificationActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupOtpInputs() {
        otp1.addTextChangedListener(new OtpTextWatcher(otp1, otp2));
        otp2.addTextChangedListener(new OtpTextWatcher(otp2, otp3));
        otp3.addTextChangedListener(new OtpTextWatcher(otp3, otp4));
        otp4.addTextChangedListener(new OtpTextWatcher(otp4, null));

        otp1.setOnKeyListener(new OtpKeyListener(otp1, null));
        otp2.setOnKeyListener(new OtpKeyListener(otp2, otp1));
        otp3.setOnKeyListener(new OtpKeyListener(otp3, otp2));
        otp4.setOnKeyListener(new OtpKeyListener(otp4, otp3));
    }

    private void startResendTimer() {
        tvResendTimer.setTextColor(android.graphics.Color.parseColor("#FF8C00"));
        isTimerRunning = true;
        countDownTimer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tvResendTimer.setText("Resend in " + millisUntilFinished / 1000 + "s");
            }

            @Override
            public void onFinish() {
                isTimerRunning = false;
                tvResendTimer.setText("Resend Code");
                tvResendTimer.setTextColor(android.graphics.Color.parseColor("#FFFFFF"));
            }
        }.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    private class OtpTextWatcher implements TextWatcher {
        private View currentView;
        private View nextView;

        public OtpTextWatcher(View currentView, View nextView) {
            this.currentView = currentView;
            this.nextView = nextView;
        }

        @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

        @Override
        public void afterTextChanged(Editable s) {
            String text = s.toString();
            if (text.length() == 1 && nextView != null) {
                nextView.requestFocus();
            }
        }
    }

    private class OtpKeyListener implements View.OnKeyListener {
        private EditText currentView;
        private EditText previousView;

        public OtpKeyListener(EditText currentView, EditText previousView) {
            this.currentView = currentView;
            this.previousView = previousView;
        }

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DEL) {
                if (currentView.getText().toString().isEmpty() && previousView != null) {
                    previousView.requestFocus();
                    previousView.setText("");
                    return true;
                }
            }
            return false;
        }
    }
}
