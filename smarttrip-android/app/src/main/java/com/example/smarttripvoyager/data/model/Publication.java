package com.example.smarttripvoyager.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.Date;

public class Publication {
    private Long id;
    private String contenu;
    private String photoUrl;
    private String statut;
    
    @SerializedName("date")
    private String dateCreation;
    
    private Integer nbLikes;
    private Integer nbCommentaires;
    private Integer nbPartages;
    private String categorie;
    private AuthorDTO utilisateur;
    private Long lieuId;
    private String lieuNom;
    private Double aiRankingScore;
    private boolean likedByMe;
    private boolean savedByMe;

    public static class AuthorDTO {
        public Long id;
        public String nom;
        public String photoUrl;
    }
    
    public Long getId() { return id; }
    public String getContenu() { return contenu; }
    public String getImageUrl() { return photoUrl; }
    public String getDateCreation() { return dateCreation; }
    public int getLikesCount() { return nbLikes != null ? nbLikes : 0; }
    public int getCommentsCount() { return nbCommentaires != null ? nbCommentaires : 0; }
    public void setCommentsCount(int count) { this.nbCommentaires = count; }
    public String getAuthorName() { return (utilisateur != null && utilisateur.nom != null) ? utilisateur.nom : "Utilisateur"; }
    public String getAuthorPhotoUrl() { return (utilisateur != null) ? utilisateur.photoUrl : null; }
    
    public boolean isLikedByMe() { return likedByMe; }
    public void setLikedByMe(boolean likedByMe) { this.likedByMe = likedByMe; }
    
    public boolean isSavedByMe() { return savedByMe; }
    public void setSavedByMe(boolean savedByMe) { this.savedByMe = savedByMe; }
    
    public Double getAiRankingScore() { return aiRankingScore; }
    public void setAiRankingScore(Double aiRankingScore) { this.aiRankingScore = aiRankingScore; }
    public void setLikesCount(int count) { this.nbLikes = count; }

    public void setContenu(String contenu) { this.contenu = contenu; }
    public void setImageUrl(String imageUrl) { this.photoUrl = imageUrl; }

    public String getCategorie() { return categorie; }
    public void setCategorie(String categorie) { this.categorie = categorie; }
}
