package library_system.CLI;

import library_system.Repository.*;
import library_system.domain.*;
import library_system.notification.EmailNotifier;
import library_system.service.*;
import library_system.service.OverdueReportService;

import java.util.List;
import java.util.Scanner;

/**
 * Full CLI for Library System (Sprint 1 → Sprint 5)
 */
public class Main {

    private static final Scanner scanner = new Scanner(System.in);

    private static final AdminService adminService = new AdminService();
    private static final UserService userService = new UserService();
    private static final BookService bookService = new BookService();
    private static final BorrowService borrowService = new BorrowService();
    private static final OverdueReportService overdueReportService = new OverdueReportService();
    private static final ReminderService reminderService = new ReminderService();

    public static void main(String[] args) {

        // Add default email observer
        reminderService.addObserver(new EmailNotifier());

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
                case "1":
                    handleAdminLogin();
                    break;
                case "2":
                    handleUserLogin();
                    break;
                case "3":
                    handleUserRegistration();
                    break;
                case "4":
                    System.out.println("Exiting system...");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option. Try again.");
            }
        }

        scanner.close();
    }

    // =============================== ADMIN LOGIN =============================== //

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
                System.out.println("❌ Wrong credentials! Try again.\n");
            }
        }
    }

    // =============================== ADMIN MENU =============================== //

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
                case "1":
                    addBook();
                    break;

                case "2":
                    addCD();
                    break;

                case "3":
                    searchBooks();
                    break;

                case "4":
                    searchCDs();
                    break;

                case "5":
                    reminderService.sendOverdueReminders();
                    System.out.println("Overdue reminders sent!");
                    break;
                case "6": {
                    System.out.print("Enter username to remove: ");
                    String userToRemove = scanner.nextLine();
                    String result = adminService.unregisterUser(userToRemove);
                    System.out.println(result);
                    break;
                }
                case "7":
                    adminService.logout();
                    adminRunning = false;
                    System.out.println("Logged out from admin.");
                    break;

                default:
                    System.out.println("Invalid option.");
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
        System.out.println("Book added successfully.");
    }

    private static void addCD() {
        System.out.print("CD Title: ");
        String title = scanner.nextLine();

        System.out.print("Artist: ");
        String artist = scanner.nextLine();

        CDRepository.addCD(new CD(title, artist));
        System.out.println("CD added successfully.");
    }

    private static void searchBooks() {
        System.out.print("Enter book title: ");
        String title = scanner.nextLine();

        var results = BookRepository.findByTitle(title);

        if (results.isEmpty()) System.out.println("No books found.");
        else {
            System.out.println("\n--- Books ---");
            for (Book b : results) {
                System.out.println("- " + b.getTitle() + " | " + b.getAuthor());
            }
        }
    }

    private static void searchCDs() {
        System.out.print("Enter CD title: ");
        String title = scanner.nextLine();

        var results = CDRepository.findByTitle(title);

        if (results.isEmpty()) System.out.println("No CDs found.");
        else {
            System.out.println("\n--- CDs ---");
            for (CD cd : results) {
                System.out.println("- " + cd.getTitle() + " | " + cd.getArtist());
            }
        }
    }


    // =============================== USER SECTION =============================== //

    private static void handleUserRegistration() {
        System.out.print("Choose username: ");
        String username = scanner.nextLine();

        System.out.print("Choose password: ");
        String password = scanner.nextLine();

        if (userService.register(username, password)) {
            System.out.println("User registered successfully!");
        } else {
            System.out.println("❌ Username already exists!");
        }
    }

    private static void handleUserLogin() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();

        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        if (userService.login(username, password)) {
            System.out.println("User login successful!");
            userMenu();
        } else {
            System.out.println("❌ Login failed. Wrong username or password.");
        }
    }

    // =============================== USER MENU =============================== //

    private static void userMenu() {
        boolean userRunning = true;
        User logged = userService.getLoggedUser();

        while (userRunning) {
            System.out.println("\n--- User Menu ---");
            System.out.println("1. Search Books");
            System.out.println("2. Search CDs");
            System.out.println("3. Borrow Book");
            System.out.println("4. Borrow CD");
            System.out.println("5. View My Loans");
            System.out.println("6. View Overdue Report");
            System.out.println("7. Pay Fine");
            System.out.println("8. Logout");
            System.out.print("Choose: ");

            String option = scanner.nextLine();

            switch (option) {

                case "1":
                    searchBooks();
                    break;

                case "2":
                    searchCDs();
                    break;

                case "3":
                    borrowBook(logged);
                    break;

                case "4":
                    borrowCD(logged);
                    break;

                case "5":
                    viewUserLoans(logged);
                    break;

                case "6":
                    System.out.println(overdueReportService.generateReport(logged));
                    break;

                case "7":
                    payFine(logged);
                    break;

                case "8":
                    userService.logout();
                    userRunning = false;
                    System.out.println("Logged out.");
                    break;

                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    // =============================== USER ACTIONS =============================== //

    private static void borrowBook(User user) {
        System.out.print("Enter book title: ");
        String title = scanner.nextLine();

        boolean ok = borrowService.borrowBook(user, title);
        System.out.println(ok ? "Book borrowed!" : "Cannot borrow this book.");
    }

    private static void borrowCD(User user) {
        System.out.print("Enter CD title: ");
        String title = scanner.nextLine();

        boolean ok = borrowService.borrowCD(user, title);
        System.out.println(ok ? "CD borrowed!" : "Cannot borrow this CD.");
    }

    private static void viewUserLoans(User user) {
        List<Loan> loans = LoanRepository.getUserLoans(user.getUsername());

        if (loans.isEmpty()) {
            System.out.println("You have no loans.");
            return;
        }

        System.out.println("\n--- Your Loans ---");
        for (Loan l : loans) {
            System.out.println("- " + l.getItem().getTitle() +
                    " | Borrowed: " + l.getBorrowedDate() +
                    " | Due: " + l.getDueDate() +
                    (l.isOverdue() ? " ❌ OVERDUE" : " ✔ On time"));
        }
    }

    private static void payFine(User user) {
        System.out.println("Your fine: " + user.getFineBalance());
        System.out.print("Enter amount to pay: ");

        try {
            double amount = Double.parseDouble(scanner.nextLine());

            if (amount <= 0 || amount > user.getFineBalance()) {
                System.out.println("Invalid amount.");
            } else {
                user.payFine(amount);
                System.out.println("Payment successful. Remaining fine: " + user.getFineBalance());
            }

        } catch (Exception e) {
            System.out.println("Invalid input.");
        }
    }
}
