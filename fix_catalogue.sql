-- Fix lieux photo URLs (Wikimedia CDN returns 403, switch to loremflickr) + catalogue expansion

UPDATE lieux SET photo_url = 'https://loremflickr.com/640/480/morocco,medina,fes?lock=1' WHERE id = 1;
UPDATE lieux SET photo_url = 'https://loremflickr.com/640/480/morocco,gate,blue?lock=2' WHERE id = 2;
UPDATE lieux SET photo_url = 'https://loremflickr.com/640/480/morocco,tannery,leather?lock=3' WHERE id = 3;
UPDATE lieux SET photo_url = 'https://loremflickr.com/640/480/morocco,madrasa,architecture?lock=4' WHERE id = 4;
UPDATE lieux SET photo_url = 'https://loremflickr.com/640/480/morocco,fountain,mosaic?lock=5' WHERE id = 5;
UPDATE lieux SET photo_url = 'https://loremflickr.com/640/480/morocco,museum,wood?lock=6' WHERE id = 6;
UPDATE lieux SET photo_url = 'https://loremflickr.com/640/480/morocco,mosque,islamic?lock=7' WHERE id = 7;
UPDATE lieux SET photo_url = 'https://loremflickr.com/640/480/morocco,souk,market?lock=8' WHERE id = 8;
UPDATE lieux SET photo_url = 'https://loremflickr.com/640/480/morocco,fortress,fes?lock=9' WHERE id = 9;
UPDATE lieux SET photo_url = 'https://loremflickr.com/640/480/morocco,garden,park?lock=10' WHERE id = 10;
UPDATE lieux SET photo_url = 'https://loremflickr.com/640/480/morocco,palace,gate?lock=11' WHERE id = 11;
UPDATE lieux SET photo_url = 'https://loremflickr.com/640/480/morocco,jewish,quarter?lock=12' WHERE id = 12;
UPDATE lieux SET photo_url = 'https://loremflickr.com/640/480/morocco,roman,ruins?lock=13' WHERE id = 13;
UPDATE lieux SET photo_url = 'https://loremflickr.com/640/480/morocco,mausoleum,meknes?lock=14' WHERE id = 14;
UPDATE lieux SET photo_url = 'https://loremflickr.com/640/480/morocco,gate,meknes?lock=15' WHERE id = 15;
UPDATE lieux SET photo_url = 'https://loremflickr.com/640/480/morocco,square,meknes?lock=16' WHERE id = 16;
UPDATE lieux SET photo_url = 'https://loremflickr.com/640/480/morocco,granary,ruins?lock=17' WHERE id = 17;
UPDATE lieux SET photo_url = 'https://loremflickr.com/640/480/morocco,lake,nature?lock=18' WHERE id = 18;
UPDATE lieux SET photo_url = 'https://loremflickr.com/640/480/morocco,cedar,forest?lock=19' WHERE id = 19;
UPDATE lieux SET photo_url = 'https://loremflickr.com/640/480/morocco,museum,palace?lock=20' WHERE id = 20;

-- Remove duplicate legacy rows (no FK references, superseded by ids 1,2,15,16)
DELETE FROM lieux WHERE id IN (21,22,23,24);

-- Catalogue expansion: restaurants, hotels, activities (Fes & Meknes only)
INSERT INTO lieux (nom, categorie, ville, latitude, longitude, storytelling_fr, storytelling_ar, storytelling_en, prix_entree, horaires, contact, photo_url, note_moyenne, nombre_avis) VALUES
('Restaurant Dar Hatim','RESTAURANT','Fès',34.0625,-4.9756,
 'Restaurant traditionnel niché dans un riad du XVIIe siècle, Dar Hatim propose une cuisine marocaine raffinée servie dans un cadre authentique avec spectacle de musique andalouse.',
 'مطعم تقليدي يقع في رياض من القرن السابع عشر، يقدم دار حاتم مأكولات مغربية راقية في إطار أصيل مع عرض موسيقي أندلسي.',
 'A traditional restaurant set in a 17th-century riad, Dar Hatim serves refined Moroccan cuisine in an authentic setting with live Andalusian music.',
 '200-400 MAD','12h-23h','0535-634918','https://loremflickr.com/640/480/morocco,restaurant,food?lock=21',4.3,28),

('Café Clock Fès','RESTAURANT','Fès',34.0633,-4.9745,
 'Institution culturelle et culinaire au cœur de la médina, le Café Clock mêle plats marocains modernisés et ambiance artistique animée par des concerts et ateliers.',
 'مؤسسة ثقافية وغذائية في قلب المدينة، يمزج كافيه كلوك بين الأطباق المغربية العصرية وأجواء فنية مع حفلات وورشات.',
 'A cultural and culinary institution in the heart of the medina, Café Clock blends modernized Moroccan dishes with a lively artistic atmosphere of concerts and workshops.',
 '80-150 MAD','9h-22h','0535-637855','https://loremflickr.com/640/480/morocco,cafe,food?lock=22',4.5,64),

