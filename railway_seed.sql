-- ============================================================
-- SmartTrip Railway Seed — circuits diversifies + publications
-- A coller dans Railway > Postgres > Console
-- Necessite qu'au moins 1 utilisateur soit inscrit dans l'app
-- ============================================================

DO $$
DECLARE v_user BIGINT;
BEGIN
    SELECT id INTO v_user FROM utilisateurs ORDER BY id LIMIT 1;
    IF v_user IS NULL THEN
        RAISE EXCEPTION 'Aucun utilisateur trouvé. Inscris-toi dans l''app d''abord, puis relance ce script.';
    END IF;

    -- ========================================================
    -- CIRCUITS diversifies (12 themes differents)
    -- ========================================================
    INSERT INTO circuits (titre, description, theme, ville, duree_jours, prix_estime, photo_url, statut, note_moyenne, nombre_avis, utilisateur_id, created_at)
    VALUES
    ('Découverte Culinaire de Fès',
     'Une journée dédiée aux saveurs de Fès : cours de cuisine marocaine, dégustation au Café Clock et dîner traditionnel au Riad Dar Hatim.',
     'Gastronomie', 'Fès', 1, 400,
     'https://loremflickr.com/640/480/morocco,tagine,food?lock=201', 'PUBLIE', 4.6, 15, v_user, now()),

    ('Souks et Artisanat de Fès',
     'Plongez dans l''effervescence des souks de Fès : tanneries Chouara, souk Attarine et ateliers d''artisans.',
     'Artisanat', 'Fès', 1, 250,
     'https://loremflickr.com/640/480/morocco,souk,leather?lock=202', 'PUBLIE', 4.3, 20, v_user, now()),

    ('Fès Authentique - Tour Photo',
     'Un circuit pensé pour les photographes : ruelles de la médina, façades en zellige et panorama au coucher du soleil.',
     'Photographie', 'Fès', 2, 600,
     'https://loremflickr.com/640/480/morocco,photography,medina?lock=203', 'PUBLIE', 4.7, 10, v_user, now()),

    ('Hammam & Bien-être à Fès',
     'Une parenthèse de détente : hammam traditionnel et soins au spa du Riad Fès, suivis d''un moment de relaxation.',
     'Bien-être', 'Fès', 1, 350,
     'https://loremflickr.com/640/480/morocco,spa,hammam?lock=204', 'PUBLIE', 4.4, 9, v_user, now()),

    ('Volubilis & Moulay Idriss - Immersion Antique',
     'Voyage dans le temps vers les ruines romaines de Volubilis (UNESCO) puis découverte du Mausolée Moulay Ismaïl.',
     'Histoire', 'Meknès', 1, 450,
     'https://loremflickr.com/640/480/morocco,volubilis,ruins?lock=205', 'PUBLIE', 4.6, 14, v_user, now()),

    ('Randonnée Moyen Atlas - Ifrane & Azrou',
     'Deux jours de grand air dans le Moyen Atlas : randonnée en forêt de cèdres, singes magots et lac Dayet Aoua.',
     'Aventure', 'Azrou', 2, 800,
     'https://loremflickr.com/640/480/morocco,atlas,hiking?lock=206', 'PUBLIE', 4.5, 11, v_user, now()),

    ('Séjour Détente à Ifrane',
     'Trois jours au calme dans la "Suisse marocaine" : air pur, balades en forêt et promenades au bord du lac.',
     'Détente', 'Ifrane', 3, 1800,
     'https://loremflickr.com/640/480/morocco,ifrane,nature?lock=207', 'PUBLIE', 4.2, 7, v_user, now()),

    ('Grand Tour Impérial Fès-Meknès',
     'L''expérience complète des villes impériales : médina de Fès, médersa Bou Inania et ruines de Volubilis sur six jours.',
     'Culture', 'Fès', 6, 3500,
     'https://loremflickr.com/640/480/morocco,imperial,tour?lock=208', 'PUBLIE', 4.9, 25, v_user, now()),

    ('Aventure Famille Moyen Atlas',
     'Un séjour en famille entre nature et découvertes : forêts de cèdres, lacs et randonnées adaptées à tous les âges.',
     'Aventure', 'Azrou', 4, 2200,
     'https://loremflickr.com/640/480/morocco,family,forest?lock=209', 'PUBLIE', 4.4, 13, v_user, now()),

    ('Week-end Gastronomique & Spa',
     'Un week-end pour les épicuriens : cours de cuisine, déjeuner gastronomique et nuit au Riad Fès.',
     'Gastronomie', 'Fès', 2, 1200,
     'https://loremflickr.com/640/480/morocco,gourmet,riad?lock=210', 'PUBLIE', 4.7, 18, v_user, now()),

    ('Fès Express - Découverte Médina',
     'L''essentiel de Fès en une journée : Bab Bou Jeloud, médina historique et mosquée Al-Qarawiyyin.',
     'Culture', 'Fès', 1, 200,
     'https://loremflickr.com/640/480/morocco,fes,medina?lock=211', 'PUBLIE', 4.0, 22, v_user, now()),

    ('Luxe Impérial - Riad & Gastronomie',
     'Une expérience haut de gamme : suite au Riad Fès, dîners gastronomiques et cours de cuisine privé.',
     'Gastronomie', 'Fès', 3, 4500,
     'https://loremflickr.com/640/480/morocco,luxury,fes?lock=212', 'PUBLIE', 4.9, 9, v_user, now()),

    ('Médersa Bou Inania & Fondouks',
     'Une exploration approfondie de l''architecture mérinide de Fès : médersas, fondouks et palais royaux.',
     'Histoire', 'Fès', 1, 300,
     'https://loremflickr.com/640/480/morocco,madrasa,architecture?lock=213', 'PUBLIE', 4.5, 16, v_user, now()),

    ('Nature & Cèdres de la Forêt d''Azrou',
     'Immersion totale dans la nature du Moyen Atlas : cèdres centenaires, singes magots et randonnée avec guide.',
     'Nature', 'Azrou', 1, 350,
     'https://loremflickr.com/640/480/morocco,cedar,forest?lock=214', 'PUBLIE', 4.3, 8, v_user, now()),

    ('Festival & Musique à Fès',
     'Découvrez Fès au rythme de ses festivals : musiques du monde, gnaoua et soirées culturelles dans la médina.',
     'Festivals', 'Fès', 2, 700,
     'https://loremflickr.com/640/480/morocco,festival,music?lock=215', 'PUBLIE', 4.6, 12, v_user, now());

    -- ========================================================
    -- PUBLICATIONS (feed) diversifiees
    -- ========================================================
    INSERT INTO publications (contenu, photo_url, region, categorie, statut, nb_likes, utilisateur_id, date)
    VALUES
    ('📍 Fès — Les tanneries Chouara au coucher du soleil, un spectacle de couleurs inoubliable ! #Artisanat #Fès',
     'https://loremflickr.com/640/480/morocco,tannery,leather?lock=301', 'Fès', 'Artisanat', 'APPROUVE', 12, v_user, now() - interval '1 day'),

    ('📍 Azrou — Rencontre avec les singes magots dans la forêt de cèdres du Moyen Atlas 🐒 #Nature #Aventure',
     'https://loremflickr.com/640/480/morocco,monkey,cedar?lock=302', 'Azrou', 'Aventure', 'APPROUVE', 8, v_user, now() - interval '2 days'),

    ('📍 Fès — Déjeuner incroyable au Café Clock ! Le tagine de kefta était parfait. #Restaurants #Gastronomie',
     'https://loremflickr.com/640/480/morocco,tagine,restaurant?lock=303', 'Fès', 'Restaurants', 'APPROUVE', 15, v_user, now() - interval '3 days'),

    ('📍 Meknès — Les ruines de Volubilis sous le soleil du matin, un moment hors du temps 🏛️ #Histoire',
     'https://loremflickr.com/640/480/morocco,ruins,roman?lock=304', 'Meknès', 'Monuments', 'APPROUVE', 20, v_user, now() - interval '4 days'),

    ('📍 Ifrane — La petite "Suisse marocaine" en automne, les couleurs sont magnifiques 🍂 #Nature',
     'https://loremflickr.com/640/480/morocco,ifrane,autumn?lock=305', 'Ifrane', 'Nature', 'APPROUVE', 6, v_user, now() - interval '5 days'),

    ('📍 Fès — La médersa Bou Inania, chef-d''œuvre de l''architecture mérinide en plein cœur de la médina #Culture',
     'https://loremflickr.com/640/480/morocco,madrasa,tile?lock=306', 'Fès', 'Culture', 'APPROUVE', 18, v_user, now() - interval '6 days'),

    ('📍 Fès — Hammam traditionnel au Riad Fès : la meilleure façon de se ressourcer après une journée de visite 🧖',
     'https://loremflickr.com/640/480/morocco,hammam,spa?lock=307', 'Fès', 'Bien-être', 'APPROUVE', 9, v_user, now() - interval '7 days'),

    ('📍 Fès — Les souks de la médina en plein ramadan, une ambiance festive et chaleureuse #Festivals',
     'https://loremflickr.com/640/480/morocco,souk,night?lock=308', 'Fès', 'Culture', 'APPROUVE', 14, v_user, now() - interval '8 days'),

    ('📍 Azrou — Randonnée au lever du soleil dans le Moyen Atlas, vue à 360° sur les cèdres 🌄 #Aventure',
     'https://loremflickr.com/640/480/morocco,sunrise,hiking?lock=309', 'Azrou', 'Aventure', 'APPROUVE', 11, v_user, now() - interval '9 days'),

    ('📍 Fès — Cours de cuisine marocaine : j''ai appris à faire la pastilla ! Expérience unique 👩‍🍳 #Gastronomie',
     'https://loremflickr.com/640/480/morocco,cooking,pastilla?lock=310', 'Fès', 'Restaurants', 'APPROUVE', 22, v_user, now() - interval '10 days'),

    ('📍 Meknès — Bab Mansour, la plus belle porte impériale du Maroc en plein soleil #Monuments #Histoire',
     'https://loremflickr.com/640/480/morocco,gate,imperial?lock=311', 'Meknès', 'Monuments', 'APPROUVE', 16, v_user, now() - interval '11 days'),

    ('📍 Fès — Séance photo dans les ruelles de la vieille médina avec un photographe local 📸 #Photographie',
     'https://loremflickr.com/640/480/morocco,alley,medina?lock=312', 'Fès', 'Photographie', 'APPROUVE', 7, v_user, now() - interval '12 days'),

    ('📍 Ifrane — Lac Dayet Aoua au crépuscule, le reflet des cèdres dans l''eau est hypnotisant 🌊 #Nature',
     'https://loremflickr.com/640/480/morocco,lake,reflection?lock=313', 'Ifrane', 'Nature', 'APPROUVE', 10, v_user, now() - interval '13 days'),

    ('📍 Fès — Dégustation de pastilla aux pigeons et thé à la menthe au Palais de Fès. Le voyage culinaire ultime !',
     'https://loremflickr.com/640/480/morocco,pastilla,mint?lock=314', 'Fès', 'Restaurants', 'APPROUVE', 19, v_user, now() - interval '14 days'),

    ('📍 Fès — Nuit au Riad Dar Hatim : le rêve made in Morocco, patios étoilés et silence absolu ⭐ #Bien-être',
     'https://loremflickr.com/640/480/morocco,riad,courtyard?lock=315', 'Fès', 'Bien-être', 'APPROUVE', 13, v_user, now() - interval '15 days');

    RAISE NOTICE 'Migration terminée : 15 circuits + 15 publications insérés pour userId=%', v_user;
END $$;
