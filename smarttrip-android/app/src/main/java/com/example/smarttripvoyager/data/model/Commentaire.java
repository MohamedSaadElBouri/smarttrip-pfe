package com.example.smarttripvoyager.data.model;

public class Commentaire {
    private Long id;
    private String contenu;
    private String date;
    private String auteurNom;
    private String auteurPhotoUrl;

    public Long getId() { return id; }
    public String getContenu() { return contenu; }
    public String getDate() { return date; }
    public String getAuteurNom() { return auteurNom != null ? auteurNom : "Utilisateur"; }
    public String getAuteurPhotoUrl() { return auteurPhotoUrl; }
}
