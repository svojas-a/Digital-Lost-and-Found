 Digital Lost & Found System — README (Person 1)

⸻

👤 Role: Person 1 — Core Model Implementation

This module implements the foundation (Model layer) of the system using Java OOP principles and a Factory Design Pattern.

All further components (Search, Claim, Admin) will build on top of this module.

⸻

✅ What Has Been Implemented

 1. Core Classes (Model Layer)

✔ Item.java (Superclass)

Represents a general item in the system.

Attributes:
	•	itemId
	•	name
	•	description
	•	dateReported
	•	status
	•	ownerName (optional)
	•	contactInfo (optional)
	•	location

Methods:
	•	updateStatus(String newStatus)
	•	displayItem()
	•	getItemId()
	•	getStatus()
	•	setOwnerName(String ownerName)
	•	setContactInfo(String contactInfo)

⸻

✔ LostItem.java (Subclass)

Extends Item

Additional Attributes:
	•	lostLocation
	•	dateLost

Feature:
	•	Overrides displayItem()

⸻

✔ FoundItem.java (Subclass)

Extends Item

Additional Attributes:
	•	foundLocation
	•	dateFound

Feature:
	•	Overrides displayItem()

⸻

 2. Design Pattern Implemented

✔ Factory Pattern (ItemFactory.java)

Used to create objects without exposing creation logic.

Method:
Item createItem(type, parameters)

Supports:
	•	"lost" → creates LostItem
	•	"found" → creates FoundItem

⸻

🧠 3. OOP Concepts Used
	•	✔ Encapsulation
	•	✔ Inheritance
	•	✔ Abstraction
	•	✔ Method Overriding

⸻

🔁 4. Status Flow (IMPORTANT)

All team members must follow this EXACT flow:
Reported → Searching → Matched → Claimed → Closed
⚠️ Do NOT change these values.


🚀 How to Run the Project

🖥️ Requirements
	•	Java JDK 8 or above
	•	Terminal / Command Prompt
	•	Any OS (Mac / Windows / Linux)

⸻

📁 Project Structure

project/
 ├── Item.java
 ├── LostItem.java
 ├── FoundItem.java
 ├── ItemFactory.java
 ├── Main.java


 ⚙️ Compile

On Mac / Linux:
javac *.java

On Windows (Command Prompt):
javac *.java

▶️ Run
java Main

⚠️ Notes
	•	File names must EXACTLY match class names
	•	Java is case-sensitive (Main, not main)

⸻

📌 What Next Person (Person 2) Will Do

👤 Role: Search & Matching System

Responsibilities:
	•	Implement search functionality
	•	Match lost and found items

⸻

🔧 What You Can Use From Existing Code

✔ From Item:
	•	getItemId()
	•	getStatus()
	•	displayItem()

✔ From ItemFactory:
	•	Use to create test data

⸻

🧠 What You Should Implement

Create:
	•	SearchService.java

Features:
	•	Search items by:
	•	name
	•	location
	•	Match lost & found items

⸻

👤 Role: Person 2 — Implemented the Search and Matching System
## Project Structure
Digital-Lost-and-Found/
├── model/
│   ├── Item.java
│   ├── LostItem.java
│   ├── FoundItem.java
│   ├── ItemFactory.java
│   ├── MatchResult.java
│   └── SearchCriteria.java
├── service/
│   ├── MatchingStrategy.java
│   ├── BasicMatchingStrategy.java
│   ├── SearchService.java
│   └── MatchObserver.java
├── controller/
│   └── SearchController.java
├── view/
│   └── SearchView.java
├── Main.java
└── SearchMain.java

---

## How to Run

**Compile:**
```bash
javac model/*.java service/*.java controller/*.java view/*.java *.java
```

**Run:**
```bash
java Main        # Basic item creation + status update
java SearchMain  # Full search + matching demo
```

---

## Features

