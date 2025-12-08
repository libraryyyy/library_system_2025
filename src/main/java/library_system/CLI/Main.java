package library_system.CLI;

import library_system.Repository.*;
import library_system.domain.*;
import library_system.notification.EmailNotifier;
import library_system.service.*;
import java.util.List;
import java.util.Scanner;

/**
 * Console-based CLI for the Library Management System.
 */
public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final AdminService adminService = new AdminService();
    private static final UserService userService = new UserService();
    private static final BookService bookService = new BookService();
    private static final CDService cdService = new CDService();
    private static final BorrowService borrowService = new BorrowService();
    private static final OverdueReportService overdueReportService = new OverdueReportService();
    private static final ReminderService reminderService = new ReminderService();

    /**
     * Application entry point.
     *
     * @param args CLI args (not used)
     */
    public static void main(String[] args) {

        // Load repositories (they perform auto-repair if necessary)
        UserRepository.loadFromFile();
        BookRepository.loadFromFile();
        CDRepository.loadFromFile();
        LoanRepository.loadFromFile();

        try {
            reminderService.addObserver(new EmailNotifier(
                    "saraabdaldayem1969@gmail.com",
                    "oylkqfgwngrcunf"   // optional app password
            ));
            System.out.println("Email notifier configured.");
        } catch (Exception e) {
            System.out.println("Real email notifier unavailable; using console notifier.");
            reminderService.addObserver((user, message) -> {
                System.out.println("--- Notification (console) ---");
                System.out.println("To: " + user.getEmail());
                System.out.println("Message: " + message);
                System.out.println("------------------------------");
            });
        }

        System.out.println("=== Library Management System ===");

        boolean running = true;
        while (running) {
            System.out.println("\n--- Main Menu ---");
            System.out.println("1. Admin Login");
            System.out.println("2. User Login");
            System.out.println("3. User Registration");
            System.out.println("4. Exit");
            System.out.print("Choose: ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1": handleAdminLogin(); break;
                case "2": handleUserLogin(); break;
                case "3": handleUserRegistration(); break;
                case "4": running = false; break;
                default: System.out.println("Invalid option.");
            }
        }

        // Persist repositories on exit
        UserRepository.saveToFile();
        BookRepository.saveToFile();
        CDRepository.saveToFile();
        LoanRepository.saveToFile();
        scanner.close();
    }

    private static void handleAdminLogin() {
        Admin admin = AdminRepository.getAdmin();
        boolean success = false;

        while (!success) {
            System.out.print("Admin username: ");
            String username = scanner.nextLine();
            System.out.print("Admin password: ");
            String password = scanner.nextLine();

            if (adminService.login(admin, username, password)) {
                System.out.println("Admin login successful!");
                success = true;
                adminMenu();
            } else {
                System.out.println("Wrong credentials!");
                return; // return to main after failure to avoid infinite loop
            }
        }
    }

    private static void adminMenu() {
        boolean adminRunning = true;

        while (adminRunning) {
            System.out.println("\n--- Admin Menu ---");
            System.out.println("1. Add Book");
            System.out.println("2. Add CD");
            System.out.println("3. Search Books");
            System.out.println("4. Search CDs");
            System.out.println("5. Send Overdue Reminders");
            System.out.println("6. Unregister User");
            System.out.println("7. Logout");
            System.out.println("8. Edit Book Quantity");
            System.out.println("9. Edit CD Quantity");
            System.out.print("Choose: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1": addBook(); break;
                case "2": addCD(); break;
                case "3": searchBooks(false); break; // admin read-only
                case "4": searchCDs(false); break; // admin read-only
                case "5":
                    int status = reminderService.sendOverdueReminders();
                    if (status == 2) System.out.println("Reminders sent!");
                    break;
                case "6":
                    System.out.print("Enter username: ");
                    String userToRemove = scanner.nextLine();
                    System.out.println(adminService.unregisterUser(userToRemove));
                    break;
                case "7": adminRunning = false; break;
                case "8": editBookQuantity(); break;
                case "9": editCDQuantity(); break;
                default: System.out.println("Invalid option.");
            }
        }
    }

    private static void addBook() {
        System.out.print("Title: ");
        String title = scanner.nextLine().trim();
        System.out.print("Author: ");
        String author = scanner.nextLine().trim();
        System.out.print("ISBN: ");
        String isbn = scanner.nextLine().trim();
        int qty;
        System.out.print("Quantity (default 1): ");
        String q = scanner.nextLine().trim();
        if (q.isEmpty()) {
            qty = 1;
        } else {
            try { qty = Math.max(0, Integer.parseInt(q)); } catch (NumberFormatException e) { System.out.println("Invalid quantity, using 1."); qty = 1; }
        }
        Book b = new Book(title, author, isbn);
        b.setQuantity(qty);
        BookRepository.addBook(b);
        System.out.println("Book added.");
    }

    private static void addCD() {
        System.out.print("CD Title: ");
        String title = scanner.nextLine().trim();
        System.out.print("Artist: ");
        String artist = scanner.nextLine().trim();
        int qty;
        System.out.print("Quantity (default 1): ");
        String q = scanner.nextLine().trim();
        if (q.isEmpty()) {
            qty = 1;
        } else {
            try { qty = Math.max(0, Integer.parseInt(q)); } catch (NumberFormatException e) { System.out.println("Invalid quantity, using 1."); qty = 1; }
        }
        CD c = new CD(title, artist);
        c.setQuantity(qty);
        CDRepository.addCD(c);
        System.out.println("CD added.");
    }

    private static void editBookQuantity() {
        System.out.print("Enter part of book title to find: ");
        String q = scanner.nextLine();
        List<Book> results = bookService.searchByTitle(q);
        if (results.isEmpty()) { System.out.println("No matching items found."); return; }
        System.out.println("Found:");
        for (int i=0;i<results.size();i++) {
            Book b = results.get(i);
            System.out.println((i+1)+". " + b.getTitle() + " | " + b.getAuthor() + " | Qty: " + b.getQuantity());
        }
        System.out.print("Select number to edit or press Enter to cancel: ");
        String sel = scanner.nextLine();
        if (sel.isBlank()) return;
        try {
            int idx = Integer.parseInt(sel)-1;
            if (idx<0 || idx>=results.size()) { System.out.println("Invalid selection."); return; }
            Book chosen = results.get(idx);
            System.out.print("Enter new quantity: ");
            String nq = scanner.nextLine();
            int newQ = Integer.parseInt(nq);
            if (newQ<0) { System.out.println("Quantity must be >= 0."); return; }
            chosen.setQuantity(newQ);
            BookRepository.saveToFile();
            System.out.println("Quantity updated.");
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
        }
    }

    private static void editCDQuantity() {
        System.out.print("Enter part of CD title to find: ");
        String q = scanner.nextLine();
        List<CD> results = cdService.search(q);
        if (results.isEmpty()) { System.out.println("No matching items found."); return; }
        System.out.println("Found:");
        for (int i=0;i<results.size();i++) {
            CD c = results.get(i);
            System.out.println((i+1)+". " + c.getTitle() + " | " + c.getArtist() + " | Qty: " + c.getQuantity());
        }
        System.out.print("Select number to edit or press Enter to cancel: ");
        String sel = scanner.nextLine();
        if (sel.isBlank()) return;
        try {
            int idx = Integer.parseInt(sel)-1;
            if (idx<0 || idx>=results.size()) { System.out.println("Invalid selection."); return; }
            CD chosen = results.get(idx);
            System.out.print("Enter new quantity: ");
            String nq = scanner.nextLine();
            int newQ = Integer.parseInt(nq);
            if (newQ<0) { System.out.println("Quantity must be >= 0."); return; }
            chosen.setQuantity(newQ);
            CDRepository.saveToFile();
            System.out.println("Quantity updated.");
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
        }
    }

    /* -------------------- USER SECTION -------------------- */

    private static void handleUserRegistration() {
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();

        if (userService.register(username, password, email))
            System.out.println("User registered!");
        else
            System.out.println("Registration failed.");
    }

    private static void handleUserLogin() {
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        if (userService.login(username, password)) {
            System.out.println("Login successful!");
            userMenu();
        } else {
            System.out.println("Wrong credentials.");
        }
    }

    private static void userMenu() {
        User logged = userService.getLoggedUser();
        if (logged == null) { System.out.println("No user logged in."); return; }
        boolean running = true;

        while (running) {
            System.out.println("\n--- User Menu ---");
            System.out.println("1. Search & Borrow Books");
            System.out.println("2. Search & Borrow CDs");
            System.out.println("3. Borrow Book");
            System.out.println("4. Borrow CD");
            System.out.println("5. Return Item");
            System.out.println("6. View My Loans");
            System.out.println("7. View Overdue Report");
            System.out.println("8. Pay Fine");
            System.out.println("9. Logout");
            System.out.print("Choose: ");

            switch (scanner.nextLine()) {
                case "1": searchBooks(true); break; // allow borrow
                case "2": searchCDs(true); break; // allow borrow
                case "3": borrowBookFlow(); break;
                case "4": borrowCDFlow(); break;
                case "5": returnItemFlow(logged); break;
                case "6": viewUserLoans(logged); break;
                case "7": System.out.println(overdueReportService.generateReport(logged)); break;
                case "8": payFine(logged); break;
                case "9": running = false; break;
                default: System.out.println("Invalid option.");
            }
        }
    }

    private static void borrowBookFlow() {
        User logged = userService.getLoggedUser();
        if (logged == null) { System.out.println("Please login first."); return; }
        System.out.println("Borrow Book by: 1) Title 2) Author 3) ISBN");
        String opt = scanner.nextLine();
        List<Book> results;
        switch (opt) {
            case "1": System.out.print("Enter title or part: "); results = bookService.searchByTitle(scanner.nextLine()); break;
            case "2": System.out.print("Enter author or part: "); results = bookService.searchByAuthor(scanner.nextLine()); break;
            case "3": System.out.print("Enter ISBN or part: "); results = bookService.searchByIsbn(scanner.nextLine()); break;
            default: System.out.println("Invalid option."); return;
        }
        if (results.isEmpty()) { System.out.println("No matching items found."); return; }
        System.out.println("Matches:");
        for (int i=0;i<results.size();i++) {
            Book b = results.get(i);
            String avail = b.getQuantity()>0?"Available":"Not Available";
            System.out.println((i+1)+". " + b.getTitle()+" | " + b.getAuthor()+" | Qty: " + b.getQuantity() + " | " + avail);
        }
        System.out.print("Select number to borrow or press Enter to cancel: ");
        String sel = scanner.nextLine(); if (sel.isBlank()) return;
        try {
            int idx = Integer.parseInt(sel)-1; if (idx<0||idx>=results.size()){ System.out.println("Invalid selection."); return; }
            boolean ok = borrowService.borrowBookInstance(logged, results.get(idx));
            System.out.println(ok?"Book borrowed successfully.":"This item is out of stock.");
        } catch (NumberFormatException e) { System.out.println("Invalid input."); }
    }

    private static void borrowCDFlow() {
        User logged = userService.getLoggedUser();
        if (logged == null) { System.out.println("Please login first."); return; }
        System.out.println("Borrow CD by: 1) Title 2) Artist");
        String opt = scanner.nextLine();
        List<CD> results;
        switch (opt) {
            case "1": System.out.print("Enter title or part: "); results = cdService.search(scanner.nextLine()); break;
            case "2": System.out.print("Enter artist or part: "); results = cdService.search(scanner.nextLine()); break;
            default: System.out.println("Invalid option."); return;
        }
        if (results.isEmpty()) { System.out.println("No matching items found."); return; }
        System.out.println("Matches:");
        for (int i=0;i<results.size();i++) {
            CD c = results.get(i);
            String avail = c.getQuantity() > 0 ? "Available" : "Not Available";
            System.out.println((i+1)+". " + c.getTitle()+" | " + c.getArtist()+" | Qty: " + c.getQuantity() + " | " + avail);
        }
        System.out.print("Select number to borrow or press Enter to cancel: ");
        String sel = scanner.nextLine(); if (sel.isBlank()) return;
        try {
            int idx = Integer.parseInt(sel)-1; if (idx<0||idx>=results.size()){ System.out.println("Invalid selection."); return; }
            boolean ok = borrowService.borrowCDInstance(logged, results.get(idx));
            System.out.println(ok?"CD borrowed successfully.":"This item is out of stock.");
        } catch (NumberFormatException e) { System.out.println("Invalid input."); }
    }

    /**
     * Flow to return an item: user chooses from their active loans, then the
     * selected loan is marked returned and repositories updated.
     *
     * @param user logged-in user
     */
    private static void returnItemFlow(User user) {
        List<Loan> loans = LoanRepository.getUserLoans(user.getUsername());
        List<Loan> active = new java.util.ArrayList<>();
        for (Loan l : loans) if (!l.isReturned()) active.add(l);

        if (active.isEmpty()) {
            System.out.println("You have no active loans to return.");
            return;
        }

        System.out.println("\n--- Select Loan to Return ---");
        for (int i = 0; i < active.size(); i++) {
            Loan l = active.get(i);
            System.out.println((i+1) + ". " + l.getItem().getTitle() + " | Due: " + l.getDueDate());
        }
        System.out.print("Select number to return or press Enter to cancel: ");
        String s = scanner.nextLine();
        if (s.isBlank()) return;
        try {
            int idx = Integer.parseInt(s)-1;
            if (idx < 0 || idx >= active.size()) { System.out.println("Invalid selection."); return; }
            Loan chosen = active.get(idx);
            boolean ok = borrowService.returnItem(user, chosen.getItem());
            System.out.println(ok ? "Item returned." : "Failed to return item.");
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
        }
    }

    private static void viewUserLoans(User user) {
        List<Loan> loans = LoanRepository.getUserLoans(user.getUsername());
        if (loans.isEmpty()) {
            System.out.println("No loans.");
            return;
        }

        System.out.println("\n--- Your Loans ---");
        for (Loan l : loans) {
            System.out.println("- " + l.getItem().getTitle()
                    + " | Borrowed: " + l.getBorrowedDate()
                    + " | Due: " + l.getDueDate()
                    + (l.isOverdue() ? " | OVERDUE" : ""));
        }
    }

    private static void payFine(User user) {
        System.out.println("Fine: " + user.getFineBalance());
        System.out.print("Enter amount: ");

        try {
            double amount = Double.parseDouble(scanner.nextLine());
            if (user.payFine(amount))
                {
                    UserRepository.updateUser(user);
                    System.out.println("Payment done.");
                }
            else
                System.out.println("Invalid amount.");
        } catch (Exception e) {
            System.out.println("Invalid input.");
        }
    }

    // -------------------- BOOK SEARCH --------------------

    private static void searchBooks(boolean allowBorrow) {
        System.out.println("\n--- Search Books ---");
        System.out.println("1. Search by Title");
        System.out.println("2. Search by Author");
        System.out.println("3. Search by ISBN");
        System.out.print("Choose: ");
        String opt = scanner.nextLine();

        List<Book> results;

        switch (opt) {
            case "1":
                System.out.print("Enter part of title: ");
                results = bookService.searchByTitle(scanner.nextLine());
                break;

            case "2":
                System.out.print("Enter part of author name: ");
                results = bookService.searchByAuthor(scanner.nextLine());
                break;

            case "3":
                System.out.print("Enter ISBN: ");
                results = bookService.searchByIsbn(scanner.nextLine());
                break;

            default:
                System.out.println("Invalid option.");
                return;
        }

        if (results.isEmpty()) System.out.println("No books found.");
        else {
            System.out.println("\n--- Books Found ---");
            for (int i = 0; i < results.size(); i++) {
                Book b = results.get(i);
                String avail = b.getQuantity() > 0 ? "Available (Qty: " + b.getQuantity() + ")" : "Not Available (Qty: 0)";
                System.out.println((i+1) + ". " + b.getTitle() + " | " + b.getAuthor() + " | ISBN: " + b.getIsbn() + " | " + avail);
            }

            if (allowBorrow) {
                System.out.print("Select number to borrow or press Enter to return: ");
                String sel = scanner.nextLine();
                if (!sel.isBlank()) {
                    try {
                        int index = Integer.parseInt(sel) - 1;
                        if (index >= 0 && index < results.size()) {
                            User logged = userService.getLoggedUser();
                            if (logged == null) { System.out.println("Please login first."); return; }
                            boolean ok = borrowService.borrowBookInstance(logged, results.get(index));
                            System.out.println(ok ? "Book borrowed!" : "Cannot borrow this book.");
                          } else {
                            System.out.println("Invalid selection.");
                          }
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid input.");
                    }
                }
            } else {
                System.out.println("(Admin view — read only)");
            }
        }
    }

    // -------------------- CD SEARCH --------------------

    private static void searchCDs(boolean allowBorrow) {
        System.out.println("\n--- Search CDs ---");
        System.out.println("1. Search by Title");
        System.out.println("2. Search by Artist");
        System.out.print("Choose: ");
        String opt = scanner.nextLine();

        List<CD> results;

        switch (opt) {
            case "1":
                System.out.print("Enter part of title: ");
                results = cdService.search(scanner.nextLine());
                break;

            case "2":
                System.out.print("Enter part of artist: ");
                results = cdService.search(scanner.nextLine());
                break;

            default:
                System.out.println("Invalid option.");
                return;
        }

        if (results.isEmpty()) System.out.println("No CDs found.");
        else {
            System.out.println("\n--- CDs Found ---");
            for (int i = 0; i < results.size(); i++) {
                CD c = results.get(i);
                String avail = c.getQuantity() > 0 ? "Available" : "Not Available";
                System.out.println((i+1) + ". " + c.getTitle() + " | " + c.getArtist() + " | Qty: " + c.getQuantity() + " | " + avail);
            }

            if (allowBorrow) {
                System.out.print("Select number to borrow or press Enter to return: ");
                String sel = scanner.nextLine();
                if (!sel.isBlank()) {
                    try {
                        int index = Integer.parseInt(sel) - 1;
                        if (index >= 0 && index < results.size()) {
                            User logged = userService.getLoggedUser();
                            if (logged == null) { System.out.println("Please login first."); return; }
                            boolean ok = borrowService.borrowCDInstance(logged, results.get(index));
                            System.out.println(ok ? "CD borrowed!" : "Cannot borrow this CD.");
                        } else {
                            System.out.println("Invalid selection.");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid input.");
                    }
                }
            } else {
                System.out.println("(Admin view — read only)");
            }
        }
    }
}
