package com.example.smarttripvoyager.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.smarttripvoyager.R;
import com.example.smarttripvoyager.data.model.Publication;
import com.example.smarttripvoyager.util.DateUtils;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    /** Notified when an item is removed locally after an unsave (see {@link #setRemoveOnUnsave}). */
    public interface OnPublicationRemovedListener {
        void onPublicationRemoved(int remainingCount);
    }

    private List<Publication> publications;
    private Context context;
    private boolean removeOnUnsave = false;
    private OnPublicationRemovedListener removalListener;

    public PostAdapter(Context context, List<Publication> publications) {
        this.context = context;
        this.publications = publications;
    }

    public void setPublications(List<Publication> publications) {
        this.publications = publications;
        notifyDataSetChanged();
    }

    /**
     * When true, un-saving a publication removes it immediately from this adapter's
     * list instead of just toggling its bookmark icon. Used for the "saved posts" list
     * in the profile screen, where an unsaved item should no longer be displayed.
     */
    public void setRemoveOnUnsave(boolean removeOnUnsave) {
        this.removeOnUnsave = removeOnUnsave;
    }

    public void setOnPublicationRemovedListener(OnPublicationRemovedListener listener) {
        this.removalListener = listener;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Publication pub = publications.get(position);
        holder.tvAuthorName.setText(pub.getAuthorName());
        holder.tvPostContent.setText(pub.getContenu());

        holder.tvPostDate.setText(DateUtils.relativeDate(pub.getDateCreation()));

        String categorie = pub.getCategorie();
        if (categorie != null && !categorie.isEmpty()) {
            holder.tvCategorie.setText(categorieLabel(categorie));
            holder.tvCategorie.setVisibility(View.VISIBLE);
        } else {
            holder.tvCategorie.setVisibility(View.GONE);
        }

        holder.tvAiBadge.setVisibility(pub.getAiRankingScore() != null ? View.VISIBLE : View.GONE);

        // Load Author Avatar
        String avatarUrl = pub.getAuthorPhotoUrl();
        if (avatarUrl != null && !avatarUrl.isEmpty()) {
            Glide.with(context)
                 .load(avatarUrl)
                 .circleCrop()
                 .placeholder(R.drawable.ic_launcher_background)
                 .into(holder.ivAuthorAvatar);
        } else {
            holder.ivAuthorAvatar.setImageResource(R.drawable.ic_launcher_background);
        }

        // Load Image using Glide
        if (pub.getImageUrl() != null && !pub.getImageUrl().isEmpty()) {
            Glide.with(context)
                 .load(pub.getImageUrl())
                 .centerCrop()
                 .into(holder.ivPostImage);
            holder.ivPostImage.setVisibility(View.VISIBLE);
        } else {
            holder.ivPostImage.setVisibility(View.GONE);
        }
        holder.btnLike.setText(String.valueOf(pub.getLikesCount()));
        holder.btnLike.setIconTintResource(pub.isLikedByMe() ? R.color.primary : R.color.charcoal);

        holder.btnComment.setText(String.valueOf(pub.getCommentsCount()));

        holder.btnSave.setIconTintResource(pub.isSavedByMe() ? R.color.primary : R.color.charcoal);

        holder.btnLike.setOnClickListener(v -> {
            boolean isLiked = pub.isLikedByMe();
            int newLikes = pub.getLikesCount() + (isLiked ? -1 : 1);
            pub.setLikedByMe(!isLiked);
            pub.setLikesCount(newLikes);
            holder.btnLike.setIconTintResource(pub.isLikedByMe() ? R.color.primary : R.color.charcoal);
            holder.btnLike.setText(String.valueOf(newLikes));
            
            com.example.smarttripvoyager.network.ApiService apiService = com.example.smarttripvoyager.network.RetrofitClient.getApiService(context);
            apiService.toggleLike(pub.getId()).enqueue(new retrofit2.Callback<com.example.smarttripvoyager.data.model.ApiResponse<java.util.Map<String, Object>>>() {
                @Override
                public void onResponse(retrofit2.Call<com.example.smarttripvoyager.data.model.ApiResponse<java.util.Map<String, Object>>> call, retrofit2.Response<com.example.smarttripvoyager.data.model.ApiResponse<java.util.Map<String, Object>>> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().data != null) {
                        java.util.Map<String, Object> data = response.body().data;
                        if (data.containsKey("likesCount")) {
                            Double likesDouble = (Double) data.get("likesCount");
                            pub.setLikesCount(likesDouble.intValue());
                            holder.btnLike.setText(String.valueOf(pub.getLikesCount()));
                        }
                        if (data.containsKey("liked")) {
                            Boolean liked = (Boolean) data.get("liked");
                            pub.setLikedByMe(liked);
                            holder.btnLike.setIconTintResource(pub.isLikedByMe() ? R.color.primary : R.color.charcoal);
                        }
                    }
                }
                @Override
                public void onFailure(retrofit2.Call<com.example.smarttripvoyager.data.model.ApiResponse<java.util.Map<String, Object>>> call, Throwable t) {}
            });
        });
        
        holder.btnSave.setOnClickListener(v -> {
            boolean isSaved = pub.isSavedByMe();
            boolean removeFromThisList = isSaved && removeOnUnsave;
            int removedPosition = RecyclerView.NO_POSITION;

            if (removeFromThisList) {
                removedPosition = holder.getAdapterPosition();
                if (removedPosition != RecyclerView.NO_POSITION) {
                    publications.remove(removedPosition);
                    notifyItemRemoved(removedPosition);
                    if (removalListener != null) {
                        removalListener.onPublicationRemoved(publications.size());
                    }
                }
            } else {
                pub.setSavedByMe(!isSaved);
                holder.btnSave.setIconTintResource(pub.isSavedByMe() ? R.color.primary : R.color.charcoal);
            }
            Toast.makeText(context, "Publication " + (isSaved ? "retirée des sauvegardes" : "sauvegardée"), Toast.LENGTH_SHORT).show();

            final int rollbackPosition = removedPosition;
            com.example.smarttripvoyager.network.ApiService apiService = com.example.smarttripvoyager.network.RetrofitClient.getApiService(context);
            apiService.toggleSauvegarde(pub.getId()).enqueue(new retrofit2.Callback<com.example.smarttripvoyager.data.model.ApiResponse<java.util.Map<String, Boolean>>>() {
                @Override
                public void onResponse(retrofit2.Call<com.example.smarttripvoyager.data.model.ApiResponse<java.util.Map<String, Boolean>>> call, retrofit2.Response<com.example.smarttripvoyager.data.model.ApiResponse<java.util.Map<String, Boolean>>> response) {
                    if (removeFromThisList && (!response.isSuccessful() || response.body() == null)) {
                        restoreRemovedItem(rollbackPosition);
                    }
                }
                @Override
                public void onFailure(retrofit2.Call<com.example.smarttripvoyager.data.model.ApiResponse<java.util.Map<String, Boolean>>> call, Throwable t) {
                    if (removeFromThisList) {
                        restoreRemovedItem(rollbackPosition);
                    }
                }

                private void restoreRemovedItem(int position) {
                    if (position == RecyclerView.NO_POSITION) return;
                    int insertAt = Math.min(position, publications.size());
                    publications.add(insertAt, pub);
                    notifyItemInserted(insertAt);
                    if (removalListener != null) {
                        removalListener.onPublicationRemoved(publications.size());
                    }
                }
            });
        });
        
        holder.btnComment.setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(context, com.example.smarttripvoyager.ui.home.CommentsActivity.class);
            intent.putExtra("publication_id", pub.getId());
            context.startActivity(intent);
        });

        holder.btnShare.setOnClickListener(v -> {
            android.content.Intent shareIntent = new android.content.Intent(android.content.Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            String shareMessage = pub.getAuthorName() + " a publié sur SmartTrip Voyager :\n\n" 
                + pub.getContenu() + "\n\nRetrouvez d'autres publications sur l'application !";
            shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareMessage);
            context.startActivity(android.content.Intent.createChooser(shareIntent, "Partager via"));
        });
    }

    @Override
    public int getItemCount() {
        return publications == null ? 0 : publications.size();
    }

    /** Traduit la valeur de categorie envoyee a l'IA (ex. "food") vers son libelle affiche (ex. "Gastronomie"). */
    private String categorieLabel(String categorie) {
        String[] values = context.getResources().getStringArray(R.array.publication_categories_values);
        String[] labels = context.getResources().getStringArray(R.array.publication_categories_labels);
        for (int i = 0; i < values.length; i++) {
            if (values[i].equalsIgnoreCase(categorie)) {
                return labels[i];
            }
        }
        return categorie;
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        ImageView ivAuthorAvatar, ivPostImage;
        TextView tvAuthorName, tvPostDate, tvPostContent, tvCategorie, tvAiBadge;
        MaterialButton btnLike, btnShare, btnComment, btnSave;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAuthorAvatar = itemView.findViewById(R.id.ivAuthorAvatar);
            ivPostImage = itemView.findViewById(R.id.ivPostImage);
            tvAuthorName = itemView.findViewById(R.id.tvAuthorName);
            tvPostDate = itemView.findViewById(R.id.tvPostDate);
            tvPostContent = itemView.findViewById(R.id.tvPostContent);
            tvCategorie = itemView.findViewById(R.id.tvCategorie);
            tvAiBadge = itemView.findViewById(R.id.tvAiBadge);
            btnLike = itemView.findViewById(R.id.btnLike);
            btnShare = itemView.findViewById(R.id.btnShare);
            btnComment = itemView.findViewById(R.id.btnComment);
            btnSave = itemView.findViewById(R.id.btnSave);
        }
    }
}
