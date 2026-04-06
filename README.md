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


