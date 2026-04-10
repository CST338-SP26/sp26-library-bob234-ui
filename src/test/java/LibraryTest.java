import Utilities.Code;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Name: Emiliano Gomez-Salgado
 * Assignment: Project 01 Part 04/04: LibraryTest.java
 */
public class LibraryTest {

    private Library library;
    private Book sciFiBook;
    private Book educationBook;
    private Book adventureBook;
    private Reader reader1;
    private Reader reader2;

    @BeforeEach
    void setUp() {
        library       = new Library("Test Library");
        sciFiBook     = new Book("42-w-87", "Hitchhikers Guide To the Galaxy", "sci-fi", 42, "Douglas Adams", LocalDate.of(1970, 1, 1));
        educationBook = new Book("1337", "Headfirst Java", "education", 1337, "Grady Booch", LocalDate.of(1970, 1, 1));
        adventureBook = new Book("5297", "Count of Monte Cristo", "Adventure", 999, "Alexandre Dumas", LocalDate.of(1970, 1, 1));
        reader1       = new Reader(1, "Drew Clinkenbeard", "831-582-4007");
        reader2       = new Reader(2, "Jennifer Clinkenbeard", "831-555-6284");
    }

    // -------------------------------------------------------------------------
    // init
    // -------------------------------------------------------------------------

    @Test
    void testInitSuccess() {
        Code result = library.init("Library00.csv");
        assertEquals(Code.SUCCESS, result);
    }

    @Test
    void testInitFileNotFound() {
        Code result = library.init("doesNotExist.csv");
        assertEquals(Code.FILE_NOT_FOUND_ERROR, result);
    }

    // -------------------------------------------------------------------------
    // addBook
    // -------------------------------------------------------------------------

    @Test
    void testAddBookNewBook() {
        library.addShelf("sci-fi");
        Code result = library.addBook(sciFiBook);
        assertEquals(Code.SUCCESS, result);
    }

    @Test
    void testAddBookIncrementsCount() {
        library.addShelf("sci-fi");
        library.addBook(sciFiBook);
        Code result = library.addBook(sciFiBook);
        assertEquals(Code.SUCCESS, result);
    }

    @Test
    void testAddBookNoShelf() {
        Code result = library.addBook(sciFiBook);
        assertEquals(Code.SHELF_EXISTS_ERROR, result);
    }

    // -------------------------------------------------------------------------
    // returnBook
    // -------------------------------------------------------------------------

    @Test
    void testReturnBookSuccess() {
        library.addShelf("sci-fi");
        library.addBook(sciFiBook);
        library.addReader(reader1);
        library.checkoutBook(reader1, sciFiBook);
        Code result = library.returnBook(reader1, sciFiBook);
        assertEquals(Code.SUCCESS, result);
    }

    @Test
    void testReturnBookReaderDoesntHaveBook() {
        library.addShelf("sci-fi");
        library.addBook(sciFiBook);
        library.addReader(reader1);
        Code result = library.returnBook(reader1, sciFiBook);
        assertEquals(Code.READER_DOESNT_HAVE_BOOK_ERROR, result);
    }

    @Test
    void testReturnBookNoShelf() {
        library.addBook(sciFiBook);
        library.addReader(reader1);
        reader1.addBook(sciFiBook);
        Code result = library.returnBook(reader1, sciFiBook);
        assertEquals(Code.SHELF_EXISTS_ERROR, result);
    }

    // -------------------------------------------------------------------------
    // checkoutBook
    // -------------------------------------------------------------------------

    @Test
    void testCheckoutBookSuccess() {
        library.addShelf("sci-fi");
        library.addBook(sciFiBook);
        library.addReader(reader1);
        Code result = library.checkoutBook(reader1, sciFiBook);
        assertEquals(Code.SUCCESS, result);
        assertTrue(reader1.hasBook(sciFiBook));
    }

