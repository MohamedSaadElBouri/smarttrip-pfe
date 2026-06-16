package com.example.smarttripvoyager.data.model;

public class UpdatePreferencesRequest {
    private String preferences;
    private Boolean consentementGeolocalisation;
    private String langue;

    public UpdatePreferencesRequest(String preferences, Boolean consentementGeolocalisation, String langue) {
        this.preferences = preferences;
        this.consentementGeolocalisation = consentementGeolocalisation;
        this.langue = langue;
    }

    public String getPreferences() { return preferences; }
    public void setPreferences(String preferences) { this.preferences = preferences; }

    public Boolean getConsentementGeolocalisation() { return consentementGeolocalisation; }
    public void setConsentementGeolocalisation(Boolean consentementGeolocalisation) { this.consentementGeolocalisation = consentementGeolocalisation; }

    public String getLangue() { return langue; }
    public void setLangue(String langue) { this.langue = langue; }
}
