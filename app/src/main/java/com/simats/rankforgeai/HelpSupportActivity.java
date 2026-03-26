package com.simats.rankforgeai;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.simats.rankforgeai.adapters.SupportTicketAdapter;
import com.simats.rankforgeai.core.network.internal.api.ApiClient;
import com.simats.rankforgeai.core.network.internal.api.ApiService;
import com.simats.rankforgeai.models.MessageResponse;
import com.simats.rankforgeai.models.SupportTicketRequest;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HelpSupportActivity extends AppCompatActivity {

    private EditText etSupportMessage;
    private Button btnSubmitSupport;
    private RecyclerView rvTickets;
    private SupportTicketAdapter adapter;
    private List<SupportTicketRequest> ticketList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_help_support);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        etSupportMessage = findViewById(R.id.et_support_message);
        btnSubmitSupport = findViewById(R.id.btn_submit_support);
        rvTickets = findViewById(R.id.rv_tickets);

        rvTickets.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SupportTicketAdapter(ticketList);
        rvTickets.setAdapter(adapter);

        btnSubmitSupport.setOnClickListener(v -> submitSupportTicket());

        fetchSupportTickets();
    }

    private void fetchSupportTickets() {
        SharedPreferences prefs = getSharedPreferences("RankForgePrefs", MODE_PRIVATE);
        String token = prefs.getString("accessToken", null);

        if (token == null) return;

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.getSupportTickets("Bearer " + token).enqueue(new Callback<List<SupportTicketRequest>>() {
            @Override
            public void onResponse(Call<List<SupportTicketRequest>> call, Response<List<SupportTicketRequest>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ticketList.clear();
                    ticketList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<SupportTicketRequest>> call, Throwable t) {
                // Silently fail or log
            }
        });
    }

    private void submitSupportTicket() {
        String message = etSupportMessage.getText().toString().trim();
        if (message.isEmpty()) {
            Toast.makeText(this, "Please enter a message.", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences prefs = getSharedPreferences("RankForgePrefs", MODE_PRIVATE);
        String token = prefs.getString("accessToken", null);

        if (token == null) {
            Toast.makeText(this, "Session expired. Please log in again.", Toast.LENGTH_SHORT).show();
            return;
        }

        btnSubmitSupport.setEnabled(false);
        btnSubmitSupport.setText("Submitting...");

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        SupportTicketRequest request = new SupportTicketRequest(message);

        apiService.createSupportTicket("Bearer " + token, request).enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                btnSubmitSupport.setEnabled(true);
                btnSubmitSupport.setText("Submit Ticket");
                if (response.isSuccessful()) {
                    Toast.makeText(HelpSupportActivity.this, "Ticket submitted successfully!", Toast.LENGTH_SHORT).show();
                    etSupportMessage.setText(""); // Clear the input
                    fetchSupportTickets(); // Refresh history
                } else {
                    Toast.makeText(HelpSupportActivity.this, "Failed to submit ticket.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MessageResponse> call, Throwable t) {
                btnSubmitSupport.setEnabled(true);
                btnSubmitSupport.setText("Submit Ticket");
                Toast.makeText(HelpSupportActivity.this, "Network error.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
