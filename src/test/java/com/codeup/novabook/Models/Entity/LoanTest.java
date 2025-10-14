package com.codeup.novabook.Models.Entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for Loan entity and fine calculation
 * Author: Luis Alfredo - Clan Cienaga
 */
@DisplayName("Loan Entity and Fine Calculation Tests")
class LoanTest {

    private Loan loan;
    private Book book;
    private Partner partner;
    
    // Fine calculation constants (business rules)
    private static final double DAILY_FINE_RATE = 1.0; // $1.0 per day
    private static final int LOAN_PERIOD_DAYS = 14; // 14 days standard loan period

    @BeforeEach
    void setUp() {
        // Create a sample book
        book = new Book();
        book.setId(1);
        book.setTitulo("Clean Code");
        book.setAutor("Robert C. Martin");
        book.setIsbn("978-0132350884");
        book.setAnioPublicacion(2008);
        book.setDisponible(false); // Book is loaned
        book.setFechaAlta(LocalDateTime.now());

        // Create a sample partner
        partner = new Partner();
        partner.setId(1);
        partner.setNombre("Luis Alfredo");
        partner.setEmail("luis.alfredo@cienaga.com");
        partner.setTelefono("555-0123");
        partner.setDireccion("Cienaga, Colombia");
        partner.setActivo(true);
        partner.setFechaRegistro(LocalDateTime.now().minusMonths(1));

        // Create a loan
        loan = new Loan();
        loan.setId(1);
        loan.setLibro(book);
        loan.setSocio(partner);
        loan.setFechaPrestamo(LocalDate.now().minusDays(10));
        loan.setFechaDevolucion(null); // Not returned yet
        loan.setDevuelto(false);
    }

    @Test
    @DisplayName("Should create loan with valid data")
    void testLoanCreation() {
        // Given - setUp method provides a valid loan

        // When - loan is created (done in setUp)

        // Then - all fields should be set correctly
        assertThat(loan.getId()).isEqualTo(1);
        assertThat(loan.getLibro()).isEqualTo(book);
        assertThat(loan.getSocio()).isEqualTo(partner);
        assertThat(loan.getFechaPrestamo()).isEqualTo(LocalDate.now().minusDays(10));
        assertThat(loan.getFechaDevolucion()).isNull();
        assertThat(loan.isDevuelto()).isFalse();
    }

    @Test
    @DisplayName("Should calculate no fine for loan returned on time")
    void testNoFineForOnTimeLoan() {
        // Given - a loan returned within the allowed period
        LocalDate loanDate = LocalDate.now().minusDays(7); // 7 days ago
        LocalDate returnDate = LocalDate.now(); // Returned today
        loan.setFechaPrestamo(loanDate);
        loan.setFechaDevolucion(returnDate);
        loan.setDevuelto(true);

        // When - calculating fine
        double fine = calculateFine(loan);

        // Then - no fine should be applied
        assertThat(fine).isEqualTo(0.0);
        assertThat(loan.isDevuelto()).isTrue();
    }

    @ParameterizedTest
    @DisplayName("Should calculate correct fines for overdue loans")
    @CsvSource({
        "15, 1.0",   // 1 day overdue: $1.0
        "16, 2.0",   // 2 days overdue: $2.0
        "20, 6.0",   // 6 days overdue: $6.0
        "28, 14.0"   // 14 days overdue: $14.0
    })
    void testFineCalculationForOverdueLoans(int daysAgo, double expectedFine) {
        // Given - a loan that started X days ago and is still not returned
        LocalDate loanDate = LocalDate.now().minusDays(daysAgo);
        loan.setFechaPrestamo(loanDate);
        loan.setFechaDevolucion(null);
        loan.setDevuelto(false);

        // When - calculating fine for current date
        double fine = calculateFine(loan);

        // Then - fine should match expected amount
        assertThat(fine).isEqualTo(expectedFine);
    }

    @Test
    @DisplayName("Should calculate fine for loan returned late")
    void testFineForLateReturn() {
        // Given - a loan returned 5 days late
        LocalDate loanDate = LocalDate.now().minusDays(20); // 20 days ago
        LocalDate returnDate = LocalDate.now().minusDays(1); // Returned yesterday (19 days total)
        loan.setFechaPrestamo(loanDate);
        loan.setFechaDevolucion(returnDate);
        loan.setDevuelto(true);

        // When - calculating fine
        double fine = calculateFine(loan);

        // Then - fine should be for 5 days overdue (19 - 14 = 5)
        assertThat(fine).isEqualTo(5.0);
    }

    @Test
    @DisplayName("Should handle edge case of loan returned on exact due date")
    void testLoanReturnedOnDueDate() {
        // Given - a loan returned exactly on the due date
        LocalDate loanDate = LocalDate.now().minusDays(LOAN_PERIOD_DAYS); // Exactly 14 days ago
        LocalDate returnDate = LocalDate.now(); // Returned today
        loan.setFechaPrestamo(loanDate);
        loan.setFechaDevolucion(returnDate);
        loan.setDevuelto(true);

        // When - calculating fine
        double fine = calculateFine(loan);

        // Then - no fine should be applied
        assertThat(fine).isEqualTo(0.0);
    }

