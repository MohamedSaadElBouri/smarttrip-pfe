package com.example.smarttripvoyager.ui.exploration;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.smarttripvoyager.R;
import com.example.smarttripvoyager.data.model.Lieu;

import java.util.List;

public class LieuAdapter extends RecyclerView.Adapter<LieuAdapter.LieuViewHolder> {

    private Context context;
    private List<Lieu> lieux;

    public LieuAdapter(Context context, List<Lieu> lieux) {
        this.context = context;
        this.lieux = lieux;
    }

    public void setLieux(List<Lieu> lieux) {
        this.lieux = lieux;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public LieuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_lieu, parent, false);
        return new LieuViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LieuViewHolder holder, int position) {
        Lieu lieu = lieux.get(position);
        holder.tvLieuName.setText(lieu.getNom());
        holder.tvLieuCategory.setText(lieu.getCategorie());
        holder.tvLieuRating.setText("⭐ " + (lieu.getNoteMoyenne() != null ? lieu.getNoteMoyenne() : "N/A"));

        if (lieu.getPhotoUrl() != null && !lieu.getPhotoUrl().isEmpty()) {
            Glide.with(context)
                 .load(lieu.getPhotoUrl())
                 .centerCrop()
                 .into(holder.ivLieuImage);
        }
        
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, LieuDetailActivity.class);
            intent.putExtra("id", lieu.getId());
            intent.putExtra("nom", lieu.getNom());
            intent.putExtra("categorie", lieu.getCategorie());
            intent.putExtra("photoUrl", lieu.getPhotoUrl());
            intent.putExtra("storytelling", lieu.getStorytelling());
            intent.putExtra("prixEntree", lieu.getPrixEntree());
            intent.putExtra("horaires", lieu.getHoraires());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return lieux == null ? 0 : lieux.size();
    }

    public static class LieuViewHolder extends RecyclerView.ViewHolder {
        ImageView ivLieuImage;
        TextView tvLieuName, tvLieuCategory, tvLieuRating;

        public LieuViewHolder(@NonNull View itemView) {
            super(itemView);
            ivLieuImage = itemView.findViewById(R.id.ivLieuImage);
            tvLieuName = itemView.findViewById(R.id.tvLieuName);
            tvLieuCategory = itemView.findViewById(R.id.tvLieuCategory);
            tvLieuRating = itemView.findViewById(R.id.tvLieuRating);
        }
    }
}
