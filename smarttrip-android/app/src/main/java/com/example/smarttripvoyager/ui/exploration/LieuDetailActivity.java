package com.example.smarttripvoyager.ui.exploration;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.smarttripvoyager.R;
import com.example.smarttripvoyager.util.InteractionTracker;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;

public class LieuDetailActivity extends AppCompatActivity {

    private MaterialButton btnAddReview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lieu_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        TextView tvNom = findViewById(R.id.tvLieuNameDetail);
        Chip chipCategory = findViewById(R.id.chipCategory);
        TextView tvDescription = findViewById(R.id.tvLieuDescription);
        TextView tvHoraires = findViewById(R.id.tvLieuHoraires);
        TextView tvPrix = findViewById(R.id.tvLieuPrix);
        ImageView ivImage = findViewById(R.id.ivLieuImageDetail);
        btnAddReview = findViewById(R.id.btnAddReview);

        String nom = getIntent().getStringExtra("nom");
        String categorie = getIntent().getStringExtra("categorie");
        String storytelling = getIntent().getStringExtra("storytelling");
        String photoUrl = getIntent().getStringExtra("photoUrl");
        String prixEntree = getIntent().getStringExtra("prixEntree");
        String horaires = getIntent().getStringExtra("horaires");

        tvNom.setText(nom != null ? nom : "");
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(nom != null ? nom : "");
        }
        chipCategory.setText(categorie != null ? categorie : "");
        tvDescription.setText(storytelling != null && !storytelling.isEmpty()
                ? storytelling : "Aucune description disponible pour ce lieu.");
        tvHoraires.setText("🕐 " + (horaires != null && !horaires.isEmpty() ? horaires : "Non renseigné"));
        tvPrix.setText("💰 " + (prixEntree != null && !prixEntree.isEmpty() ? prixEntree : "Non renseigné"));

        if (photoUrl != null && !photoUrl.isEmpty()) {
            Glide.with(this).load(photoUrl).centerCrop().into(ivImage);
        }

        long lieuId = getIntent().getLongExtra("id", -1);
        if (lieuId != -1) {
            InteractionTracker.logView(this, "LIEU", lieuId);
        }

        btnAddReview.setOnClickListener(v -> {
            // TODO: Afficher la popup "Laisser un avis" (AlertDialog)
            Toast.makeText(this, "Popup d'avis bientôt disponible", Toast.LENGTH_SHORT).show();
        });
    }
}
