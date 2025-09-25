# 🎵 [Nextune - Music Application](https://nextune.ddnsgeek.com/)

A **Spotify-inspired music application** with a scalable backend built on **Java Spring Boot**, powered by a **hybrid recommendation system**. Deployed on **AWS EC2** with **Docker** and automated using **GitHub Actions CI/CD**.

---

## 📌 Overview

**Nextune** is an individual project developed to explore end-to-end application development, from **backend architecture design** to **deployment on the cloud**.  
It provides all the essential features of a modern music platform with **personalized recommendations** and a **robust authentication system**.

---

## 🚀 Tech Stack

- **Backend:** Java, Spring Boot (RESTful API)  
- **Database:** MySQL  
- **Machine Learning:** Python (hybrid recommender system)  
- **DevOps:** Docker, GitHub Actions (CI/CD), AWS EC2  
- **Other Tools:** MapStruct (Entity-DTO mapping), Git  

---

## ✨ Features

### 🔐 Authentication & Security
- User authentication with **refresh tokens** for seamless login.
- **OTP-based email service** for account verification and recovery.

### 🎶 Music Platform Essentials
- User and admin functionalities for:
  - Auth
  - Album
  - Follow
  - Genre
  - OTP
  - Playlist
  - Profile
  - Track
- Entity-to-DTO mapping with **MapStruct** for clean API responses.

### 🗣️ AI Voice Assistant
- **Gemini API integration:** Powering natural language understanding.  
- **Voice commands:** Play tracks by name, skip, or control playback.  
- **Seamless interaction:** Hands-free, intuitive control of the application.  

### 🤖 Recommendation System
- **Content-based filtering:** Lyrics, genres, and track metadata.
- **Collaborative filtering:** Based on user listening history.
- Hybrid approach → personalized track recommendations.

### ⚙️ CI/CD & Deployment
- Automated **CI/CD pipelines** using **GitHub Actions**.
- Containerized with **Docker**.
- Deployed on **AWS EC2** for scalability and availability.

---

## 🧪 CI/CD with GitHub Actions

Every push to the `dev` branch triggers:
- Build & test backend with Maven.  
- Build Docker image & push to AWS.  
- Deploy on **AWS EC2** automatically.  

---

## 🧑‍💻 My Role

- Designed and implemented the backend architecture with **Spring Boot**.  
- Built the **hybrid recommendation system** (content-based + collaborative).  
- Integrated **JWT + OTP authentication system**.  
- Setup **CI/CD pipelines** and deployed on **AWS EC2** with Docker.  

---

## 📎 [Demo](https://nextune.ddnsgeek.com/)


