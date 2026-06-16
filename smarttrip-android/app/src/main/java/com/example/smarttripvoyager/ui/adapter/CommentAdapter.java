package com.example.smarttripvoyager.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.smarttripvoyager.R;
import com.example.smarttripvoyager.data.model.Commentaire;
import com.example.smarttripvoyager.util.DateUtils;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private List<Commentaire> comments;
    private final Context context;

    public CommentAdapter(Context context, List<Commentaire> comments) {
        this.context = context;
        this.comments = comments;
    }

    public void setComments(List<Commentaire> comments) {
        this.comments = comments;
        notifyDataSetChanged();
    }

    public void addComment(Commentaire comment) {
        comments.add(comment);
        notifyItemInserted(comments.size() - 1);
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Commentaire comment = comments.get(position);
        holder.tvAuthorName.setText(comment.getAuteurNom());
        holder.tvContent.setText(comment.getContenu());
        holder.tvDate.setText(DateUtils.relativeDate(comment.getDate()));

        String avatarUrl = comment.getAuteurPhotoUrl();
        if (avatarUrl != null && !avatarUrl.isEmpty()) {
            Glide.with(context)
                    .load(avatarUrl)
                    .circleCrop()
                    .placeholder(R.drawable.ic_launcher_background)
                    .into(holder.ivAvatar);
        } else {
            holder.ivAvatar.setImageResource(R.drawable.ic_launcher_background);
        }
    }

    @Override
    public int getItemCount() {
        return comments == null ? 0 : comments.size();
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        ImageView ivAvatar;
        TextView tvAuthorName, tvDate, tvContent;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.ivCommentAvatar);
            tvAuthorName = itemView.findViewById(R.id.tvCommentAuthor);
            tvDate = itemView.findViewById(R.id.tvCommentDate);
            tvContent = itemView.findViewById(R.id.tvCommentContent);
        }
    }
}
