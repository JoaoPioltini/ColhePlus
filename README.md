# 🌾 Colhe+

Colhe+ is a digital platform designed to optimize agricultural commercialization by enabling the aggregation of small and medium crop demands into viable harvest operations.

The system connects producers and buyers, allowing multiple purchase requests to be grouped into a single lot. Once the total demand reaches a minimum viable volume, the harvest is activated, reducing waste and improving economic efficiency for producers.

---

## 🚀 Key Features

- 🔐 Authentication & Role-Based Access Control (RBAC)  
  Supports Producers, Buyers, and Administrators with different permissions.

- 🌾 Lot Management (Producers)  
  Producers can create and manage crop lots, defining minimum viable volume, pricing, and delivery constraints.

- 🛒 Order Management (Buyers)  
  Buyers can browse available lots and place or adjust orders dynamically.

- 🔁 Smart Aggregation System  
  Orders are automatically summed, activating the lot when the minimum threshold is reached.

- 🚚 Geolocation & Delivery Validation  
  Calculates distance between producer and buyer to validate delivery feasibility.

- 🛠 Administrative Control  
  Admins can manage users and monitor system activity.

---

## ⚙️ Tech Stack

- Backend: Java with Spring Boot  
- Database: PostgreSQL  
- Containerization: Docker  
- Cloud: AWS (or equivalent)  
- CI/CD: GitHub Actions with Maven and automated testing  

---

## 🔁 CI/CD Pipeline

The project implements a Continuous Integration and Continuous Delivery (CI/CD) pipeline to ensure code quality and reliability.  
Every commit triggers automated build and testing processes, while merges enable container generation and deployment preparation.

---

## 🎯 Objective

Colhe+ aims to reduce agricultural waste and increase profitability by making small-scale production economically viable through demand aggregation and smart logistics.

---

## 📌 Status

🚧 In development — academic project focused on software engineering best practices, scalability, and real-world problem solving.
