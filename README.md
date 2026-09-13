# ENVIRO365 INVESTMENTS - Withdrawal Notice System

A full-stack investment management system that automates withdrawal notices, enforces business rules, and provides portfolio management with CSV reporting.

---


## Overview

**Enviro365 Investments** is a system that allows investors to:
- View their investment portfolios and holdings
- Submit withdrawal requests with automatic business rule validation
- Track withdrawal history
- Export statements to CSV

This system eliminates manual errors, improves efficiency, and delivers a better investor experience through automation.

---

## Features

### Backend (Spring Boot 4.1.1)
- RESTful API for investors, portfolios, withdrawals, and reports
- Business rule enforcement (age, balance, 90% limit)
- H2 in-memory database with sample data
- DTO layer for clean data transfer
- Global exception handling
- Input validation with Jakarta Bean Validation
- CSV export with OpenCSV
- Comprehensive unit & integration tests

### Frontend (React + Vite + Tailwind CSS)
-  Portfolio dashboard with real-time data
-  Dynamic investor selector (fetches from backend)
-  Withdrawal form with client-side validation
-  Withdrawal history table with status indicators
-  CSV download button
-  Toast notifications for user feedback
-  Responsive design

---

## Tech Stack

### Backend
| Technology | Version | Purpose |
|------------|---------|---------|
| Java | 17 | Programming language |
| Spring Boot | 4.1.1 | Application framework |
| Spring Data JPA | Latest | Data persistence |
| H2 Database | Latest | In-memory database |
| OpenCSV | 5.8 | CSV export |
| Lombok | Latest | Boilerplate reduction |
| JUnit 5 + Mockito | Latest | Testing |

### Frontend
| Technology | Version | Purpose |
|------------|---------|---------|
| React | 18+ | UI framework |
| Vite | Latest | Build tool |
| React Router | v6 | Navigation |
| Axios | Latest | HTTP client |
| React Hot Toast | Latest | Notifications |
| Tailwind CSS | 3.4 | Styling |
| date-fns | Latest | Date formatting |

---

## Business Rules

The system enforces three critical business rules on every withdrawal request:

| # | Rule | Description |
|---|------|-------------|
| 1 | **Retirement Age Rule** | Retirement withdrawals are only allowed for investors **over 65 years old** |
| 2 | **Balance Rule** | Withdrawal amount **cannot exceed** the portfolio's available balance |
| 3 | **90% Cap Rule** | Withdrawal amount **cannot exceed 90%** of the portfolio's total balance |

**Violations return HTTP 400 with a descriptive error message.**

---

## Setup Instructions

