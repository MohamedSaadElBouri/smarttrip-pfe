package com.example.smarttripvoyager.ui.questionnaire;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smarttripvoyager.R;
import com.example.smarttripvoyager.data.model.TouristProfileRequest;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import android.widget.AutoCompleteTextView;

public class QuestionnaireActivity extends AppCompatActivity {

    /** Option affichee en francais dans le spinner, associee a la valeur attendue par l'IA. */
    private static class Option {
        final String label;
        final String value;

        Option(String label, String value) {
            this.label = label;
            this.value = value;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    private AutoCompleteTextView etNationality;
    private Spinner spinnerAgeGroup, spinnerGender, spinnerHasVisited;
    private Spinner spinnerGroupType, spinnerGroupSize;
    private Spinner spinnerBudget, spinnerDuration, spinnerStyle, spinnerActivityLevel, spinnerNoise, spinnerSeason;
    private Spinner spinnerFood, spinnerAccommodation, spinnerTransport, spinnerSpecialNeeds;
    private Chip chipHistory, chipNature, chipFood, chipAdventure, chipPhotography, chipWellness, chipShopping, chipFestivals;
    private MaterialButton btnGenerate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questionnaire);

        etNationality = findViewById(R.id.etNationality);
        spinnerAgeGroup = findViewById(R.id.spinnerAgeGroup);
        spinnerGender = findViewById(R.id.spinnerGender);
        spinnerHasVisited = findViewById(R.id.spinnerHasVisited);
        spinnerGroupType = findViewById(R.id.spinnerGroupType);
        spinnerGroupSize = findViewById(R.id.spinnerGroupSize);
        spinnerBudget = findViewById(R.id.spinnerBudget);
        spinnerDuration = findViewById(R.id.spinnerDuration);
        spinnerStyle = findViewById(R.id.spinnerStyle);
        spinnerActivityLevel = findViewById(R.id.spinnerActivityLevel);
        spinnerNoise = findViewById(R.id.spinnerNoise);
        spinnerSeason = findViewById(R.id.spinnerSeason);
        spinnerFood = findViewById(R.id.spinnerFood);
        spinnerAccommodation = findViewById(R.id.spinnerAccommodation);
        spinnerTransport = findViewById(R.id.spinnerTransport);
        spinnerSpecialNeeds = findViewById(R.id.spinnerSpecialNeeds);

        chipHistory = findViewById(R.id.chipHistory);
        chipNature = findViewById(R.id.chipNature);
        chipFood = findViewById(R.id.chipFood);
        chipAdventure = findViewById(R.id.chipAdventure);
        chipPhotography = findViewById(R.id.chipPhotography);
        chipWellness = findViewById(R.id.chipWellness);
        chipShopping = findViewById(R.id.chipShopping);
        chipFestivals = findViewById(R.id.chipFestivals);

        btnGenerate = findViewById(R.id.btnGenerate);

        setupNationality();
        setupSpinners();

        btnGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generateTrip();
            }
        });
    }

    private void setupNationality() {
        String[] countries = {"France", "Espagne", "États-Unis", "Canada", "Royaume-Uni", "Allemagne", "Italie", "Japon", "Chine", "Brésil", "Maroc", "Algérie", "Tunisie", "Égypte", "Sénégal", "Côte d'Ivoire", "Belgique", "Suisse", "Russie", "Australie"};
        ArrayAdapter<String> countryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, countries);
        etNationality.setAdapter(countryAdapter);
    }

    private void setupSpinners() {
        setOptions(spinnerAgeGroup, new Option[]{
                new Option("18-24 ans", "18-24"),
                new Option("25-34 ans", "25-34"),
                new Option("35-44 ans", "35-44"),
                new Option("45-54 ans", "45-54"),
                new Option("55-64 ans", "55-64"),
                new Option("65 ans et plus", "65+"),
        });

        setOptions(spinnerGender, new Option[]{
                new Option("Homme", "male"),
                new Option("Femme", "female"),
                new Option("Autre", "other"),
        });

        ArrayAdapter<String> yesNoAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{"Non", "Oui"});
        yesNoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerHasVisited.setAdapter(yesNoAdapter);

        setOptions(spinnerGroupType, new Option[]{
                new Option("Solo", "solo"),
                new Option("Couple", "couple"),
                new Option("Famille", "family"),
                new Option("Amis", "friends"),
                new Option("Groupe organisé", "group_tour"),
        });

        setOptions(spinnerGroupSize, new Option[]{
                new Option("1 personne", "1"),
                new Option("2 personnes", "2"),
                new Option("3 à 5 personnes", "3-5"),
                new Option("6 à 10 personnes", "6-10"),
        });

        setOptions(spinnerBudget, new Option[]{
                new Option("Économique", "budget"),
                new Option("Intermédiaire", "mid_range"),
                new Option("Confort", "comfort"),
                new Option("Luxe", "luxury"),
        });

        setOptions(spinnerDuration, new Option[]{
                new Option("1 à 2 jours", "1-2"),
                new Option("3 à 5 jours", "3-5"),
                new Option("6 à 7 jours", "6-7"),
                new Option("8 à 14 jours", "8-14"),
        });

        setOptions(spinnerStyle, new Option[]{
                new Option("Culturel", "cultural"),
                new Option("Aventure", "adventure"),
                new Option("Détente", "relaxation"),
                new Option("Gastronomie", "foodie"),
                new Option("Photographie", "photography"),
                new Option("Spirituel", "spiritual"),
        });

        setOptions(spinnerActivityLevel, new Option[]{
                new Option("Faible", "low"),
                new Option("Modéré", "moderate"),
                new Option("Élevé", "high"),
        });

        setOptions(spinnerNoise, new Option[]{
                new Option("Animée", "lively"),
                new Option("Modérée", "moderate"),
                new Option("Calme", "quiet"),
        });

        setOptions(spinnerSeason, new Option[]{
                new Option("Printemps", "spring"),
                new Option("Été", "summer"),
                new Option("Automne", "fall"),
                new Option("Hiver", "winter"),
        });

        setOptions(spinnerFood, new Option[]{
                new Option("Marocaine locale", "local_moroccan"),
                new Option("Internationale", "international"),
                new Option("Végétarienne", "vegetarian"),
                new Option("Halal strict", "halal_strict"),
                new Option("Pas de préférence", "no_preference"),
        });

        setOptions(spinnerAccommodation, new Option[]{
                new Option("Riad", "riad"),
                new Option("Hôtel 3 étoiles", "hotel_3star"),
                new Option("Hôtel 5 étoiles", "hotel_5star"),
                new Option("Auberge", "hostel"),
                new Option("Airbnb", "airbnb"),
                new Option("Camping", "camping"),
        });

        setOptions(spinnerTransport, new Option[]{
                new Option("Mixte", "mixed"),
                new Option("Chauffeur privé", "private_driver"),
                new Option("Transport public", "public_transport"),
                new Option("Voiture de location", "rental_car"),
                new Option("À pied", "walking"),
        });

        setOptions(spinnerSpecialNeeds, new Option[]{
                new Option("Aucun", "none"),
                new Option("Accessibilité (PMR)", "accessibility"),
                new Option("Adapté aux enfants", "child_friendly"),
                new Option("Adapté aux seniors", "elderly_friendly"),
        });
    }

    private void setOptions(Spinner spinner, Option[] options) {
        ArrayAdapter<Option> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, options);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private String valueOf(Spinner spinner) {
        Object selected = spinner.getSelectedItem();
        return selected instanceof Option ? ((Option) selected).value : null;
    }

    private void generateTrip() {
        Toast.makeText(this, "Génération de votre voyage idéal...", Toast.LENGTH_LONG).show();

        TouristProfileRequest request = new TouristProfileRequest();
        if (!etNationality.getText().toString().isEmpty()) {
            request.nationality = etNationality.getText().toString();
        }

        request.ageGroup = valueOf(spinnerAgeGroup);
        request.gender = valueOf(spinnerGender);
        request.hasVisitedBefore = spinnerHasVisited.getSelectedItemPosition();

        request.groupType = valueOf(spinnerGroupType);
        request.groupSize = valueOf(spinnerGroupSize);

        request.budgetLevel = valueOf(spinnerBudget);
        request.tripDurationDays = valueOf(spinnerDuration);
        request.travelStyle = valueOf(spinnerStyle);
        request.activityLevel = valueOf(spinnerActivityLevel);
        request.noisePreference = valueOf(spinnerNoise);
        request.preferredSeason = valueOf(spinnerSeason);

        request.interestHistory = chipHistory.isChecked() ? 1 : 0;
        request.interestNature = chipNature.isChecked() ? 1 : 0;
        request.interestFood = chipFood.isChecked() ? 1 : 0;
        request.interestAdventure = chipAdventure.isChecked() ? 1 : 0;
        request.interestPhotography = chipPhotography.isChecked() ? 1 : 0;
        request.interestWellness = chipWellness.isChecked() ? 1 : 0;
        request.interestShopping = chipShopping.isChecked() ? 1 : 0;
        request.interestFestivals = chipFestivals.isChecked() ? 1 : 0;

        request.foodPreference = valueOf(spinnerFood);
        request.accommodationType = valueOf(spinnerAccommodation);
        request.transportPreference = valueOf(spinnerTransport);
        request.specialNeeds = valueOf(spinnerSpecialNeeds);

        Intent intent = new Intent(this, LoadingAiActivity.class);
        intent.putExtra("request_json", new com.google.gson.Gson().toJson(request));
        startActivity(intent);
    }
}
