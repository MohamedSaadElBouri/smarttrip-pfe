package ma.groupe07.tourisme.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.groupe07.tourisme.modules.auth.model.Utilisateur;
import ma.groupe07.tourisme.modules.auth.repository.UtilisateurRepository;
import ma.groupe07.tourisme.modules.circuit.model.*;
import ma.groupe07.tourisme.modules.circuit.repository.*;
import ma.groupe07.tourisme.modules.evenement.model.Evenement;
import ma.groupe07.tourisme.modules.evenement.repository.EvenementRepository;
import ma.groupe07.tourisme.modules.formulaire.model.QuestionFormulaire;
import ma.groupe07.tourisme.modules.formulaire.repository.QuestionFormulaireRepository;
import ma.groupe07.tourisme.modules.lieu.model.Lieu;
import ma.groupe07.tourisme.modules.lieu.repository.LieuRepository;
import ma.groupe07.tourisme.modules.publication.model.Publication;
import ma.groupe07.tourisme.modules.publication.repository.PublicationRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements ApplicationRunner {

    private static final String MORE_PUBLICATIONS_MARKER =
            "Atelier de calligraphie arabe organisé dans un riad de la médina, une initiation fascinante à cet art ancestral.";

    private final UtilisateurRepository userRepo;
    private final LieuRepository lieuRepo;
    private final CircuitRepository circuitRepo;
    private final EtapeCircuitRepository etapeRepo;
    private final EvenementRepository evenementRepo;
    private final QuestionFormulaireRepository questionRepo;
    private final PublicationRepository pubRepo;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        if (userRepo.count() == 0) seedUsers();
        if (lieuRepo.count() == 0) seedLieux();
        if (circuitRepo.count() == 0) seedCircuits();
        if (evenementRepo.count() == 0) seedEvenements();
        if (questionRepo.count() == 0) seedQuestions();
        if (pubRepo.count() == 0) seedPublications();
        if (!pubRepo.existsByContenu(MORE_PUBLICATIONS_MARKER)) seedMorePublications();
        backfillSeedUserPhotos();
    }

    /** Donne une photo de profil aux comptes de demonstration s'ils n'en ont pas encore (evite l'avatar par defaut identique pour tous). */
    private void backfillSeedUserPhotos() {
        userRepo.findByEmail("admin@smarttrip.ma").ifPresent(u -> {
            if (u.getPhotoUrl() == null || u.getPhotoUrl().isEmpty()) {
                u.setPhotoUrl("https://i.pravatar.cc/300?img=13");
                userRepo.save(u);
            }
        });
        userRepo.findByEmail("tourist@smarttrip.ma").ifPresent(u -> {
            if (u.getPhotoUrl() == null || u.getPhotoUrl().isEmpty()) {
                u.setPhotoUrl("https://i.pravatar.cc/300?img=51");
                userRepo.save(u);
            }
        });
    }

    private void seedUsers() {
        userRepo.save(Utilisateur.builder()
                .nom("Admin SmartTrip").email("admin@smarttrip.ma")
                .motDePasse(passwordEncoder.encode("Admin123!"))
                .role("ADMIN").langue("FR").pays("Maroc").ville("Fès").build());
        userRepo.save(Utilisateur.builder()
                .nom("Youssef Alami").email("tourist@smarttrip.ma")
                .motDePasse(passwordEncoder.encode("Tourist123!"))
                .role("TOURISTE").langue("FR").pays("Maroc").ville("Fès")
                .preferences("CULTURE,HISTOIRE").age(28).sexe("HOMME").build());
        log.info("✅ Users seeded");
    }

    private void seedLieux() {
        // Fès - 12 places
        lieuRepo.save(lieu("Médina de Fès","PATRIMOINE","Fès",34.0617,-4.9767,
            "La médina de Fès est l'une des plus grandes médinas du monde, classée au patrimoine mondial de l'UNESCO.",
            "مدينة فاس العتيقة هي واحدة من أكبر المدن القديمة في العالم.",
            "The medina of Fes is one of the largest in the world, a UNESCO World Heritage Site.",
            "Entrée libre","7h-20h","0535-123456",
            "https://loremflickr.com/640/480/morocco,medina,fes?lock=1"));
        lieuRepo.save(lieu("Bab Bou Jeloud","MONUMENT","Fès",34.0646,-4.9788,
            "La porte Bab Bou Jeloud, surnommée la Porte Bleue, est l'entrée principale de la médina de Fès.",
            "باب بوجلود أو الباب الأزرق هو المدخل الرئيسي للمدينة القديمة لفاس.",
            "Bab Bou Jeloud, the Blue Gate, is the main entrance to Fes medina.",
            "Entrée libre","Ouvert 24h/24",null,
            "https://loremflickr.com/640/480/morocco,gate,blue?lock=2"));
        lieuRepo.save(lieu("Tanneries Chouara","ARTISANAT","Fès",34.0637,-4.9740,
            "Les tanneries Chouara sont les plus grandes et les plus anciennes tanneries de cuir de Fès.",
            "دباغة الشوارة هي أكبر وأقدم مدبغة جلود في فاس.",
            "Chouara Tanneries are the largest and oldest leather tanneries in Fes.",
            "Vue depuis les terrasses boutiques","8h-18h",null,
            "https://loremflickr.com/640/480/morocco,tannery,leather?lock=3"));
        lieuRepo.save(lieu("Medersa Bou Inania","PATRIMOINE","Fès",34.0651,-4.9783,
            "La Médersa Bou Inania est un chef-d'œuvre de l'architecture mérinide, construite au XIVe siècle.",
            "مدرسة بوعنانية هي تحفة معمارية مرينية بُنيت في القرن الرابع عشر.",
            "Medersa Bou Inania is a masterpiece of Marinid architecture built in the 14th century.",
            "20 MAD","9h-18h",null,
            "https://loremflickr.com/640/480/morocco,madrasa,architecture?lock=4"));
        lieuRepo.save(lieu("Fontaine Nejjarine","MONUMENT","Fès",34.0638,-4.9763,
            "La Fontaine Nejjarine est une fontaine historique ornée de magnifiques mosaïques et scultures en bois.",
            "نافورة النجارين نافورة تاريخية مزخرفة بالفسيفساء الرائعة.",
            "Nejjarine Fountain is a historic fountain adorned with magnificent mosaics and wood carvings.",
            "Entrée libre","24h/24",null,
            "https://loremflickr.com/640/480/morocco,fountain,mosaic?lock=5"));
        lieuRepo.save(lieu("Musée Nejjarine du Bois","MUSEE","Fès",34.0639,-4.9764,
            "Installé dans un ancien caravansérail du XVIIIe siècle, ce musée présente les arts et métiers du bois.",
            "يقع في فندق قديم من القرن الثامن عشر ويعرض فنون وحرف الخشب.",
            "Housed in an 18th-century caravanserai, this museum showcases wood arts and crafts.",
            "20 MAD","9h-17h","0535-740580",
            "https://loremflickr.com/640/480/morocco,museum,wood?lock=6"));
        lieuRepo.save(lieu("Mosquée Al-Qarawiyyin","RELIGION","Fès",34.0636,-4.9740,
            "Fondée en 859, Al-Qarawiyyin est considérée comme la plus ancienne université au monde.",
            "تأسست عام 859 وتعتبر القرويين الجامعة الأقدم في العالم.",
            "Founded in 859, Al-Qarawiyyin is considered the world's oldest university.",
            "Accessible aux non-musulmans à l'extérieur","7h-21h",null,
            "https://loremflickr.com/640/480/morocco,mosque,islamic?lock=7"));
        lieuRepo.save(lieu("Souk Attarine","ARTISANAT","Fès",34.0637,-4.9751,
            "Le Souk des Épiciers, connu pour ses parfums et épices exotiques, est l'un des plus beaux souks de Fès.",
            "سوق العطارين المعروف بعطوره وتوابله الغريبة من أجمل أسواق فاس.",
            "The Spice Market, known for its exotic perfumes and spices, is one of the most beautiful souks in Fes.",
            "Entrée libre","9h-19h",null,
            "https://loremflickr.com/640/480/morocco,souk,market?lock=8"));
        lieuRepo.save(lieu("Borj Nord","MUSEE","Fès",34.0680,-4.9783,
            "Le Borj Nord est une ancienne fortification du XVIe siècle qui abrite aujourd'hui un musée des armes.",
            "برج الشمال حصن قديم من القرن السادس عشر يضم متحف الأسلحة.",
            "Borj Nord is a 16th-century fortification that now houses a weapons museum.",
            "10 MAD","9h-16h",null,
            "https://loremflickr.com/640/480/morocco,fortress,fes?lock=9"));
        lieuRepo.save(lieu("Jardin Jnan Sbil","NATURE","Fès",34.0678,-4.9814,
            "Ce magnifique jardin botanique royal offre une oasis de verdure au cœur de la ville.",
            "هذه الحديقة النباتية الملكية الرائعة توفر واحة خضراء في قلب المدينة.",
            "This magnificent royal botanical garden offers a green oasis in the heart of the city.",
            "Entrée libre","8h-18h",null,
            "https://loremflickr.com/640/480/morocco,garden,park?lock=10"));
        lieuRepo.save(lieu("Palais Royal de Fès","MONUMENT","Fès",34.0573,-4.9832,
            "Le Palais Royal de Fès impressionne par ses gigantesques portes en bronze et ses magnifiques mosaïques.",
            "يبهر القصر الملكي بفاس بأبوابه البرونزية العملاقة وفسيفسائه الرائعة.",
            "The Royal Palace of Fes impresses with its giant bronze doors and magnificent mosaics.",
            "Vue extérieure uniquement","Extérieur visible 24h/24",null,
            "https://loremflickr.com/640/480/morocco,palace,gate?lock=11"));
        lieuRepo.save(lieu("Mellah de Fès","PATRIMOINE","Fès",34.0570,-4.9810,
            "Le Mellah est l'ancien quartier juif de Fès, fondé au XIVe siècle, témoignage de la cohabitation culturelle.",
            "الملاح هو الحي اليهودي القديم في فاس الذي أُسس في القرن الرابع عشر.",
            "The Mellah is the ancient Jewish quarter of Fes, founded in the 14th century.",
            "Entrée libre","9h-18h",null,
            "https://loremflickr.com/640/480/morocco,jewish,quarter?lock=12"));

        // Meknès - 5 places
        lieuRepo.save(lieu("Volubilis","RUINES","Meknès",34.0724,-5.5539,
            "Volubilis est un site archéologique romain exceptionnel, classé au patrimoine mondial de l'UNESCO en 1997.",
            "وليلي موقع أثري روماني استثنائي مدرج في قائمة اليونسكو للتراث العالمي منذ 1997.",
            "Volubilis is an exceptional Roman archaeological site, UNESCO World Heritage since 1997.",
            "70 MAD","9h-18h","0535-544182",
            "https://loremflickr.com/640/480/morocco,roman,ruins?lock=13"));
        lieuRepo.save(lieu("Mausolée Moulay Ismail","MONUMENT","Meknès",33.8966,-5.5537,
            "Le mausolée de Moulay Ismail, sultan alaouite qui fit de Meknès la capitale du Maroc au XVIIe siècle.",
            "ضريح مولاي إسماعيل السلطان العلوي الذي جعل مكناس عاصمة المغرب في القرن السابع عشر.",
            "The mausoleum of Moulay Ismail, the Alaouite sultan who made Meknes the capital of Morocco.",
            "Entrée libre (non-musulmans admis)","9h-12h/15h-18h",null,
            "https://loremflickr.com/640/480/morocco,mausoleum,meknes?lock=14"));
        lieuRepo.save(lieu("Bab Mansour","MONUMENT","Meknès",33.8953,-5.5548,
            "La Porte Mansour est considérée comme l'une des plus belles portes du Maroc et de toute l'Afrique.",
            "باب منصور يعتبر من أجمل أبواب المغرب وأفريقيا بأسرها.",
            "Bab Mansour is considered one of the most beautiful gates in Morocco and all of Africa.",
            "Entrée libre","Visible 24h/24",null,
            "https://loremflickr.com/640/480/morocco,gate,meknes?lock=15"));
        lieuRepo.save(lieu("Place El Hedim","PLACE","Meknès",33.8952,-5.5551,
            "La Place El Hedim est la grande place centrale de Meknès, animée surtout le soir avec ses spectacles.",
            "ساحة الهديم هي الميدان الكبير المركزي لمكناس والمفعم بالحياة خاصة في المساء.",
            "Place El Hedim is the central square of Meknes, lively especially in the evening.",
            "Entrée libre","Accessible 24h/24",null,
            "https://loremflickr.com/640/480/morocco,square,meknes?lock=16"));
        lieuRepo.save(lieu("Grenier Royal Heri Souani","MONUMENT","Meknès",33.8990,-5.5520,
            "Les greniers royaux Heri Souani sont d'impressionnantes constructions du XVIIe siècle destinées au stockage.",
            "هري السواني هي مخازن ملكية ضخمة من القرن السابع عشر.",
            "Heri Souani royal granaries are impressive 17th-century buildings built for food storage.",
            "10 MAD","9h-18h",null,
            "https://loremflickr.com/640/480/morocco,granary,ruins?lock=17"));

        // Région - 3 places
        lieuRepo.save(lieu("Lac Dayet Aoua","NATURE","Ifrane",33.5167,-5.0833,
            "Le lac Dayet Aoua est un magnifique lac naturel entouré de forêts de cèdres dans le Moyen Atlas.",
            "بحيرة ذاية عوا بحيرة طبيعية رائعة محاطة بغابات الأرز في الأطلس المتوسط.",
            "Lake Dayet Aoua is a beautiful natural lake surrounded by cedar forests in the Middle Atlas.",
            "Entrée libre","Accessible toute l'année",null,
            "https://loremflickr.com/640/480/morocco,lake,nature?lock=18"));
        lieuRepo.save(lieu("Forêt de Cèdres d'Azrou","NATURE","Azrou",33.4333,-5.2167,
            "La célèbre forêt de cèdres d'Azrou abrite des cèdres centenaires et des singes Magots en liberté.",
            "غابة أرز أزرو الشهيرة تضم أشجار أرز معمرة وقردة الماكاك البربري.",
            "The famous cedar forest of Azrou is home to centuries-old cedars and wild Barbary macaques.",
            "Entrée libre","Accessible toute l'année",null,
            "https://loremflickr.com/640/480/morocco,cedar,forest?lock=19"));
        lieuRepo.save(lieu("Musée Dar Jamaï","MUSEE","Meknès",33.8967,-5.5534,
            "Le Musée Dar Jamaï, installé dans un palais du XIXe siècle, présente les arts décoratifs marocains.",
            "متحف دار الجامعي في قصر من القرن التاسع عشر يعرض الفنون التزيينية المغربية.",
            "Dar Jamai Museum, set in a 19th-century palace, presents Moroccan decorative arts.",
            "20 MAD","9h-16h","0535-530863",
            "https://loremflickr.com/640/480/morocco,museum,palace?lock=20"));

        // Restaurants, hôtels & activités - Fès & Meknès
        lieuRepo.save(lieu("Restaurant Dar Hatim","RESTAURANT","Fès",34.0625,-4.9756,
            "Restaurant traditionnel niché dans un riad du XVIIe siècle, Dar Hatim propose une cuisine marocaine raffinée servie dans un cadre authentique avec spectacle de musique andalouse.",
            "مطعم تقليدي يقع في رياض من القرن السابع عشر، يقدم دار حاتم مأكولات مغربية راقية في إطار أصيل مع عرض موسيقي أندلسي.",
            "A traditional restaurant set in a 17th-century riad, Dar Hatim serves refined Moroccan cuisine in an authentic setting with live Andalusian music.",
            "200-400 MAD","12h-23h","0535-634918",
            "https://loremflickr.com/640/480/morocco,restaurant,food?lock=21"));
        lieuRepo.save(lieu("Café Clock Fès","RESTAURANT","Fès",34.0633,-4.9745,
            "Institution culturelle et culinaire au cœur de la médina, le Café Clock mêle plats marocains modernisés et ambiance artistique animée par des concerts et ateliers.",
            "مؤسسة ثقافية وغذائية في قلب المدينة، يمزج كافيه كلوك بين الأطباق المغربية العصرية وأجواء فنية مع حفلات وورشات.",
            "A cultural and culinary institution in the heart of the medina, Café Clock blends modernized Moroccan dishes with a lively artistic atmosphere of concerts and workshops.",
            "80-150 MAD","9h-22h","0535-637855",
            "https://loremflickr.com/640/480/morocco,cafe,food?lock=22"));
        lieuRepo.save(lieu("Riad Fès","HOTEL","Fès",34.0640,-4.9770,
            "Ce riad de luxe allie architecture traditionnelle et confort moderne, avec patios fleuris, fontaines et spa au cœur de la médina.",
            "يمزج هذا الرياض الفاخر بين العمارة التقليدية والراحة العصرية، مع باحات مزهرة ونوافير ومنتجع صحي في قلب المدينة.",
            "This luxury riad blends traditional architecture with modern comfort, featuring flower-filled patios, fountains and a spa in the heart of the medina.",
            "1200-2500 MAD/nuit","Réception 24h/24","0535-947210",
            "https://loremflickr.com/640/480/morocco,riad,courtyard?lock=23"));
        lieuRepo.save(lieu("Hôtel Sahrai","HOTEL","Fès",34.0580,-4.9900,
            "Hôtel design surplombant la médina de Fès, le Sahrai offre piscine, spa et vue panoramique exceptionnelle sur la ville impériale.",
            "فندق عصري يطل على مدينة فاس العتيقة، يقدم ساحراي مسبحاً ومنتجعاً صحياً وإطلالة بانورامية استثنائية على المدينة الإمبراطورية.",
            "A design hotel overlooking the Fes medina, the Sahrai offers a pool, spa and an exceptional panoramic view of the imperial city.",
            "1800-4000 MAD/nuit","Réception 24h/24","0535-940332",
            "https://loremflickr.com/640/480/morocco,hotel,pool?lock=24"));
        lieuRepo.save(lieu("Cours de Cuisine Marocaine","ACTIVITE","Fès",34.0630,-4.9760,
            "Apprenez à préparer un tajine et des pâtisseries marocaines avec un chef local, visite du marché aux épices incluse.",
            "تعلم تحضير الطاجين والحلويات المغربية مع طاهٍ محلي، وتشمل الجولة زيارة سوق التوابل.",
            "Learn to prepare a tagine and Moroccan pastries with a local chef, including a visit to the spice market.",
            "350 MAD/personne","10h-14h","0661-223344",
            "https://loremflickr.com/640/480/morocco,cooking,spices?lock=25"));
        lieuRepo.save(lieu("Balade en Calèche - Médina","ACTIVITE","Fès",34.0620,-4.9770,
            "Découvrez les remparts et les portes monumentales de Fès à bord d'une calèche traditionnelle, un moyen original de profiter du panorama.",
            "اكتشف أسوار وبوابات فاس الأثرية على متن عربة تقليدية تجرها الخيول، وسيلة أصلية للاستمتاع بالمناظر الخلابة.",
            "Discover the ramparts and monumental gates of Fes aboard a traditional horse-drawn carriage, a charming way to enjoy the views.",
            "150 MAD/calèche","9h-19h",null,
            "https://loremflickr.com/640/480/morocco,carriage,horse?lock=26"));
        lieuRepo.save(lieu("Restaurant Collier de la Colombe","RESTAURANT","Meknès",33.8960,-5.5530,
            "Perché sur les remparts de Meknès avec une vue imprenable sur la vallée, ce restaurant sert une cuisine marocaine traditionnelle dans un cadre romantique.",
            "يقع هذا المطعم على أسوار مكناس مع إطلالة رائعة على الوادي، ويقدم المأكولات المغربية التقليدية في جو رومانسي.",
            "Perched on the ramparts of Meknes with a stunning valley view, this restaurant serves traditional Moroccan cuisine in a romantic setting.",
            "150-300 MAD","12h-22h","0535-555041",
            "https://loremflickr.com/640/480/morocco,restaurant,terrace?lock=27"));
        lieuRepo.save(lieu("Hôtel Ibis Meknès","HOTEL","Meknès",33.8930,-5.5470,
            "Hôtel moderne et confortable situé à proximité de la médina de Meknès, idéal pour explorer la ville impériale et Volubilis.",
            "فندق عصري ومريح يقع بالقرب من مدينة مكناس العتيقة، مثالي لاستكشاف المدينة الإمبراطورية ووليلي.",
            "A modern, comfortable hotel located near the Meknes medina, ideal for exploring the imperial city and Volubilis.",
            "450-700 MAD/nuit","Réception 24h/24","0535-401111",
            "https://loremflickr.com/640/480/morocco,hotel,modern?lock=28"));
        lieuRepo.save(lieu("Randonnée Moyen Atlas","ACTIVITE","Meknès",33.8900,-5.5500,
            "Partez en randonnée guidée dans les paysages verdoyants du Moyen Atlas, entre forêts de cèdres et villages berbères.",
            "انطلق في رحلة مشي برفقة مرشد عبر المناظر الخضراء للأطلس المتوسط، بين غابات الأرز والقرى الأمازيغية.",
            "Set off on a guided hike through the green landscapes of the Middle Atlas, between cedar forests and Berber villages.",
            "300 MAD/personne","8h-16h","0662-778899",
            "https://loremflickr.com/640/480/morocco,atlas,hiking?lock=29"));

        log.info("✅ 29 lieux seeded");
    }

    private Lieu lieu(String nom, String cat, String ville, double lat, double lng,
                      String fr, String ar, String en, String prix, String horaires, String contact, String photoUrl) {
        return Lieu.builder()
                .nom(nom).categorie(cat).ville(ville)
                .latitude(lat).longitude(lng)
                .storytellingFr(fr).storytellingAr(ar).storytellingEn(en)
                .prixEntree(prix).horaires(horaires).contact(contact)
                .photoUrl(photoUrl)
                .noteMoyenne(0.0).nombreAvis(0).build();
    }

    private void seedCircuits() {
        List<Lieu> lieux = lieuRepo.findAll();

        // Circuit 1: Fès Historique
        Circuit c1 = circuitRepo.save(Circuit.builder()
                .titre("Fès Historique & Culturel").description("Plongez dans l'histoire millénaire de Fès, la capitale spirituelle du Maroc. Un voyage inoubliable à travers les ruelles de la plus grande médina médiévale du monde.")
                .theme("CULTURE,HISTOIRE").ville("Fès").dureeJours(3)
                .prixEstime(450.0).statut("PUBLIE").build());
        lieux.stream().filter(l -> l.getNom().equals("Bab Bou Jeloud")).findFirst().ifPresent(l ->
            etapeRepo.save(EtapeCircuit.builder().circuit(c1).lieu(l).ordre(1).heureVisite("09:00").dureeMinutes(30).notes("Point de départ idéal. Prenez des photos depuis l'extérieur.").build()));
        lieux.stream().filter(l -> l.getNom().equals("Medersa Bou Inania")).findFirst().ifPresent(l ->
            etapeRepo.save(EtapeCircuit.builder().circuit(c1).lieu(l).ordre(2).heureVisite("10:00").dureeMinutes(60).notes("Ne manquez pas les mosaïques et les sculptures en stuc.").build()));
        lieux.stream().filter(l -> l.getNom().equals("Tanneries Chouara")).findFirst().ifPresent(l ->
            etapeRepo.save(EtapeCircuit.builder().circuit(c1).lieu(l).ordre(3).heureVisite("14:00").dureeMinutes(45).notes("Vue depuis les terrasses. Meilleures couleurs en matinée.").build()));
        lieux.stream().filter(l -> l.getNom().equals("Musée Nejjarine du Bois")).findFirst().ifPresent(l ->
            etapeRepo.save(EtapeCircuit.builder().circuit(c1).lieu(l).ordre(4).heureVisite("16:00").dureeMinutes(60).notes("Collections d'objets en bois sculptés uniques.").build()));

        // Circuit 2: Meknès Impériale
        Circuit c2 = circuitRepo.save(Circuit.builder()
                .titre("Meknès Impériale & Volubilis").description("Découvrez Meknès, la ville impériale oubliée, et les majestueuses ruines romaines de Volubilis.")
                .theme("HISTOIRE,PATRIMOINE").ville("Meknès").dureeJours(2)
                .prixEstime(350.0).statut("PUBLIE").build());
        lieux.stream().filter(l -> l.getNom().equals("Bab Mansour")).findFirst().ifPresent(l ->
            etapeRepo.save(EtapeCircuit.builder().circuit(c2).lieu(l).ordre(1).heureVisite("09:00").dureeMinutes(30).notes("La plus belle porte du Maroc. Admirez les mosaïques.").build()));
        lieux.stream().filter(l -> l.getNom().equals("Mausolée Moulay Ismail")).findFirst().ifPresent(l ->
            etapeRepo.save(EtapeCircuit.builder().circuit(c2).lieu(l).ordre(2).heureVisite("10:30").dureeMinutes(45).notes("Accès autorisé aux non-musulmans jusqu'à la première salle.").build()));
        lieux.stream().filter(l -> l.getNom().equals("Volubilis")).findFirst().ifPresent(l ->
            etapeRepo.save(EtapeCircuit.builder().circuit(c2).lieu(l).ordre(3).heureVisite("14:00").dureeMinutes(120).notes("Meilleure visite tôt le matin ou en fin d'après-midi. Portez de bonnes chaussures.").build()));

        // Circuit 3: Artisanat & Gastronomie
        Circuit c3 = circuitRepo.save(Circuit.builder()
                .titre("Artisanat & Gastronomie de Fès").description("Une journée immersive dans les souks de Fès pour découvrir l'artisanat traditionnel et la gastronomie marocaine.")
                .theme("ARTISANAT,GASTRONOMIE").ville("Fès").dureeJours(1)
                .prixEstime(250.0).statut("PUBLIE").build());
        lieux.stream().filter(l -> l.getNom().equals("Souk Attarine")).findFirst().ifPresent(l ->
            etapeRepo.save(EtapeCircuit.builder().circuit(c3).lieu(l).ordre(1).heureVisite("09:00").dureeMinutes(60).notes("Négociez les prix. Les épices en vrac sont moins chères.").build()));
        lieux.stream().filter(l -> l.getNom().equals("Fontaine Nejjarine")).findFirst().ifPresent(l ->
            etapeRepo.save(EtapeCircuit.builder().circuit(c3).lieu(l).ordre(2).heureVisite("11:00").dureeMinutes(30).notes("Belle fontaine décorée de zellige. Parfait pour les photos.").build()));
        lieux.stream().filter(l -> l.getNom().equals("Tanneries Chouara")).findFirst().ifPresent(l ->
            etapeRepo.save(EtapeCircuit.builder().circuit(c3).lieu(l).ordre(3).heureVisite("14:00").dureeMinutes(45).notes("Achetez du cuir authentique directement aux artisans.").build()));

        // Circuits 4-15 : variete de themes/durees/budgets pour les recommandations IA
        Circuit c4 = circuitRepo.save(Circuit.builder()
                .titre("Découverte Culinaire de Fès")
                .description("Une journée dédiée aux saveurs de Fès : cours de cuisine marocaine, dégustation au Café Clock et dîner traditionnel au Riad Dar Hatim. Idéal pour les amateurs de gastronomie locale.")
                .theme("Gastronomie").ville("Fès").dureeJours(1).prixEstime(400.0)
                .photoUrl("https://loremflickr.com/640/480/morocco,tagine,food?lock=101")
                .statut("PUBLIE").noteMoyenne(4.6).nombreAvis(15).build());
        etape(c4, lieux, "Cours de Cuisine Marocaine", 1, "10:00", 150, "Cours de cuisine avec un chef local : tagine, pastilla et pâtisseries marocaines.");
        etape(c4, lieux, "Café Clock Fès", 2, "13:00", 90, "Déjeuner sur la terrasse du Café Clock, vue imprenable sur la médina.");
        etape(c4, lieux, "Restaurant Dar Hatim", 3, "19:30", 120, "Dîner traditionnel dans le cadre authentique du Riad Dar Hatim.");

        Circuit c5 = circuitRepo.save(Circuit.builder()
                .titre("Souks et Artisanat de Fès")
                .description("Plongez dans l'effervescence des souks de Fès : tanneries Chouara, souk Attarine aux épices et ateliers d'artisans. Négociation, couleurs et authenticité garanties.")
                .theme("Artisanat").ville("Fès").dureeJours(1).prixEstime(250.0)
                .photoUrl("https://loremflickr.com/640/480/morocco,souk,leather?lock=102")
                .statut("PUBLIE").noteMoyenne(4.3).nombreAvis(20).build());
        etape(c5, lieux, "Souk Attarine", 1, "09:00", 90, "Exploration du souk Attarine : épices, cuir et artisanat local. Négociez les prix !");
        etape(c5, lieux, "Tanneries Chouara", 2, "11:00", 60, "Vue sur les tanneries Chouara et démonstration du tannage traditionnel.");

        Circuit c6 = circuitRepo.save(Circuit.builder()
                .titre("Fès Authentique - Tour Photo")
                .description("Un circuit pensé pour les photographes : ruelles de la médina, façades en zellige, fontaines historiques et balade en calèche au coucher du soleil pour les plus belles lumières.")
                .theme("Photographie").ville("Fès").dureeJours(2).prixEstime(600.0)
                .photoUrl("https://loremflickr.com/640/480/morocco,photography,medina?lock=103")
                .statut("PUBLIE").noteMoyenne(4.7).nombreAvis(10).build());
        etape(c6, lieux, "Bab Bou Jeloud", 1, "09:00", 60, "Lever de soleil sur Bab Bou Jeloud, la porte bleue emblématique de Fès.");
        etape(c6, lieux, "Fontaine Nejjarine", 2, "11:00", 45, "La Fontaine Nejjarine et ses mosaïques, un classique pour les photographes.");
        etape(c6, lieux, "Borj Nord", 3, "17:00", 60, "Panorama sur la médina depuis Borj Nord au coucher du soleil.");
        etape(c6, lieux, "Balade en Calèche - Médina", 4, "18:30", 60, "Balade en calèche dans la médina illuminée.");

        Circuit c7 = circuitRepo.save(Circuit.builder()
                .titre("Hammam & Bien-être à Fès")
                .description("Une parenthèse de détente : hammam traditionnel et soins au spa du Riad Fès, suivis d'un moment de relaxation dans le cadre raffiné de l'Hôtel Sahrai.")
                .theme("Bien-être").ville("Fès").dureeJours(1).prixEstime(350.0)
                .photoUrl("https://loremflickr.com/640/480/morocco,spa,hammam?lock=104")
                .statut("PUBLIE").noteMoyenne(4.4).nombreAvis(9).build());
        etape(c7, lieux, "Riad Fès", 1, "10:00", 120, "Hammam traditionnel et gommage au spa du Riad Fès.");
        etape(c7, lieux, "Hôtel Sahrai", 2, "14:00", 90, "Relaxation autour de la piscine et soins au spa de l'Hôtel Sahrai.");

        Circuit c8 = circuitRepo.save(Circuit.builder()
                .titre("Volubilis & Moulay Idriss - Immersion Antique")
                .description("Voyage dans le temps vers les ruines romaines de Volubilis, classées à l'UNESCO, puis découverte du Mausolée Moulay Ismaïl à Meknès.")
                .theme("Histoire").ville("Meknès").dureeJours(1).prixEstime(450.0)
                .photoUrl("https://loremflickr.com/640/480/morocco,volubilis,ruins?lock=105")
                .statut("PUBLIE").noteMoyenne(4.6).nombreAvis(14).build());
        etape(c8, lieux, "Volubilis", 1, "09:00", 120, "Découverte des mosaïques romaines et des colonnes de Volubilis.");
        etape(c8, lieux, "Mausolée Moulay Ismail", 2, "13:00", 60, "Visite du Mausolée Moulay Ismaïl à Meknès.");

        Circuit c9 = circuitRepo.save(Circuit.builder()
                .titre("Randonnée Moyen Atlas - Ifrane & Azrou")
                .description("Deux jours de grand air dans le Moyen Atlas : randonnée dans la forêt de cèdres d'Azrou à la rencontre des singes magots, puis détente au bord du lac Dayet Aoua.")
                .theme("Aventure").ville("Azrou").dureeJours(2).prixEstime(800.0)
                .photoUrl("https://loremflickr.com/640/480/morocco,atlas,hiking?lock=106")
                .statut("PUBLIE").noteMoyenne(4.5).nombreAvis(11).build());
        etape(c9, lieux, "Randonnée Moyen Atlas", 1, "09:00", 240, "Randonnée guidée dans le Moyen Atlas à la rencontre des singes magots.");
        etape(c9, lieux, "Forêt de Cèdres d'Azrou", 2, "14:00", 90, "Promenade dans la forêt de cèdres d'Azrou.");
        etape(c9, lieux, "Lac Dayet Aoua", 3, "10:00", 120, "Détente au bord du lac Dayet Aoua (jour 2).");

        Circuit c10 = circuitRepo.save(Circuit.builder()
                .titre("Séjour Détente à Ifrane")
                .description("Trois jours au calme dans la \"Suisse marocaine\" : air pur, balades autour du lac Dayet Aoua et promenades en forêt pour se ressourcer loin de l'agitation des médinas.")
                .theme("Détente").ville("Ifrane").dureeJours(3).prixEstime(1800.0)
                .photoUrl("https://loremflickr.com/640/480/morocco,ifrane,nature?lock=107")
                .statut("PUBLIE").noteMoyenne(4.2).nombreAvis(7).build());
        etape(c10, lieux, "Lac Dayet Aoua", 1, "10:00", 150, "Balade tranquille autour du lac Dayet Aoua.");
        etape(c10, lieux, "Forêt de Cèdres d'Azrou", 2, "10:00", 120, "Promenade ressourçante dans la forêt de cèdres (jour 2).");

        Circuit c11 = circuitRepo.save(Circuit.builder()
                .titre("Grand Tour Impérial Fès-Meknès")
                .description("L'expérience complète des villes impériales : médina de Fès, médersa Bou Inania, mosquée Al-Qarawiyyin, Bab Mansour et ruines de Volubilis sur six jours inoubliables.")
                .theme("Culture").ville("Fès").dureeJours(6).prixEstime(3500.0)
                .photoUrl("https://loremflickr.com/640/480/morocco,imperial,tour?lock=108")
                .statut("PUBLIE").noteMoyenne(4.9).nombreAvis(25).build());
        etape(c11, lieux, "Médina de Fès", 1, "09:00", 120, "Visite guidée de la médina de Fès, joyau classé à l'UNESCO.");
        etape(c11, lieux, "Medersa Bou Inania", 2, "11:30", 60, "Medersa Bou Inania : chef-d'œuvre d'architecture mérinide.");
        etape(c11, lieux, "Mosquée Al-Qarawiyyin", 3, "14:00", 45, "Vue extérieure sur la mosquée Al-Qarawiyyin.");
        etape(c11, lieux, "Bab Mansour", 4, "09:00", 30, "Bab Mansour, la plus belle porte impériale de Meknès (jour suivant).");
        etape(c11, lieux, "Volubilis", 5, "11:00", 120, "Journée à Volubilis, site archéologique romain.");

        Circuit c12 = circuitRepo.save(Circuit.builder()
                .titre("Aventure Famille Moyen Atlas")
                .description("Un séjour en famille entre nature et découvertes : forêts de cèdres, lacs du Moyen Atlas et randonnées adaptées à tous les âges, pour petits et grands aventuriers.")
                .theme("Aventure").ville("Azrou").dureeJours(4).prixEstime(2200.0)
                .photoUrl("https://loremflickr.com/640/480/morocco,family,forest?lock=109")
                .statut("PUBLIE").noteMoyenne(4.4).nombreAvis(13).build());
        etape(c12, lieux, "Forêt de Cèdres d'Azrou", 1, "09:00", 120, "Initiation à la randonnée en famille dans la forêt de cèdres d'Azrou.");
        etape(c12, lieux, "Randonnée Moyen Atlas", 2, "11:30", 180, "Randonnée Moyen Atlas adaptée aux familles.");
        etape(c12, lieux, "Lac Dayet Aoua", 3, "10:00", 120, "Pique-nique et jeux au bord du lac Dayet Aoua (jour 3).");

        Circuit c13 = circuitRepo.save(Circuit.builder()
                .titre("Week-end Gastronomique & Spa")
                .description("Un week-end pensé pour les épicuriens : cours de cuisine, déjeuner gastronomique au Café Clock, nuit au Riad Fès et moment bien-être à l'Hôtel Sahrai.")
                .theme("Gastronomie").ville("Fès").dureeJours(2).prixEstime(1200.0)
                .photoUrl("https://loremflickr.com/640/480/morocco,gourmet,riad?lock=110")
                .statut("PUBLIE").noteMoyenne(4.7).nombreAvis(18).build());
        etape(c13, lieux, "Cours de Cuisine Marocaine", 1, "10:00", 150, "Cours de cuisine marocaine avec un chef.");
        etape(c13, lieux, "Café Clock Fès", 2, "13:00", 90, "Déjeuner gastronomique au Café Clock.");
        etape(c13, lieux, "Riad Fès", 3, "16:00", 0, "Installation et nuit au Riad Fès.");
        etape(c13, lieux, "Hôtel Sahrai", 4, "10:00", 120, "Moment bien-être au spa de l'Hôtel Sahrai (jour 2).");

        Circuit c14 = circuitRepo.save(Circuit.builder()
                .titre("Fès Express - Découverte Express")
                .description("Pour les visiteurs de passage : l'essentiel de Fès en une journée, entre Bab Bou Jeloud et la médina historique, à petit budget.")
                .theme("Culture").ville("Fès").dureeJours(1).prixEstime(200.0)
                .photoUrl("https://loremflickr.com/640/480/morocco,fes,daytrip?lock=111")
                .statut("PUBLIE").noteMoyenne(4.0).nombreAvis(22).build());
        etape(c14, lieux, "Bab Bou Jeloud", 1, "09:00", 45, "Photo souvenir devant Bab Bou Jeloud.");
        etape(c14, lieux, "Médina de Fès", 2, "10:00", 180, "Découverte express de la médina de Fès, ses ruelles et ses places emblématiques.");

        Circuit c15 = circuitRepo.save(Circuit.builder()
                .titre("Luxe Impérial - Riad & Gastronomie")
                .description("Une expérience haut de gamme : suite au Riad Fès, dîners gastronomiques au Café Clock et à Dar Hatim, cours de cuisine privé et services exclusifs de l'Hôtel Sahrai.")
                .theme("Gastronomie").ville("Fès").dureeJours(3).prixEstime(4500.0)
                .photoUrl("https://loremflickr.com/640/480/morocco,luxury,fes?lock=112")
                .statut("PUBLIE").noteMoyenne(4.9).nombreAvis(9).build());
        etape(c15, lieux, "Riad Fès", 1, "15:00", 0, "Installation dans une suite du Riad Fès.");
        etape(c15, lieux, "Café Clock Fès", 2, "13:00", 90, "Déjeuner raffiné au Café Clock (jour 2).");
        etape(c15, lieux, "Restaurant Dar Hatim", 3, "20:00", 120, "Dîner gastronomique au Restaurant Dar Hatim (jour 2).");
        etape(c15, lieux, "Cours de Cuisine Marocaine", 4, "10:00", 150, "Cours de cuisine privé avec un chef étoilé (jour 3).");

        log.info("✅ 15 circuits seeded with étapes");
    }

    private void etape(Circuit circuit, List<Lieu> lieux, String lieuNom, int ordre, String heure, int dureeMinutes, String notes) {
        lieux.stream().filter(l -> l.getNom().equals(lieuNom)).findFirst().ifPresent(l ->
            etapeRepo.save(EtapeCircuit.builder().circuit(circuit).lieu(l).ordre(ordre)
                    .heureVisite(heure).dureeMinutes(dureeMinutes).notes(notes).build()));
    }

    private void seedEvenements() {
        List<Lieu> lieux = lieuRepo.findAll();
        Lieu hedim = lieux.stream().filter(l -> l.getNom().equals("Place El Hedim")).findFirst().orElse(null);
        Lieu volubilis = lieux.stream().filter(l -> l.getNom().equals("Volubilis")).findFirst().orElse(null);

        evenementRepo.save(Evenement.builder()
                .nom("Festival de Fès des Musiques Sacrées du Monde")
                .description("Le célèbre festival international de musiques sacrées qui rassemble des artistes du monde entier.")
                .categorie("CULTURE").ville("Fès")
                .dateDebut(LocalDateTime.of(2026,6,5,18,0))
                .dateFin(LocalDateTime.of(2026,6,14,23,0))
                .gratuit(false).prixEntree(150.0).build());

        evenementRepo.save(Evenement.builder()
                .nom("Nuit des Musées à Meknès")
                .description("Une nuit exceptionnelle d'ouverture gratuite de tous les musées de Meknès avec spectacles et animations.")
                .categorie("TOURISME").ville("Meknès")
                .dateDebut(LocalDateTime.of(2026,5,18,20,0))
                .dateFin(LocalDateTime.of(2026,5,19,1,0))
                .gratuit(true).lieu(hedim).build());

        log.info("✅ 2 evenements seeded");
    }

    private void seedQuestions() {
        questionRepo.save(q("Quel type d'expérience recherchez-vous ?","ما نوع التجربة التي تبحث عنها؟","What type of experience are you looking for?","SINGLE_CHOICE","CULTURE,HISTOIRE,NATURE,GASTRONOMIE,AVENTURE","preferences",1,"🎭"));
        questionRepo.save(q("Quel est votre budget (MAD) ?","ما هي ميزانيتك (درهم)؟","What is your budget (MAD)?","SLIDER","0,5000,50","budget",2,"💰"));
        questionRepo.save(q("Combien de jours ?","كم عدد الأيام؟","How many days?","NUMBER","1,30","dureeJours",3,"📅"));
        questionRepo.save(q("Combien de personnes ?","كم عدد الأشخاص؟","How many people?","NUMBER","1,20","nombrePersonnes",4,"👥"));
        questionRepo.save(q("Quel rythme préférez-vous ?","ما الوتيرة التي تفضلها؟","What pace do you prefer?","SINGLE_CHOICE","Relaxed,Moderate,Intense","rythme",5,"⚡"));
        questionRepo.save(q("Avez-vous des contraintes de mobilité ?","هل لديك قيود على الحركة؟","Do you have mobility constraints?","TOGGLE","true,false","accessibilite",6,"♿"));
        questionRepo.save(q("Quel moment de la journée préférez-vous ?","ما الوقت المفضل لديك في اليوم؟","What time of day do you prefer?","MULTI_CHOICE","Matin,Après-midi,Soir","horaire",7,"🕐"));
        questionRepo.save(q("Type d'hébergement souhaité ?","نوع الإقامة المطلوبة؟","Preferred accommodation type?","SINGLE_CHOICE","Budget,Standard,Luxury","hebergement",8,"🏨"));
        log.info("✅ 8 questions formulaire seeded");
    }

    /** 40 publications variees (Fes-Meknes-Ifrane-Azrou) avec categorie + image reelle. */
    private void seedPublications() {
        List<Lieu> lieux = lieuRepo.findAll();
        List<Utilisateur> users = userRepo.findAll();

        String[][] data = {
            // {categorie, contenu, tags loremflickr, region, nom du lieu (ou ""), nbLikes}
            {"culture", "La Médina de Fès, classée au patrimoine mondial de l'UNESCO, n'a pas son pareil pour se perdre dans ses 9000 ruelles ! Une expérience hors du temps.", "morocco,medina,culture", "Fès", "Médina de Fès", "42"},
            {"culture", "Visite de la Medersa Bou Inania aujourd'hui : les zelliges et le plafond en cèdre sculpté sont impressionnants. Un chef-d'œuvre de l'architecture mérinide.", "morocco,medersa,architecture", "Fès", "Medersa Bou Inania", "18"},
            {"culture", "Coucher de soleil sur la médina depuis Borj Nord, vue à 360° sur Fès. Un moment magique à ne pas manquer !", "morocco,sunset,medina", "Fès", "Borj Nord", "35"},
            {"culture", "Le Mellah de Fès, ancien quartier juif, regorge d'histoire et de magnifiques balcons en bois sculpté. Une autre facette de la ville.", "morocco,mellah,heritage", "Fès", "Mellah de Fès", "9"},
            {"culture", "Petite pause devant le Palais Royal de Fès : les portes en bronze doré sont absolument magnifiques, impossible de ne pas s'arrêter pour une photo.", "morocco,palace,gate", "Fès", "Palais Royal de Fès", "27"},

            {"nature", "Journée ressourcement au bord du Lac Dayet Aoua à Ifrane. L'air frais du Moyen Atlas fait vraiment du bien après l'agitation de la médina !", "morocco,lake,nature", "Ifrane", "Lac Dayet Aoua", "51"},
            {"nature", "Promenade dans la forêt de cèdres d'Azrou, à la rencontre des célèbres singes magots. Un vrai bol d'air et des rencontres inoubliables.", "morocco,forest,monkeys", "Azrou", "Forêt de Cèdres d'Azrou", "38"},
            {"nature", "Le jardin Jnan Sbil en plein cœur de Fès est un petit havre de paix avec ses bassins et ses allées ombragées. Parfait pour une pause entre deux visites.", "morocco,garden,park", "Fès", "Jardin Jnan Sbil", "14"},
            {"nature", "Randonnée dans le Moyen Atlas ce week-end : paysages à couper le souffle entre forêts de cèdres et sommets enneigés au loin.", "morocco,atlas,mountains", "Meknès", "Randonnée Moyen Atlas", "29"},
            {"nature", "Ifrane mérite vraiment son surnom de \"Suisse marocaine\", avec ses chalets et son climat frais, on se sent ailleurs qu'au Maroc !", "morocco,ifrane,chalet", "Ifrane", "", "22"},

            {"food", "Cours de cuisine marocaine aujourd'hui : on a appris à préparer un tagine d'agneau aux pruneaux. Les odeurs d'épices envahissaient toute la pièce !", "morocco,cooking,tagine", "Fès", "Cours de Cuisine Marocaine", "60"},
            {"food", "Déjeuner sur la terrasse du Café Clock avec vue sur la médina, leur fameux camel burger est à essayer absolument.", "morocco,cafe,burger", "Fès", "Café Clock Fès", "47"},
            {"food", "Dîner traditionnel au Restaurant Dar Hatim : pastilla, couscous et pâtisseries au miel, un festin royal dans un cadre somptueux.", "morocco,pastilla,couscous", "Fès", "Restaurant Dar Hatim", "33"},
            {"food", "Petit-déjeuner marocain typique : msemen, baghrir et thé à la menthe. Le meilleur moyen de démarrer la journée !", "morocco,breakfast,tea", "Fès", "", "11"},
            {"food", "Découverte du Restaurant Collier de la Colombe à Meknès, vue magnifique sur la vallée et cuisine traditionnelle excellente.", "morocco,restaurant,view", "Meknès", "Restaurant Collier de la Colombe", "19"},

            {"adventure", "Randonnée dans le Moyen Atlas avec une guide locale : sentiers escarpés, panoramas grandioses, journée intense mais incroyable !", "morocco,hiking,atlas", "Meknès", "Randonnée Moyen Atlas", "44"},
            {"adventure", "Balade à dos de mulet dans les environs d'Azrou, une autre façon de découvrir la forêt de cèdres.", "morocco,mule,forest", "Azrou", "Forêt de Cèdres d'Azrou", "25"},
            {"adventure", "Sortie VTT entre Ifrane et Azrou : pistes forestières superbes, on a croisé des troupeaux de moutons en chemin.", "morocco,mountainbike,forest", "Azrou", "", "8"},
            {"adventure", "Exploration des ruelles cachées de la médina de Fès, GPS coupé, juste pour le plaisir de se perdre et tout découvrir par hasard.", "morocco,medina,alley", "Fès", "Médina de Fès", "31"},
            {"adventure", "Lever très tôt pour observer le lever du soleil depuis les hauteurs de Fès, le froid du matin valait vraiment le coup.", "morocco,sunrise,fes", "Fès", "", "16"},

            {"history", "Visite des ruines romaines de Volubilis : les mosaïques sont incroyablement bien conservées après presque 2000 ans !", "morocco,volubilis,roman", "Meknès", "Volubilis", "55"},
            {"history", "Le Mausolée Moulay Ismaïl à Meknès est d'une beauté saisissante, les zelliges verts et bleus illuminent toute la salle.", "morocco,mausoleum,zellige", "Meknès", "Mausolée Moulay Ismail", "40"},
            {"history", "Bab Mansour, la porte monumentale de Meknès, est un véritable chef-d'œuvre. On comprend pourquoi Meknès était surnommée le Versailles marocain.", "morocco,gate,meknes", "Meknès", "Bab Mansour", "28"},
            {"history", "La mosquée Al-Qarawiyyin abrite l'une des plus anciennes universités du monde, fondée au 9e siècle. Impossible de ne pas être impressionné par tant d'histoire.", "morocco,mosque,history", "Fès", "Mosquée Al-Qarawiyyin", "12"},
            {"history", "Visite du Musée Dar Jamaï à Meknès, ancien palais transformé en musée d'art marocain, magnifique architecture andalouse.", "morocco,museum,art", "Meknès", "Musée Dar Jamaï", "21"},

            {"wellness", "Moment de pur bien-être au spa du Riad Fès après une longue journée de visite. Hammam, gommage, massage... la détente totale.", "morocco,spa,riad", "Fès", "Riad Fès", "37"},
            {"wellness", "Première expérience de hammam traditionnel marocain : intense mais tellement relaxant après coup, la peau est toute douce !", "morocco,hammam,relax", "Fès", "", "6"},
            {"wellness", "Après-midi piscine et spa à l'Hôtel Sahrai, vue imprenable sur la médina depuis la terrasse. Un vrai cocon de tranquillité.", "morocco,pool,spa", "Fès", "Hôtel Sahrai", "48"},
            {"wellness", "Séance de yoga au lever du soleil dans le jardin de notre riad, le calme avant l'effervescence de la médina.", "morocco,yoga,sunrise", "Fès", "", "13"},
            {"wellness", "Petit séjour détente à Ifrane, loin du bruit, juste le chant des oiseaux et l'air pur du Moyen Atlas. Exactement ce dont j'avais besoin.", "morocco,relax,nature", "Ifrane", "", "24"},

            {"shopping", "Session shopping intense au Souk Attarine : épices, tapis, lanternes... difficile de résister à tout acheter !", "morocco,souk,spices", "Fès", "Souk Attarine", "46"},
            {"shopping", "Visite des tanneries Chouara, l'odeur est forte mais le spectacle des bassins colorés est unique au monde.", "morocco,tannery,leather", "Fès", "Tanneries Chouara", "32"},
            {"shopping", "Trouvé une magnifique théière en cuivre dans un petit atelier de la médina, négociée avec le sourire comme il se doit.", "morocco,copper,craft", "Fès", "", "9"},
            {"shopping", "Balade dans les souks de Meknès autour de la Place El Hedim, ambiance électrique en fin de journée avec les vendeurs ambulants.", "morocco,market,meknes", "Meknès", "Place El Hedim", "20"},
            {"shopping", "Acheté un magnifique tapis berbère fait main, le vendeur nous a raconté toute son histoire pendant le thé à la menthe.", "morocco,carpet,berber", "Fès", "", "53"},

            {"festivals", "Ambiance incroyable ce soir à la Place El Hedim avec les musiciens et conteurs traditionnels, on se sent transportés dans une autre époque.", "morocco,music,storyteller", "Meknès", "Place El Hedim", "41"},
            {"festivals", "Festival des Cerises de Sefrou : musique, défilés et bien sûr des cerises à volonté ! Une tradition locale à découvrir.", "morocco,festival,cherries", "Fès", "", "17"},
            {"festivals", "Soirée musique gnaoua improvisée dans la médina, les rythmes et les couleurs des costumes traditionnels étaient magnifiques.", "morocco,gnaoua,music", "Fès", "", "7"},
            {"festivals", "Le Festival de Fès des Musiques Sacrées du Monde attire des artistes de tous horizons, une programmation toujours exceptionnelle.", "morocco,festival,music", "Fès", "", "30"},
            {"festivals", "Petite fête de quartier improvisée près de Bab Bou Jeloud, tout le monde dansait au son des tambours. Pure spontanéité marocaine !", "morocco,celebration,dance", "Fès", "Bab Bou Jeloud", "36"},
        };

        for (int i = 0; i < data.length; i++) {
            String[] row = data[i];
            Lieu lieu = row[4].isEmpty() ? null
                    : lieux.stream().filter(l -> l.getNom().equals(row[4])).findFirst().orElse(null);
            Utilisateur author = users.get(i % users.size());

            pubRepo.save(Publication.builder()
                    .contenu(row[1])
                    .photoUrl("https://loremflickr.com/640/480/" + row[2] + "?lock=" + (301 + i))
                    .region(row[3])
                    .categorie(row[0])
                    .statut("APPROUVE")
                    .nbLikes(Integer.parseInt(row[5]))
                    .utilisateur(author)
                    .lieu(lieu)
                    .build());
        }
        log.info("✅ 40 publications seeded");
    }

    /** 20 publications supplementaires, memes 10 categories, themes festival/restaurant/spiritualite/luxe/famille/decouverte locale inclus. */
    private void seedMorePublications() {
        List<Lieu> lieux = lieuRepo.findAll();
        List<Utilisateur> users = userRepo.findAll();

        String[][] data = {
            {"culture", MORE_PUBLICATIONS_MARKER, "morocco,calligraphy,art", "Fès", "", "23"},
            {"culture", "Visite guidée du Mellah de Fès, l'ancien quartier juif : ses balcons en bois sculpté racontent une autre page de l'histoire de la ville.", "morocco,mellah,jewish", "Fès", "Mellah de Fès", "16"},

            {"nature", "Pique-nique en famille au bord du Lac Dayet Aoua, les enfants ont adoré observer les oiseaux migrateurs.", "morocco,lake,family", "Ifrane", "Lac Dayet Aoua", "34"},
            {"nature", "Lever de soleil sur la forêt de cèdres d'Azrou, le silence et la brume matinale donnent une ambiance presque irréelle.", "morocco,cedar,sunrise", "Azrou", "Forêt de Cèdres d'Azrou", "29"},

            {"food", "Dégustation de pastilla au pigeon et de cornes de gazelle dans une petite échoppe locale, une explosion de saveurs authentiques.", "morocco,pastilla,dessert", "Fès", "", "39"},
            {"food", "Soirée gastronomique au Restaurant Dar Hatim avec spectacle de musique andalouse, un dîner inoubliable en famille.", "morocco,gastronomy,music", "Fès", "Restaurant Dar Hatim", "27"},

            {"adventure", "Sortie en famille pour une randonnée douce dans le Moyen Atlas, parfait pour initier les enfants à la marche en pleine nature.", "morocco,family,hiking", "Meknès", "Randonnée Moyen Atlas", "22"},
            {"adventure", "Excursion à VTT autour d'Ifrane, entre forêts et clairières, une belle dose d'adrénaline pour les amateurs de sensations.", "morocco,mountainbike,ifrane", "Ifrane", "", "18"},

            {"history", "Moment de recueillement à la mosquée Al-Qarawiyyin, plus ancienne université du monde fondée en 859, une expérience spirituelle forte.", "morocco,mosque,spiritual", "Fès", "Mosquée Al-Qarawiyyin", "31"},
            {"history", "Exploration du Grenier Royal Heri Souani à Meknès, impressionnant par l'ampleur de son architecture du XVIIe siècle.", "morocco,granary,history", "Meknès", "Grenier Royal Heri Souani", "14"},

            {"wellness", "Nuit de luxe au Riad Fès : suite raffinée, patio fleuri et petit-déjeuner servi sur la terrasse avec vue sur la médina.", "morocco,luxury,riad", "Fès", "Riad Fès", "45"},
            {"wellness", "Séjour à l'Hôtel Sahrai, le service est impeccable et la piscine à débordement offre une vue à couper le souffle sur Fès.", "morocco,hotel,pool", "Fès", "Hôtel Sahrai", "33"},

            {"shopping", "Petite découverte locale : un atelier de poterie familial transmis depuis quatre générations, niché dans une ruelle discrète de la médina.", "morocco,pottery,craft", "Fès", "", "20"},
            {"shopping", "Trouvé de magnifiques lanternes en fer forgé au marché de Meknès, parfaites pour ramener un souvenir authentique.", "morocco,lantern,market", "Meknès", "Place El Hedim", "17"},

            {"festivals", "Soirée d'ouverture du Festival de Fès des Musiques Sacrées du Monde, une programmation éblouissante réunissant des artistes du monde entier.", "morocco,festival,event", "Fès", "", "50"},
            {"festivals", "Nuit des Musées à Meknès : entrée gratuite, animations et spectacles dans toute la ville jusqu'à une heure avancée.", "morocco,museum,night", "Meknès", "", "26"},

            {"restaurants", "Brunch dominical au Café Clock, ambiance décontractée et plats marocains réinventés, un classique incontournable à Fès.", "morocco,brunch,cafe", "Fès", "Café Clock Fès", "24"},
            {"restaurants", "Dîner romantique au Restaurant Collier de la Colombe à Meknès, vue sur la vallée au coucher du soleil, un cadre idéal.", "morocco,restaurant,sunset", "Meknès", "Restaurant Collier de la Colombe", "19"},

            {"monuments", "Admiration sans fin devant Bab Mansour illuminée la nuit, ses mosaïques scintillent sous les projecteurs.", "morocco,monument,night", "Meknès", "Bab Mansour", "28"},
            {"monuments", "Visite du Borj Nord, ses remparts du XVIe siècle offrent un panorama exceptionnel sur toute la médina de Fès.", "morocco,fortress,panorama", "Fès", "Borj Nord", "21"},
        };

        for (int i = 0; i < data.length; i++) {
            String[] row = data[i];
            Lieu lieu = row[4].isEmpty() ? null
                    : lieux.stream().filter(l -> l.getNom().equals(row[4])).findFirst().orElse(null);
            Utilisateur author = users.get(i % users.size());

            pubRepo.save(Publication.builder()
                    .contenu(row[1])
                    .photoUrl("https://loremflickr.com/640/480/" + row[2] + "?lock=" + (341 + i))
                    .region(row[3])
                    .categorie(row[0])
                    .statut("APPROUVE")
                    .nbLikes(Integer.parseInt(row[5]))
                    .utilisateur(author)
                    .lieu(lieu)
                    .build());
        }
        log.info("✅ 20 publications supplementaires seeded");
    }

    private QuestionFormulaire q(String fr, String ar, String en, String type,
                                  String opts, String champ, int ordre, String icone) {
        return QuestionFormulaire.builder()
                .texteFr(fr).texteAr(ar).texteEn(en)
                .typeQuestion(type).options(opts)
                .champCible(champ).ordre(ordre).icone(icone).build();
    }
}