- Report lost and found items
- Search by keyword, location, or type
- Auto-match lost vs found items with confidence scoring
- Observer hook for notification system (Person 4 integration)
- Status tracking: `Reported → Matched → Claimed → Closed`

---

## Design Patterns Used

| Pattern | Where |
|--------|-------|
| MVC | Model / Service / Controller / View |
| Strategy | `MatchingStrategy` — swappable matching algorithm |
| Observer | `MatchObserver` — notifies on match found |
| Factory | `ItemFactory` — creates Lost/Found items |

---

👤 Role: Person 3 — Claim + Verification System

## Overview

Implements the Claim and Verification workflow for matched lost & found items with state machine validation and immutable audit trails.

---

## Project Structure

```
model/
  ├── claim/
  │   ├── ClaimStatus.java
  │   ├── VerificationDecision.java
  │   ├── VerificationMethod.java
  │   ├── Verification.java
  │   └── Claim.java
  └── validator/
      ├── ItemStatusValidator.java
      └── ClaimValidator.java
service/
  ├── claim/
  │   ├── ClaimRepository.java
  │   └── ClaimService.java
  ├── impl/
  │   └── InMemoryClaimRepository.java
  ├── observer/
  │   └── ClaimCreationObserver.java
  └── SearchService.java (modified)
controller/
  └── ClaimController.java
view/
  └── ClaimView.java
ClaimMain.java
```

---

## How to Run

**Compile Everything**
```bash
javac -d . \
  model/claim/*.java \
  model/validator/*.java \
  service/claim/*.java \
  service/impl/*.java \
  service/observer/*.java \
  controller/ClaimController.java \
  view/ClaimView.java \
  ClaimMain.java
```

**Run the Complete Demo**
```bash
java ClaimMain
```

---

## Features

- Auto-create claims from matched items via Observer pattern
- State machine with strict claim lifecycle: `MATCHED → CLAIM_REQUESTED → UNDER_VERIFICATION → CLAIM_APPROVED/REJECTED → CLOSED`
- Immutable verification records for audit trail
- Duplicate claim prevention
- Automatic ownership transfer on approval
- Item status reversion on rejection (available for re-matching)
- Full timeline tracking with timestamps

---

## Design Patterns Used

| Pattern | Where |
|---------|-------|
| State Machine | Claim lifecycle with enforced transitions |
| Observer | ClaimCreationObserver — auto-creates claims on match |
| Repository | ClaimRepository — abstraction for persistence |
| Validator | ClaimValidator + ItemStatusValidator — business rules |
| MVC | ClaimController / ClaimView / ClaimService |

---

## Integration Points

| With | Integration |
|------|-----------|
| Person 2 (SearchService) | Listens via MatchObserver; auto-creates claims |
| Person 1 (Item) | Updates status, transfers ownership via setters |
| Person 4 (Future) | Extension point: register observers for notifications |

---

## Key Components

**ClaimService** (9 core methods)
- `createClaimFromMatch()` — auto-create from match detection
- `requestClaim()` — claimant initiates
- `startVerification()` — admin assigns verifier
- `submitVerification()` — verifier decision
- `approveClaim()` — ownership transfer + close
- `rejectClaim()` — revert items + close

**Claim State Machine**
- Validates every transition
- Records all changes with timestamps
- Terminal states: `CLOSED`, `CLAIM_REJECTED`

---

## Notes

- In-memory repository suitable for MVP; replace with database for production
- All verifications are immutable (audit trail integrity)
- Status history maintained for complete traceability


## Team Split

| Person | Module |
|--------|--------|
| 1 | Item model + storage + CRUD |
| 2 | Search + Matching system *(this module)* |
| 3 | Claim + Verification system |
| 4 | Admin + Notifications + Reports |

---

## Notes

- Windows users: run `chcp 65001` before `java SearchMain` for clean output
- Matching threshold: score ≥ 30 (location +50, name +30, description +20)


