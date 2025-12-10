# Library Management System — 2025

A Java-based library management system that supports user registration, admin control,
book & CD borrowing, return flow, overdue tracking, fines, and reminder notifications.

## Features

### User Features
- Register & login
- Search books (by title, author, ISBN)
- Search CDs (by title, artist)
- Borrow books & CDs
- Return borrowed items
- View active loans
- View overdue report & fines
- Pay fines

### Admin Features
- Login as admin
- Add books & CDs
- Search media
- Send overdue reminders
- Unregister users (only if no active loans or unpaid fines)

## Search System
- Books: title, author, ISBN
- CDs: title, artist  
Each search result displays title, author/artist, and availability.

## Borrowing Rules
A user can borrow items only if:
- There are no unpaid fines
- There are no overdue items

Borrowing is done through:
- Search & Borrow Books
- Search & Borrow CDs

Each loan records:
- Borrow date
- Due date
- Media type (Book or CD)
- Fine strategy

## Overdue & Fines

| Media | Borrow Duration | Fine |
|------|----------------|------|
| Book | 28 days | 10 NIS per overdue book |
| CD | 7 days | 20 NIS per overdue CD |

The overdue report shows:
- Total overdue books
- Total overdue CDs
- Total fine
- Detailed breakdown per loan

## Reminder System
Admins can send overdue reminders.
The system returns:
- 0 → No users exist
- 1 → Users exist but none are overdue
- 2 → Reminders sent successfully

## JSON Persistence
Data is stored using JSON files:
- books.json
- cds.json

Runtime-generated files:
- users.json
- loans.json  
(these are created automatically at runtime and excluded from version control)

## Design Patterns Used
- Strategy Pattern – fine calculation
- Observer Pattern – reminder notifications
- Repository Pattern – JSON persistence
- Polymorphism – Media as base class for Book & CD

## Technologies
- Java 21
- Jackson
- Maven
- JUnit
- IntelliJ IDEA
- CLI-based interface

## How to Run
```bash
mvn clean compile
mvn exec:java -Dexec.mainClass="library_system.CLI.Main"

## Authors
- **Sana Zafer Jabr**
- **Sara abd aldayem**
- Software Engineering — 2025  

