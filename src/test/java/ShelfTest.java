/*
    File Name: ShelfTest.java
    Abstract: This file holds test variables and objects as well as
              tests for the constructor, getters and setters, and other
              methods found within Shelf.java.
    Author: Andrea Ultreras
    Date: 03/01/21
 */
import Utilities.Code;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class ShelfTest {
    Shelf shelf;
    Shelf shelf1;
    Shelf shelf2;
    Shelf shelf3;
    Shelf shelf4;
    HashMap<Book, Integer> books;
    Book book;
    Book book1;

    //CONSTRUCTOR & DESTRUCTOR-------------------------------------------------
    @BeforeEach
    void setUp() {
        //constructor test---
        shelf = null;
        assertNull(shelf);
        shelf = new Shelf();
        assertNotNull(shelf);

        //field setting and getter test---
        books = new HashMap<>();
        shelf1 = new Shelf();

        //equality test---
        shelf2 = new Shelf();
        shelf3 = new Shelf();

        //setter test---
        shelf4 = new Shelf();

        //other variables---
        book = new Book("34-w-34", "Dune", "sci-fi", 235, "Frank Herbert", LocalDate.of(2021,2,10));
        book1 = new Book("5297", "Count of Monte Cristo", "Adventure", 999, "Alexandrea Dumas", LocalDate.of(2021, 1, 1));
    }

    @AfterEach
    void tearDown() {
        shelf = null;
        shelf1 = null;
        shelf2 = null;
        shelf3 = null;
        shelf4 = null;
        books = null;
        book = null;
        book1 = null;
    }

    //GETTERS & SETTERS--------------------------------------------------------
    @Test
    void getShelfNumber() {
        shelf1.setShelfNumber(1);
        assertEquals(01, shelf1.getShelfNumber());                  //field setting and getter test
        shelf2.setShelfNumber(2);
        assertNotEquals(shelf1.getShelfNumber(), shelf2.getShelfNumber());  //equality test
        shelf3.setShelfNumber(2);
        assertEquals(shelf2.getShelfNumber(), shelf3.getShelfNumber());     //equality test
    }

    @Test
    void setShelfNumber() {
        shelf4.setShelfNumber(1);                               //set value to new parameter, setter test
        assertNotEquals(0, shelf4.getShelfNumber());  //setter test
        assertEquals(1, shelf4.getShelfNumber());       //setter test
    }

    //---------------------------------
    @Test
    void getSubject() {
        shelf1.setSubject("sci-fi");
        assertEquals("sci-fi", shelf1.getSubject());
        shelf2.setSubject("adventure");
        assertNotEquals(shelf1.getSubject(), shelf2.getSubject());
        shelf3.setSubject("adventure");
        assertEquals(shelf2.getSubject(), shelf3.getSubject());
    }

    @Test
    void setSubject() {
        shelf4.setSubject("education");
        assertNotEquals("adventure", shelf4.getSubject());
        assertEquals("education", shelf4.getSubject());
    }

    //---------------------------------
    @Test
    void getBooks() {
        HashMap<Book, Integer> books1 = new HashMap<>();
        books1.put(book, 1);

        shelf1.setBooks(books);
        assertEquals(books, shelf1.getBooks());
        shelf2.setBooks(books1);
        assertNotEquals(shelf1.getBooks(), shelf2.getBooks());
        shelf3.setBooks(books1);
        assertEquals(shelf2.getBooks(), shelf3.getBooks());
    }

    @Test
    void setBooks() {
        HashMap<Book, Integer> books1 = new HashMap<>();
        books1.put(book, 1);

        shelf4.setBooks(books1);
        assertNotEquals(books, shelf4.getBooks());
        assertEquals(books1, shelf4.getBooks());
    }

    //OTHER FUNCTIONS----------------------------------------------------------
    @Test
    void getBookCount() {
        shelf.setSubject("sci-fi");
        Random random = new Random();
        Integer count = random.nextInt(5) + 1;    //gets random # range is 0-5
        for (int i = 0; i < count; i++) {               //adds random number of books to shelf
            shelf.addBook(book);
        }

        assertEquals(count, shelf.getBookCount(book));
        shelf.removeBook(book);
        assertNotEquals(count, shelf.getBookCount(book));
        assertNotEquals(count, shelf.getBookCount(book1));
    }

    @Test
    void addBook() {
        shelf.setSubject("sci-fi");
        assertEquals(shelf.addBook(book), Code.SUCCESS);             //add book to shelf
        assertEquals(1, shelf.getBookCount(book));           //check the count
        assertEquals(shelf.addBook(book), Code.SUCCESS);             //add the book again
        assertEquals(2, shelf.getBookCount(book));           //check the count
        assertEquals(shelf.addBook(book1), Code.SHELF_SUBJECT_MISMATCH_ERROR);  //adds mismatching book to shelf
    }

    @Test
    void removeBook() {
        shelf.setSubject("sci-fi");
        assertEquals(shelf.removeBook(book), Code.BOOK_NOT_IN_INVENTORY_ERROR);    //removes book that doesn't exist
        shelf.addBook(book);                                    //add the book to shelf
        assertEquals(1, shelf.getBookCount(book));      //check the count
        assertEquals(shelf.removeBook(book), Code.SUCCESS);     //removes book that does exist
        assertEquals(0, shelf.getBookCount(book));      //check the count
    }

    @Test
    void listBooks() {
        String listBook = "0 books on shelf: 1 : sci-fi";
        shelf.setSubject("sci-fi");
        shelf.setShelfNumber(1);
        assertEquals(listBook.trim(), shelf.listBooks().trim());
        shelf.addBook(book);
        listBook = "1 book on shelf: 1 : sci-fi\n";
        listBook += book.toString() + " 1";
        assertEquals(listBook.trim(), shelf.listBooks().trim());

    }
}