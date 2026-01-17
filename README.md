# 🚀 DigitalWallet: Event-Driven Microservices

A high-scale, distributed financial platform built with **Spring Boot** and **Spring Cloud**. This project demonstrates the transition from a monolithic architecture to a modern **Event-Driven Architecture (EDA)** using **Apache Kafka** for reliable Inter-Process Communication (IPC).

---

## 🏗 System Architecture

The ecosystem consists of specialized microservices designed for high availability and loose coupling:

- **APIGateway** The *Front Door*. Handles JWT-based authentication, request routing, and serves UI templates.

- **DiscoveryService (Eureka)** The *Service Registry*. Enables dynamic IPC by allowing services to locate each other without hardcoded IP addresses.

- **WalletService** The *ledger*. Subscribes to transaction events via Kafka to perform atomic balance updates.

- **MoneyTransactionService** The *orchestrator*. Validates transfer requests and publishes events to Kafka.

- **Apache Kafka** The backbone of **Asynchronous IPC**.

---

## 📡 IPC Patterns & Messaging Models

This project implements a hybrid communication strategy to balance consistency and performance:

| Pattern | Messaging Style | Implementation | Use Case                                 |
|------|---------------|---------------|------------------------------------------|
| Request-Response | Synchronous | REST | User Login, Account Creation             |
| Publish-Subscribe | **Asynchronous** | **Apache Kafka** | User Registration                        |
| Service Discovery | Dynamic | Netflix Eureka | Service Registry Lookup                  |

---

## 🛠 Features

- **Decoupled IPC** Kafka enables the `MoneyTransactionService` to operate even if the `WalletService` is temporarily offline (*Temporal Decoupling*).

- **JWT Security** Stateless authentication verified at the API Gateway to protect internal microservices.

- **Eventual Consistency** Wallet balances are updated as Kafka consumers process the event stream.

---

## 📂 Project Structure

```text
DigitalWallet/
├── APIGateway/               
├── DiscoveryService/         
├── WalletService/           
├── MoneyTransactionService/ 
```
---

## 🚀 Getting Started

### 1. Prerequisites

- Java **17+**
- Maven **3.6+**
- Homebrew/Docker for Kafka

---

### 2. Infrastructure Setup

Start Kafka using homebrew or Docker:

```bash
brew services start kafka
```

# 🚀 Microservices Deployment Guide

This guide outlines the startup sequence for the microservices architecture. Proper sequencing ensures that **Service Discovery** is active before business logic services attempt to register their network locations.

---

## 🏗️ Architecture Summary
The system relies on **Inter-Process Communication (IPC)** facilitated by:
* **Service Discovery:** Eureka acts as the central registry.
* **API Gateway:** Routes external requests to internal microservices.
* **Messaging:** An asynchronous backbone (likely utilizing a **Publish-Subscribe** model) handles decoupled communication between services.

---

## 🛠️ Service Startup Order

To ensure proper registration and connectivity, follow these steps in order:

### Step 1: Discovery Service (Eureka)
Start the registry first so that subsequent services have a location to register their instances.

```bash
cd DiscoveryService
mvn spring-boot:run
```

### Step 2: Core Business Services
Once Eureka is up, start the business logic layer. These services will "check in" with the Discovery Service. The order between these two does not matter.

**Wallet Service**

```bash
cd WalletService
mvn spring-boot:run
```

### Money Transaction Service

```bash
cd MoneyTransactionService
mvn spring-boot:run
```

### Step 3: API Gateway
Finally, start the Gateway. It will use Eureka to locate the business services and begin routing traffic.

```bash
cd APIGateway
mvn spring-boot:run
```

### 🌐 Access the Application
Once all services are running, the application is accessible via the API Gateway:

**Main Entry Point:** http://localhost:8000
