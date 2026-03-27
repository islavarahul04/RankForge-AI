package com.simats.rankforgeai.core.network.internal.api;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static final String BASE_URL = "http://180.235.121.253:8124/api/";
    private static final String EMULATOR_URL = "http://10.0.2.2:8000/api/";
    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .addInterceptor(chain -> chain.proceed(chain.request().newBuilder()
                            .addHeader("ngrok-skip-browser-warning", "69420")
                            .build()))
                    .build();

            // Detect if running on emulator to use local machine's IP (more stable)
            String baseUrl = getBaseUrl();

            com.google.gson.Gson gson = new com.google.gson.GsonBuilder()
                    .serializeNulls()
                    .create();

            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }

    public static String getBaseUrl() {
        if (android.os.Build.FINGERPRINT.contains("generic") 
            || android.os.Build.FINGERPRINT.contains("unknown") 
            || android.os.Build.MODEL.contains("google_sdk") 
            || android.os.Build.MODEL.contains("Emulator") 
            || android.os.Build.MODEL.contains("Android SDK built for x86")) {
            return EMULATOR_URL;
        }
        
        // Automatically ensure /api/ suffix
        String url = BASE_URL;
        if (!url.endsWith("/")) url += "/";
        if (!url.toLowerCase().endsWith("/api/")) url += "api/";
        return url;
    }

    public static String getServerUrl() {
        String baseUrl = getBaseUrl();
        if (baseUrl.endsWith("/api/")) {
            return baseUrl.substring(0, baseUrl.length() - 4); // Returns domain with a single slash
        }
        return baseUrl;
    }
}
