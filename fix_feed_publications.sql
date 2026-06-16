-- ============================================================
-- 40 publications APPROUVE diversifiees (Fes-Meknes-Ifrane-Azrou)
-- avec categorie + images loremflickr reelles, pour remplacer
-- les 3 publications generiques existantes dans le feed.
-- ============================================================

INSERT INTO publications (contenu, photo_url, region, categorie, statut, nb_likes, nb_partages, utilisateur_id, lieu_id, date) VALUES
-- CULTURE
('La Médina de Fès, classée au patrimoine mondial de l''UNESCO, n''a pas son pareil pour se perdre dans ses 9000 ruelles ! Une expérience hors du temps.', 'https://loremflickr.com/640/480/morocco,medina,culture?lock=301', 'Fès', 'culture', 'APPROUVE', 42, 6, 1, 1, now() - interval '2 days'),
('Visite de la Medersa Bou Inania aujourd''hui : les zelliges et le plafond en cèdre sculpté sont impressionnants. Un chef-d''œuvre de l''architecture mérinide.', 'https://loremflickr.com/640/480/morocco,medersa,architecture?lock=302', 'Fès', 'culture', 'APPROUVE', 18, 2, 2, 4, now() - interval '5 days'),
('Coucher de soleil sur la médina depuis Borj Nord, vue à 360° sur Fès. Un moment magique à ne pas manquer !', 'https://loremflickr.com/640/480/morocco,sunset,medina?lock=303', 'Fès', 'culture', 'APPROUVE', 35, 5, 3, 9, now() - interval '9 days'),
('Le Mellah de Fès, ancien quartier juif, regorge d''histoire et de magnifiques balcons en bois sculpté. Une autre facette de la ville.', 'https://loremflickr.com/640/480/morocco,mellah,heritage?lock=304', 'Fès', 'culture', 'APPROUVE', 9, 1, 4, 12, now() - interval '14 days'),
('Petite pause devant le Palais Royal de Fès : les portes en bronze doré sont absolument magnifiques, impossible de ne pas s''arrêter pour une photo.', 'https://loremflickr.com/640/480/morocco,palace,gate?lock=305', 'Fès', 'culture', 'APPROUVE', 27, 3, 1, 11, now() - interval '20 days'),

-- NATURE
('Journée ressourcement au bord du Lac Dayet Aoua à Ifrane. L''air frais du Moyen Atlas fait vraiment du bien après l''agitation de la médina !', 'https://loremflickr.com/640/480/morocco,lake,nature?lock=306', 'Ifrane', 'nature', 'APPROUVE', 51, 8, 2, 18, now() - interval '3 days'),
('Promenade dans la forêt de cèdres d''Azrou, à la rencontre des célèbres singes magots. Un vrai bol d''air et des rencontres inoubliables.', 'https://loremflickr.com/640/480/morocco,forest,monkeys?lock=307', 'Azrou', 'nature', 'APPROUVE', 38, 5, 3, 19, now() - interval '7 days'),
('Le jardin Jnan Sbil en plein cœur de Fès est un petit havre de paix avec ses bassins et ses allées ombragées. Parfait pour une pause entre deux visites.', 'https://loremflickr.com/640/480/morocco,garden,park?lock=308', 'Fès', 'nature', 'APPROUVE', 14, 2, 4, 10, now() - interval '12 days'),
('Randonnée dans le Moyen Atlas ce week-end : paysages à couper le souffle entre forêts de cèdres et sommets enneigés au loin.', 'https://loremflickr.com/640/480/morocco,atlas,mountains?lock=309', 'Meknès', 'nature', 'APPROUVE', 29, 4, 1, 33, now() - interval '18 days'),
('Ifrane mérite vraiment son surnom de "Suisse marocaine", avec ses chalets et son climat frais, on se sent ailleurs qu''au Maroc !', 'https://loremflickr.com/640/480/morocco,ifrane,chalet?lock=310', 'Ifrane', 'nature', 'APPROUVE', 22, 3, 2, NULL, now() - interval '25 days'),

