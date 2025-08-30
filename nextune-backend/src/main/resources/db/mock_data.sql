-- MySQL dump 10.13  Distrib 8.0.29, for Win64 (x86_64)
--
-- Host: localhost    Database: nextune
-- ------------------------------------------------------
-- Server version	8.0.29

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Dumping data for table `album_genre`
--

LOCK TABLES `album_genre` WRITE;
/*!40000 ALTER TABLE `album_genre` DISABLE KEYS */;
INSERT INTO `album_genre` VALUES ('al1','g1'),('al1','g2'),('al2','g3');
/*!40000 ALTER TABLE `album_genre` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `album_playlist_collection`
--

LOCK TABLES `album_playlist_collection` WRITE;
/*!40000 ALTER TABLE `album_playlist_collection` DISABLE KEYS */;
INSERT INTO `album_playlist_collection` VALUES (1,'2025-08-11 14:18:15.000000','al1','p1'),(1,'2025-08-11 14:18:15.000000','al2','p2');
/*!40000 ALTER TABLE `album_playlist_collection` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `albums`
--

LOCK TABLES `albums` WRITE;
/*!40000 ALTER TABLE `albums` DISABLE KEYS */;
INSERT INTO `albums` VALUES (1,'2024-01-01',1,NULL,NULL,2,'u2','al1',NULL,'First Album'),(1,'2024-05-10',1,NULL,NULL,3,'u2','al2',NULL,'Second Album');
/*!40000 ALTER TABLE `albums` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `available_country`
--

LOCK TABLES `available_country` WRITE;
/*!40000 ALTER TABLE `available_country` DISABLE KEYS */;
INSERT INTO `available_country` VALUES ('VN','t1'),('US','t2');
/*!40000 ALTER TABLE `available_country` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `comment`
--

LOCK TABLES `comment` WRITE;
/*!40000 ALTER TABLE `comment` DISABLE KEYS */;
INSERT INTO `comment` VALUES ('2025-08-11 14:18:15.000000','Great song!','t1','u3');
/*!40000 ALTER TABLE `comment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `country`
--

LOCK TABLES `country` WRITE;
/*!40000 ALTER TABLE `country` DISABLE KEYS */;
INSERT INTO `country` VALUES ('US','United States'),('VN','Vietnam');
/*!40000 ALTER TABLE `country` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `follow`
--

LOCK TABLES `follow` WRITE;
/*!40000 ALTER TABLE `follow` DISABLE KEYS */;
INSERT INTO `follow` VALUES (1,'u3','u2');
/*!40000 ALTER TABLE `follow` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `genre`
--

LOCK TABLES `genre` WRITE;
/*!40000 ALTER TABLE `genre` DISABLE KEYS */;
INSERT INTO `genre` VALUES ('g1','Pop'),('g2','Rock'),('g3','Jazz');
/*!40000 ALTER TABLE `genre` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `notifications`
--

LOCK TABLES `notifications` WRITE;
/*!40000 ALTER TABLE `notifications` DISABLE KEYS */;
INSERT INTO `notifications` VALUES ('2025-08-11 14:18:15.000000',NULL,'n1','New track released!','u3','TRACK');
/*!40000 ALTER TABLE `notifications` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `playlist`
--

LOCK TABLES `playlist` WRITE;
/*!40000 ALTER TABLE `playlist` DISABLE KEYS */;
INSERT INTO `playlist` VALUES (_binary '',NULL,NULL,NULL,2,'2025-08-11 14:18:15.000000',NULL,NULL,'p1',NULL,'My Playlist','u3'),(_binary '',NULL,NULL,NULL,1,'2025-08-11 14:18:15.000000',NULL,NULL,'p2',NULL,'Rock Hits','u3');
/*!40000 ALTER TABLE `playlist` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `playlist_genre`
--

LOCK TABLES `playlist_genre` WRITE;
/*!40000 ALTER TABLE `playlist_genre` DISABLE KEYS */;
INSERT INTO `playlist_genre` VALUES ('g1','p1'),('g2','p2');
/*!40000 ALTER TABLE `playlist_genre` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `rating`
--

LOCK TABLES `rating` WRITE;
/*!40000 ALTER TABLE `rating` DISABLE KEYS */;
INSERT INTO `rating` VALUES (5,'al1','u3');
/*!40000 ALTER TABLE `rating` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `reports`
--

LOCK TABLES `reports` WRITE;
/*!40000 ALTER TABLE `reports` DISABLE KEYS */;
INSERT INTO `reports` VALUES ('2025-08-11 14:18:15.000000','Inappropriate content',NULL,'r1',NULL,'u3','ACTIVE');
/*!40000 ALTER TABLE `reports` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `roles`
--

LOCK TABLES `roles` WRITE;
/*!40000 ALTER TABLE `roles` DISABLE KEYS */;
INSERT INTO `roles` VALUES ('role_admin','ADMIN'),('role_artist','ARTIST'),('role_user','USER');
/*!40000 ALTER TABLE `roles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `social_medias`
--

LOCK TABLES `social_medias` WRITE;
/*!40000 ALTER TABLE `social_medias` DISABLE KEYS */;
INSERT INTO `social_medias` VALUES ('fb.com/bobartist','sm1','insta.com/bobartist','u2',NULL);
/*!40000 ALTER TABLE `social_medias` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `subcriptions`
--

LOCK TABLES `subcriptions` WRITE;
/*!40000 ALTER TABLE `subcriptions` DISABLE KEYS */;
INSERT INTO `subcriptions` VALUES (30,1,'2025-08-11 14:18:15.000000','sub1','Premium Monthly','9.99');
/*!40000 ALTER TABLE `subcriptions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `track`
--

LOCK TABLES `track` WRITE;
/*!40000 ALTER TABLE `track` DISABLE KEYS */;
INSERT INTO `track` VALUES (210,NULL,1,100,'2025-08-11 14:18:15.000000','al1',NULL,'t1',NULL,NULL,'Song One',NULL),(180,NULL,1,50,'2025-08-11 14:18:15.000000','al1',NULL,'t2',NULL,NULL,'Song Two',NULL),(240,NULL,1,200,'2025-08-11 14:18:15.000000','al2',NULL,'t3',NULL,NULL,'Song Three',NULL);
/*!40000 ALTER TABLE `track` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `track_artist`
--

LOCK TABLES `track_artist` WRITE;
/*!40000 ALTER TABLE `track_artist` DISABLE KEYS */;
INSERT INTO `track_artist` VALUES ('u2','Singer','t1'),('u2','Singer','t2'),('u2','Composer','t3');
/*!40000 ALTER TABLE `track_artist` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `track_collection`
--

LOCK TABLES `track_collection` WRITE;
/*!40000 ALTER TABLE `track_collection` DISABLE KEYS */;
INSERT INTO `track_collection` VALUES (1,'2025-08-11 14:18:15.000000','p1','t1'),(2,'2025-08-11 14:18:15.000000','p1','t2'),(1,'2025-08-11 14:18:15.000000','p2','t2');
/*!40000 ALTER TABLE `track_collection` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `track_genre`
--

LOCK TABLES `track_genre` WRITE;
/*!40000 ALTER TABLE `track_genre` DISABLE KEYS */;
INSERT INTO `track_genre` VALUES ('g1','t1'),('g2','t2'),('g3','t3');
/*!40000 ALTER TABLE `track_genre` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `transactions`
--

LOCK TABLES `transactions` WRITE;
/*!40000 ALTER TABLE `transactions` DISABLE KEYS */;
INSERT INTO `transactions` VALUES (1,9.99,0.99,'2025-08-11 14:18:15.000000','Credit Card','sub1','u3');
/*!40000 ALTER TABLE `transactions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `user_genre`
--

LOCK TABLES `user_genre` WRITE;
/*!40000 ALTER TABLE `user_genre` DISABLE KEYS */;
INSERT INTO `user_genre` VALUES ('g1','u3'),('g2','u3');
/*!40000 ALTER TABLE `user_genre` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `user_listen_history`
--

LOCK TABLES `user_listen_history` WRITE;
/*!40000 ALTER TABLE `user_listen_history` DISABLE KEYS */;
INSERT INTO `user_listen_history` VALUES (1,'2025-08-11 14:18:15.000000',NULL,'t1','u3'),(1,'2025-08-11 14:18:15.000000',NULL,'t2','u3');
/*!40000 ALTER TABLE `user_listen_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (NULL,2,NULL,NULL,NULL,NULL,NULL,'leetuan0342@gmail.com','5a517a3a-7b0a-40a5-8c0d-9486c6f980e8',NULL,'$2a$10$4nbnHGL.qi9w9BoAprN81ehh7j7SSwKxKmdVpcN/l54hYJeLlFzFy','role_user'),(_binary '',1,NULL,'2025-08-11 14:18:15.000000',NULL,NULL,NULL,'alice@example.com','u1','Alice Admin','pass123','role_admin'),(_binary '\0',1,NULL,'2025-08-11 14:18:15.000000',NULL,NULL,NULL,'bob@example.com','u2','Bob Artist','pass123','role_artist'),(_binary '\0',1,NULL,'2025-08-11 14:18:15.000000',NULL,NULL,NULL,'charlie@example.com','u3','Charlie User','pass123','role_user');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-08-11 14:22:56