('Riad Fès','HOTEL','Fès',34.0640,-4.9770,
 'Ce riad de luxe allie architecture traditionnelle et confort moderne, avec patios fleuris, fontaines et spa au cœur de la médina.',
 'يمزج هذا الرياض الفاخر بين العمارة التقليدية والراحة العصرية، مع باحات مزهرة ونوافير ومنتجع صحي في قلب المدينة.',
 'This luxury riad blends traditional architecture with modern comfort, featuring flower-filled patios, fountains and a spa in the heart of the medina.',
 '1200-2500 MAD/nuit','Réception 24h/24','0535-947210','https://loremflickr.com/640/480/morocco,riad,courtyard?lock=23',4.7,112),

('Hôtel Sahrai','HOTEL','Fès',34.0580,-4.9900,
 'Hôtel design surplombant la médina de Fès, le Sahrai offre piscine, spa et vue panoramique exceptionnelle sur la ville impériale.',
 'فندق عصري يطل على مدينة فاس العتيقة، يقدم ساحراي مسبحاً ومنتجعاً صحياً وإطلالة بانورامية استثنائية على المدينة الإمبراطورية.',
 'A design hotel overlooking the Fes medina, the Sahrai offers a pool, spa and an exceptional panoramic view of the imperial city.',
 '1800-4000 MAD/nuit','Réception 24h/24','0535-940332','https://loremflickr.com/640/480/morocco,hotel,pool?lock=24',4.6,87),

('Cours de Cuisine Marocaine','ACTIVITE','Fès',34.0630,-4.9760,
 'Apprenez à préparer un tajine et des pâtisseries marocaines avec un chef local, visite du marché aux épices incluse.',
 'تعلم تحضير الطاجين والحلويات المغربية مع طاهٍ محلي، وتشمل الجولة زيارة سوق التوابل.',
 'Learn to prepare a tagine and Moroccan pastries with a local chef, including a visit to the spice market.',
 '350 MAD/personne','10h-14h','0661-223344','https://loremflickr.com/640/480/morocco,cooking,spices?lock=25',4.8,45),

('Balade en Calèche - Médina','ACTIVITE','Fès',34.0620,-4.9770,
 'Découvrez les remparts et les portes monumentales de Fès à bord d''une calèche traditionnelle, un moyen original de profiter du panorama.',
 'اكتشف أسوار وبوابات فاس الأثرية على متن عربة تقليدية تجرها الخيول، وسيلة أصلية للاستمتاع بالمناظر الخلابة.',
 'Discover the ramparts and monumental gates of Fes aboard a traditional horse-drawn carriage, a charming way to enjoy the views.',
 '150 MAD/calèche','9h-19h',NULL,'https://loremflickr.com/640/480/morocco,carriage,horse?lock=26',4.2,33),

('Restaurant Collier de la Colombe','RESTAURANT','Meknès',33.8960,-5.5530,
 'Perché sur les remparts de Meknès avec une vue imprenable sur la vallée, ce restaurant sert une cuisine marocaine traditionnelle dans un cadre romantique.',
 'يقع هذا المطعم على أسوار مكناس مع إطلالة رائعة على الوادي، ويقدم المأكولات المغربية التقليدية في جو رومانسي.',
 'Perched on the ramparts of Meknes with a stunning valley view, this restaurant serves traditional Moroccan cuisine in a romantic setting.',
 '150-300 MAD','12h-22h','0535-555041','https://loremflickr.com/640/480/morocco,restaurant,terrace?lock=27',4.4,52),

('Hôtel Ibis Meknès','HOTEL','Meknès',33.8930,-5.5470,
 'Hôtel moderne et confortable situé à proximité de la médina de Meknès, idéal pour explorer la ville impériale et Volubilis.',
 'فندق عصري ومريح يقع بالقرب من مدينة مكناس العتيقة، مثالي لاستكشاف المدينة الإمبراطورية ووليلي.',
 'A modern, comfortable hotel located near the Meknes medina, ideal for exploring the imperial city and Volubilis.',
 '450-700 MAD/nuit','Réception 24h/24','0535-401111','https://loremflickr.com/640/480/morocco,hotel,modern?lock=28',4.1,76),

('Randonnée Moyen Atlas','ACTIVITE','Meknès',33.8900,-5.5500,
 'Partez en randonnée guidée dans les paysages verdoyants du Moyen Atlas, entre forêts de cèdres et villages berbères.',
 'انطلق في رحلة مشي برفقة مرشد عبر المناظر الخضراء للأطلس المتوسط، بين غابات الأرز والقرى الأمازيغية.',
 'Set off on a guided hike through the green landscapes of the Middle Atlas, between cedar forests and Berber villages.',
 '300 MAD/personne','8h-16h','0662-778899','https://loremflickr.com/640/480/morocco,atlas,hiking?lock=29',4.6,38);
