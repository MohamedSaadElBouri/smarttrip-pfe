package com.example.smarttripvoyager.data.model;

import java.util.List;

/** Reflete CircuitDTO cote Spring Boot (/api/v1/circuits/{id}). */
public class Circuit {
    private Long id;
    private String titre;
    private String description;
    private String theme;
    private String ville;
    private String photoUrl;
    private String statut;
    private Integer dureeJours;
    private Double prixEstime;
    private Double noteMoyenne;
    private Integer nombreAvis;
    private List<Etape> etapes;

    public Long getId() { return id; }
    public String getTitre() { return titre; }
    public String getDescription() { return description; }
    public String getTheme() { return theme; }
    public String getVille() { return ville; }
    public String getPhotoUrl() { return photoUrl; }
    public String getStatut() { return statut; }
    public Integer getDureeJours() { return dureeJours; }
    public Double getPrixEstime() { return prixEstime; }
    public Double getNoteMoyenne() { return noteMoyenne; }
    public Integer getNombreAvis() { return nombreAvis; }
    public List<Etape> getEtapes() { return etapes; }

    public static class Etape {
        private Long id;
        private Integer ordre;
        private String heureVisite;
        private String notes;
        private Integer dureeMinutes;
        private LieuSimple lieu;

        public Long getId() { return id; }
        public Integer getOrdre() { return ordre; }
        public String getHeureVisite() { return heureVisite; }
        public String getNotes() { return notes; }
        public Integer getDureeMinutes() { return dureeMinutes; }
        public LieuSimple getLieu() { return lieu; }
    }

    public static class LieuSimple {
        private Long id;
        private String nom;
        private String categorie;
        private String ville;
        private String photoUrl;
        private Double latitude;
        private Double longitude;

        public Long getId() { return id; }
        public String getNom() { return nom; }
        public String getCategorie() { return categorie; }
        public String getVille() { return ville; }
        public String getPhotoUrl() { return photoUrl; }
        public Double getLatitude() { return latitude; }
        public Double getLongitude() { return longitude; }
    }
}