-- FOOD
('Cours de cuisine marocaine aujourd''hui : on a appris à préparer un tagine d''agneau aux pruneaux. Les odeurs d''épices envahissaient toute la pièce !', 'https://loremflickr.com/640/480/morocco,cooking,tagine?lock=311', 'Fès', 'food', 'APPROUVE', 60, 9, 3, 29, now() - interval '1 days'),
('Déjeuner sur la terrasse du Café Clock avec vue sur la médina, leur fameux camel burger est à essayer absolument.', 'https://loremflickr.com/640/480/morocco,cafe,burger?lock=312', 'Fès', 'food', 'APPROUVE', 47, 7, 4, 26, now() - interval '6 days'),
('Dîner traditionnel au Restaurant Dar Hatim : pastilla, couscous et pâtisseries au miel, un festin royal dans un cadre somptueux.', 'https://loremflickr.com/640/480/morocco,pastilla,couscous?lock=313', 'Fès', 'food', 'APPROUVE', 33, 5, 1, 25, now() - interval '11 days'),
('Petit-déjeuner marocain typique : msemen, baghrir et thé à la menthe. Le meilleur moyen de démarrer la journée !', 'https://loremflickr.com/640/480/morocco,breakfast,tea?lock=314', 'Fès', 'food', 'APPROUVE', 11, 1, 2, NULL, now() - interval '16 days'),
('Découverte du Restaurant Collier de la Colombe à Meknès, vue magnifique sur la vallée et cuisine traditionnelle excellente.', 'https://loremflickr.com/640/480/morocco,restaurant,view?lock=315', 'Meknès', 'food', 'APPROUVE', 19, 2, 3, 31, now() - interval '23 days'),

-- ADVENTURE
('Randonnée dans le Moyen Atlas avec une guide locale : sentiers escarpés, panoramas grandioses, journée intense mais incroyable !', 'https://loremflickr.com/640/480/morocco,hiking,atlas?lock=316', 'Meknès', 'adventure', 'APPROUVE', 44, 6, 4, 33, now() - interval '4 days'),
('Balade à dos de mulet dans les environs d''Azrou, une autre façon de découvrir la forêt de cèdres.', 'https://loremflickr.com/640/480/morocco,mule,forest?lock=317', 'Azrou', 'adventure', 'APPROUVE', 25, 3, 1, 19, now() - interval '8 days'),
('Sortie VTT entre Ifrane et Azrou : pistes forestières superbes, on a croisé des troupeaux de moutons en chemin.', 'https://loremflickr.com/640/480/morocco,mountainbike,forest?lock=318', 'Azrou', 'adventure', 'APPROUVE', 8, 1, 2, NULL, now() - interval '13 days'),
('Exploration des ruelles cachées de la médina de Fès, GPS coupé, juste pour le plaisir de se perdre et tout découvrir par hasard.', 'https://loremflickr.com/640/480/morocco,medina,alley?lock=319', 'Fès', 'adventure', 'APPROUVE', 31, 4, 3, 1, now() - interval '19 days'),
('Lever très tôt pour observer le lever du soleil depuis les hauteurs de Fès, le froid du matin valait vraiment le coup.', 'https://loremflickr.com/640/480/morocco,sunrise,fes?lock=320', 'Fès', 'adventure', 'APPROUVE', 16, 2, 4, NULL, now() - interval '27 days'),

-- HISTORY
('Visite des ruines romaines de Volubilis : les mosaïques sont incroyablement bien conservées après presque 2000 ans !', 'https://loremflickr.com/640/480/morocco,volubilis,roman?lock=321', 'Meknès', 'history', 'APPROUVE', 55, 8, 1, 13, now() - interval '10 days'),
('Le Mausolée Moulay Ismaïl à Meknès est d''une beauté saisissante, les zelliges verts et bleus illuminent toute la salle.', 'https://loremflickr.com/640/480/morocco,mausoleum,zellige?lock=322', 'Meknès', 'history', 'APPROUVE', 40, 6, 2, 14, now() - interval '15 days'),
('Bab Mansour, la porte monumentale de Meknès, est un véritable chef-d''œuvre. On comprend pourquoi Meknès était surnommée le Versailles marocain.', 'https://loremflickr.com/640/480/morocco,gate,meknes?lock=323', 'Meknès', 'history', 'APPROUVE', 28, 4, 3, 15, now() - interval '21 days'),
('La mosquée Al-Qarawiyyin abrite l''une des plus anciennes universités du monde, fondée au 9e siècle. Impossible de ne pas être impressionné par tant d''histoire.', 'https://loremflickr.com/640/480/morocco,mosque,history?lock=324', 'Fès', 'history', 'APPROUVE', 12, 1, 4, 7, now() - interval '28 days'),
('Visite du Musée Dar Jamaï à Meknès, ancien palais transformé en musée d''art marocain, magnifique architecture andalouse.', 'https://loremflickr.com/640/480/morocco,museum,art?lock=325', 'Meknès', 'history', 'APPROUVE', 21, 3, 1, 20, now() - interval '33 days'),

