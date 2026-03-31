# DigitalWallet

DigitalWallet is a full-stack digital wallet platform built on a Java Spring Boot microservices architecture. It lets users register, link their real bank accounts, verify them, and move money around — all secured behind JWT authentication and wired together through Kafka-based async messaging.

The backend is split across several independent services, each owning its own data and responsibilities. They discover each other through a central Eureka registry and communicate externally through a single API Gateway. The frontend is a React + TypeScript app with a glassmorphism UI, talking exclusively to the gateway.

---

## How It's Put Together

Everything starts with the **Discovery Service** — a Netflix Eureka server running on port `8761`. Every other service registers itself here on boot, which lets the gateway route requests by service name rather than hardcoded addresses.

The **API Gateway** (port `8085`) is the only entry point from the outside. It uses Spring Cloud Gateway with WebFlux, load-balances requests across registered service instances, and handles CORS for the frontend. No service is directly exposed — everything goes through here.

The **Authentication Service** (port `8086`) manages user accounts. It stores credentials in an in-memory H2 database, hashes passwords with BCrypt, and issues RSA-signed JWTs on login. Every other service runs as an OAuth2 resource server that validates those tokens using the public key — no shared secrets, no session state.

The **Wallet Service** (port `8087`) is the core of the system. It maintains each user's wallet — their linked bank accounts, their primary account, and their credit score. When a user registers a bank account, it doesn't get activated immediately. The user has to explicitly request verification, which kicks off the async verification pipeline.

The **Verification Service** (port `8088`) handles that pipeline. The Wallet Service runs a scheduler every 10 seconds that scans for pending verification requests and publishes them as Kafka messages to the `bank-account-verification` topic. The Verification Service consumes those messages, runs its validation logic (currently checking that the email registered with the bank matches the user's account email), and then publishes the result back on `bank-account-verification-reply`. The Wallet Service listens on that reply topic and flips the account status to `VERIFIED` or `FAILED`. The whole thing is non-blocking — the user polls the frontend for status updates while it happens in the background.

The **Payment Service** (port `8089`) handles money movement. It tracks each user's wallet balance, starts new accounts at ₹1000 by default, and exposes endpoints for deposits, withdrawals, and peer-to-peer transfers. It validates that the target account exists before any transfer goes through.

The **Loan Service** is scaffolded but not yet implemented.

---

## Running It

You need JDK 17+, Maven, Node.js 18+, and Kafka running locally on port `9092`.

Start services in this order — Discovery first, Gateway last:

```bash
cd DiscoveryService   && mvn spring-boot:run  # :8761
cd AuthenticationService && mvn spring-boot:run  # :8086
cd WalletService      && mvn spring-boot:run  # :8087
cd VerificationService && mvn spring-boot:run  # :8088
cd PaymentService     && mvn spring-boot:run  # :8089
cd APIGatewayService  && mvn spring-boot:run  # :8085
```

For the frontend:

```bash
cd frontend && npm install && npm run dev  # localhost:5173
```

---
