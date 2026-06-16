package com.example.smarttripvoyager.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.smarttripvoyager.R;
import com.example.smarttripvoyager.data.model.Publication;
import com.example.smarttripvoyager.network.ApiService;
import com.example.smarttripvoyager.network.RetrofitClient;
import com.example.smarttripvoyager.ui.adapter.PostAdapter;
import com.example.smarttripvoyager.ui.questionnaire.QuestionnaireActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {

    private RecyclerView recyclerViewFeed;
    private SwipeRefreshLayout swipeRefreshFeed;
    private LinearLayout layoutFeedEmpty;
    private ProgressBar progressFeed;
    private PostAdapter postAdapter;
    private BottomNavigationView bottomNavigation;
    private com.google.android.material.floatingactionbutton.FloatingActionButton fabAddPost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        recyclerViewFeed = findViewById(R.id.recyclerViewFeed);
        swipeRefreshFeed = findViewById(R.id.swipeRefreshFeed);
        layoutFeedEmpty = findViewById(R.id.layoutFeedEmpty);
        progressFeed = findViewById(R.id.progressFeed);
        bottomNavigation = findViewById(R.id.bottomNavigation);
        fabAddPost = findViewById(R.id.fabAddPost);

        setupRecyclerView();
        setupBottomNavigation();

        swipeRefreshFeed.setColorSchemeResources(R.color.primary);
        swipeRefreshFeed.setOnRefreshListener(this::loadFeed);

        fabAddPost.setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, com.example.smarttripvoyager.ui.home.CreatePostActivity.class));
        });

        loadFeed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadFeed();
    }

    private void setupRecyclerView() {
        recyclerViewFeed.setLayoutManager(new LinearLayoutManager(this));
        postAdapter = new PostAdapter(this, new ArrayList<>());
        recyclerViewFeed.setAdapter(postAdapter);
    }

    private void setupBottomNavigation() {
        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                // Déjà sur la Home
                return true;
            } else if (itemId == R.id.nav_catalogue) {
                startActivity(new Intent(this, com.example.smarttripvoyager.ui.exploration.CatalogueLieuxActivity.class));
                return true;
            } else if (itemId == R.id.nav_ai) {
                startActivity(new Intent(this, QuestionnaireActivity.class));
                return true;
            } else if (itemId == R.id.nav_trips) {
                startActivity(new Intent(this, com.example.smarttripvoyager.ui.home.MyTripsActivity.class));
                return true;
            } else if (itemId == R.id.nav_profile) {
                startActivity(new Intent(this, com.example.smarttripvoyager.ui.profile.ProfileActivity.class));
                return true;
            }
            return false;
        });
    }

    private void loadFeed() {
        ApiService apiService = RetrofitClient.getApiService(this);

        if (!swipeRefreshFeed.isRefreshing()) {
            progressFeed.setVisibility(View.VISIBLE);
        }
        layoutFeedEmpty.setVisibility(View.GONE);

        apiService.getFeed().enqueue(new Callback<com.example.smarttripvoyager.data.model.ApiResponse<com.example.smarttripvoyager.data.model.Page<Publication>>>() {
            @Override
            public void onResponse(Call<com.example.smarttripvoyager.data.model.ApiResponse<com.example.smarttripvoyager.data.model.Page<Publication>>> call, Response<com.example.smarttripvoyager.data.model.ApiResponse<com.example.smarttripvoyager.data.model.Page<Publication>>> response) {
                progressFeed.setVisibility(View.GONE);
                swipeRefreshFeed.setRefreshing(false);
                if (response.isSuccessful() && response.body() != null && response.body().data != null) {
                    List<Publication> posts = response.body().data.content;
                    postAdapter.setPublications(posts);
                    layoutFeedEmpty.setVisibility(posts == null || posts.isEmpty() ? View.VISIBLE : View.GONE);
                } else {
                    Toast.makeText(HomeActivity.this, "Erreur chargement Feed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<com.example.smarttripvoyager.data.model.ApiResponse<com.example.smarttripvoyager.data.model.Page<Publication>>> call, Throwable t) {
                progressFeed.setVisibility(View.GONE);
                swipeRefreshFeed.setRefreshing(false);
                Toast.makeText(HomeActivity.this, "Erreur réseau: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