-- WELLNESS
('Moment de pur bien-être au spa du Riad Fès après une longue journée de visite. Hammam, gommage, massage... la détente totale.', 'https://loremflickr.com/640/480/morocco,spa,riad?lock=326', 'Fès', 'wellness', 'APPROUVE', 37, 5, 2, 27, now() - interval '2 days'),
('Première expérience de hammam traditionnel marocain : intense mais tellement relaxant après coup, la peau est toute douce !', 'https://loremflickr.com/640/480/morocco,hammam,relax?lock=327', 'Fès', 'wellness', 'APPROUVE', 6, 0, 3, NULL, now() - interval '6 days'),
('Après-midi piscine et spa à l''Hôtel Sahrai, vue imprenable sur la médina depuis la terrasse. Un vrai cocon de tranquillité.', 'https://loremflickr.com/640/480/morocco,pool,spa?lock=328', 'Fès', 'wellness', 'APPROUVE', 48, 7, 4, 28, now() - interval '11 days'),
('Séance de yoga au lever du soleil dans le jardin de notre riad, le calme avant l''effervescence de la médina.', 'https://loremflickr.com/640/480/morocco,yoga,sunrise?lock=329', 'Fès', 'wellness', 'APPROUVE', 13, 1, 1, NULL, now() - interval '17 days'),
('Petit séjour détente à Ifrane, loin du bruit, juste le chant des oiseaux et l''air pur du Moyen Atlas. Exactement ce dont j''avais besoin.', 'https://loremflickr.com/640/480/morocco,relax,nature?lock=330', 'Ifrane', 'wellness', 'APPROUVE', 24, 3, 2, NULL, now() - interval '24 days'),

-- SHOPPING
('Session shopping intense au Souk Attarine : épices, tapis, lanternes... difficile de résister à tout acheter !', 'https://loremflickr.com/640/480/morocco,souk,spices?lock=331', 'Fès', 'shopping', 'APPROUVE', 46, 7, 3, 8, now() - interval '3 days'),
('Visite des tanneries Chouara, l''odeur est forte mais le spectacle des bassins colorés est unique au monde.', 'https://loremflickr.com/640/480/morocco,tannery,leather?lock=332', 'Fès', 'shopping', 'APPROUVE', 32, 5, 4, 3, now() - interval '8 days'),
('Trouvé une magnifique théière en cuivre dans un petit atelier de la médina, négociée avec le sourire comme il se doit.', 'https://loremflickr.com/640/480/morocco,copper,craft?lock=333', 'Fès', 'shopping', 'APPROUVE', 9, 1, 1, NULL, now() - interval '14 days'),
('Balade dans les souks de Meknès autour de la Place El Hedim, ambiance électrique en fin de journée avec les vendeurs ambulants.', 'https://loremflickr.com/640/480/morocco,market,meknes?lock=334', 'Meknès', 'shopping', 'APPROUVE', 20, 3, 2, 16, now() - interval '20 days'),
('Acheté un magnifique tapis berbère fait main, le vendeur nous a raconté toute son histoire pendant le thé à la menthe.', 'https://loremflickr.com/640/480/morocco,carpet,berber?lock=335', 'Fès', 'shopping', 'APPROUVE', 53, 8, 3, NULL, now() - interval '30 days'),

-- FESTIVALS
('Ambiance incroyable ce soir à la Place El Hedim avec les musiciens et conteurs traditionnels, on se sent transportés dans une autre époque.', 'https://loremflickr.com/640/480/morocco,music,storyteller?lock=336', 'Meknès', 'festivals', 'APPROUVE', 41, 6, 4, 16, now() - interval '5 days'),
('Festival des Cerises de Sefrou : musique, défilés et bien sûr des cerises à volonté ! Une tradition locale à découvrir.', 'https://loremflickr.com/640/480/morocco,festival,cherries?lock=337', 'Fès', 'festivals', 'APPROUVE', 17, 2, 1, NULL, now() - interval '10 days'),
('Soirée musique gnaoua improvisée dans la médina, les rythmes et les couleurs des costumes traditionnels étaient magnifiques.', 'https://loremflickr.com/640/480/morocco,gnaoua,music?lock=338', 'Fès', 'festivals', 'APPROUVE', 7, 0, 2, NULL, now() - interval '16 days'),
('Le Festival de Fès des Musiques Sacrées du Monde attire des artistes de tous horizons, une programmation toujours exceptionnelle.', 'https://loremflickr.com/640/480/morocco,festival,music?lock=339', 'Fès', 'festivals', 'APPROUVE', 30, 4, 3, NULL, now() - interval '22 days'),
('Petite fête de quartier improvisée près de Bab Bou Jeloud, tout le monde dansait au son des tambours. Pure spontanéité marocaine !', 'https://loremflickr.com/640/480/morocco,celebration,dance?lock=340', 'Fès', 'festivals', 'APPROUVE', 36, 5, 4, 2, now() - interval '35 days');