### Prerequisites
- **Java 17+** ([Download](https://adoptium.net/))
- **Node.js 18+** ([Download](https://nodejs.org/))
- **Maven** (bundled via `mvnw` wrapper)
- **VS Code** (recommended) with extensions:
  - Extension Pack for Java
  - Spring Boot Extension Pack

### Backend Setup


# 1. Navigate to backend
cd backend/enviro365-investments

# 2. Build the project
./mvnw clean install

# 3. Run the application
./mvnw spring-boot:run

- The backend starts on http://localhost:8080

---
### Frontend Setup

# 1. Open a NEW terminal and navigate to frontend
cd frontend

# 2. Install dependencies
npm install

# 3. Run the development server
npm run dev

- The frontend starts on http://localhost:5173

---
# API Documentation
## Base URL
- http://localhost:8080/api

## Endpoints
### Investors
| Method | Endpoint | Description |
|------------|---------|---------|
| GET | /investors | Get all investors |
| GET | /investors/{id} | Get investor by ID |

### Portfolios
| Method | Endpoint | Description |
|------------|---------|---------|
| GET | /portfolios/investor/{investorId} | Get all portfolios for an investor |
| GET | /portfolios/{portfolioId} | Get Portfolio by ID |

### Withdrawals
| Method | Endpoint | Description |
|------------|---------|---------|
| POST | /withdrawals/create | Create a withdrawal notice |
| GET | /withdrawals/investor/{investorId} | Get withdrawal history |

### Reports
| Method | Endpoint | Description |
|------------|---------|---------|
| GET | /reports/withdrawals/csv?investorId={id} | Export withdrawal history as CSV |

## Testing
- The project includes comprehensive unit and integration tests.

## Screenshots
Below are screenshots demonstrating the system in action, covering successful and unsuccessful withdrawal scenarios.

<img width="1906" height="1027" alt="Dashboard" src="https://github.com/user-attachments/assets/b6aca423-b916-447d-8179-1195aca71208" />

1. Portfolio Dashboard
What it shows:
- Total portfolios, total balance, and available withdrawal amount (90% cap)
- Individual portfolio cards with type badges (RETIREMENT vs TAXABLE)
- Holdings breakdown per portfolio
- Dynamic investor selector (fetches real data from backend)

<img width="1911" height="1012" alt="withdraw" src="https://github.com/user-attachments/assets/84fe7195-a9ef-48c8-af2b-998a82d4b84b" />

2. Withdrawal Form — Investor Age 71
What it shows:
- Investor dropdown showing Age 71 — a qualifying retirement investor
- Empty portfolio selection prompting the user to choose
- Form is ready for portfolio selection

Why this matters: Mukhethwa Magadani is over 65, so he can access his Retirement Fund.

<img width="1914" height="1076" alt="Selectportofolio" src="https://github.com/user-attachments/assets/4b5e4740-51f7-4894-922d-5eda16b93561" />

3. Withdrawal Form — Selecting a Portfolio
What it shows:
- Portfolio dropdown expanded with available options
- Each option displays portfolio name and current balance
- Investor can only see portfolios belonging to them

<img width="1910" height="1018" alt="displayDetailPort" src="https://github.com/user-attachments/assets/a8631bed-1681-4b9e-861d-e67cb03556a5" />

4. Withdrawal Form — Portfolio Details (Retirement Fund)
What it shows:
- Retirement Fund selected (type: RETIREMENT)
- Current Balance: displayed
- Max Withdrawal (90%): displayed in green
- "Age > 65 required" badge indicating retirement rules apply
- User enters the withdrawal amount

Business rule check: Because Mukhethwa Magadani is 71 (over 65), this withdrawal will be permitted.

<img width="1910" height="1079" alt="SuccessWith" src="https://github.com/user-attachments/assets/c57769d6-5304-41de-bbb9-3f5651e2e091" />

5. Withdrawal History — Successful Submission
What it shows:
- A successful $5,000.00 withdrawal from the Retirement Fund
- Status: PENDING (yellow badge)
- Remaining Balance: updated to reflect the deduction
- Date and time of submission
- Withdrawal appears in the history table

Validation: All business rules passed (age > 65, amount ≤ balance, amount ≤ 90%).

<img width="1905" height="1022" alt="failSuccess" src="https://github.com/user-attachments/assets/b59374f1-fbf0-4bb1-80e5-693e1715c612" />

6. Withdrawal History — Failed Submission (Amount Exceeded)
What it shows:
- An unsuccessful withdrawal attempt where the amount exceeded the 90% cap
- Error toast notification appears at top: "Withdrawal amount cannot exceed 90% of balance"
- No new withdrawal record was created (business rule blocked it)
- Portfolio balance remains unchanged — the transaction was rejected before any deduction

Validation: Business Rule 3 (90% cap) successfully prevented an invalid withdrawal. The backend returned HTTP 400 with a descriptive error, and the frontend displayed a toast notification.

## AI Usage
This project was developed with the assistance of AI tools as part of the assessment requirement. AI was used for:

### Areas of AI Assistance
- Code structure & best practices — Guidance on Spring Boot package layout, DTO patterns, and layered architecture
- Exception handling patterns — Global exception handler design with @RestControllerAdvice
- Testing strategies — Unit test structure with Mockito, integration tests with MockMvc
