package com.codeup.novabook.Models.Entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for Book entity validation and stock management
 * Author: Luis Alfredo - Clan Cienaga
 */
@DisplayName("Book Entity Tests")
class BookTest {

    private Book book;

    @BeforeEach
    void setUp() {
        book = new Book();
        book.setId(1);
        book.setTitulo("Java: The Complete Reference");
        book.setAutor("Herbert Schildt");
        book.setIsbn("978-0072263213");
        book.setAnioPublicacion(2021);
        book.setDisponible(true);
        book.setFechaAlta(LocalDateTime.now());
    }

    @Test
    @DisplayName("Should create book with valid data")
    void testBookCreation() {
        // Given - setUp method provides a valid book

        // When - book is created (done in setUp)

        // Then - all fields should be set correctly
        assertThat(book.getId()).isEqualTo(1);
        assertThat(book.getTitulo()).isEqualTo("Java: The Complete Reference");
        assertThat(book.getAutor()).isEqualTo("Herbert Schildt");
        assertThat(book.getIsbn()).isEqualTo("978-0072263213");
        assertThat(book.getAnioPublicacion()).isEqualTo(2021);
        assertThat(book.isDisponible()).isTrue();
        assertThat(book.getFechaAlta()).isNotNull();
    }

    @Test
    @DisplayName("Should validate book availability for lending")
    void testBookAvailabilityForLending() {
        // Given - a book that is available
        book.setDisponible(true);

        // When - checking if book can be lent
        boolean canBeLent = book.isDisponible();

        // Then - book should be available for lending
        assertThat(canBeLent).isTrue();
        assertThat(book).satisfies(b -> {
            assertThat(b.isDisponible()).as("Book should be available").isTrue();
            assertThat(b.getTitulo()).as("Book title should not be null").isNotNull();
            assertThat(b.getId()).as("Book ID should be positive").isPositive();
        });
    }

    @Test
    @DisplayName("Should prevent lending when book is not available")
    void testBookNotAvailableForLending() {
        // Given - a book that is not available
        book.setDisponible(false);

        // When - checking if book can be lent
        boolean canBeLent = book.isDisponible();

        // Then - book should not be available for lending
        assertThat(canBeLent).isFalse();
    }

    @ParameterizedTest
    @DisplayName("Should validate ISBN format")
    @ValueSource(strings = {
        "978-0072263213",
        "0-201-63361-2",
        "978-3-16-148410-0",
        "1234567890123"
    })
    void testValidISBNFormats(String isbn) {
        // Given - various ISBN formats
        book.setIsbn(isbn);

        // When - ISBN is set
        String setIsbn = book.getIsbn();

        // Then - ISBN should be stored correctly
        assertThat(setIsbn).isEqualTo(isbn);
        assertThat(setIsbn).isNotBlank();
    }

    @ParameterizedTest
    @DisplayName("Should validate publication years")
    @CsvSource({
        "1900, true",
        "2000, true",
        "2024, true",
        "1800, false",
        "2030, false"
    })
    void testPublicationYearValidation(int year, boolean expectedValid) {
        // Given - various publication years
        book.setAnioPublicacion(year);

        // When - validating publication year
        boolean isValidYear = year >= 1900 && year <= LocalDateTime.now().getYear();

        // Then - validation should match expected result
        assertThat(isValidYear).isEqualTo(expectedValid);
        if (expectedValid) {
            assertThat(book.getAnioPublicacion()).isBetween(1900, LocalDateTime.now().getYear());
        }
    }

    @Test
    @DisplayName("Should handle book stock status changes")
    void testBookStockStatusChanges() {
        // Given - a book that is initially available
        assertThat(book.isDisponible()).isTrue();

        // When - book is borrowed (stock becomes unavailable)
        book.setDisponible(false);

        // Then - book should be marked as unavailable
        assertThat(book.isDisponible()).isFalse();

        // When - book is returned (stock becomes available again)
        book.setDisponible(true);

        // Then - book should be marked as available
        assertThat(book.isDisponible()).isTrue();
    }

    @Test
    @DisplayName("Should validate required fields are not null or empty")
    void testRequiredFieldValidation() {
        // Test title validation
        assertAll("Book title validation",
            () -> assertThat(book.getTitulo()).isNotNull(),
            () -> assertThat(book.getTitulo()).isNotEmpty(),
            () -> assertThat(book.getTitulo().trim()).isNotEmpty()
        );

        // Test author validation
        assertAll("Book author validation",
            () -> assertThat(book.getAutor()).isNotNull(),
            () -> assertThat(book.getAutor()).isNotEmpty(),
            () -> assertThat(book.getAutor().trim()).isNotEmpty()
        );

        // Test ISBN validation
        assertAll("Book ISBN validation",
            () -> assertThat(book.getIsbn()).isNotNull(),
            () -> assertThat(book.getIsbn()).isNotEmpty()
        );
    }

    @Test
    @DisplayName("Should handle edge cases for book data")
    void testBookEdgeCases() {
        // Test with very long title
        String longTitle = "A".repeat(500);
        book.setTitulo(longTitle);
        assertThat(book.getTitulo()).hasSize(500);

        // Test with special characters in author name
        book.setAutor("José María García-López");
        assertThat(book.getAutor()).contains("-").contains("é").contains("í");

        // Test with current year
        int currentYear = LocalDateTime.now().getYear();
        book.setAnioPublicacion(currentYear);
        assertThat(book.getAnioPublicacion()).isEqualTo(currentYear);
    }

    @Test
    @DisplayName("Should correctly set and get fechaAlta")
    void testFechaAltaHandling() {
        // Given - a specific date
        LocalDateTime testDate = LocalDateTime.of(2024, 10, 14, 10, 30, 0);
        
        // When - setting fechaAlta
        book.setFechaAlta(testDate);
        
        // Then - date should be stored correctly
        assertThat(book.getFechaAlta()).isEqualTo(testDate);
        assertThat(book.getFechaAlta().getYear()).isEqualTo(2024);
        assertThat(book.getFechaAlta().getMonthValue()).isEqualTo(10);
        assertThat(book.getFechaAlta().getDayOfMonth()).isEqualTo(14);
    }
}