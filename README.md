# Logistics Resource Planner (LRP)

A production-ready Full-Stack application designed to optimize logistics assignments using mathematically structured Linear Programming. Built with **Spring Boot** and **React**. 

This application utilizes the **Apache Commons Math Simplex Solver** to solve the Transportation Problem, minimizing Total Base Cost (Fuel × Distance) while adhering to strict capacity limits and perishable environment constraints.

## Tech Stack
* **Backend:** Java 17+, Spring Boot, Spring Data JPA, H2 Database (In-Memory), Apache Commons Math 3.
* **Frontend:** React, Vite, Bootstrap 5, Custom CSS Glassmorphism UI.

## Features
* **Polymorphic Database Entities**: Dynamic generation using JPA `@Inheritance(strategy = InheritanceType.JOINED)` for abstract `Vehicle` and `Cargo` relationships.
* **Big-M Method Implementation**: Incorporates artificial constraints so Perishable cargo is absolutely restricted from being assigned to non-Refrigerated trucks.
* **Dynamic Cost Minimization**: Automatically generates the most efficient routes strictly by calculating dynamic Fuel prices and Distance variables mapped into the Simplex equations.
* **Live Dashboard UI**: Clean Glassmorphism dashboard for rapid real-time system mapping.

## Running the Application

### 1. Backend Server
Navigate to the `backend` directory and run the Spring Boot server (Requires JDK 17). It natively runs on `http://localhost:8080`.
```bash
cd backend
./mvnw spring-boot:run
```

### 2. Frontend UI Dashboard
Navigate to the `frontend` directory and initialize the Vite Dev server. Check the terminal for your local port (Usually `http://localhost:5173`).
```bash
cd frontend
npm install
npm run dev
```

## Structure
- `backend/` - Contains Spring Boot pure OOP domain models, global exceptions, the Simplex mathematical algorithm, controllers, and services.
- `frontend/` - Contains the React Vite SPA with UI components, connection logic, and custom styles.
