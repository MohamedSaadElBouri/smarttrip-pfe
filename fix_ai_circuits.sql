-- ============================================================
-- 12 nouveaux circuits PUBLIE diversifies (Fes-Meknes-Ifrane-Azrou)
-- pour donner de la variete aux recommandations IA (recommend-trips)
-- ============================================================

INSERT INTO circuits (id, titre, description, theme, ville, duree_jours, prix_estime, photo_url, statut, note_moyenne, nombre_avis, utilisateur_id, created_at) OVERRIDING SYSTEM VALUE VALUES
(11, 'Découverte Culinaire de Fès',
 'Une journée dédiée aux saveurs de Fès : cours de cuisine marocaine, dégustation au Café Clock et dîner traditionnel au Riad Dar Hatim. Idéal pour les amateurs de gastronomie locale.',
 'Gastronomie', 'Fès', 1, 400, 'https://loremflickr.com/640/480/morocco,tagine,food?lock=101', 'PUBLIE', 4.6, 15, 1, now()),

(12, 'Souks et Artisanat de Fès',
 'Plongez dans l''effervescence des souks de Fès : tanneries Chouara, souk Attarine aux épices et ateliers d''artisans. Négociation, couleurs et authenticité garanties.',
 'Artisanat', 'Fès', 1, 250, 'https://loremflickr.com/640/480/morocco,souk,leather?lock=102', 'PUBLIE', 4.3, 20, 2, now()),

(13, 'Fès Authentique - Tour Photo',
 'Un circuit pensé pour les photographes : ruelles de la médina, façades en zellige, fontaines historiques et balade en calèche au coucher du soleil pour les plus belles lumières.',
 'Photographie', 'Fès', 2, 600, 'https://loremflickr.com/640/480/morocco,photography,medina?lock=103', 'PUBLIE', 4.7, 10, 3, now()),

(14, 'Hammam & Bien-être à Fès',
 'Une parenthèse de détente : hammam traditionnel et soins au spa du Riad Fès, suivis d''un moment de relaxation dans le cadre raffiné de l''Hôtel Sahrai.',
 'Bien-être', 'Fès', 1, 350, 'https://loremflickr.com/640/480/morocco,spa,hammam?lock=104', 'PUBLIE', 4.4, 9, 4, now()),

(15, 'Volubilis & Moulay Idriss - Immersion Antique',
 'Voyage dans le temps vers les ruines romaines de Volubilis, classées à l''UNESCO, puis découverte du Mausolée Moulay Ismaïl à Meknès.',
 'Histoire', 'Meknès', 1, 450, 'https://loremflickr.com/640/480/morocco,volubilis,ruins?lock=105', 'PUBLIE', 4.6, 14, 1, now()),

(16, 'Randonnée Moyen Atlas - Ifrane & Azrou',
 'Deux jours de grand air dans le Moyen Atlas : randonnée dans la forêt de cèdres d''Azrou à la rencontre des singes magots, puis détente au bord du lac Dayet Aoua.',
 'Aventure', 'Azrou', 2, 800, 'https://loremflickr.com/640/480/morocco,atlas,hiking?lock=106', 'PUBLIE', 4.5, 11, 2, now()),

(17, 'Séjour Détente à Ifrane',
 'Trois jours au calme dans la "Suisse marocaine" : air pur, balades autour du lac Dayet Aoua et promenades en forêt pour se ressourcer loin de l''agitation des médinas.',
 'Détente', 'Ifrane', 3, 1800, 'https://loremflickr.com/640/480/morocco,ifrane,nature?lock=107', 'PUBLIE', 4.2, 7, 3, now()),

(18, 'Grand Tour Impérial Fès-Meknès',
 'L''expérience complète des villes impériales : médina de Fès, médersa Bou Inania, mosquée Al-Qarawiyyin, Bab Mansour et ruines de Volubilis sur six jours inoubliables.',
 'Culture', 'Fès', 6, 3500, 'https://loremflickr.com/640/480/morocco,imperial,tour?lock=108', 'PUBLIE', 4.9, 25, 4, now()),

(19, 'Aventure Famille Moyen Atlas',
 'Un séjour en famille entre nature et découvertes : forêts de cèdres, lacs du Moyen Atlas et randonnées adaptées à tous les âges, pour petits et grands aventuriers.',
 'Aventure', 'Azrou', 4, 2200, 'https://loremflickr.com/640/480/morocco,family,forest?lock=109', 'PUBLIE', 4.4, 13, 1, now()),

(20, 'Week-end Gastronomique & Spa',
 'Un week-end pensé pour les épicuriens : cours de cuisine, déjeuner gastronomique au Café Clock, nuit au Riad Fès et moment bien-être à l''Hôtel Sahrai.',
 'Gastronomie', 'Fès', 2, 1200, 'https://loremflickr.com/640/480/morocco,gourmet,riad?lock=110', 'PUBLIE', 4.7, 18, 2, now()),

(21, 'Fès Express - Découverte Express',
 'Pour les visiteurs de passage : l''essentiel de Fès en une journée, entre Bab Bou Jeloud et la médina historique, à petit budget.',
 'Culture', 'Fès', 1, 200, 'https://loremflickr.com/640/480/morocco,fes,daytrip?lock=111', 'PUBLIE', 4.0, 22, 3, now()),

(22, 'Luxe Impérial - Riad & Gastronomie',
 'Une expérience haut de gamme : suite au Riad Fès, dîners gastronomiques au Café Clock et à Dar Hatim, cours de cuisine privé et services exclusifs de l''Hôtel Sahrai.',
 'Gastronomie', 'Fès', 3, 4500, 'https://loremflickr.com/640/480/morocco,luxury,fes?lock=112', 'PUBLIE', 4.9, 9, 4, now());

