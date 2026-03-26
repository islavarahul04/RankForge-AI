package com.simats.rankforgeai;

import android.os.Bundle;
import android.view.WindowManager;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RadioButton;
import android.widget.Toast;
import android.app.DatePickerDialog;
import java.util.Calendar;
import android.content.SharedPreferences;
import android.net.Uri;
import java.io.InputStream;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import com.bumptech.glide.Glide;


import com.simats.rankforgeai.core.network.internal.api.ApiClient;
import com.simats.rankforgeai.core.network.internal.api.ApiService;
import com.simats.rankforgeai.models.UserProfile;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;

public class EditProfileActivity extends AppCompatActivity {

    private EditText etFullName, etEmail, etPhone, etDob, etCity;
    private RadioGroup rgGender;
    private RadioButton rbMale, rbFemale, rbOther;
    private Spinner spinnerExam;
    private String jwtToken = null;
    private Uri selectedImageUri = null;
    private String currentProfilePictureUrl = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
                
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_profile);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
        
        // --- Image Picker Setup ---
        ImageView ivCamera = findViewById(R.id.iv_camera);
        ImageView ivAvatar = findViewById(R.id.iv_avatar_image);
        android.widget.TextView tvAvatarText = findViewById(R.id.tv_avatar);

        ActivityResultLauncher<android.content.Intent> imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData();
                        if (selectedImageUri != null) {
                            ivAvatar.setImageURI(selectedImageUri);
                            tvAvatarText.setVisibility(android.view.View.GONE);
                            ivAvatar.setVisibility(android.view.View.VISIBLE);
                        }
                    }
                }
        );

        android.view.View.OnClickListener pickImageListener = v -> {
            android.content.Intent intent = new android.content.Intent(android.content.Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imagePickerLauncher.launch(intent);
        };

        ivCamera.setOnClickListener(pickImageListener);
        android.view.View flAvatarContainer = findViewById(R.id.fl_avatar_container);
        flAvatarContainer.setOnClickListener(pickImageListener);

        flAvatarContainer.setOnLongClickListener(v -> {
            if (currentProfilePictureUrl != null && !currentProfilePictureUrl.isEmpty()) {
                showDeletePhotoDialog();
                return true;
            }
            return false;
        });

        etFullName = findViewById(R.id.et_full_name);
        etEmail = findViewById(R.id.et_email);
        etPhone = findViewById(R.id.et_phone);
        etDob = findViewById(R.id.et_dob);
        etCity = findViewById(R.id.et_city);
        rgGender = findViewById(R.id.rg_gender);
        rbMale = findViewById(R.id.rb_male);
        rbFemale = findViewById(R.id.rb_female);
        rbOther = findViewById(R.id.rb_other);
        spinnerExam = findViewById(R.id.spinner_target_exam);

        String[] exams = new String[]{"SSC CHSL", "UPSC", "Banking", "Railways", "Defence", "State PSC"};
        ArrayAdapter<String> examAdapter = new ArrayAdapter<>(this, R.layout.item_spinner, exams);
        examAdapter.setDropDownViewResource(R.layout.item_spinner);
        spinnerExam.setAdapter(examAdapter);

        etDob.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dialog = new DatePickerDialog(EditProfileActivity.this,
                    (view, year1, month1, dayOfMonth) -> {
                        String formattedDate = String.format("%04d-%02d-%02d", year1, (month1 + 1), dayOfMonth);
                        etDob.setText(formattedDate);
                    }, year, month, day);
            dialog.show();
        });

        SharedPreferences prefs = getSharedPreferences("RankForgePrefs", MODE_PRIVATE);
        jwtToken = prefs.getString("accessToken", null);
        
        String savedEmail = prefs.getString("userEmail", "");
        if (!savedEmail.isEmpty()) {
            etEmail.setText(savedEmail);
        }

        if (jwtToken != null) {
            fetchUserProfile();
        }

        findViewById(R.id.tv_action_save).setOnClickListener(v -> saveUserProfile());
    }

    private void fetchUserProfile() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.getProfile("Bearer " + jwtToken).enqueue(new Callback<UserProfile>() {
            @Override
            public void onResponse(Call<UserProfile> call, Response<UserProfile> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserProfile profile = response.body();
                    
                    if (profile.getFullName() != null) etFullName.setText(profile.getFullName());
                    if (profile.getEmail() != null) etEmail.setText(profile.getEmail());
                    if (profile.getPhoneNumber() != null) etPhone.setText(profile.getPhoneNumber());
                    if (profile.getDob() != null) etDob.setText(profile.getDob());
                    if (profile.getCity() != null) etCity.setText(profile.getCity());
                    
                    if (profile.getGender() != null) {
                        if (profile.getGender().equalsIgnoreCase("Male")) rbMale.setChecked(true);
                        else if (profile.getGender().equalsIgnoreCase("Female")) rbFemale.setChecked(true);
                        else rbOther.setChecked(true);
                    }

                    if (profile.getTargetExam() != null) {
                        for (int i = 0; i < spinnerExam.getCount(); i++) {
                            if (spinnerExam.getItemAtPosition(i).toString().equalsIgnoreCase(profile.getTargetExam())) {
                                spinnerExam.setSelection(i);
                                break;
                            }
                        }
                    }

                    currentProfilePictureUrl = profile.getProfilePicture();
                    if (currentProfilePictureUrl != null && !currentProfilePictureUrl.isEmpty()) {
                        String serverUrl = ApiClient.getServerUrl();
                        String imageUrl = currentProfilePictureUrl.startsWith("http") ? currentProfilePictureUrl : serverUrl + currentProfilePictureUrl;
                        
                        ImageView ivAvatar = findViewById(R.id.iv_avatar_image);
                        android.widget.TextView tvAvatarText = findViewById(R.id.tv_avatar);
                        
                        GlideUrl glideUrl = new GlideUrl(imageUrl, new LazyHeaders.Builder()
                            .addHeader("ngrok-skip-browser-warning", "69420")
                            .build());

                        Glide.with(EditProfileActivity.this)
                            .load(glideUrl)
                            .placeholder(R.drawable.bg_circle_profile)
                            .error(R.drawable.bg_circle_profile)
                            .circleCrop()
                            .into(ivAvatar);
                            
                        tvAvatarText.setVisibility(android.view.View.GONE);
                        ivAvatar.setVisibility(android.view.View.VISIBLE);
                        
                        getSharedPreferences("RankForgePrefs", MODE_PRIVATE).edit()
                            .putString("profilePictureUrl", imageUrl).apply();
                    }
                }
            }

            @Override
            public void onFailure(Call<UserProfile> call, Throwable t) {
                Toast.makeText(EditProfileActivity.this, "Failed to load profile", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveUserProfile() {
        if (jwtToken == null) {
            Toast.makeText(this, "Authentication error. Please login again.", Toast.LENGTH_SHORT).show();
            return;
        }

        android.widget.TextView tvSave = findViewById(R.id.tv_action_save);
        tvSave.setText("Saving...");
        tvSave.setEnabled(false);

        String name = etFullName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String dob = etDob.getText().toString().trim();
        String city = etCity.getText().toString().trim();
        
        String gender = "Other";
        if (rbMale.isChecked()) gender = "Male";
        else if (rbFemale.isChecked()) gender = "Female";

        String targetExam = spinnerExam.getSelectedItem().toString();

        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        if (selectedImageUri != null) {
            try {
                InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
                byte[] bytes = new byte[inputStream.available()];
                inputStream.read(bytes);
                
                RequestBody requestFile = RequestBody.create(MediaType.parse(getContentResolver().getType(selectedImageUri)), bytes);
                MultipartBody.Part body = MultipartBody.Part.createFormData("profile_picture", "profile_" + System.currentTimeMillis() + ".jpg", requestFile);
                
                RequestBody rbName = RequestBody.create(MultipartBody.FORM, name);
                RequestBody rbPhone = RequestBody.create(MultipartBody.FORM, phone != null ? phone : "");
                RequestBody rbDob = RequestBody.create(MultipartBody.FORM, dob != null ? dob : "");
                RequestBody rbCity = RequestBody.create(MultipartBody.FORM, city != null ? city : "");
                RequestBody rbGender = RequestBody.create(MultipartBody.FORM, gender);
                RequestBody rbExam = RequestBody.create(MultipartBody.FORM, targetExam);

                apiService.updateProfileMultipart("Bearer " + jwtToken, body, rbName, rbPhone, rbDob, rbCity, rbGender, rbExam).enqueue(callback);
            } catch (Exception e) {
                Toast.makeText(this, "Error processing image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                tvSave.setText("Save");
                tvSave.setEnabled(true);
            }
        } else {
            UserProfile profile = new UserProfile(name, phone, dob, city, gender, targetExam);
            apiService.updateProfile("Bearer " + jwtToken, profile).enqueue(callback);
        }
    }

    private Callback<UserProfile> callback = new Callback<UserProfile>() {
        @Override
        public void onResponse(Call<UserProfile> call, Response<UserProfile> response) {
            android.widget.TextView tvSave = findViewById(R.id.tv_action_save);
            tvSave.setText("Save");
            tvSave.setEnabled(true);
            
            if (response.isSuccessful() && response.body() != null) {
                Toast.makeText(EditProfileActivity.this, "Profile Saved Successfully!", Toast.LENGTH_SHORT).show();
                
                UserProfile updatedProfile = response.body();
                SharedPreferences.Editor editor = getSharedPreferences("RankForgePrefs", MODE_PRIVATE).edit();
                editor.putString("userName", updatedProfile.getFullName());
                
                if (updatedProfile.getProfilePicture() != null) {
                    String serverUrl = ApiClient.getServerUrl();
                    String imageUrl = updatedProfile.getProfilePicture().startsWith("http") ? updatedProfile.getProfilePicture() : serverUrl + updatedProfile.getProfilePicture();
                    editor.putString("profilePictureUrl", imageUrl);
                }
                
                editor.apply();
                finish();
            } else {
                Toast.makeText(EditProfileActivity.this, "Error saving profile. Check inputs.", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onFailure(Call<UserProfile> call, Throwable t) {
            android.widget.TextView tvSave = findViewById(R.id.tv_action_save);
            tvSave.setText("Save");
            tvSave.setEnabled(true);
            Toast.makeText(EditProfileActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };

    private void showDeletePhotoDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Delete Profile Picture")
                .setMessage("Are you sure you want to remove your profile picture?")
                .setPositiveButton("Delete", (dialog, which) -> deleteProfilePicture())
                .setNeutralButton("Change Photo", (dialog, which) -> {
                    findViewById(R.id.fl_avatar_container).performClick();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteProfilePicture() {
        if (jwtToken == null) return;

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        java.util.Map<String, Object> fields = new java.util.HashMap<>();
        fields.put("profile_picture", null);

        apiService.updateProfilePartial("Bearer " + jwtToken, fields).enqueue(new Callback<UserProfile>() {
            @Override
            public void onResponse(Call<UserProfile> call, Response<UserProfile> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(EditProfileActivity.this, "Profile picture deleted", Toast.LENGTH_SHORT).show();
                    currentProfilePictureUrl = null;
                    selectedImageUri = null;
                    
                    // Update UI to show initials
                    ImageView ivAvatar = findViewById(R.id.iv_avatar_image);
                    android.widget.TextView tvAvatarText = findViewById(R.id.tv_avatar);
                    ivAvatar.setVisibility(android.view.View.GONE);
                    tvAvatarText.setVisibility(android.view.View.VISIBLE);
                    
                    String name = etFullName.getText().toString().trim();
                    if (!name.isEmpty()) {
                        String initials = "";
                        String[] parts = name.split(" ");
                        if (parts.length > 0 && !parts[0].isEmpty()) initials += parts[0].charAt(0);
                        if (parts.length > 1 && !parts[1].isEmpty()) initials += parts[1].charAt(0);
                        tvAvatarText.setText(initials.toUpperCase());
                    }

                    // Clear from SharedPreferences
                    getSharedPreferences("RankForgePrefs", MODE_PRIVATE).edit()
                            .remove("profilePictureUrl").apply();
                } else {
                    Toast.makeText(EditProfileActivity.this, "Failed to delete picture", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserProfile> call, Throwable t) {
                Toast.makeText(EditProfileActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
