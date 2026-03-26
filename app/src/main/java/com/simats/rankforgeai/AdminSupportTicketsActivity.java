package com.simats.rankforgeai;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.simats.rankforgeai.adapters.AdminSupportTicketAdapter;
import com.simats.rankforgeai.core.network.internal.api.ApiClient;
import com.simats.rankforgeai.core.network.internal.api.ApiService;
import com.simats.rankforgeai.models.AdminSupportTicket;
import com.simats.rankforgeai.models.MessageResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminSupportTicketsActivity extends AppCompatActivity implements AdminSupportTicketAdapter.OnReplyClickListener {

    private RecyclerView rvTickets;
    private ProgressBar pbLoading;
    private AdminSupportTicketAdapter adapter;
    private List<AdminSupportTicket> ticketList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_support_tickets);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        rvTickets = findViewById(R.id.rv_admin_tickets);
        pbLoading = findViewById(R.id.pb_loading);

        rvTickets.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdminSupportTicketAdapter(ticketList, this);
        rvTickets.setAdapter(adapter);

        fetchTickets();
    }

    private void fetchTickets() {
        pbLoading.setVisibility(View.VISIBLE);
        SharedPreferences prefs = getSharedPreferences("RankForgePrefs", MODE_PRIVATE);
        String token = prefs.getString("accessToken", null);

        if (token == null) {
            Toast.makeText(this, "Admin session expired", Toast.LENGTH_SHORT).show();
            pbLoading.setVisibility(View.GONE);
            return;
        }

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.getAdminSupportTickets("Bearer " + token).enqueue(new Callback<List<AdminSupportTicket>>() {
            @Override
            public void onResponse(Call<List<AdminSupportTicket>> call, Response<List<AdminSupportTicket>> response) {
                pbLoading.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    ticketList.clear();
                    ticketList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(AdminSupportTicketsActivity.this, "Failed to load tickets", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<AdminSupportTicket>> call, Throwable t) {
                pbLoading.setVisibility(View.GONE);
                Toast.makeText(AdminSupportTicketsActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onReplyClick(int ticketId) {
        showReplyDialog(ticketId);
    }

    private void showReplyDialog(int ticketId) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_admin_reply, null);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .create();

        EditText etReply = dialogView.findViewById(R.id.et_admin_reply);
        View btnCancel = dialogView.findViewById(R.id.btn_cancel);
        View btnSend = dialogView.findViewById(R.id.btn_send_reply);

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnSend.setOnClickListener(v -> {
            String replyText = etReply.getText().toString().trim();
            if (replyText.isEmpty()) {
                Toast.makeText(this, "Please enter a reply", Toast.LENGTH_SHORT).show();
                return;
            }
            sendReply(ticketId, replyText, dialog);
        });

        dialog.show();
    }

    private void sendReply(int ticketId, String replyText, AlertDialog dialog) {
        SharedPreferences prefs = getSharedPreferences("RankForgePrefs", MODE_PRIVATE);
        String token = prefs.getString("accessToken", null);

        if (token == null) return;

        Map<String, Object> body = new HashMap<>();
        body.put("ticket_id", ticketId);
        body.put("reply", replyText);
        body.put("is_resolved", true);

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.replyToSupportTicket("Bearer " + token, body).enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AdminSupportTicketsActivity.this, "Reply sent successfully", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    fetchTickets(); // Refresh list
                } else {
                    Toast.makeText(AdminSupportTicketsActivity.this, "Failed to send reply", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MessageResponse> call, Throwable t) {
                Toast.makeText(AdminSupportTicketsActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