    @Test
    void testCheckoutBookReaderNotInLibrary() {
        library.addShelf("sci-fi");
        library.addBook(sciFiBook);
        Code result = library.checkoutBook(reader1, sciFiBook);
        assertEquals(Code.READER_NOT_IN_LIBRARY_ERROR, result);
    }

    @Test
    void testCheckoutBookNotInInventory() {
        library.addReader(reader1);
        Code result = library.checkoutBook(reader1, sciFiBook);
        assertEquals(Code.BOOK_NOT_IN_INVENTORY_ERROR, result);
    }

    @Test
    void testCheckoutBookLendingLimitReached() {
        library.addShelf("sci-fi");
        library.addReader(reader1);

        for (int i = 0; i < Library.LENDING_LIMIT; i++) {
            Book b = new Book("isbn-" + i, "Title " + i, "sci-fi", 100, "Author", LocalDate.of(1970, 1, 1));
            library.addBook(b);
            library.checkoutBook(reader1, b);
        }

        Book extraBook = new Book("isbn-extra", "Extra Book", "sci-fi", 100, "Author", LocalDate.of(1970, 1, 1));
        library.addBook(extraBook);
        Code result = library.checkoutBook(reader1, extraBook);
        assertEquals(Code.BOOK_LIMIT_REACHED_ERROR, result);
    }

    @Test
    void testCheckoutBookNoShelf() {
        library.addBook(sciFiBook);
        library.addReader(reader1);
        Code result = library.checkoutBook(reader1, sciFiBook);
        assertEquals(Code.BOOK_NOT_IN_INVENTORY_ERROR, result);
    }

    // -------------------------------------------------------------------------
    // getBookByISBN
    // -------------------------------------------------------------------------

    @Test
    void testGetBookByISBNFound() {
        library.addShelf("sci-fi");
        library.addBook(sciFiBook);
        Book found = library.getBookByISBN("42-w-87");
        assertEquals(sciFiBook, found);
    }

    @Test
    void testGetBookByISBNNotFound() {
        Book found = library.getBookByISBN("0000");
        assertNull(found);
    }

    // -------------------------------------------------------------------------
    // listBooks
    // -------------------------------------------------------------------------

    @Test
    void testListBooksReturnsCount() {
        library.addShelf("sci-fi");
        library.addShelf("education");
        library.addBook(sciFiBook);
        library.addBook(sciFiBook);
        library.addBook(educationBook);
        int count = library.listBooks();
        assertEquals(3, count);
    }

    // -------------------------------------------------------------------------
    // addShelf / getShelf / listShelves
    // -------------------------------------------------------------------------

    @Test
    void testAddShelfByString() {
        Code result = library.addShelf("sci-fi");
        assertEquals(Code.SUCCESS, result);
    }

    @Test
    void testAddShelfDuplicate() {
        library.addShelf("sci-fi");
        Code result = library.addShelf("sci-fi");
        assertEquals(Code.SHELF_EXISTS_ERROR, result);
    }

    @Test
    void testGetShelfBySubject() {
        library.addShelf("sci-fi");
        Shelf shelf = library.getShelf("sci-fi");
        assertNotNull(shelf);
        assertEquals("sci-fi", shelf.getSubject());
    }

    @Test
    void testGetShelfBySubjectNotFound() {
        Shelf shelf = library.getShelf("mystery");
        assertNull(shelf);
    }

    @Test
    void testGetShelfByNumber() {
        library.addShelf("sci-fi");
        Shelf shelf = library.getShelf(1);
        assertNotNull(shelf);
    }

    @Test
    void testGetShelfByNumberNotFound() {
        Shelf shelf = library.getShelf(99);
        assertNull(shelf);
    }

    @Test
    void testListShelvesReturnsCount() {
        library.addShelf("sci-fi");
        library.addShelf("education");
        int count = library.listShelves();
        assertEquals(2, count);
    }

    @Test
    void testListShelvesShowBooks() {
        library.addShelf("sci-fi");
        library.addBook(sciFiBook);
        int count = library.listShelves(true);
        assertEquals(1, count);
    }

