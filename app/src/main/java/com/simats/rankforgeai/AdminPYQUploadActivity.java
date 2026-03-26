package com.simats.rankforgeai;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.simats.rankforgeai.core.network.internal.api.ApiClient;
import com.simats.rankforgeai.core.network.internal.api.ApiService;
import com.simats.rankforgeai.models.PYQPaper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminPYQUploadActivity extends AppCompatActivity {

    private EditText etTitle, etCategory, etYear;
    private TextView tvFileName;
    private Uri selectedPdfUri;
    private ApiService apiService;
    private SharedPreferences sharedPreferences;

    private final ActivityResultLauncher<String> pdfPickerLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    selectedPdfUri = uri;
                    tvFileName.setText(getFileName(uri));
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_pyq_upload);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etTitle = findViewById(R.id.et_paper_title);
        etCategory = findViewById(R.id.et_exam_category);
        etYear = findViewById(R.id.et_paper_year);
        tvFileName = findViewById(R.id.tv_selected_file_name);

        apiService = ApiClient.getClient().create(ApiService.class);
        sharedPreferences = getSharedPreferences("RankForgePrefs", Context.MODE_PRIVATE);

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
        findViewById(R.id.btn_select_pdf).setOnClickListener(v -> pdfPickerLauncher.launch("application/pdf"));
        findViewById(R.id.btn_post_paper).setOnClickListener(v -> uploadPaper());
    }

    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (index != -1) result = cursor.getString(index);
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) result = result.substring(cut + 1);
        }
        return result;
    }

    private void uploadPaper() {
        String title = etTitle.getText().toString().trim();
        String category = etCategory.getText().toString().trim();
        String year = etYear.getText().toString().trim();

        if (title.isEmpty() || category.isEmpty() || year.isEmpty() || selectedPdfUri == null) {
            Toast.makeText(this, "Please fill all fields and select a PDF", Toast.LENGTH_SHORT).show();
            return;
        }

        String token = sharedPreferences.getString("accessToken", null);
        if (token == null) {
            Toast.makeText(this, "Session expired", Toast.LENGTH_SHORT).show();
            return;
        }

        findViewById(R.id.btn_post_paper).setEnabled(false);
        Toast.makeText(this, "Uploading...", Toast.LENGTH_SHORT).show();

        try {
            File file = getFileFromUri(selectedPdfUri);
            RequestBody requestFile = RequestBody.create(MediaType.parse("application/pdf"), file);
            MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
            RequestBody titlePart = RequestBody.create(MediaType.parse("text/plain"), title);
            RequestBody yearPart = RequestBody.create(MediaType.parse("text/plain"), year);
            RequestBody categoryPart = RequestBody.create(MediaType.parse("text/plain"), category);

            apiService.uploadPYQ("Bearer " + token, body, titlePart, yearPart, categoryPart).enqueue(new Callback<PYQPaper>() {
                @Override
                public void onResponse(Call<PYQPaper> call, Response<PYQPaper> response) {
                    findViewById(R.id.btn_post_paper).setEnabled(true);
                    if (response.isSuccessful()) {
                        Toast.makeText(AdminPYQUploadActivity.this, "Paper Uploaded Successfully", Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        Toast.makeText(AdminPYQUploadActivity.this, "Upload failed: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<PYQPaper> call, Throwable t) {
                    findViewById(R.id.btn_post_paper).setEnabled(true);
                    Toast.makeText(AdminPYQUploadActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception e) {
            findViewById(R.id.btn_post_paper).setEnabled(true);
            Toast.makeText(this, "Error processing file: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private File getFileFromUri(Uri uri) throws Exception {
        InputStream inputStream = getContentResolver().openInputStream(uri);
        File file = new File(getCacheDir(), getFileName(uri));
        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            byte[] buffers = new byte[1024];
            int read;
            while ((read = inputStream.read(buffers)) != -1) {
                outputStream.write(buffers, 0, read);
            }
        }
        return file;
    }
}
