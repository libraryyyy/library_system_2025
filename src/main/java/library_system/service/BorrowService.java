package library_system.service;

import library_system.Repository.BookRepository;
import library_system.Repository.CDRepository;
import library_system.Repository.LoanRepository;
import library_system.domain.*;

import java.util.List;

/**
 * Service to handle borrowing of Books and CDs using Media polymorphism.
 */
public class BorrowService {

    public boolean borrowBook(User user, String title) {

        List<Book> books = BookRepository.findByTitle(title);
        Book selected = null;

        for (Book b : books) {
            if (!b.isBorrowed()) {
                selected = b;
                break;
            }
        }

        if (selected == null) return false;

        Loan loan = new Loan(user, selected);
        LoanRepository.addLoan(loan);
        selected.setBorrowed(true);
        return true;
    }

    public boolean borrowCD(User user, String title) {

        List<CD> cds = CDRepository.findByTitle(title);
        CD selected = null;

        for (CD cd : cds) {
            if (!cd.isBorrowed()) {
                selected = cd;
                break;
            }
        }

        if (selected == null) return false;

        Loan loan = new Loan(user, selected);
        LoanRepository.addLoan(loan);
        selected.setBorrowed(true);
        return true;
    }
}
