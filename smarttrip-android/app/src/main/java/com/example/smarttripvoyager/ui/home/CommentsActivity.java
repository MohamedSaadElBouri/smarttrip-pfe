package com.example.smarttripvoyager.ui.home;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smarttripvoyager.R;
import com.example.smarttripvoyager.data.model.ApiResponse;
import com.example.smarttripvoyager.data.model.Commentaire;
import com.example.smarttripvoyager.network.ApiService;
import com.example.smarttripvoyager.network.RetrofitClient;
import com.example.smarttripvoyager.ui.adapter.CommentAdapter;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentsActivity extends AppCompatActivity {

    private CommentAdapter adapter;
    private ApiService apiService;
    private Long pubId;
    private RecyclerView recyclerView;
    private ProgressBar progressComments;
    private LinearLayout layoutCommentsEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        pubId = getIntent().getLongExtra("publication_id", -1);
        apiService = RetrofitClient.getApiService(this);

        recyclerView = findViewById(R.id.recyclerViewComments);
        progressComments = findViewById(R.id.progressComments);
        layoutCommentsEmpty = findViewById(R.id.layoutCommentsEmpty);
        adapter = new CommentAdapter(this, new ArrayList<>());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        if (pubId != -1) {
            loadComments();
        } else {
            progressComments.setVisibility(View.GONE);
            layoutCommentsEmpty.setVisibility(View.VISIBLE);
        }

        TextInputEditText etComment = findViewById(R.id.etComment);
        MaterialButton btnSendComment = findViewById(R.id.btnSendComment);

        btnSendComment.setOnClickListener(v -> {
            String comment = etComment.getText().toString().trim();
            if (!comment.isEmpty() && pubId != -1) {
                btnSendComment.setEnabled(false);
                apiService.addComment(pubId, Map.of("contenu", comment)).enqueue(new Callback<ApiResponse<Commentaire>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<Commentaire>> call, Response<ApiResponse<Commentaire>> response) {
                        btnSendComment.setEnabled(true);
                        if (response.isSuccessful() && response.body() != null && response.body().data != null) {
                            adapter.addComment(response.body().data);
                            layoutCommentsEmpty.setVisibility(View.GONE);
                            recyclerView.scrollToPosition(adapter.getItemCount() - 1);
                            etComment.setText("");
                        } else {
                            Toast.makeText(CommentsActivity.this, "Erreur lors de l'envoi du commentaire", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<Commentaire>> call, Throwable t) {
                        btnSendComment.setEnabled(true);
                        Toast.makeText(CommentsActivity.this, "Erreur réseau, veuillez réessayer", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void loadComments() {
        progressComments.setVisibility(View.VISIBLE);
        layoutCommentsEmpty.setVisibility(View.GONE);

        apiService.getComments(pubId).enqueue(new Callback<ApiResponse<List<Commentaire>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Commentaire>>> call, Response<ApiResponse<List<Commentaire>>> response) {
                progressComments.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null && response.body().data != null) {
                    List<Commentaire> comments = response.body().data;
                    adapter.setComments(comments);
                    layoutCommentsEmpty.setVisibility(comments.isEmpty() ? View.VISIBLE : View.GONE);
                } else {
                    Toast.makeText(CommentsActivity.this, "Impossible de charger les commentaires", Toast.LENGTH_SHORT).show();
                    layoutCommentsEmpty.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Commentaire>>> call, Throwable t) {
                progressComments.setVisibility(View.GONE);
                layoutCommentsEmpty.setVisibility(View.VISIBLE);
                Toast.makeText(CommentsActivity.this, "Erreur réseau : impossible de charger les commentaires", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
