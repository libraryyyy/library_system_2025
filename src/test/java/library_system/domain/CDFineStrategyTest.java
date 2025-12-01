package library_system.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CDFineStrategyTest {

    @Test
    void testCDFinePerDay() {
        CDFineStrategy strategy = new CDFineStrategy();
        assertEquals(20, strategy.calculateFine(1));
        assertEquals(60, strategy.calculateFine(3));
        assertEquals(0, strategy.calculateFine(0));
    }

    @Test
    void testFineNotNegative() {
        CDFineStrategy strategy = new CDFineStrategy();
        assertEquals(0, strategy.calculateFine(-2));
    }
}
