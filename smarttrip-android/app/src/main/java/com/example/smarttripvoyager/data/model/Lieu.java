package com.example.smarttripvoyager.data.model;

public class Lieu {
    private Long id;
    private String nom;
    private String categorie;
    private String ville;
    private Double noteMoyenne;
    private String photoUrl;
    private String prixEntree;
    private String horaires;
    private String storytelling;

    public Long getId() { return id; }
    public String getNom() { return nom; }
    public String getCategorie() { return categorie; }
    public String getVille() { return ville; }
    public Double getNoteMoyenne() { return noteMoyenne; }
    public String getPhotoUrl() { return photoUrl; }
    public String getPrixEntree() { return prixEntree; }
    public String getHoraires() { return horaires; }
    public String getStorytelling() { return storytelling; }
}