-- Realigner la sequence d'identite apres insertion avec ids explicites
SELECT setval(pg_get_serial_sequence('circuits','id'), (SELECT MAX(id) FROM circuits));

-- ============================================================
-- Etapes (itineraires) pour les nouveaux circuits
-- ============================================================

INSERT INTO etapes_circuit (circuit_id, lieu_id, ordre, heure_visite, duree_minutes, notes) VALUES
-- 11: Découverte Culinaire de Fès
(11, 29, 1, '10:00', 150, 'Cours de cuisine avec un chef local : tagine, pastilla et pâtisseries marocaines.'),
(11, 26, 2, '13:00', 90, 'Déjeuner sur la terrasse du Café Clock, vue imprenable sur la médina.'),
(11, 25, 3, '19:30', 120, 'Dîner traditionnel dans le cadre authentique du Riad Dar Hatim.'),

-- 12: Souks et Artisanat de Fès
(12, 8, 1, '09:00', 90, 'Exploration du souk Attarine : épices, cuir et artisanat local. Négociez les prix !'),
(12, 3, 2, '11:00', 60, 'Vue sur les tanneries Chouara et démonstration du tannage traditionnel.'),

-- 13: Fès Authentique - Tour Photo
(13, 2, 1, '09:00', 60, 'Lever de soleil sur Bab Bou Jeloud, la porte bleue emblématique de Fès.'),
(13, 5, 2, '11:00', 45, 'La Fontaine Nejjarine et ses mosaïques, un classique pour les photographes.'),
(13, 9, 3, '17:00', 60, 'Panorama sur la médina depuis Borj Nord au coucher du soleil.'),
(13, 30, 4, '18:30', 60, 'Balade en calèche dans la médina illuminée.'),

-- 14: Hammam & Bien-être à Fès
(14, 27, 1, '10:00', 120, 'Hammam traditionnel et gommage au spa du Riad Fès.'),
(14, 28, 2, '14:00', 90, 'Relaxation autour de la piscine et soins au spa de l''Hôtel Sahrai.'),

-- 15: Volubilis & Moulay Idriss - Immersion Antique
(15, 13, 1, '09:00', 120, 'Découverte des mosaïques romaines et des colonnes de Volubilis.'),
(15, 14, 2, '13:00', 60, 'Visite du Mausolée Moulay Ismaïl à Meknès.'),

-- 16: Randonnée Moyen Atlas - Ifrane & Azrou
(16, 33, 1, '09:00', 240, 'Randonnée guidée dans le Moyen Atlas à la rencontre des singes magots.'),
(16, 19, 2, '14:00', 90, 'Promenade dans la forêt de cèdres d''Azrou.'),
(16, 18, 3, '10:00', 120, 'Détente au bord du lac Dayet Aoua (jour 2).'),

-- 17: Séjour Détente à Ifrane
(17, 18, 1, '10:00', 150, 'Balade tranquille autour du lac Dayet Aoua.'),
(17, 19, 2, '10:00', 120, 'Promenade ressourçante dans la forêt de cèdres (jour 2).'),

-- 18: Grand Tour Impérial Fès-Meknès
(18, 1, 1, '09:00', 120, 'Visite guidée de la médina de Fès, joyau classé à l''UNESCO.'),
(18, 4, 2, '11:30', 60, 'Medersa Bou Inania : chef-d''œuvre d''architecture mérinide.'),
(18, 7, 3, '14:00', 45, 'Vue extérieure sur la mosquée Al-Qarawiyyin.'),
(18, 15, 4, '09:00', 30, 'Bab Mansour, la plus belle porte impériale de Meknès (jour suivant).'),
(18, 13, 5, '11:00', 120, 'Journée à Volubilis, site archéologique romain.'),

-- 19: Aventure Famille Moyen Atlas
(19, 19, 1, '09:00', 120, 'Initiation à la randonnée en famille dans la forêt de cèdres d''Azrou.'),
(19, 33, 2, '11:30', 180, 'Randonnée Moyen Atlas adaptée aux familles.'),
(19, 18, 3, '10:00', 120, 'Pique-nique et jeux au bord du lac Dayet Aoua (jour 3).'),

-- 20: Week-end Gastronomique & Spa
(20, 29, 1, '10:00', 150, 'Cours de cuisine marocaine avec un chef.'),
(20, 26, 2, '13:00', 90, 'Déjeuner gastronomique au Café Clock.'),
(20, 27, 3, '16:00', 0, 'Installation et nuit au Riad Fès.'),
(20, 28, 4, '10:00', 120, 'Moment bien-être au spa de l''Hôtel Sahrai (jour 2).'),

-- 21: Fès Express - Découverte Express
(21, 2, 1, '09:00', 45, 'Photo souvenir devant Bab Bou Jeloud.'),
(21, 1, 2, '10:00', 180, 'Découverte express de la médina de Fès, ses ruelles et ses places emblématiques.'),

-- 22: Luxe Impérial - Riad & Gastronomie
(22, 27, 1, '15:00', 0, 'Installation dans une suite du Riad Fès.'),
(22, 26, 2, '13:00', 90, 'Déjeuner raffiné au Café Clock (jour 2).'),
(22, 25, 3, '20:00', 120, 'Dîner gastronomique au Restaurant Dar Hatim (jour 2).'),
(22, 29, 4, '10:00', 150, 'Cours de cuisine privé avec un chef étoilé (jour 3).');
