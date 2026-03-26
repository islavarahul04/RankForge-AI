package com.simats.rankforgeai;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.simats.rankforgeai.core.network.internal.api.ApiClient;
import com.simats.rankforgeai.core.network.internal.api.ApiService;
import com.simats.rankforgeai.models.PYQPaper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PYQPaperAdapter extends RecyclerView.Adapter<PYQPaperAdapter.ViewHolder> {

    private final List<PYQPaper> paperList;
    private final Context context;

    public PYQPaperAdapter(Context context, List<PYQPaper> paperList) {
        this.context = context;
        this.paperList = paperList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pyq_paper, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PYQPaper paper = paperList.get(position);
        holder.tvTitle.setText(paper.getTitle());
        String info = paper.getCategory() + " • " + paper.getYear();
        holder.tvInfo.setText(info);

        holder.btnOpen.setOnClickListener(v -> {
            openPdf(paper);
        });
    }

    private void openPdf(PYQPaper paper) {
        String fileName = "pyq_" + paper.getId() + ".pdf";
        File file = new File(context.getCacheDir(), fileName);

        if (file.exists()) {
            launchPdfIntent(file);
        } else {
            downloadAndOpen(paper, file);
        }
    }

    private void downloadAndOpen(PYQPaper paper, File file) {
        Toast.makeText(context, "Opening PDF...", Toast.LENGTH_SHORT).show();
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.downloadFile(paper.getFileUrl()).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    new SaveFileTask(response.body(), file).execute();
                } else {
                    Toast.makeText(context, "Failed to download PDF", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private class SaveFileTask extends AsyncTask<Void, Void, Boolean> {
        private final ResponseBody body;
        private final File file;

        SaveFileTask(ResponseBody body, File file) {
            this.body = body;
            this.file = file;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try (InputStream inputStream = body.byteStream();
                 FileOutputStream outputStream = new FileOutputStream(file)) {
                byte[] fileReader = new byte[4096];
                while (true) {
                    int read = inputStream.read(fileReader);
                    if (read == -1) break;
                    outputStream.write(fileReader, 0, read);
                }
                outputStream.flush();
                return true;
            } catch (Exception e) {
                Log.e("PYQPaperAdapter", "File save error", e);
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                launchPdfIntent(file);
            } else {
                Toast.makeText(context, "Failed to save PDF", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void launchPdfIntent(File file) {
        try {
            Uri uri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", file);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, "application/pdf");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(context, "No PDF viewer found", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getItemCount() {
        return paperList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvInfo;
        Button btnOpen;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_paper_title);
            tvInfo = itemView.findViewById(R.id.tv_paper_info);
            btnOpen = itemView.findViewById(R.id.btn_open_pdf);
        }
    }
}
