package com.simats.rankforgeai.core.network.internal.api;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Clean and robust API Client for RankForge AI.
 */
public class ApiClient {
    // --- Configuration ---
    // Previous Django Server Link (Ngrok)
    private static final String BASE_URL = "https://kindra-venulose-innoxiously.ngrok-free.dev/api/";
    
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
                            .addHeader("ngrok-skip-browser-warning", "69420") // Bypass Ngrok warning page
                            .build()))
                    .build();

            // 3. Setup Gson
            Gson gson = new GsonBuilder()
                    .serializeNulls()
                    .setLenient()
                    .create();

            // 4. Build Retrofit
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }

    /**
     * Returns the current base URL.
     */
    public static String getBaseUrl() {
        return BASE_URL;
    }

    /**
     * Returns the server URL without the /api/ suffix (for media/images).
     */
    public static String getServerUrl() {
        if (BASE_URL.endsWith("/api/")) {
            return BASE_URL.substring(0, BASE_URL.length() - 4);
        }
        return BASE_URL;
    }
}
