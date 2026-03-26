package com.simats.rankforgeai;

import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class PaymentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
                
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_payment);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        // Get intents from SubscriptionActivity
        String type = getIntent().getStringExtra("PLAN_TYPE");
        if (type == null) type = "Monthly";
        final String planType = type;
        
        int planPrice = getIntent().getIntExtra("PLAN_PRICE", 99);
        int planId = getIntent().getIntExtra("PLAN_ID", -1);
        String planDescExtra = getIntent().getStringExtra("PLAN_DESC");

        // Calculate GST (18%)
        double gstAmount = planPrice * 0.18;
        double totalAmount = planPrice + gstAmount;

        // UI references
        TextView tvPlanName = findViewById(R.id.tv_plan_summary_name);
        TextView tvPlanDesc = findViewById(R.id.tv_plan_summary_desc);
        TextView tvPlanPrice = findViewById(R.id.tv_plan_summary_price);
        TextView tvSubtotal = findViewById(R.id.tv_subtotal_amount);
        TextView tvGst = findViewById(R.id.tv_gst_amount);

        // Set text
        tvPlanName.setText("Pro Plan — " + planType);
        if (planDescExtra != null && !planDescExtra.isEmpty()) {
            tvPlanDesc.setText(planDescExtra);
        } else {
            tvPlanDesc.setText("First 7 days free, then ₹" + planPrice + (planType.equals("Annual") ? "/yr" : "/month"));
        }
        tvPlanPrice.setText("₹" + planPrice);

        tvSubtotal.setText(String.format("₹%.2f", (double) planPrice));
        tvGst.setText(String.format("₹%.2f", gstAmount));

        AppCompatButton btnPayNow = findViewById(R.id.btn_pay_now);
        btnPayNow.setText(String.format("Pay ₹%.2f Security", totalAmount));

        // Setup Net Banking Spinner
        android.widget.Spinner spinnerBank = findViewById(R.id.spinner_bank_name);
        String[] banks = new String[]{
                "Select Bank",
                "State Bank of India (SBI)",
                "HDFC Bank",
                "ICICI Bank",
                "Axis Bank",
                "Kotak Mahindra Bank",
                "Punjab National Bank (PNB)",
                "Bank of Baroda",
                "Canara Bank",
                "Union Bank of India",
                "IndusInd Bank",
                "Yes Bank",
                "IDFC FIRST Bank",
                "Federal Bank",
                "Indian Bank",
                "Central Bank of India",
                "Bank of India",
                "UCO Bank",
                "South Indian Bank",
                "RBL Bank"
        };
        android.widget.ArrayAdapter<String> adapter = new android.widget.ArrayAdapter<>(
                this, R.layout.item_spinner, banks);
        adapter.setDropDownViewResource(R.layout.item_spinner);
        spinnerBank.setAdapter(adapter);

        // Selection Interaction logic
        androidx.constraintlayout.widget.ConstraintLayout clUpiMethod = findViewById(R.id.cl_upi_method);
        androidx.constraintlayout.widget.ConstraintLayout clCardMethod = findViewById(R.id.cl_card_method);
        androidx.constraintlayout.widget.ConstraintLayout clBankMethod = findViewById(R.id.cl_bank_method);

        android.widget.ImageView ivUpiRadio = findViewById(R.id.iv_upi_radio);
        android.widget.ImageView ivCardRadio = findViewById(R.id.iv_card_radio);
        android.widget.ImageView ivBankRadio = findViewById(R.id.iv_bank_radio);
        
        // Hide all input rows initially
        androidx.constraintlayout.widget.ConstraintLayout upiInputLayout = findViewById(R.id.et_upi_id).getParent() instanceof androidx.constraintlayout.widget.ConstraintLayout ? (androidx.constraintlayout.widget.ConstraintLayout) findViewById(R.id.et_upi_id).getParent() : null;
        androidx.constraintlayout.widget.ConstraintLayout cardInputLayout = findViewById(R.id.cl_card_details);
        androidx.constraintlayout.widget.ConstraintLayout bankInputLayout = findViewById(R.id.cl_bank_details);

        Runnable clearSelection = () -> {
            clUpiMethod.setBackgroundResource(R.drawable.bg_white_card);
            clCardMethod.setBackgroundResource(R.drawable.bg_white_card);
            clBankMethod.setBackgroundResource(R.drawable.bg_white_card);
            
            ivUpiRadio.setImageResource(R.drawable.ic_circle_outline);
            ivUpiRadio.setColorFilter(0xFFD0D5FF); // hex color D0D5FF
            
            ivCardRadio.setImageResource(R.drawable.ic_circle_outline);
            ivCardRadio.setColorFilter(0xFFD0D5FF);
            
            ivBankRadio.setImageResource(R.drawable.ic_circle_outline);
            ivBankRadio.setColorFilter(0xFFD0D5FF);

            if (upiInputLayout != null) upiInputLayout.setVisibility(android.view.View.GONE);
            if (cardInputLayout != null) cardInputLayout.setVisibility(android.view.View.GONE);
            if (bankInputLayout != null) bankInputLayout.setVisibility(android.view.View.GONE);
        };
        
        final String[] selectedMethod = {""};
        
        clUpiMethod.setOnClickListener(v -> {
            clearSelection.run();
            selectedMethod[0] = "UPI";
            clUpiMethod.setBackgroundResource(R.drawable.bg_orange_border_card);
            ivUpiRadio.setImageResource(R.drawable.ic_check_circle);
            ivUpiRadio.setColorFilter(0xFFFF8C00); // orange
            if (upiInputLayout != null) upiInputLayout.setVisibility(android.view.View.VISIBLE);
        });

        clCardMethod.setOnClickListener(v -> {
            clearSelection.run();
            selectedMethod[0] = "Card";
            clCardMethod.setBackgroundResource(R.drawable.bg_orange_border_card);
            ivCardRadio.setImageResource(R.drawable.ic_check_circle);
            ivCardRadio.setColorFilter(0xFFFF8C00);
            if (cardInputLayout != null) cardInputLayout.setVisibility(android.view.View.VISIBLE);
        });

        clBankMethod.setOnClickListener(v -> {
            clearSelection.run();
            selectedMethod[0] = "Bank";
            clBankMethod.setBackgroundResource(R.drawable.bg_orange_border_card);
            ivBankRadio.setImageResource(R.drawable.ic_check_circle);
            ivBankRadio.setColorFilter(0xFFFF8C00);
            if (bankInputLayout != null) bankInputLayout.setVisibility(android.view.View.VISIBLE);
        });

        btnPayNow.setOnClickListener(v -> {
            if ("Bank".equals(selectedMethod[0])) {
                int position = spinnerBank.getSelectedItemPosition();
                if (position > 0) {
                    String[] bankUrls = new String[]{
                            "", // Select Bank
                            "https://retail.onlinesbi.sbi/",
                            "https://netbanking.hdfcbank.com/",
                            "https://m.icicibank.com/",
                            "https://retail.axisbank.co.in/",
                            "https://netbanking.kotak.com/",
                            "https://netpnb.com/",
                            "https://www.bobibanking.com/",
                            "https://netbanking.canarabank.in/",
                            "https://www.unionbankonline.co.in/",
                            "https://bfil.indusnet.co.in/",
                            "https://retail.yesbank.co.in/",
                            "https://my.idfcfirstbank.com/",
                            "https://www.fednetbank.com/",
                            "https://www.indianbank.net.in/",
                            "https://www.centralbank.net.in/",
                            "https://starconnectcbs.bankofindia.com/",
                            "https://www.ucoebanking.com/",
                            "https://sibernet.southindianbank.com/",
                            "https://online.rblbank.com/"
                    };
                    if (position < bankUrls.length) {
                        String url = bankUrls[position];
                        android.content.Intent browserIntent = new android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(url));
                        startActivity(browserIntent);
                    }
                    return; // Prevent going to success page purely on click if we are redirecting
                } else {
                    android.widget.Toast.makeText(PaymentActivity.this, "Please select a bank", android.widget.Toast.LENGTH_SHORT).show();
                    return;
                }
            } else if (selectedMethod[0].isEmpty()) {
                android.widget.Toast.makeText(PaymentActivity.this, "Please select a payment method", android.widget.Toast.LENGTH_SHORT).show();
                return;
            }

            android.content.SharedPreferences prefs = getSharedPreferences("RankForgePrefs", MODE_PRIVATE);
            String token = prefs.getString("accessToken", null);
            
            if(token != null) {
                com.simats.rankforgeai.core.network.internal.api.ApiService apiService = com.simats.rankforgeai.core.network.internal.api.ApiClient.getClient().create(com.simats.rankforgeai.core.network.internal.api.ApiService.class);
                com.simats.rankforgeai.models.SubscribeRequest request = new com.simats.rankforgeai.models.SubscribeRequest(planId);
                
                android.widget.Toast.makeText(PaymentActivity.this, "Processing Subscription...", android.widget.Toast.LENGTH_SHORT).show();
                
                apiService.subscribePlan("Bearer " + token, request).enqueue(new retrofit2.Callback<com.simats.rankforgeai.models.MessageResponse>() {
                    @Override
                    public void onResponse(retrofit2.Call<com.simats.rankforgeai.models.MessageResponse> call, retrofit2.Response<com.simats.rankforgeai.models.MessageResponse> response) {
                        if (response.isSuccessful()) {
                            // Backend confirms is_premium=True! Navigate to success layer.
                            android.content.Intent intent = new android.content.Intent(PaymentActivity.this, PaymentSuccessfulActivity.class);
                            intent.putExtra("PLAN_TYPE", planType);
                            startActivity(intent);
                            finish();
                        } else {
                            android.widget.Toast.makeText(PaymentActivity.this, "Subscription Sync Failed.", android.widget.Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(retrofit2.Call<com.simats.rankforgeai.models.MessageResponse> call, Throwable t) {
                        android.widget.Toast.makeText(PaymentActivity.this, "Network Error. Could not sync.", android.widget.Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                android.widget.Toast.makeText(PaymentActivity.this, "Missing Authentication Token.", android.widget.Toast.LENGTH_SHORT).show();
            }
        });
        
        findViewById(R.id.btn_verify_upi).setOnClickListener(v -> {
            Toast.makeText(this, "UPI Verification Pending", Toast.LENGTH_SHORT).show();
        });
    }
}
