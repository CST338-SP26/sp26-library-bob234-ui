import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


/**
 * @author Andrea Ultreras
 * @since 2021.02.15
 * Abstract: This file holds test variables and objects as well as
 *           tests for the constructor and getters and setters.
 *
 * This test was written by an excellent student.
 *
 */
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
class BookTest {
    Book book;
    Book book1;
    Book book2;
    Book book3;
    Book book4;
    String ISBN;
    String Title;
    String Subject;
    int PageCount;
    String Author;
    LocalDate DueDate;
    String ISBN_;
    String Title_;
    String Subject_;
    int PageCount_;
    String Author_;
    LocalDate DueDate_;

    //CONSTRUCTOR & DESTRUCTOR-----------------------------------------------------------
    @BeforeEach
    void setUp() {
        //constructor test---
        book = null;
        assertNull(book);
        book = new Book("", "", "", 0, "", LocalDate.of(2021, 2, 10));
        assertNotNull(book);

        //field setting and getter test---
        ISBN = "1337";
        Title = "Headfirst Java";
        Subject = "education";
        PageCount = 1337;
        Author = "Grady Booch";
        DueDate = LocalDate.of(2002, 1, 8);
        book1 = new Book(ISBN, Title, Subject, PageCount, Author, DueDate);

        //equality test---
        book2 = new Book("5297", "Count of Monte Cristo", "Adventure", 999, "Alexandrea Dumas", LocalDate.of(2021, 1, 1));
        book3 = new Book("5297", "Count of Monte Cristo", "Adventure", 999, "Alexandrea Dumas", LocalDate.of(2021, 1, 1));

        //setter test---
        book4 = new Book(ISBN, Title, Subject, PageCount, Author, DueDate);
        ISBN_ = "34-w-34";
        Title_ = "Dune";
        Subject_ = "sci-fi";
        PageCount_ = 235;
        Author_ = "Frank Herbert";
        DueDate_ = LocalDate.of(2020, 5, 12);
    }

    @AfterEach
    void tearDown() {
        book = null;
        book1 = null;
        book2 = null;
        book3 = null;
        book4 = null;
        ISBN = null;
        Title = null;
        Subject = null;
        PageCount = 0;
        Author = null;
        DueDate = null;
    }

    //GETTERS AND SETTERS----------------------------------------------------------------

    @Test
    void staticTest(){
        assertEquals(0, Book.ISBN_);
        assertEquals(1, Book.TITLE_);
        assertEquals(2, Book.SUBJECT_);
        assertEquals(3, Book.PAGE_COUNT_);
        assertEquals(4, Book.AUTHOR_);
        assertEquals(5, Book.DUE_DATE_);
    }

    @Test
    void getISBN() {
        assertEquals(ISBN, book1.getISBN());                //field setting and getter test
        assertNotEquals(book1.getISBN(), book2.getISBN());  //equality test
        assertEquals(book2.getISBN(), book3.getISBN());     //equality test
    }

    @Test
    void setISBN() {
        book4.setISBN(ISBN_);                   //set value to new parameter, setter test
        assertNotEquals(ISBN, book4.getISBN()); //setter test
        assertEquals(ISBN_, book4.getISBN());   //setter test
    }

    @Test
    void getTitle() {
        assertEquals(Title, book1.getTitle());
        assertNotEquals(book1.getTitle(), book2.getTitle());
        assertEquals(book2.getTitle(), book3.getTitle());
    }

    @Test
    void setTitle() {
        book4.setTitle(Title_);
        assertNotEquals(Title, book4.getTitle());
        assertEquals(Title_, book4.getTitle());
    }

    @Test
    void getSubject() {
        assertEquals(Subject, book1.getSubject());
        assertNotEquals(book1.getSubject(), book2.getSubject());
        assertEquals(book2.getSubject(), book3.getSubject());
    }

    @Test
    void setSubject() {
        book4.setSubject(Subject_);
        assertNotEquals(Subject, book4.getSubject());
        assertEquals(Subject_, book4.getSubject());
    }

    @Test
    void getPageCount() {
        assertEquals(PageCount, book1.getPageCount());
        assertNotEquals(book1.getPageCount(), book2.getPageCount());
        assertEquals(book2.getPageCount(), book3.getPageCount());
    }

    @Test
    void setPageCount() {
        book4.setPageCount(PageCount_);
        assertNotEquals(PageCount, book4.getPageCount());
        assertEquals(PageCount_, book4.getPageCount());
    }

    @Test
    void getAuthor() {
        assertEquals(Author, book1.getAuthor());
        assertNotEquals(book1.getAuthor(), book2.getAuthor());
        assertEquals(book2.getAuthor(), book3.getAuthor());
    }

    @Test
    void setAuthor() {
        book4.setAuthor(Author_);
        assertNotEquals(Author, book4.getAuthor());
        assertEquals(Author_, book4.getAuthor());
    }

    @Test
    void getDueDate() {
        assertEquals(DueDate, book1.getDueDate());
        assertNotEquals(book1.getDueDate(), book2.getDueDate());
        assertEquals(book2.getDueDate(), book3.getDueDate());
    }

    @Test
    void setDueDate() {
        book4.setDueDate(DueDate_);
        assertNotEquals(DueDate, book4.getDueDate());
        assertEquals(DueDate_, book4.getDueDate());
    }
}