    @Test
    @DisplayName("Should identify overdue loans correctly")
    void testOverdueLoanIdentification() {
        // Given - a loan that started 20 days ago and is not returned
        LocalDate loanDate = LocalDate.now().minusDays(20);
        loan.setFechaPrestamo(loanDate);
        loan.setFechaDevolucion(null);
        loan.setDevuelto(false);

        // When - checking if loan is overdue
        boolean isOverdue = isLoanOverdue(loan);
        long daysOverdue = getDaysOverdue(loan);

        // Then - loan should be identified as overdue
        assertThat(isOverdue).isTrue();
        assertThat(daysOverdue).isEqualTo(6); // 20 - 14 = 6 days overdue
    }

    @Test
    @DisplayName("Should not mark returned loans as overdue")
    void testReturnedLoanNotOverdue() {
        // Given - a loan that was returned (even if it was late)
        LocalDate loanDate = LocalDate.now().minusDays(20);
        LocalDate returnDate = LocalDate.now().minusDays(2);
        loan.setFechaPrestamo(loanDate);
        loan.setFechaDevolucion(returnDate);
        loan.setDevuelto(true);

        // When - checking if loan is currently overdue
        boolean isCurrentlyOverdue = isLoanCurrentlyOverdue(loan);

        // Then - loan should not be marked as currently overdue since it's returned
        assertThat(isCurrentlyOverdue).isFalse();
        assertThat(loan.isDevuelto()).isTrue();
    }

    @Test
    @DisplayName("Should handle null return date for active loans")
    void testActiveLoanWithNullReturnDate() {
        // Given - an active loan with no return date
        loan.setFechaDevolucion(null);
        loan.setDevuelto(false);

        // When - processing the loan
        LocalDate returnDate = loan.getFechaDevolucion();
        boolean isReturned = loan.isDevuelto();

        // Then - loan should be properly identified as active
        assertThat(returnDate).isNull();
        assertThat(isReturned).isFalse();
        assertThat(loan.getLibro().isDisponible()).isFalse(); // Book should be unavailable
    }

    @Test
    @DisplayName("Should handle loan return process correctly")
    void testLoanReturnProcess() {
        // Given - an active loan
        assertThat(loan.isDevuelto()).isFalse();
        assertThat(loan.getFechaDevolucion()).isNull();

        // When - processing loan return
        LocalDate returnDate = LocalDate.now();
        loan.setFechaDevolucion(returnDate);
        loan.setDevuelto(true);
        loan.getLibro().setDisponible(true); // Make book available again

        // Then - loan should be marked as returned and book available
        assertThat(loan.isDevuelto()).isTrue();
        assertThat(loan.getFechaDevolucion()).isEqualTo(returnDate);
        assertThat(loan.getLibro().isDisponible()).isTrue();
    }

    /**
     * Calculate fine for a loan based on business rules
     * @param loan The loan to calculate fine for
     * @return Fine amount in dollars
     */
    private double calculateFine(Loan loan) {
        if (loan.isDevuelto()) {
            // For returned loans, calculate based on return date
            long totalDays = ChronoUnit.DAYS.between(loan.getFechaPrestamo(), loan.getFechaDevolucion());
            long overdueDays = Math.max(0, totalDays - LOAN_PERIOD_DAYS);
            return overdueDays * DAILY_FINE_RATE;
        } else {
            // For active loans, calculate based on current date
            long totalDays = ChronoUnit.DAYS.between(loan.getFechaPrestamo(), LocalDate.now());
            long overdueDays = Math.max(0, totalDays - LOAN_PERIOD_DAYS);
            return overdueDays * DAILY_FINE_RATE;
        }
    }

    /**
     * Check if a loan is overdue (regardless of return status)
     * @param loan The loan to check
     * @return true if loan period has exceeded allowed time
     */
    private boolean isLoanOverdue(Loan loan) {
        LocalDate comparisonDate = loan.isDevuelto() ? loan.getFechaDevolucion() : LocalDate.now();
        long totalDays = ChronoUnit.DAYS.between(loan.getFechaPrestamo(), comparisonDate);
        return totalDays > LOAN_PERIOD_DAYS;
    }

    /**
     * Get number of days a loan is overdue
     * @param loan The loan to check
     * @return Number of overdue days (0 if not overdue)
     */
    private long getDaysOverdue(Loan loan) {
        LocalDate comparisonDate = loan.isDevuelto() ? loan.getFechaDevolucion() : LocalDate.now();
        long totalDays = ChronoUnit.DAYS.between(loan.getFechaPrestamo(), comparisonDate);
        return Math.max(0, totalDays - LOAN_PERIOD_DAYS);
    }

    /**
     * Check if a loan is currently overdue (only for active loans)
     * @param loan The loan to check
     * @return true if loan is active and overdue
     */
    private boolean isLoanCurrentlyOverdue(Loan loan) {
        if (loan.isDevuelto()) {
            return false; // Returned loans are not currently overdue
        }
        return isLoanOverdue(loan);
    }
}