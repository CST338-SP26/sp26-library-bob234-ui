/*
    File Name: ReaderTest.java
    Abstract: This file holds test variables and objects as well as
              tests for the constructor, getters and setters, and other
              methods found within Reader.java.
    Author: Andrea Ultreras
    Date: 02/22/21
 */

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import Utilities.Code;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ReaderTest {
    //DECLARE VARIABLES--------------------------------------------------------
    Reader read;
    Reader read1;
    Reader read2;
    Reader read3;
    Reader read4;

    int cardNumber;
    String name;
    String phone;
    List<Book> books;

    int cardNumber_;
    String name_;
    String phone_;
    List<Book> books_;

    Book bookTest;

    //CONSTRUCTOR & DESTRUCTOR-------------------------------------------------
    @BeforeEach
    void setUp() {
        //constructor test---
        read =null;
        assertNull(read);
        read = new Reader(0, "", "");
        assertNotNull(read);

        //field setting and getter test---
        cardNumber = 1;
        name = "Drew Clinkenbeard";
        phone = "831-582-4007";
        read1 = new Reader(cardNumber, name, phone);

        //equality test---
        read2 = new Reader(2, "Jennifer Clinkenbeard", "831-555-6284");
        read3 = new Reader(2, "Jennifer Clinkenbeard", "831-555-6284");

        //setter test---
        read4 = new Reader(cardNumber, name, phone);
        cardNumber_ = 3;
        name_ = "Monte Ray";
        phone_ = "555-555-4444";

        //other variable declarations---
        bookTest = new Book("5297", "Count of Monte Cristo", "Adventure", 999, "Alexandrea Dumas", LocalDate.of(2021, 1, 1));

    }

    @AfterEach
    void tearDown() {
        read = null;
        read1 = null;
        read2 = null;
        read3 = null;
        read4 = null;
        cardNumber = 0;
        name = null;
        phone = null;
        books = null;
        cardNumber_ = 0;
        name_ = null;
        phone_ = null;
        books_ = null;
    }

    //GETTERS AND SETTERS------------------------------------------------------
    @Test
    void getCardNumber() {
        assertEquals(cardNumber, read1.getCardNumber());                //field setting and getter test
        assertNotEquals(read1.getCardNumber(), read2.getCardNumber());  //equality test
        assertEquals(read2.getCardNumber(), read3.getCardNumber());     //equality test
    }

    @Test
    void setCardNumber() {
        read4.setCardNumber(cardNumber_);                   //set value to new parameter, setter test
        assertNotEquals(cardNumber, read4.getCardNumber()); //setter test
        assertEquals(cardNumber_, read4.getCardNumber());   //setter test
    }

    @Test
    void getName() {
        assertEquals(name, read1.getName());
        assertNotEquals(read1.getName(), read2.getName());
        assertEquals(read2.getName(), read3.getName());
    }

    @Test
    void setName() {
        read4.setName(name_);
        assertNotEquals(name, read4.getName());
        assertEquals(name_, read4.getName());
    }

    @Test
    void getPhone() {
        assertEquals(phone, read1.getPhone());
        assertNotEquals(read1.getPhone(), read2.getPhone());
        assertEquals(read2.getPhone(), read3.getPhone());
    }

    @Test
    void setPhone() {
        read4.setPhone(phone_);
        assertNotEquals(phone, read4.getPhone());
        assertEquals(phone_, read4.getPhone());
    }

    @Test
    void getBooks() {
        assertEquals(new ArrayList<Book>(), read1.getBooks());  //use ArrayList to declare list as empty not null bc getBooks is empty not null
        read1.addBook(bookTest);                                //add a book to increase size, note: did not use books.add(bookTest)
        assertNotEquals(read1.getBooks(), read2.getBooks());    //has (1,0)
        assertEquals(read2.getBooks(), read3.getBooks());       //has (0,0)
    }

    @Test
    void setBooks() {
        books_ = new ArrayList<Book>();           //set list to 0
        read4.setBooks(books_);                   //assign books_ to read4
        read4.addBook(bookTest);                  //add a book to list
        assertNotEquals(books, read4.getBooks()); //has (0,1)
        assertEquals(books_, read4.getBooks());   //has (1,1)
    }

    //OTHER FUNCTIONS----------------------------------------------------------
    @Test
    void addBook() {
        //TODO: how to check status code
        Reader reader = new Reader(0, "", "");
        assertEquals(reader.addBook(bookTest), Code.SUCCESS);
        assertNotEquals(reader.addBook(bookTest), Code.SUCCESS);    //shows that the same book can't be added twice
        assertEquals(reader.addBook(bookTest), Code.BOOK_ALREADY_CHECKED_OUT_ERROR);    //shows that the book was already added
    }

    @Test
    void removeBook() {
        Reader reader = new Reader(0, "", "");
        assertEquals(reader.removeBook(bookTest), Code.READER_DOESNT_HAVE_BOOK_ERROR);
        reader.addBook(bookTest);
        assertEquals(reader.removeBook(bookTest), Code.SUCCESS);
    }

    @Test
    void hasBook() {
        Reader reader = new Reader(0, "", "");
        assertFalse(reader.hasBook(bookTest));
        reader.addBook(bookTest);
        assertTrue(reader.hasBook(bookTest));
    }

    @Test
    void getBookCount() {
        Reader reader = new Reader(0, "", "");
        assert reader.getBookCount() == 0;      //use reader.getBookCount() not books.size()
        reader.addBook(bookTest);
        assert reader.getBookCount() == 1;
        reader.removeBook(bookTest);
        assert reader.getBookCount() == 0;
    }
}