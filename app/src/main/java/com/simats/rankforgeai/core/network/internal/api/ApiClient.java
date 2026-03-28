package com.simats.rankforgeai.core.network.internal.api;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Clean and robust API Client for RankForge AI.
 * Handles server URL switching between College Server and Emulator.
 */
public class ApiClient {
    // --- Configuration ---
    private static final String COLLEGE_SERVER_URL = "http://180.235.121.253:8124/api/";
    private static final String EMULATOR_URL = "http://10.0.2.2:8000/api/";
    
    private static Retrofit retrofit = null;

    /**
     * Returns a singleton Retrofit instance.
     */
    public static Retrofit getClient() {
        if (retrofit == null) {
            // 1. Setup Logging
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            // 2. Setup OkHttp Client
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .addInterceptor(chain -> chain.proceed(chain.request().newBuilder()
                            .addHeader("Accept", "application/json")
                            .addHeader("Content-Type", "application/json")
                            .build()))
                    .build();

            // 3. Setup Gson
            Gson gson = new GsonBuilder()
                    .serializeNulls()
                    .setLenient()
                    .create();

            // 4. Build Retrofit
            retrofit = new Retrofit.Builder()
                    .baseUrl(getBaseUrl())
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }

    /**
     * Determines the base URL based on the running environment.
     */
    public static String getBaseUrl() {
        if (isEmulator()) {
            return EMULATOR_URL;
        }
        return COLLEGE_SERVER_URL;
    }

    /**
     * Returns the server URL without the /api/ suffix (for media/images).
     */
    public static String getServerUrl() {
        String baseUrl = getBaseUrl();
        if (baseUrl.endsWith("/api/")) {
            return baseUrl.substring(0, baseUrl.length() - 4);
        }
        return baseUrl;
    }

    /**
     * Helper to detect if the app is running on an Android Emulator.
     */
    private static boolean isEmulator() {
        return android.os.Build.FINGERPRINT.contains("generic")
                || android.os.Build.FINGERPRINT.contains("unknown")
                || android.os.Build.MODEL.contains("google_sdk")
                || android.os.Build.MODEL.contains("Emulator")
                || android.os.Build.MODEL.contains("Android SDK built for x86");
    }
}