    // -------------------------------------------------------------------------
    // addReader / removeReader / getReaderByCard / listReaders
    // -------------------------------------------------------------------------

    @Test
    void testAddReaderSuccess() {
        Code result = library.addReader(reader1);
        assertEquals(Code.SUCCESS, result);
    }

    @Test
    void testAddReaderDuplicate() {
        library.addReader(reader1);
        Code result = library.addReader(reader1);
        assertEquals(Code.READER_ALREADY_EXISTS_ERROR, result);
    }

    @Test
    void testAddReaderSameCardNumber() {
        Reader duplicate = new Reader(1, "Someone Else", "000-000-0000");
        library.addReader(reader1);
        Code result = library.addReader(duplicate);
        assertEquals(Code.READER_CARD_NUMBER_ERROR, result);
    }

    @Test
    void testRemoveReaderSuccess() {
        library.addReader(reader1);
        Code result = library.removeReader(reader1);
        assertEquals(Code.SUCCESS, result);
    }

    @Test
    void testRemoveReaderStillHasBooks() {
        library.addShelf("sci-fi");
        library.addBook(sciFiBook);
        library.addReader(reader1);
        library.checkoutBook(reader1, sciFiBook);
        Code result = library.removeReader(reader1);
        assertEquals(Code.READER_STILL_HAS_BOOKS_ERROR, result);
    }

    @Test
    void testRemoveReaderNotInLibrary() {
        Code result = library.removeReader(reader1);
        assertEquals(Code.READER_NOT_IN_LIBRARY_ERROR, result);
    }

    @Test
    void testGetReaderByCardFound() {
        library.addReader(reader1);
        Reader found = library.getReaderByCard(1);
        assertEquals(reader1, found);
    }

    @Test
    void testGetReaderByCardNotFound() {
        Reader found = library.getReaderByCard(99);
        assertNull(found);
    }

    @Test
    void testListReadersReturnsCount() {
        library.addReader(reader1);
        library.addReader(reader2);
        int count = library.listReaders();
        assertEquals(2, count);
    }

    @Test
    void testListReadersShowBooks() {
        library.addReader(reader1);
        int count = library.listReaders(true);
        assertEquals(1, count);
    }

    // -------------------------------------------------------------------------
    // convertInt
    // -------------------------------------------------------------------------

    @Test
    void testConvertIntSuccess() {
        int result = Library.convertInt("42", Code.BOOK_COUNT_ERROR);
        assertEquals(42, result);
    }

    @Test
    void testConvertIntFailure() {
        int result = Library.convertInt("abc", Code.BOOK_COUNT_ERROR);
        assertEquals(Code.BOOK_COUNT_ERROR.getCode(), result);
    }

    // -------------------------------------------------------------------------
    // convertDate
    // -------------------------------------------------------------------------

    @Test
    void testConvertDateSuccess() {
        LocalDate date = Library.convertDate("2020-10-12", Code.DATE_CONVERSION_ERROR);
        assertEquals(LocalDate.of(2020, 10, 12), date);
    }

    @Test
    void testConvertDateZero() {
        LocalDate date = Library.convertDate("0000", Code.DATE_CONVERSION_ERROR);
        assertEquals(LocalDate.of(1970, 1, 1), date);
    }

    @Test
    void testConvertDateBadFormat() {
        LocalDate date = Library.convertDate("not-a-date-at-all", Code.DATE_CONVERSION_ERROR);
        assertEquals(LocalDate.of(1970, 1, 1), date);
    }

    // -------------------------------------------------------------------------
    // getLibraryCardNumber
    // -------------------------------------------------------------------------

    @Test
    void testGetLibraryCardNumber() {
        library.addReader(reader1);
        library.addReader(reader2);
        int cardNum = Library.getLibraryCardNumber();
        assertTrue(cardNum > 0);
    }
}