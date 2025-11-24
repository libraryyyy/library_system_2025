package library_system.CLI;

import library_system.Repository.LoanRepository;
import library_system.domain.Admin;
import library_system.domain.Book;
import library_system.domain.Loan;
import library_system.domain.User;
import library_system.Repository.AdminRepository;
import library_system.Repository.UserRepository;
import library_system.service.*;

import java.util.List;
import java.util.Scanner;

/**
 * Full CLI for Library System (Sprint 1 + Sprint 2)
 * Includes:
 *  - Admin login + admin menu
 *  - User registration/login + user menu
 *  - Borrowing system
 *  - Search system (title + author + ISBN)
 *  - View loans + pay fines
 */
public class Main {

    private static final Scanner scanner = new Scanner(System.in);

    private static final AdminService adminService = new AdminService();
    private static final UserService userService = new UserService();
    private static final BookService bookService = new BookService();
    private static final BorrowService borrowService = new BorrowService();

    public static void main(String[] args) {

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

    // =============================== ADMIN SECTION =============================== //

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

    private static void adminMenu() {
        boolean adminRunning = true;

        while (adminRunning) {
            System.out.println("\n--- Admin Menu ---");
            System.out.println("1. Add Book");
            System.out.println("2. Search Book by Title");
            System.out.println("3. Search Book by Author");
            System.out.println("4. Search Book by ISBN");
            System.out.println("5. Send Overdue Reminders");
            System.out.println("6. Logout");
            System.out.print("Choose: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    handleAddBook();
                    break;
                case "2":
                    searchByTitle();
                    break;
                case "3":
                    searchByAuthor();
                    break;
                case "4":
                    searchByIsbn();
                    break;

                case "5":
                    ReminderService reminder = new ReminderService();
                    reminder.addObserver(new EmailNotifier());

                    reminder.sendOverdueReminders();
                    break;                case "6":
                    adminService.logout();
                    adminRunning = false;
                    System.out.println("Logged out from admin.");
                    break;
                default:
                    System.out.println("Invalid option.");
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

    private static void userMenu() {
        boolean userRunning = true;
        User loggedUser = userService.getLoggedUser();

        while (userRunning) {
            System.out.println("\n--- User Menu ---");
            System.out.println("1. Search Books");
            System.out.println("2. Borrow Book");
            System.out.println("3. View My Loans");
            System.out.println("4. Pay Fine");
            System.out.println("5. Logout");
            System.out.print("Choose: ");

            String option = scanner.nextLine();

            switch (option) {
                case "1":
                    userSearchMenu();
                    break;
                case "2":
                    userBorrowBook(loggedUser);
                    break;
                case "3":
                    viewUserLoans(loggedUser);
                    break;
                case "4":
                    handlePayFine(loggedUser);
                    break;
                case "5":
                    userService.logout();
                    userRunning = false;
                    System.out.println("Logged out.");
                    break;
                default:
                    System.out.println("Invalid option!");
            }
        }
    }


    // =============================== SEARCH SECTION =============================== //

    private static void userSearchMenu() {
        System.out.println("\n--- Search Menu ---");
        System.out.println("1. By Title");
        System.out.println("2. By Author");
        System.out.println("3. By ISBN");
        System.out.print("Choose: ");

        switch (scanner.nextLine()) {
            case "1": searchByTitle(); break;
            case "2": searchByAuthor(); break;
            case "3": searchByIsbn(); break;
            default: System.out.println("Invalid search option.");
        }
    }

    private static void searchByTitle() {
        System.out.print("Enter title: ");
        String title = scanner.nextLine();

        List<Book> books = bookService.searchByTitle(title);
        printBooks(books);
    }

    private static void searchByAuthor() {
        System.out.print("Enter author: ");
        String author = scanner.nextLine();

        List<Book> books = bookService.searchByAuthor(author);
        printBooks(books);
    }

    private static void searchByIsbn() {
        System.out.print("Enter ISBN: ");
        String isbn = scanner.nextLine();

        List<Book> books = bookService.searchByIsbn(isbn);
        printBooks(books);
    }

    private static void printBooks(List<Book> books) {
        if (books.isEmpty()) {
            System.out.println("No books found.");
        } else {
            for (Book b : books) {
                System.out.println("- " + b.getTitle() + " by " + b.getAuthor() +
                        " (ISBN: " + b.getIsbn() + ")" +
                        (b.isBorrowed() ? " [NOT AVAILABLE]" : " [AVAILABLE]"));
            }
        }
    }


    // =============================== BORROW SECTION =============================== //

    private static void userBorrowBook(User user) {
        System.out.print("Enter book title to borrow: ");
        String title = scanner.nextLine();

        boolean success = borrowService.borrow(user, title);

        if (success) {
            System.out.println("Book borrowed successfully!");
        } else {
            System.out.println("❌ Borrow failed. No available copy.");
        }
    }

    private static void viewUserLoans(User user) {
        List<Loan> loans = LoanRepository.getUserLoans(user.getUsername());

        if (loans.isEmpty()) {
            System.out.println("You have no loans.");
            return;
        }

        System.out.println("\n--- Your Loans ---");
        for (Loan loan : loans) {
            System.out.println("- " + loan.getBook().getTitle() +
                    " | Borrowed: " + loan.getBorrowedDate() +
                    " | Due: " + loan.getDueDate() +
                    (loan.isOverdue() ? " ❌ OVERDUE" : " ✔ On time"));
        }
    }

    // =============================== FINE SECTION =============================== //

    private static void handlePayFine(User user) {
        System.out.println("Your current fine: " + user.getFineBalance());

        System.out.print("Enter amount to pay: ");
        double amount = Double.parseDouble(scanner.nextLine());

        boolean success = user.payFine(amount);

        if (success) {
            System.out.println("Payment successful! Remaining fine: " + user.getFineBalance());
        } else {
            System.out.println("❌ Invalid amount or no fine to pay.");
        }

    }

    // =============================== ADD BOOK =============================== //

    private static void handleAddBook() {
        System.out.print("Title: ");
        String title = scanner.nextLine();

        System.out.print("Author: ");
        String author = scanner.nextLine();

        System.out.print("ISBN: ");
        String isbn = scanner.nextLine();

        bookService.addBook(new Book(title, author, isbn));
        System.out.println("Book added successfully.");
    }
}
