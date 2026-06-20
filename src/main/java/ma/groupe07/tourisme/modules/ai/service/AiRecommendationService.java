package ma.groupe07.tourisme.modules.ai.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.groupe07.tourisme.modules.ai.dto.FeedPostRequest;
import ma.groupe07.tourisme.modules.ai.dto.TouristProfileRequest;
import ma.groupe07.tourisme.modules.ai.model.RecommandationIA;
import ma.groupe07.tourisme.modules.ai.model.SignalComportement;
import ma.groupe07.tourisme.modules.ai.repository.RecommandationIARepository;
import ma.groupe07.tourisme.modules.ai.repository.SignalComportementRepository;
import ma.groupe07.tourisme.modules.auth.model.Utilisateur;
import ma.groupe07.tourisme.modules.auth.repository.UtilisateurRepository;
import ma.groupe07.tourisme.modules.circuit.model.AdhesionCircuit;
import ma.groupe07.tourisme.modules.circuit.model.Circuit;
import ma.groupe07.tourisme.modules.circuit.repository.AdhesionCircuitRepository;
import ma.groupe07.tourisme.modules.circuit.repository.CircuitRepository;
import ma.groupe07.tourisme.modules.lieu.model.Lieu;
import ma.groupe07.tourisme.modules.lieu.repository.LieuRepository;
import ma.groupe07.tourisme.modules.publication.model.Commentaire;
import ma.groupe07.tourisme.modules.publication.model.LikePublication;
import ma.groupe07.tourisme.modules.publication.model.Publication;
import ma.groupe07.tourisme.modules.publication.model.SauvegardePublication;
import ma.groupe07.tourisme.modules.publication.repository.CommentaireRepository;
import ma.groupe07.tourisme.modules.publication.repository.LikePublicationRepository;
import ma.groupe07.tourisme.modules.publication.repository.PublicationRepository;
import ma.groupe07.tourisme.modules.publication.repository.SauvegardePublicationRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AiRecommendationService {

    private final RestTemplate aiRestTemplate;
    private final CircuitRepository circuitRepository;
    private final LikePublicationRepository likePublicationRepository;
    private final SauvegardePublicationRepository sauvegardePublicationRepository;
    private final CommentaireRepository commentaireRepository;
    private final AdhesionCircuitRepository adhesionCircuitRepository;
    private final SignalComportementRepository signalComportementRepository;
    private final PublicationRepository publicationRepository;
    private final LieuRepository lieuRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final RecommandationIARepository recommandationIARepository;

    @Value("${app.ai.api.url}")
    private String aiApiUrl;

    /** Themes consideres "match" pour chaque style de voyage du questionnaire. */
    private static final Map<String, Set<String>> STYLE_THEMES = Map.of(
            "cultural", Set.of("Culture", "Histoire", "Photographie", "Artisanat", "Festivals"),
            "adventure", Set.of("Aventure", "Nature"),
            "relaxation", Set.of("Détente", "Bien-être", "Gastronomie"),
            "foodie", Set.of("Gastronomie"),
            "photography", Set.of("Photographie", "Nature"),
            "spiritual", Set.of("Culture", "Histoire", "Bien-être")
    );

    /** Themes consideres "match" pour chaque type d'experience suggere par /api/recommend-experience. */
    private static final Map<String, Set<String>> EXPERIENCE_THEMES = Map.ofEntries(
            Map.entry("cultural_immersion", Set.of("Culture", "Artisanat", "Festivals")),
            Map.entry("nature_escape", Set.of("Nature", "Aventure", "Détente")),
            Map.entry("food_tour", Set.of("Gastronomie")),
            Map.entry("adventure_sport", Set.of("Aventure")),
            Map.entry("wellness_spa", Set.of("Bien-être", "Détente")),
            Map.entry("historical_exploration", Set.of("Histoire")),
            Map.entry("festival_event", Set.of("Festivals")),
            Map.entry("photography_tour", Set.of("Photographie", "Nature"))
    );

    /** Libelle FR pour chaque type d'experience suggere par /api/recommend-experience. */
    private static final Map<String, String> EXPERIENCE_LABEL_FR = Map.ofEntries(
            Map.entry("cultural_immersion", "l'immersion culturelle"),
            Map.entry("nature_escape", "l'évasion nature"),
            Map.entry("food_tour", "la découverte gastronomique"),
            Map.entry("adventure_sport", "l'aventure sportive"),
            Map.entry("wellness_spa", "le bien-être et la détente"),
            Map.entry("historical_exploration", "l'exploration historique"),
            Map.entry("festival_event", "les festivals et événements"),
            Map.entry("photography_tour", "la photographie")
    );

    /** region (FR, accents) -> ville attendue par les encodeurs IA (sans accents). */
    private static final Map<String, String> CITY_NORMALIZATION = Map.of(
            "Fès", "Fes",
            "Meknès", "Meknes",
            "Azrou", "Azrou",
            "Ifrane", "Ifrane"
    );

    /** Theme du circuit -> flag interest_* correspondant dans le profil. */
    private static final Map<String, String> THEME_INTEREST = Map.ofEntries(
            Map.entry("Culture", "interestHistory"),
            Map.entry("Histoire", "interestHistory"),
            Map.entry("Nature", "interestNature"),
            Map.entry("Gastronomie", "interestFood"),
            Map.entry("Aventure", "interestAdventure"),
            Map.entry("Photographie", "interestPhotography"),
            Map.entry("Bien-être", "interestWellness"),
            Map.entry("Détente", "interestWellness"),
            Map.entry("Artisanat", "interestShopping"),
            Map.entry("Festivals", "interestFestivals")
    );

    /** Categorie de lieu (restaurant, hotel, monument...) -> dimension d'engagement. */
    private static final Map<String, String> LIEU_CATEGORY_TO_ENGAGEMENT = Map.ofEntries(
            Map.entry("MONUMENT", "history"),
            Map.entry("PATRIMOINE", "history"),
            Map.entry("RUINES", "history"),
            Map.entry("RELIGION", "history"),
            Map.entry("PLACE", "culture"),
            Map.entry("MUSEE", "culture"),
            Map.entry("ARTISANAT", "culture"),
            Map.entry("NATURE", "nature"),
            Map.entry("ACTIVITE", "adventure"),
            Map.entry("RESTAURANT", "food"),
            Map.entry("HOTEL", "wellness")
    );

    /** Theme de circuit -> dimension d'engagement (circuits sauvegardes ou consultes). */
    private static final Map<String, String> CIRCUIT_THEME_TO_ENGAGEMENT = Map.ofEntries(
            Map.entry("Culture", "culture"),
            Map.entry("Histoire", "history"),
            Map.entry("Nature", "nature"),
            Map.entry("Gastronomie", "food"),
            Map.entry("Aventure", "adventure"),
            Map.entry("Photographie", "culture"),
            Map.entry("Bien-être", "wellness"),
            Map.entry("Détente", "wellness"),
            Map.entry("Artisanat", "culture"),
            Map.entry("Festivals", "culture")
    );

    /** Libelles FR des dimensions d'engagement, utilises dans le texte "raison". */
    private static final Map<String, String> ENGAGEMENT_LABEL_FR = Map.of(
            "food", "la gastronomie",
            "culture", "la culture",
            "nature", "la nature",
            "adventure", "l'aventure",
            "history", "l'histoire",
            "wellness", "le bien-être"
    );

    /** Libelles FR des styles de voyage du questionnaire. */
    private static final Map<String, String> TRAVEL_STYLE_LABELS_FR = Map.of(
            "cultural", "culturel",
            "adventure", "aventure",
            "relaxation", "détente",
            "foodie", "gastronomique",
            "photography", "photographie",
            "spiritual", "spirituel"
    );

    /** Libelles FR des niveaux de budget du questionnaire. */
    private static final Map<String, String> BUDGET_LABELS_FR = Map.of(
            "budget", "économique",
            "mid_range", "intermédiaire",
            "comfort", "confort",
            "luxury", "élevé"
    );

    /**
     * Orchestration complete : recommend-city + recommend-experience + recommend-trips
     * (avec les circuits publies de la DB comme candidate_trips), puis reclassement
     * cote Java en fonction du profil du questionnaire pour garantir des resultats
     * varies selon les reponses (style, budget, duree, centres d'interet).
     * Chaque appel Flask est enveloppe dans son propre try-catch : si Flask est
     * indisponible, la logique Java prend le relais et le classement reste sensible
     * aux reponses du questionnaire.
     * Format de retour aligne sur RecommendationResponse cote Android
     * (recommended_city, recommended_experience, top_trips).
     */
    @Transactional
    public Map<String, Object> getFullRecommendation(TouristProfileRequest profile, Long userId) {
        applyUserReactions(profile, userId);

        // Chaque appel Flask est isole : une defaillance ne bloque pas les suivants.
        String city = "Fes";
        try {
            String flaskCity = recommendCity(profile);
            if (flaskCity != null && !flaskCity.isBlank()) city = flaskCity;
        } catch (Exception e) {
            log.warn("Flask recommend-city indisponible, ville par defaut: Fes");
        }

        String flaskExperience = null;
        try {
            flaskExperience = recommendExperience(profile);
        } catch (Exception e) {
            log.warn("Flask recommend-experience indisponible");
        }

        // Derive experience from questionnaire answers (not from engagement-biased Flask model)
        String experience = deriveExperienceFromQuestionnaire(profile, flaskExperience);

        List<Circuit> candidates = circuitRepository.findByStatut("PUBLIE");
        profile.setCandidateTrips(buildCandidateTrips(candidates));

        List<Map<String, Object>> trips;
        try {
            trips = recommendTrips(profile);
        } catch (Exception e) {
            log.warn("Flask recommend-trips indisponible, classement Java uniquement sur {} circuits", candidates.size());
            trips = buildJavaOnlyTrips(candidates);
        }

        List<Map<String, Object>> topTrips = rerankByProfile(trips, candidates, profile, city, experience);

        List<Long> topIds = topTrips.stream()
                .map(t -> toLong(t.get("trip_id"))).collect(Collectors.toList());
        List<String> topThemes = topTrips.stream()
                .map(t -> { Long id = toLong(t.get("trip_id"));
                            Circuit c = candidates.stream().filter(x -> x.getId().equals(id)).findFirst().orElse(null);
                            return c != null ? c.getTheme() : "?"; })
                .collect(Collectors.toList());
        log.info("AI Reco userId={} style={} budget={} interests={}|{}|{}|{}|{}|{}|{}|{} flaskExp={} derivedExp={} topTrips={} themes={}",
                userId, profile.getTravelStyle(), profile.getBudgetLevel(),
                profile.getInterestHistory(), profile.getInterestNature(), profile.getInterestFood(),
                profile.getInterestAdventure(), profile.getInterestPhotography(), profile.getInterestWellness(),
                profile.getInterestShopping(), profile.getInterestFestivals(),
                flaskExperience, experience, topIds, topThemes);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("recommended_city", city);
        // Return French label so the UI displays a meaningful string (not raw English key)
        result.put("recommended_experience", EXPERIENCE_LABEL_FR.getOrDefault(experience, experience));
        result.put("top_trips", topTrips);

        persistRecommendation(profile, userId, topTrips);
        return result;
    }

    /**
     * Construit une liste de "trips" initiaux sans score Flask : chaque circuit
     * recoit un score de base egal a sa note moyenne (ou 3.0 par defaut).
     * Utilise quand Flask est indisponible : rerankByProfile() applique ensuite
     * le classement base sur le profil du questionnaire, garantissant que des
     * reponses differentes produisent des top 5 differents meme sans IA Flask.
     */
    private List<Map<String, Object>> buildJavaOnlyTrips(List<Circuit> candidates) {
        return candidates.stream()
                .map(c -> {
                    Map<String, Object> trip = new LinkedHashMap<>();
                    trip.put("trip_id", c.getId());
                    trip.put("trip_name", c.getTitre());
                    trip.put("ai_match_score", c.getNoteMoyenne() != null ? c.getNoteMoyenne() : 3.0);
                    return trip;
                })
                .collect(Collectors.toList());
    }

    /**
     * Derive le type d'experience a recommander en priorite depuis les reponses
     * explicites du questionnaire (style de voyage + coches d'interet).
     * En cas d'ambiguite (pas de coches), utilise l'engagement reel de l'utilisateur
     * comme signal de differenciation pour eviter de toujours retourner "cultural_immersion".
     */
    private String deriveExperienceFromQuestionnaire(TouristProfileRequest profile, String flaskFallback) {
        String style = profile.getTravelStyle() != null ? profile.getTravelStyle().toLowerCase().trim() : "";

        // Priority 1: travel style + interest chips (most explicit user choices)
        switch (style) {
            case "spiritual":
                // Spiritual travel in Morocco is strongly tied to history (medersa, mosques, Volubilis).
                // Default to historical_exploration, not cultural_immersion, to produce a distinct result.
                if (profile.getInterestHistory() == 1)  return "historical_exploration";
                if (profile.getInterestWellness() == 1) return "wellness_spa";
                return "historical_exploration";

            case "adventure":
                if (profile.getInterestAdventure() == 1) return "adventure_sport";
                return profile.getInterestNature() == 1 ? "nature_escape" : "adventure_sport";

            case "relaxation":
                if (profile.getInterestNature() == 1)   return "nature_escape";
                if (profile.getInterestWellness() == 1) return "wellness_spa";
                return "wellness_spa";

            case "foodie":
                return "food_tour";

            case "photography":
                return profile.getInterestNature() == 1 ? "photography_tour" : "photography_tour";

            case "cultural":
                if (profile.getInterestHistory() == 1)     return "historical_exploration";
                if (profile.getInterestNature() == 1)      return "nature_escape";
                if (profile.getInterestFood() == 1)        return "food_tour";
                if (profile.getInterestAdventure() == 1)   return "adventure_sport";
                if (profile.getInterestWellness() == 1)    return "wellness_spa";
                if (profile.getInterestPhotography() == 1) return "photography_tour";
                if (profile.getInterestFestivals() == 1)   return "festival_event";
                // No chips: use engagement to disambiguate, then fall through to historical
                String cultEng = engagementToExperience(profile);
                return cultEng != null ? cultEng : "historical_exploration";
        }

        // Priority 2: interest chips only (no style matched above)
        if (profile.getInterestAdventure() == 1)   return "adventure_sport";
        if (profile.getInterestNature() == 1)      return "nature_escape";
        if (profile.getInterestHistory() == 1)     return "historical_exploration";
        if (profile.getInterestFood() == 1)        return "food_tour";
        if (profile.getInterestWellness() == 1)    return "wellness_spa";
        if (profile.getInterestFestivals() == 1)   return "festival_event";
        if (profile.getInterestPhotography() == 1) return "photography_tour";
        if (profile.getInterestShopping() == 1)    return "cultural_immersion";

        // Priority 3: real engagement history (already computed by applyUserReactions)
        String engBased = engagementToExperience(profile);
        if (engBased != null) return engBased;

        // Priority 4: Flask model, then final fallback to historical (more specific than cultural_immersion)
        return flaskFallback != null && !flaskFallback.isBlank() ? flaskFallback : "historical_exploration";
    }

    /**
     * Derive un type d'experience depuis la categorie la plus engagee de l'utilisateur.
     * Appele apres applyUserReactions() — topEngagedCategory est deja calcule.
     * Retourne null si aucun signal d'engagement n'est disponible.
     */
    private String engagementToExperience(TouristProfileRequest profile) {
        String top = profile.getTopEngagedCategory();
        if (top == null || top.isBlank()) return null;
        switch (top.toLowerCase()) {
            case "food":      return "food_tour";
            case "nature":    return "nature_escape";
            case "adventure": return "adventure_sport";
            case "wellness":  return "wellness_spa";
            case "history":   return "historical_exploration";
            case "culture":   return "cultural_immersion";
            default:          return null;
        }
    }

    /**
     * Normalise le theme d'un circuit depuis la valeur brute stockee en DB.
     * Gere les valeurs multi-themes (ex: "CULTURE,HISTOIRE") en prenant le premier,
     * et les variantes d'ecriture (majuscules, accents) pour les faire correspondre
     * aux cles utilisees dans les tables de scoring (STYLE_THEMES, EXPERIENCE_THEMES...).
     */
    private String normalizeTheme(String raw) {
        if (raw == null || raw.isBlank()) return raw;
        String first = raw.split(",")[0].trim();
        if (first.isEmpty()) return raw;
        switch (first.toUpperCase()) {
            case "CULTURE":      return "Culture";
            case "HISTOIRE":
            case "PATRIMOINE":   return "Histoire";
            case "NATURE":       return "Nature";
            case "GASTRONOMIE":  return "Gastronomie";
            case "AVENTURE":     return "Aventure";
            case "BIEN-ÊTRE":
            case "BIEN-ETRE":    return "Bien-être";
            case "DETENTE":
            case "DÉTENTE":      return "Détente";
            case "ARTISANAT":    return "Artisanat";
            case "PHOTOGRAPHIE": return "Photographie";
            case "FESTIVALS":    return "Festivals";
            default:
                // Title-case first word as fallback
                return Character.toUpperCase(first.charAt(0)) + first.substring(1).toLowerCase();
        }
    }

    /**
     * Remplace les signaux d'engagement par defaut du profil par le veritable
     * comportement de l'utilisateur: publications aimees, sauvegardees ou
     * commentees, circuits sauvegardes (adhesions), et lieux/circuits
     * consultes (vues tracees via /api/v1/interactions). Ainsi la
     * recommandation evolue dès qu'une de ces interactions change.
     */
    private void applyUserReactions(TouristProfileRequest profile, Long userId) {
        if (userId == null) return;

        EngagementSignals signals = computeEngagementSignals(userId);
        if (signals.allScores().isEmpty()) return;

        Map<String, List<Double>> scoresByCategory = signals.scoresByCategory();
        int totalSignals = signals.allScores().size();

        profile.setTotalPostsEngaged(totalSignals);
        profile.setTotalPostsViewed(Math.max(profile.getTotalPostsViewed(), totalSignals * 3));
        profile.setAvgEngagementScore(average(signals.allScores()));

        scoresByCategory.entrySet().stream()
                .max(Comparator.comparingDouble(e -> e.getValue().stream().mapToDouble(Double::doubleValue).sum()))
                .ifPresent(e -> profile.setTopEngagedCategory(e.getKey()));

        signals.cityCounts().entrySet().stream()
                .max(Comparator.comparingLong(Map.Entry::getValue))
                .ifPresent(e -> profile.setTopEngagedCity(e.getKey()));

        profile.setEngagementFood(average(scoresByCategory.get("food")));
        profile.setEngagementCulture(average(scoresByCategory.get("culture")));
        profile.setEngagementNature(average(scoresByCategory.get("nature")));
        profile.setEngagementAdventure(average(scoresByCategory.get("adventure")));
        profile.setEngagementHistory(average(scoresByCategory.get("history")));
        profile.setEngagementWellness(average(scoresByCategory.get("wellness")));
    }

    /**
     * Calcule, pour un utilisateur, le score d'engagement moyen (0-100) par
     * categorie a partir de ses interactions reelles (likes, sauvegardes,
     * commentaires, adhesions, vues de lieux/circuits). Reutilise par
     * {@link #applyUserReactions} pour les recommandations de circuits, et
     * par le classement du fil d'actualite (PublicationService), afin que
     * toute nouvelle reaction influence a la fois les circuits suggeres et
     * l'ordre du feed.
     */
    /** Demi-vie (en heures) de la ponderation par recence du score de feed : un signal
     *  perd la moitie de son poids toutes les {@code FEED_SIGNAL_HALF_LIFE_HOURS} heures,
     *  afin qu'un gros historique d'engagement ancien ne masque plus indefiniment des
     *  reactions recentes (likes/dislikes) sur d'autres categories. */
    private static final double FEED_SIGNAL_HALF_LIFE_HOURS = 12.0;

    private record FeedSignal(String category, double score, LocalDateTime date) {
    }

    /**
     * Retourne le score d'engagement NET (signe, pondere par recence) par categorie,
     * utilise uniquement par le classement du fil d'actualite (PublicationService).
     * Contrairement a {@link #computeEngagementSignals} (utilise par les recommandations
     * de circuits, score cumulatif non pondere), cette methode :
     * - inclut les "dislikes" (retrait d'un like) comme signal NEGATIF explicite ;
     * - applique une decroissance exponentielle par anciennete de chaque signal, pour
     *   que de nouvelles reactions fassent bouger le classement rapidement, meme face
     *   a un gros historique d'interactions plus anciennes.
     */
    public Map<String, Double> getCategoryEngagementScores(Long userId) {
        if (userId == null) return Map.of();
        LocalDateTime now = LocalDateTime.now();
        Map<String, Double> scores = new HashMap<>();
        for (FeedSignal signal : collectFeedSignals(userId)) {
            if (signal.date() == null) continue;
            double ageHours = Duration.between(signal.date(), now).toSeconds() / 3600.0;
            double decay = Math.pow(0.5, Math.max(0, ageHours) / FEED_SIGNAL_HALF_LIFE_HOURS);
            scores.merge(signal.category(), signal.score() * decay, Double::sum);
        }
        return scores;
    }

    /**
     * Nombre total de signaux d'engagement (likes, dislikes, sauvegardes, commentaires,
     * adhesions, vues) de l'utilisateur. Utilise par PublicationService pour
     * declencher un classement de feed plus reactif une fois un minimum
     * d'interactions atteint.
     */
    public int getEngagementSignalCount(Long userId) {
        if (userId == null) return 0;
        return collectFeedSignals(userId).size();
    }

    /** Recueille, avec leur date, tous les signaux (positifs et negatifs) utilises pour le score de feed. */
    private List<FeedSignal> collectFeedSignals(Long userId) {
        List<FeedSignal> signals = new ArrayList<>();

        for (LikePublication like : likePublicationRepository.findByUtilisateurIdWithPublication(userId)) {
            Publication pub = like.getPublication();
            if (pub == null || pub.getCategorie() == null || pub.getCategorie().isBlank()) continue;
            signals.add(new FeedSignal(pub.getCategorie(), 75.0, like.getDate()));
        }
        for (SauvegardePublication save : sauvegardePublicationRepository.findByUtilisateurIdWithPublication(userId)) {
            Publication pub = save.getPublication();
            if (pub == null || pub.getCategorie() == null || pub.getCategorie().isBlank()) continue;
            signals.add(new FeedSignal(pub.getCategorie(), 80.0, save.getDateSauvegarde()));
        }
        for (Commentaire comment : commentaireRepository.findByUtilisateurIdWithPublication(userId)) {
            Publication pub = comment.getPublication();
            if (pub == null || pub.getCategorie() == null || pub.getCategorie().isBlank()) continue;
            signals.add(new FeedSignal(pub.getCategorie(), 60.0, comment.getDate()));
        }
        for (AdhesionCircuit adhesion : adhesionCircuitRepository.findByUtilisateurId(userId)) {
            Circuit circuit = adhesion.getCircuit();
            if (circuit == null) continue;
            String cat = CIRCUIT_THEME_TO_ENGAGEMENT.get(normalizeTheme(circuit.getTheme()));
            if (cat == null) continue;
            signals.add(new FeedSignal(cat, 85.0, adhesion.getDateDebut()));
        }
        for (SignalComportement signal : signalComportementRepository.findByUtilisateurIdAndEntiteType(userId, "LIEU")) {
            Lieu lieu = lieuRepository.findById(signal.getEntiteId()).orElse(null);
            String cat = lieu == null ? null : LIEU_CATEGORY_TO_ENGAGEMENT.get(lieu.getCategorie());
            if (cat == null) continue;
            signals.add(new FeedSignal(cat, 40.0, signal.getDate()));
        }
        for (SignalComportement signal : signalComportementRepository.findByUtilisateurIdAndEntiteType(userId, "CIRCUIT")) {
            Circuit circuit = circuitRepository.findById(signal.getEntiteId()).orElse(null);
            String cat = circuit == null ? null : CIRCUIT_THEME_TO_ENGAGEMENT.get(normalizeTheme(circuit.getTheme()));
            if (cat == null) continue;
            signals.add(new FeedSignal(cat, 45.0, signal.getDate()));
        }
        // Dislike explicite (retrait d'un like) : signal negatif fort, enregistre par
        // PublicationService.toggleLike au moment ou l'utilisateur retire son like.
        for (SignalComportement signal : signalComportementRepository.findByUtilisateurIdAndEntiteType(userId, "PUBLICATION_DISLIKE")) {
            Publication pub = publicationRepository.findById(signal.getEntiteId()).orElse(null);
            if (pub == null || pub.getCategorie() == null || pub.getCategorie().isBlank()) continue;
            signals.add(new FeedSignal(pub.getCategorie(), -75.0, signal.getDate()));
        }
        return signals;
    }

    private record EngagementSignals(Map<String, List<Double>> scoresByCategory,
                                      Map<String, Long> cityCounts,
                                      List<Double> allScores) {
    }

    private EngagementSignals computeEngagementSignals(Long userId) {
        Map<String, List<Double>> scoresByCategory = new HashMap<>();
        Map<String, Long> cityCounts = new HashMap<>();
        List<Double> allScores = new ArrayList<>();

        // 1. Publications "likees" — score fixe 75 : le fait d'avoir like = signal fort,
        //    independamment de la popularite du post (sinon score quasi nul sur DB fraiche).
        for (LikePublication like : likePublicationRepository.findByUtilisateurIdWithPublication(userId)) {
            Publication pub = like.getPublication();
            if (pub == null) continue;
            addSignal(scoresByCategory, cityCounts, allScores, pub.getCategorie(),
                    CITY_NORMALIZATION.get(pub.getRegion()), 75.0);
        }

        // 2. Publications sauvegardees (signal d'interet fort)
        for (SauvegardePublication save : sauvegardePublicationRepository.findByUtilisateurIdWithPublication(userId)) {
            Publication pub = save.getPublication();
            if (pub == null) continue;
            addSignal(scoresByCategory, cityCounts, allScores, pub.getCategorie(),
                    CITY_NORMALIZATION.get(pub.getRegion()), 80.0);
        }

        // 3. Publications commentees
        for (Commentaire comment : commentaireRepository.findByUtilisateurIdWithPublication(userId)) {
            Publication pub = comment.getPublication();
            if (pub == null) continue;
            addSignal(scoresByCategory, cityCounts, allScores, pub.getCategorie(),
                    CITY_NORMALIZATION.get(pub.getRegion()), 60.0);
        }

        // 4. Circuits sauvegardes / rejoints (adhesions)
        for (AdhesionCircuit adhesion : adhesionCircuitRepository.findByUtilisateurId(userId)) {
            Circuit circuit = adhesion.getCircuit();
            if (circuit == null) continue;
            addSignal(scoresByCategory, cityCounts, allScores,
                    CIRCUIT_THEME_TO_ENGAGEMENT.get(normalizeTheme(circuit.getTheme())),
                    CITY_NORMALIZATION.get(circuit.getVille()), 85.0);
        }

        // 5. Lieux/restaurants/hotels/monuments consultes
        for (SignalComportement signal : signalComportementRepository.findByUtilisateurIdAndEntiteType(userId, "LIEU")) {
            Lieu lieu = lieuRepository.findById(signal.getEntiteId()).orElse(null);
            if (lieu == null) continue;
            addSignal(scoresByCategory, cityCounts, allScores,
                    LIEU_CATEGORY_TO_ENGAGEMENT.get(lieu.getCategorie()),
                    CITY_NORMALIZATION.get(lieu.getVille()), 40.0);
        }

        // 6. Circuits consultes (page de detail)
        for (SignalComportement signal : signalComportementRepository.findByUtilisateurIdAndEntiteType(userId, "CIRCUIT")) {
            Circuit circuit = circuitRepository.findById(signal.getEntiteId()).orElse(null);
            if (circuit == null) continue;
            addSignal(scoresByCategory, cityCounts, allScores,
                    CIRCUIT_THEME_TO_ENGAGEMENT.get(normalizeTheme(circuit.getTheme())),
                    CITY_NORMALIZATION.get(circuit.getVille()), 45.0);
        }

        return new EngagementSignals(scoresByCategory, cityCounts, allScores);
    }

    /** Ajoute un signal d'engagement (score, categorie, ville) aux agregats utilises par applyUserReactions. */
    private void addSignal(Map<String, List<Double>> scoresByCategory, Map<String, Long> cityCounts,
                            List<Double> allScores, String category, String city, double score) {
        allScores.add(score);
        if (category != null && !category.isBlank()) {
            scoresByCategory.computeIfAbsent(category, k -> new ArrayList<>()).add(score);
        }
        if (city != null) {
            cityCounts.merge(city, 1L, Long::sum);
        }
    }

    private double average(List<Double> values) {
        if (values == null || values.isEmpty()) return 0.0;
        return values.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
    }

    public String recommendCity(TouristProfileRequest profile) {
        Map<?, ?> response = aiRestTemplate.postForObject(aiApiUrl + "/api/recommend-city", profile, Map.class);
        return response != null ? (String) response.get("recommended_city") : null;
    }

    public String recommendExperience(TouristProfileRequest profile) {
        Map<?, ?> response = aiRestTemplate.postForObject(aiApiUrl + "/api/recommend-experience", profile, Map.class);
        return response != null ? (String) response.get("recommended_experience") : null;
    }

    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> recommendTrips(TouristProfileRequest profile) {
        Map<?, ?> response = aiRestTemplate.postForObject(aiApiUrl + "/api/recommend-trips", profile, Map.class);
        return response != null ? (List<Map<String, Object>>) response.get("recommended_trips") : List.of();
    }

    /** Appelle /api/rank-feed et renvoie chaque post avec son ai_ranking_score (et publication_id). */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> rankFeed(List<FeedPostRequest> posts) {
        if (posts.isEmpty()) return List.of();
        Map<String, Object> body = Map.of("posts", posts);
        Map<?, ?> response = aiRestTemplate.postForObject(aiApiUrl + "/api/rank-feed", body, Map.class);
        return response != null ? (List<Map<String, Object>>) response.get("ranked_feed") : List.of();
    }

    private List<Map<String, Object>> buildCandidateTrips(List<Circuit> candidates) {
        return candidates.stream()
                .map(this::toCandidateTrip)
                .collect(Collectors.toList());
    }

    /** Construit les 21 colonnes attendues par feature_columns['trips'] a partir d'un Circuit. */
    private Map<String, Object> toCandidateTrip(Circuit circuit) {
        String theme = normalizeTheme(circuit.getTheme());
        Integer days = circuit.getDureeJours() != null ? circuit.getDureeJours() : 1;
        Double prix = circuit.getPrixEstime() != null ? circuit.getPrixEstime() : 0.0;

        Map<String, Object> trip = new LinkedHashMap<>();
        trip.put("trip_id", circuit.getId());
        trip.put("trip_name", circuit.getTitre());
        trip.put("trip_type", days <= 1 ? "day_trip" : days <= 3 ? "weekend" : "multi_day");
        trip.put("duration_days", days);
        trip.put("main_city", circuit.getVille());
        trip.put("num_cities", 1);
        trip.put("category", theme);
        trip.put("difficulty_level", "Aventure".equals(theme) ? "challenging"
                : isRelaxTheme(theme) ? "easy" : "moderate");
        trip.put("budget_per_person_usd", prix);
        trip.put("budget_level", prix <= 500 ? "budget" : prix <= 2000 ? "mid_range" : "luxury");
        trip.put("season", "all");
        trip.put("ideal_group_type", "any");
        trip.put("ideal_group_size_min", 1);
        trip.put("ideal_group_size_max", 6);
        trip.put("ideal_climate_origin", "any");
        trip.put("ideal_noise_preference", isRelaxTheme(theme) ? "quiet" : "moderate");
        trip.put("ideal_activity_level", isRelaxTheme(theme) ? "low" : "Aventure".equals(theme) ? "high" : "moderate");
        trip.put("ideal_personality", personalityFor(theme));
        trip.put("includes_accommodation", days > 1 ? 1 : 0);
        trip.put("includes_transport", 1);
        trip.put("includes_meals", "Gastronomie".equals(theme) || days > 1 ? 1 : 0);
        trip.put("avg_rating", circuit.getNoteMoyenne());
        trip.put("num_reviews", circuit.getNombreAvis());
        trip.put("booking_count", circuit.getNombreAvis() != null ? circuit.getNombreAvis() * 3 : 0);
        return trip;
    }

    private boolean isRelaxTheme(String theme) {
        return "Détente".equals(theme) || "Bien-être".equals(theme) || "Gastronomie".equals(theme);
    }

    private String personalityFor(String theme) {
        if ("Détente".equals(theme) || "Bien-être".equals(theme)) return "calm";
        if ("Aventure".equals(theme)) return "adventurous";
        if ("Gastronomie".equals(theme)) return "social";
        return "cultural";
    }

    /**
     * Reclasse les circuits notes par Flask en combinant le score IA (base sur les
     * caracteristiques du circuit) avec un score de correspondance au profil du
     * questionnaire (style de voyage, budget, duree, centres d'interet). Ainsi,
     * deux profils differents obtiennent des top_trips differents meme si le
     * score IA brut est identique pour tous les circuits.
     */
    private List<Map<String, Object>> rerankByProfile(List<Map<String, Object>> trips,
                                                        List<Circuit> candidates,
                                                        TouristProfileRequest profile,
                                                        String recommendedCity,
                                                        String recommendedExperience) {
        Map<Long, Circuit> circuitsById = candidates.stream()
                .collect(Collectors.toMap(Circuit::getId, c -> c, (a, b) -> a));

        long signature = profileSignature(profile);

        for (Map<String, Object> trip : trips) {
            Long tripId = toLong(trip.get("trip_id"));
            Circuit circuit = tripId != null ? circuitsById.get(tripId) : null;

            double normalizedAi = Math.max(0, Math.min(1, toDouble(trip.get("ai_match_score")) / 5.0));

            double style = circuit != null ? styleScore(profile.getTravelStyle(), normalizeTheme(circuit.getTheme())) : 0.5;
            double budget = circuit != null ? budgetScore(profile.getBudgetLevel(), circuit.getPrixEstime()) : 0.5;
            double duration = circuit != null ? durationScore(profile.getTripDurationDays(), circuit.getDureeJours()) : 0.5;
            double interest = circuit != null ? interestScore(profile, normalizeTheme(circuit.getTheme())) : 0.5;
            double engagement = engagementScore(profile, circuit);
            double cityMatch = cityMatchScore(recommendedCity, circuit);
            double experienceMatch = experienceMatchScore(recommendedExperience, circuit);
            // Questionnaire-direct factors (style, budget, duration, interest, experienceMatch)
            // dominate strongly over engagement history. This ensures that changing even one
            // questionnaire answer shifts the ranking, and prevents past food engagement from
            // always surfacing food circuits when the user selected a different interest/style.
            double profileScore = circuit != null
                    ? 0.25 * style + 0.20 * budget + 0.10 * duration + 0.25 * interest
                            + 0.05 * engagement + 0.05 * cityMatch + 0.10 * experienceMatch
                    : 0.5;

            double finalScore = 0.20 * normalizedAi + 0.80 * profileScore;

            trip.put("ai_match_score", finalScore);
            trip.put("profile_match_score", profileScore);
            if (circuit != null) {
                trip.put("trip_title", circuit.getTitre() != null ? circuit.getTitre() : "Circuit personnalisé");
                trip.put("trip_description", circuit.getDescription() != null ? circuit.getDescription() : circuit.getTheme());
                trip.put("duration_days", circuit.getDureeJours());
                trip.put("estimated_budget", circuit.getPrixEstime());
            }
            addReasonAndCategories(trip, profile, circuit, style, budget, duration, interest,
                    engagement, cityMatch, experienceMatch, recommendedCity, recommendedExperience);
        }

        List<Map<String, Object>> sorted = trips.stream()
                .sorted(Comparator.comparingDouble((Map<String, Object> t) -> toDouble(t.get("ai_match_score"))).reversed()
                        .thenComparingLong(t -> tieBreak(toLong(t.get("trip_id")), signature)))
                .collect(Collectors.toList());

        // Enforce theme diversity: at most 2 circuits of the same theme in the final top-5.
        // Prevents all 5 slots being occupied by the same category even when one theme dominates scoring.
        return enforceThemeDiversity(sorted, circuitsById);
    }

    /**
     * Selectionne les 5 meilleurs circuits en imposant qu'au maximum 2 circuits
     * partagent le meme theme. Les circuits "en trop" du theme dominant sont
     * remplaces par les meilleurs circuits d'autres themes, preservant ainsi la
     * diversite de la recommandation quel que soit le profil de l'utilisateur.
     */
    private List<Map<String, Object>> enforceThemeDiversity(List<Map<String, Object>> ranked,
                                                              Map<Long, Circuit> circuitsById) {
        final int MAX_SAME_THEME = 2;
        Map<String, Integer> themeCount = new HashMap<>();
        List<Map<String, Object>> primary = new ArrayList<>(5);
        List<Map<String, Object>> overflow = new ArrayList<>();

        for (Map<String, Object> trip : ranked) {
            Long id = toLong(trip.get("trip_id"));
            Circuit c = circuitsById.get(id);
            String theme = (c != null && c.getTheme() != null) ? normalizeTheme(c.getTheme()) : "_";
            int cnt = themeCount.getOrDefault(theme, 0);
            if (cnt < MAX_SAME_THEME) {
                primary.add(trip);
                themeCount.put(theme, cnt + 1);
            } else {
                overflow.add(trip);
            }
            if (primary.size() == 5) break;
        }

        // Fill remaining slots from overflow if primary has fewer than 5
        for (Map<String, Object> trip : overflow) {
            if (primary.size() >= 5) break;
            primary.add(trip);
        }

        return primary.subList(0, Math.min(5, primary.size()));
    }

    /**
     * Score [0,1] reflectant si le theme du circuit correspond a la dimension
     * d'engagement (recente, basee sur les interactions reelles) la plus
     * forte du profil. Sans signal d'interaction, retourne une valeur neutre
     * pour ne pas penaliser les circuits dont la thematique n'a pas encore
     * ete explorees.
     */
    private double engagementScore(TouristProfileRequest profile, Circuit circuit) {
        if (circuit == null || profile.getTotalPostsEngaged() == 0) return 0.5;
        String dimension = CIRCUIT_THEME_TO_ENGAGEMENT.get(normalizeTheme(circuit.getTheme()));
        if (dimension == null) return 0.5;
        double value = switch (dimension) {
            case "food" -> profile.getEngagementFood();
            case "culture" -> profile.getEngagementCulture();
            case "nature" -> profile.getEngagementNature();
            case "adventure" -> profile.getEngagementAdventure();
            case "history" -> profile.getEngagementHistory();
            case "wellness" -> profile.getEngagementWellness();
            default -> 50.0;
        };
        return Math.max(0, Math.min(1, value / 100.0));
    }

    /**
     * Score [0,1] reflectant si le circuit se situe dans la ville recommandee
     * par l'IA (recommend-city), elle-meme influencee par les interactions
     * recentes de l'utilisateur (topEngagedCity). Garantit la coherence entre
     * la destination annoncee et les circuits effectivement proposes.
     */
    private double cityMatchScore(String recommendedCity, Circuit circuit) {
        if (circuit == null || recommendedCity == null) return 0.3;
        String circuitCity = CITY_NORMALIZATION.getOrDefault(circuit.getVille(), circuit.getVille());
        return recommendedCity.equalsIgnoreCase(circuitCity) ? 1.0 : 0.3;
    }

    /**
     * Score [0,1] reflectant si le theme du circuit correspond au type
     * d'experience suggere par l'IA (/api/recommend-experience), lui-meme
     * issu du profil de voyage et des interactions recentes. Permet de
     * privilegier les circuits qui incarnent concretement l'experience
     * mise en avant pour l'utilisateur.
     */
    private double experienceMatchScore(String recommendedExperience, Circuit circuit) {
        if (circuit == null || recommendedExperience == null) return 0.3;
        Set<String> themes = EXPERIENCE_THEMES.get(recommendedExperience);
        if (themes == null) return 0.5;
        return themes.contains(normalizeTheme(circuit.getTheme())) ? 1.0 : 0.3;
    }

    /**
     * Ajoute aux champs "reason" (texte explicatif FR) et "matched_categories"
     * (liste des centres d'interet correspondants) de chaque circuit recommande,
     * pour que l'utilisateur comprenne pourquoi ce circuit lui est propose.
     */
    private void addReasonAndCategories(Map<String, Object> trip, TouristProfileRequest profile, Circuit circuit,
                                         double style, double budget, double duration, double interest,
                                         double engagement, double cityMatch, double experienceMatch,
                                         String recommendedCity, String recommendedExperience) {
        List<String> matched = new ArrayList<>();

        if (circuit == null) {
            trip.put("reason", "Une découverte incontournable de la région Fès-Meknès.");
            trip.put("matched_categories", matched);
            return;
        }

        List<String> reasons = new ArrayList<>();

        if (style >= 0.75 && profile.getTravelStyle() != null) {
            String label = TRAVEL_STYLE_LABELS_FR.getOrDefault(profile.getTravelStyle().toLowerCase(), profile.getTravelStyle());
            matched.add(label);
            reasons.add("correspond à votre style de voyage \"" + label + "\"");
        }

        String circuitEngagement = CIRCUIT_THEME_TO_ENGAGEMENT.get(normalizeTheme(circuit.getTheme()));
        if (interest >= 0.75 && circuitEngagement != null) {
            String label = ENGAGEMENT_LABEL_FR.getOrDefault(circuitEngagement, circuitEngagement);
            if (!matched.contains(label)) matched.add(label);
            reasons.add("correspond à votre intérêt pour " + label);
        }

        if (engagement >= 0.5 && circuitEngagement != null) {
            String label = ENGAGEMENT_LABEL_FR.getOrDefault(circuitEngagement, circuitEngagement);
            if (!matched.contains(label)) matched.add(label);
            reasons.add("correspond à vos interactions récentes liées à " + label);
        }

        if (budget >= 0.9 && profile.getBudgetLevel() != null) {
            String label = BUDGET_LABELS_FR.getOrDefault(profile.getBudgetLevel().toLowerCase(), profile.getBudgetLevel());
            reasons.add("correspond à votre budget " + label);
        }

        if (duration >= 0.9 && profile.getTripDurationDays() != null) {
            reasons.add("correspond à la durée de séjour souhaitée (" + profile.getTripDurationDays() + " jours)");
        }

        if (cityMatch >= 1.0 && recommendedCity != null) {
            reasons.add("se situe à " + circuit.getVille() + ", la destination recommandée pour vous en ce moment");
        }

        if (experienceMatch >= 1.0 && recommendedExperience != null) {
            String label = EXPERIENCE_LABEL_FR.getOrDefault(recommendedExperience, recommendedExperience);
            reasons.add("correspond à l'expérience que nous vous suggérons actuellement : " + label);
        }

        String reasonText = reasons.isEmpty()
                ? "Sélectionné pour compléter votre exploration de la région Fès-Meknès."
                : "Recommandé car ce circuit " + String.join(", ", reasons) + ".";

        trip.put("reason", reasonText);
        trip.put("matched_categories", matched);
    }

    private double styleScore(String style, String theme) {
        if (style == null || theme == null) return 0.5;
        Set<String> themes = STYLE_THEMES.get(style.toLowerCase());
        if (themes == null) return 0.5;
        return themes.contains(theme) ? 1.0 : 0.25;
    }

    private double budgetScore(String level, Double prix) {
        if (level == null || prix == null) return 0.5;
        switch (level.toLowerCase()) {
            case "budget":
                if (prix <= 500) return 1.0;
                if (prix <= 1200) return 0.4;
                return 0.1;
            case "mid_range":
                if (prix > 500 && prix <= 1200) return 1.0;
                if (prix <= 2200) return 0.5;
                return 0.2;
            case "comfort":
                if (prix > 1200 && prix <= 2200) return 1.0;
                if (prix > 500) return 0.5;
                return 0.2;
            case "luxury":
                if (prix > 2200) return 1.0;
                if (prix > 1200) return 0.5;
                return 0.1;
            default:
                return 0.5;
        }
    }

    private double durationScore(String bucket, Integer days) {
        if (bucket == null || days == null) return 0.5;
        switch (bucket) {
            case "1-2":
                if (days <= 2) return 1.0;
                if (days == 3) return 0.4;
                return 0.1;
            case "3-5":
                if (days >= 3 && days <= 5) return 1.0;
                if (days == 2 || days == 6) return 0.5;
                return 0.2;
            case "6-7":
                if (days >= 6 && days <= 7) return 1.0;
                if (days == 5) return 0.5;
                return 0.2;
            case "8-14":
                if (days >= 8) return 1.0;
                if (days >= 6) return 0.4;
                return 0.1;
            default:
                return 0.5;
        }
    }

    private double interestScore(TouristProfileRequest profile, String theme) {
        if (theme == null) return 0.5;
        String field = THEME_INTEREST.get(theme);
        if (field == null) return 0.5;
        int value = switch (field) {
            case "interestHistory" -> profile.getInterestHistory();
            case "interestNature" -> profile.getInterestNature();
            case "interestFood" -> profile.getInterestFood();
            case "interestAdventure" -> profile.getInterestAdventure();
            case "interestPhotography" -> profile.getInterestPhotography();
            case "interestWellness" -> profile.getInterestWellness();
            case "interestShopping" -> profile.getInterestShopping();
            case "interestFestivals" -> profile.getInterestFestivals();
            default -> 0;
        };
        return value == 1 ? 1.0 : 0.3;
    }

    private double toDouble(Object o) {
        return o instanceof Number ? ((Number) o).doubleValue() : 0.0;
    }

    private Long toLong(Object o) {
        return o instanceof Number ? ((Number) o).longValue() : null;
    }

    /**
     * Signature deterministe de l'ensemble des reponses du questionnaire et
     * de l'engagement courant de l'utilisateur. Utilisee uniquement pour
     * departager les circuits a egalite parfaite de score : tout changement,
     * meme minime (une seule reponse, ou une nouvelle interaction qui modifie
     * l'engagement), produit une signature differente et donc un ordre de
     * departage different, garantissant des top 5 distincts d'une generation
     * a l'autre.
     */
    private long profileSignature(TouristProfileRequest profile) {
        String raw = String.join("|",
                String.valueOf(profile.getNationality()),
                String.valueOf(profile.getAgeGroup()),
                String.valueOf(profile.getGender()),
                String.valueOf(profile.getBudgetLevel()),
                String.valueOf(profile.getTripDurationDays()),
                String.valueOf(profile.getGroupType()),
                String.valueOf(profile.getGroupSize()),
                String.valueOf(profile.getTravelStyle()),
                String.valueOf(profile.getNoisePreference()),
                String.valueOf(profile.getActivityLevel()),
                String.valueOf(profile.getFoodPreference()),
                String.valueOf(profile.getPreferredSeason()),
                String.valueOf(profile.getAccommodationType()),
                String.valueOf(profile.getTransportPreference()),
                String.valueOf(profile.getSpecialNeeds()),
                String.valueOf(profile.getHasVisitedBefore()),
                "" + profile.getInterestHistory() + profile.getInterestNature() + profile.getInterestFood()
                        + profile.getInterestAdventure() + profile.getInterestPhotography() + profile.getInterestWellness()
                        + profile.getInterestShopping() + profile.getInterestFestivals(),
                String.valueOf(profile.getTopEngagedCategory()),
                String.valueOf(profile.getTopEngagedCity()),
                String.valueOf(profile.getAvgEngagementScore())
        );
        return raw.hashCode();
    }

    /** Cle de departage pseudo-aleatoire mais deterministe, derivee de l'id du circuit et de la signature du profil. */
    private long tieBreak(Long tripId, long signature) {
        long id = tripId != null ? tripId : 0L;
        return (id * 2654435761L) ^ signature;
    }

    /**
     * Enregistre la recommandation generee (preferences utilisees, budget,
     * duree, circuits proposes) pour garder un historique par utilisateur et
     * pouvoir verifier que la recommandation evolue avec son profil.
     */
    private void persistRecommendation(TouristProfileRequest profile, Long userId, List<Map<String, Object>> topTrips) {
        if (userId == null) return;
        Utilisateur user = utilisateurRepository.findById(userId).orElse(null);
        if (user == null) return;

        String idsCsv = topTrips.stream()
                .map(t -> toLong(t.get("trip_id")))
                .filter(Objects::nonNull)
                .map(String::valueOf)
                .collect(Collectors.joining(","));

        recommandationIARepository.save(RecommandationIA.builder()
                .utilisateur(user)
                .preferencesUtilisees(buildPreferencesSummary(profile))
                .budgetUtilise(budgetLevelToAmount(profile.getBudgetLevel()))
                .dureeUtilisee(parseDurationDays(profile.getTripDurationDays()))
                .circuitsRecommandesIds(idsCsv)
                .build());
    }

    /** Resume textuel des reponses au questionnaire + de l'engagement, utilise comme trace d'audit. */
    private String buildPreferencesSummary(TouristProfileRequest profile) {
        List<String> parts = new ArrayList<>();
        if (profile.getTravelStyle() != null) parts.add("style=" + profile.getTravelStyle());
        if (profile.getGroupType() != null) parts.add("groupe=" + profile.getGroupType());
        if (profile.getAccommodationType() != null) parts.add("hebergement=" + profile.getAccommodationType());
        if (profile.getTransportPreference() != null) parts.add("transport=" + profile.getTransportPreference());

        List<String> interests = new ArrayList<>();
        if (profile.getInterestHistory() == 1) interests.add("histoire");
        if (profile.getInterestNature() == 1) interests.add("nature");
        if (profile.getInterestFood() == 1) interests.add("gastronomie");
        if (profile.getInterestAdventure() == 1) interests.add("aventure");
        if (profile.getInterestPhotography() == 1) interests.add("photographie");
        if (profile.getInterestWellness() == 1) interests.add("bien-etre");
        if (profile.getInterestShopping() == 1) interests.add("artisanat");
        if (profile.getInterestFestivals() == 1) interests.add("festivals");
        if (!interests.isEmpty()) parts.add("interets=" + String.join("|", interests));

        if (profile.getTopEngagedCategory() != null) parts.add("top_engagement=" + profile.getTopEngagedCategory());
        if (profile.getTopEngagedCity() != null) parts.add("top_ville=" + profile.getTopEngagedCity());

        return String.join(",", parts);
    }

    /** Convertit le niveau de budget du questionnaire en montant indicatif (MAD/EUR) pour l'historique. */
    private Double budgetLevelToAmount(String level) {
        if (level == null) return null;
        switch (level.toLowerCase()) {
            case "budget": return 400.0;
            case "luxury": return 2500.0;
            default: return 1000.0;
        }
    }

    /** Convertit la fourchette de duree du questionnaire en nombre de jours indicatif pour l'historique. */
    private Integer parseDurationDays(String bucket) {
        if (bucket == null) return null;
        switch (bucket) {
            case "1-2": return 2;
            case "3-5": return 4;
            case "6-7": return 7;
            case "8-14": return 10;
            default: return null;
        }
    }

    /** Si Flask est indisponible : top 5 circuits publies par note, sans score IA. */
    private Map<String, Object> fallbackRecommendation() {
        List<Map<String, Object>> trips = circuitRepository.findByStatut("PUBLIE").stream()
                .sorted(Comparator.comparing(Circuit::getNoteMoyenne).reversed())
                .limit(5)
                .map(c -> {
                    Map<String, Object> trip = new LinkedHashMap<>();
                    trip.put("trip_id", c.getId());
                    trip.put("trip_title", c.getTitre());
                    trip.put("trip_description", c.getDescription() != null ? c.getDescription() : c.getTheme());
                    trip.put("duration_days", c.getDureeJours());
                    trip.put("estimated_budget", c.getPrixEstime());
                    trip.put("ai_match_score", c.getNoteMoyenne());
                    return trip;
                })
                .collect(Collectors.toList());

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("recommended_city", "Fes");
        result.put("recommended_experience", "cultural_immersion");
        result.put("top_trips", trips);
        return result;
    }
}
