package library_system.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BookFineStrategyTest {

    @Test
    void testBookFinePerDay() {
        BookFineStrategy strategy = new BookFineStrategy();
        assertEquals(10, strategy.calculateFine(1));
        assertEquals(30, strategy.calculateFine(3));
        assertEquals(0, strategy.calculateFine(0));
    }

    @Test
    void testFineNotNegative() {
        BookFineStrategy strategy = new BookFineStrategy();
        assertEquals(0, strategy.calculateFine(-5));
    }
}
