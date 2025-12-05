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

        BookRepository.loadFromFile();

        // الآن يمكننا الوصول إلى الكتب المخزنة
        System.out.println("Loaded books: " + BookRepository.getBooks());
        UserRepository.loadFromFile();
        BookRepository.loadFromFile();
        CDRepository.loadFromFile();
        LoanRepository.loadFromFile();

        try {
            reminderService.addObserver(new EmailNotifier(
                    "saraabdaldayem1969@gmail.com",
                    "oylkqfgwngrcunf"   // ← حط الـ App Password هنا
            ));
            System.out.println("تم تفعيل إرسال الإيميلات الحقيقية بنجاح!");
        } catch (Exception e) {
            System.out.println("فشل تفعيل الإرسال الحقيقي، تم تفعيل وضع الطباعة في الكونسول...");
            reminderService.addObserver((userEmail, subject) -> {
                System.out.println("\nإيميل وهمي (لأن الإرسال فشل):");
                System.out.println("إلى: " + userEmail);
                System.out.println("الموضوع: " + subject);
                System.out.println("-".repeat(50));
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

            String choice = scanner.nextLine();
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
            System.out.print("Choose: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1": addBook(); break;
                case "2": addCD(); break;
                case "3": searchBooks(); break;
                case "4": searchCDs(); break;
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
                default: System.out.println("Invalid option.");
            }
        }
    }

    private static void addBook() {
        System.out.print("Title: ");
        String title = scanner.nextLine();
        System.out.print("Author: ");
        String author = scanner.nextLine();
        System.out.print("ISBN: ");
        String isbn = scanner.nextLine();

        BookRepository.addBook(new Book(title, author, isbn));
        System.out.println("Book added.");
    }

    private static void addCD() {
        System.out.print("CD Title: ");
        String title = scanner.nextLine();
        System.out.print("Artist: ");
        String artist = scanner.nextLine();

        CDRepository.addCD(new CD(title, artist));
        System.out.println("CD added.");
    }

    // -------------------- BOOK SEARCH --------------------

    private static void searchBooks() {
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
                String avail = b.isBorrowed() ? "Borrowed" : "Available";
                System.out.println((i+1) + ". " + b.getTitle() + " | " + b.getAuthor() + " | ISBN: " + b.getIsbn() + " | " + avail);
            }

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
        }
    }

    // -------------------- CD SEARCH --------------------

    private static void searchCDs() {
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
                String avail = c.isBorrowed() ? "Borrowed" : "Available";
                System.out.println((i+1) + ". " + c.getTitle() + " | " + c.getArtist() + " | " + avail);
            }

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
        }
    }

    // -------------------- USER SECTION --------------------

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
        boolean running = true;

        while (running) {
            System.out.println("\n--- User Menu ---");
            System.out.println("1. Search & Borrow Books");
            System.out.println("2. Search & Borrow CDs");
            System.out.println("3. Return Item");
            System.out.println("4. View My Loans");
            System.out.println("5. View Overdue Report");
            System.out.println("6. Pay Fine");
            System.out.println("7. Logout");
            System.out.print("Choose: ");

            switch (scanner.nextLine()) {
                case "1": searchBooks(); break;
                case "2": searchCDs(); break;
                case "3": returnItemFlow(logged); break;
                case "4": viewUserLoans(logged); break;
                case "5": System.out.println(overdueReportService.generateReport(logged)); break;
                case "6": payFine(logged); break;
                case "7": running = false; break;
                default: System.out.println("Invalid option.");
            }
        }
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
}
