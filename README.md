
# Library System 2025

#  Library Management System — 2025  
A Java-based library management system that supports user registration, admin control, book & CD borrowing, return flow, overdue tracking, fines, and reminder notifications.

---

##  Features  
###  User Features  
- Register & login  
- Search books (by title, author, ISBN)  
- Search CDs (by title, artist)  
- Borrow books & CDs  
- Return borrowed items  
- View active loans  
- View overdue report + fines  
- Pay fines easily  

###  Admin Features  
- Login as admin  
- Add books & CDs  
- Search media  
- Send overdue reminders  
- Unregister users (only if no fines/loans)  

---

##  Search System  
Users can search books **by:**
- Title (partial or full)
- Author
- ISBN  

Users can search CDs **by:**
- Title (partial or full)
- Artist  

Each search result displays:
- Title  
- Author/Artist  
- Availability (Available / Borrowed)

---

##  Borrowing System  
A user can borrow ONLY when:  
- No unpaid fines  
- No overdue items  
- No active loans  

Borrowing is possible through:  
- Search & Borrow Books  
- Search & Borrow CDs  

Each loan records:  
- Borrow date  
- Due date  
- Fine strategy (book OR CD pricing)  

---

##  Overdue & Fines  
Each item has a borrow duration:  

| Media | Days | Fine (per day late) |
|------|------|----------------------|
| Book | 28 days | 10 NIS/day |
| CD   | 7 days  | 20 NIS/day |

The overdue report shows:  
- Total overdue books  
- Total overdue CDs  
- Total fine  
- Breakdown per loan  

---

##  Reminder System  
Admin can send reminders.  
System returns:

- **0** → No users exist  
- **1** → Users exist but none are overdue  
- **2** → Reminders sent successfully  

---

## JSON Persistence  
All data is saved in:

```
src/main/resources/users.json
src/main/resources/books.json
src/main/resources/cds.json
src/main/resources/loans.json
```

Data **persists across runs**, including:  
- Users  
- Books  
- CDs  
- Borrow status  
- Loans  
- Fines  

---

## 🔗 Design Patterns Used  
- **Strategy Pattern** – fine calculation  
- **Observer Pattern** – email reminders  
- **Repository Pattern** – JSON storage  
- **Polymorphism** – Media as base class for Book & CD  

---

## Technologies  
- Java 17  
- Jackson (JSON serialization)  
- JUnit (tests)  
- IntelliJ IDEA  
- CLI-based interface  

---

## How to Run  
Inside the project folder:

```
javac -d out $(find src/main/java -name "*.java")
java -cp out library_system.CLI.Main
```

---

##  Authors  
- **Sana Jabr**
- **Sara abd aldayem**
- Software Engineering — 2025  



