package com.simats.rankforgeai.core.network.internal.api;

import android.os.Build;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Universal API Client for RankForge AI.
 * Automatically handles different server types and path formatting.
 */
public class ApiClient {
    
    // --- SERVER CONFIGURATION ---
    // Change this to your current server address (IP, Ngrok, or Domain)
    private static final String SERVER_URL = "https://kindra-venulose-innoxiously.ngrok-free.dev";
    
    // Default Emulator address for local testing
    private static final String EMULATOR_URL = "http://10.0.2.2:8000";
    
    private static Retrofit retrofit = null;

    /**
     * Returns a singleton Retrofit instance.
     */
    public static Retrofit getClient() {
        if (retrofit == null) {
            String baseUrl = getBaseUrl();
            
            // 1. Setup Logging
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            // 2. Setup OkHttp Client with universal compatibility headers
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .addInterceptor(chain -> chain.proceed(chain.request().newBuilder()
                            .addHeader("Accept", "application/json")
                            .addHeader("Content-Type", "application/json")
                            .addHeader("ngrok-skip-browser-warning", "69420") // Support for Ngrok testing
                            .build()))
                    .build();

            // 3. Setup Flexible Gson
            Gson gson = new GsonBuilder()
                    .serializeNulls()
                    .setLenient()
                    .create();

            // 4. Build Retrofit
            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }

    /**
     * Determines the correct Base URL and ensures it is properly formatted.
     */
    public static String getBaseUrl() {
        String url = isEmulator() ? EMULATOR_URL : SERVER_URL;
        return sanitizeUrl(url);
    }

    /**
     * Helper to return the root server URL (used for media/images).
     */
    public static String getServerUrl() {
        String url = isEmulator() ? EMULATOR_URL : SERVER_URL;
        if (!url.endsWith("/")) url += "/";
        return url;
    }

    /**
     * Automatically ensures the URL ends with /api/ and has correct slashes.
     */
    private static String sanitizeUrl(String url) {
        if (url == null) return "http://localhost/api/";
        
        String cleanUrl = url.trim();
        if (!cleanUrl.endsWith("/")) cleanUrl += "/";
        
        // Ensure /api/ suffix is present
        if (!cleanUrl.toLowerCase().endsWith("/api/")) {
            cleanUrl += "api/";
        }
        
        return cleanUrl;
    }

    /**
     * Detects if the app is running on an Android Emulator.
     */
    private static boolean isEmulator() {
        return Build.FINGERPRINT.contains("generic")
                || Build.FINGERPRINT.contains("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86");
    }
}
