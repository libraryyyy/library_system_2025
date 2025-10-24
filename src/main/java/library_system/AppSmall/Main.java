package library_system.AppSmall;

import library_system.domain.Admin;
import library_system.domain.Book;
import library_system.service.AdminService;
import library_system.service.BookService;

import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // إنشاء Admin و Services
        Admin admin = new Admin("admin", "1234");
        AdminService adminService = new AdminService();
        BookService bookService = new BookService();

        System.out.println("=== Library System Demo ===");

        // Login
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        if (adminService.login(admin, username, password)) {
            System.out.println("Login successful!\n");

            // إضافة كتب
            bookService.addBook(new Book("Java Basics", "John Doe", "111"));
            bookService.addBook(new Book("Advanced Java", "Jane Doe", "222"));

            // تجربة البحث
            System.out.print("Search book by title: ");
            String titleSearch = scanner.nextLine();
            List<Book> foundBooks = bookService.searchByTitle(titleSearch);

            if (foundBooks.isEmpty()) {
                System.out.println("No books found with title: " + titleSearch);
            } else {
                System.out.println("Books found:");
                for (Book b : foundBooks) {
                    System.out.println("- " + b.getTitle() + " by " + b.getAuthor() + " (ISBN: " + b.getIsbn() + ")");
                }
            }

        } else {
            System.out.println("Login failed! Wrong credentials.");
        }

        scanner.close();
    }
}
