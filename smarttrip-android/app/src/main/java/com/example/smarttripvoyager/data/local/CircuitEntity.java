package com.example.smarttripvoyager.data.local;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "circuits")
public class CircuitEntity {
    
    @PrimaryKey
    private Long id;
    
    private String titre;
    private String description;
    private String theme;
    private String ville;
    private int dureeJours;
    private String photoUrl;
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getTheme() { return theme; }
    public void setTheme(String theme) { this.theme = theme; }
    
    public String getVille() { return ville; }
    public void setVille(String ville) { this.ville = ville; }
    
    public int getDureeJours() { return dureeJours; }
    public void setDureeJours(int dureeJours) { this.dureeJours = dureeJours; }
    
    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }
}
