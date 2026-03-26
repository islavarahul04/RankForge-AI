package com.simats.rankforgeai;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.RecognizerIntent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Locale;

public class AiChatActivity extends AppCompatActivity {

    private LinearLayout llChatContainer;
    private ScrollView scrollChat;
    private EditText etMessageInput;
    private Handler handler;
    private View typingIndicator;
    private String token;

    private ActivityResultLauncher<Intent> speechResultLauncher;
    private ActivityResultLauncher<String> galleryResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
                
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ai_chat);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        llChatContainer = findViewById(R.id.ll_chat_container);
        scrollChat = findViewById(R.id.scroll_chat);
        etMessageInput = findViewById(R.id.et_message_input);
        handler = new Handler(Looper.getMainLooper());

        TextView tvAiGreeting = findViewById(R.id.tv_ai_greeting);

        // Get Token and Name
        android.content.SharedPreferences prefs = getSharedPreferences("RankForgePrefs", MODE_PRIVATE);
        token = prefs.getString("accessToken", null);
        String name = prefs.getString("userName", null);

        if (name != null) {
            tvAiGreeting.setText("Hello " + name + " ! \n\nHow can I help you today?");
        }

        if (token != null) {
            loadChatHistory();
        }

        findViewById(R.id.btn_close).setOnClickListener(v -> finish());
        
        // Setup Submit
        findViewById(R.id.btn_send).setOnClickListener(v -> {
            String message = etMessageInput.getText().toString().trim();
            if (!message.isEmpty()) {
                sendMessageToBackend(message);
                etMessageInput.setText("");
            }
        });

        // Setup Speech to Text
        speechResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    ArrayList<String> matches = result.getData().getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    if (matches != null && !matches.isEmpty()) {
                        sendMessageToBackend(matches.get(0));
                    }
                }
            });

        findViewById(R.id.btn_mic).setOnClickListener(v -> {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Ask RankForge AI...");
            try {
                speechResultLauncher.launch(intent);
            } catch (Exception e) {
                Toast.makeText(this, "Speech recognition not available on this device", Toast.LENGTH_SHORT).show();
            }
        });

        // Setup Gallery Picker
        galleryResultLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    // For now, simple text message as backend doesn't process images in chat model yet
                    sendMessageToBackend("Uploaded an image (Manual review needed)");
                }
            });

        findViewById(R.id.btn_gallery).setOnClickListener(v -> {
            galleryResultLauncher.launch("image/*");
        });
        
        // Setup action buttons for first AI message
        findViewById(R.id.btn_solve_problem).setOnClickListener(v -> {
            sendMessageToBackend("Let's try a problem. But keep it slightly easier than the mock test one.");
        });
        
        findViewById(R.id.btn_review_concept).setOnClickListener(v -> {
            sendMessageToBackend("Let's review the core concepts of Time & Work first.");
        });
    }

    private void loadChatHistory() {
        com.simats.rankforgeai.core.network.internal.api.ApiService apiService = com.simats.rankforgeai.core.network.internal.api.ApiClient.getClient().create(com.simats.rankforgeai.core.network.internal.api.ApiService.class);
        apiService.getChatHistory("Bearer " + token).enqueue(new retrofit2.Callback<java.util.List<com.simats.rankforgeai.models.AiChatModels.ChatMessage>>() {
            @Override
            public void onResponse(retrofit2.Call<java.util.List<com.simats.rankforgeai.models.AiChatModels.ChatMessage>> call, retrofit2.Response<java.util.List<com.simats.rankforgeai.models.AiChatModels.ChatMessage>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    llChatContainer.removeAllViews();
                    for (com.simats.rankforgeai.models.AiChatModels.ChatMessage msg : response.body()) {
                        if (msg.isUser()) {
                            addMessageToUI(msg.getMessage(), true, null);
                        } else {
                            addMessageToUI(msg.getMessage(), false, null);
                        }
                    }
                    scrollToBottom();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<java.util.List<com.simats.rankforgeai.models.AiChatModels.ChatMessage>> call, Throwable t) {
                Toast.makeText(AiChatActivity.this, "Failed to load history", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendMessageToBackend(String message) {
        // Add user message to UI immediately
        addMessageToUI(message, true, null);
        showTypingIndicator();

        com.simats.rankforgeai.core.network.internal.api.ApiService apiService = com.simats.rankforgeai.core.network.internal.api.ApiClient.getClient().create(com.simats.rankforgeai.core.network.internal.api.ApiService.class);
        com.simats.rankforgeai.models.AiChatModels.ChatRequest request = new com.simats.rankforgeai.models.AiChatModels.ChatRequest(message);

        apiService.sendChatMessage("Bearer " + token, request).enqueue(new retrofit2.Callback<com.simats.rankforgeai.models.AiChatModels.ChatMessage>() {
            @Override
            public void onResponse(retrofit2.Call<com.simats.rankforgeai.models.AiChatModels.ChatMessage> call, retrofit2.Response<com.simats.rankforgeai.models.AiChatModels.ChatMessage> response) {
                hideTypingIndicator();
                if (response.isSuccessful() && response.body() != null) {
                    addMessageToUI(response.body().getMessage(), false, null);
                } else {
                    int code = response.code();
                    if (isFinishing() || isDestroyed()) return;
                    
                    if (code == 403) {
                        Toast.makeText(AiChatActivity.this, "Trace: 403 received", Toast.LENGTH_SHORT).show();
                        showLimitExhaustedDialog();
                    } else {
                        Toast.makeText(AiChatActivity.this, "Trace: Response code " + code, Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(retrofit2.Call<com.simats.rankforgeai.models.AiChatModels.ChatMessage> call, Throwable t) {
                hideTypingIndicator();
                Toast.makeText(AiChatActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showLimitExhaustedDialog() {
        if (isFinishing() || isDestroyed()) return;
        Toast.makeText(this, "Trace: Inflating Dialog", Toast.LENGTH_SHORT).show();
        
        try {
            View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_limit_reached, null);
            androidx.appcompat.app.AlertDialog dialog = new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setView(dialogView)
                    .setCancelable(true)
                    .create();

            if (dialog.getWindow() != null) {
                dialog.getWindow().setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT));
            }

            View btnUpgrade = dialogView.findViewById(R.id.btn_upgrade_premium);
            View btnClose = dialogView.findViewById(R.id.btn_close_limit);

            if (btnUpgrade != null) {
                btnUpgrade.setOnClickListener(v -> {
                    dialog.dismiss();
                    try {
                        startActivity(new Intent(AiChatActivity.this, PaymentActivity.class));
                    } catch (Exception ex) {
                        Toast.makeText(this, "Error opening Payment", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            if (btnClose != null) {
                btnClose.setOnClickListener(v -> dialog.dismiss());
            }

            dialog.show();
            Toast.makeText(this, "Trace: Dialog .show() called", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Dialog Exception: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void addMessageToUI(String text, boolean isUser, Uri imageUri) {
        int layoutId = isUser ? R.layout.item_chat_user : R.layout.item_chat_ai;
        View view = LayoutInflater.from(this).inflate(layoutId, llChatContainer, false);
        
        if (isUser) {
            TextView tvMessage = view.findViewById(R.id.tv_user_message);
            ImageView ivImage = view.findViewById(R.id.iv_user_image);
            tvMessage.setText(text);
            if (imageUri != null) {
                ivImage.setVisibility(View.VISIBLE);
                ivImage.setImageURI(imageUri);
            }
        } else {
            TextView tvMessage = view.findViewById(R.id.tv_ai_message);
            tvMessage.setText(text);
        }
        
        llChatContainer.addView(view);
        scrollToBottom();
    }

    private void showTypingIndicator() {
        if (typingIndicator == null) {
            typingIndicator = LayoutInflater.from(this).inflate(R.layout.item_chat_typing, llChatContainer, false);
        }
        if (typingIndicator.getParent() == null) {
            llChatContainer.addView(typingIndicator);
            scrollToBottom();
        }
    }

    private void hideTypingIndicator() {
        if (typingIndicator != null && typingIndicator.getParent() != null) {
            llChatContainer.removeView(typingIndicator);
        }
    }

    private void scrollToBottom() {
        scrollChat.post(() -> scrollChat.fullScroll(View.FOCUS_DOWN));
    }
}
