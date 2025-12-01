package library_system.Service;

import library_system.Repository.LoanRepository;
import library_system.domain.*;
import library_system.service.OverdueReportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class OverdueReportServiceTest {

    private OverdueReportService reportService;
    private User user;
    private Book book;
    private CD cd;

    @BeforeEach
    void setup() {
        LoanRepository.clear();
        reportService = new OverdueReportService();
        user = new User("testUser", "pass", "testUser@example.com");

        // Book(title, author, isbn)
        book = new Book("Java Basics", "James", "1234");

        // CD(title, artist)
        cd = new CD("Best Hits", "Artist");
    }

    @Test
    void testGenerateOverdueReport() {
        Loan loan1 = new Loan(user, book);
        loan1.setDueDate(LocalDate.now().minusDays(2)); // متأخر يومين

        Loan loan2 = new Loan(user, cd);
        loan2.setDueDate(LocalDate.now().minusDays(3)); // متأخر 3 أيام

        LoanRepository.addLoan(loan1);
        LoanRepository.addLoan(loan2);

        OverdueReport report = reportService.generateReport(user);

        assertEquals(2, report.getOverdueLoans().size());
        assertEquals(1, report.getOverdueBooks());
        assertEquals(1, report.getOverdueCDs());

        int expectedFine =
                book.getFineStrategy().calculateFine(2)
                        + cd.getFineStrategy().calculateFine(3);

        assertEquals(expectedFine, report.getTotalFine());
    }
}
