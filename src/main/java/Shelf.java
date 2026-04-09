import java.util.HashMap;
import java.util.Map;
import Utilities.Code;


/**
 * Name: Emiliano Gomez-Salgado
 * Assignment: Project 01 Part 03/04: Shelf.java
 */

public class Shelf {

    public static final int SHELF_NUMBER_ = 0;
    public static final int SUBJECT_      = 1;

    private HashMap<Book, Integer> books;
    private int    shelfNumber;
    private String subject;


    public Shelf() {
        books = new HashMap<>();
    }

    public Shelf(int shelfNumber, String subject) {
        this.shelfNumber = shelfNumber;
        this.subject     = subject;
        this.books       = new HashMap<>();
    }

    public int getShelfNumber() { return shelfNumber; }
    public void setShelfNumber(int shelfNumber) { this.shelfNumber = shelfNumber; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public HashMap<Book, Integer> getBooks() { return books; }
    public void setBooks(HashMap<Book, Integer> books) { this.books = books; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Shelf)) return false;
        Shelf other = (Shelf) o;
        return shelfNumber == other.shelfNumber
                && subject != null && subject.equals(other.subject);
    }

    @Override
    public int hashCode() {
        int result = shelfNumber;
        result = 31 * result + (subject != null ? subject.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return shelfNumber + " : " + subject;
    }

    public int getBookCount(Book book) {
        if (!books.containsKey(book)) {
            return -1;
        }
        return books.get(book);
    }

    public Code addBook(Book book) {
        // Book already exists on shelf — increment count
        if (books.containsKey(book)) {
            books.put(book, books.get(book) + 1);
            System.out.println(book + " added to shelf " + this);
            return Code.SUCCESS;
        }

        if (book.getSubject().equals(this.subject)) {
            books.put(book, 1);
            System.out.println(book + " added to shelf " + this);
            return Code.SUCCESS;
        }

        // Subject mismatch
        return Code.SHELF_SUBJECT_MISMATCH_ERROR;
    }


    public Code removeBook(Book book) {

        if (!books.containsKey(book)) {
            System.out.println(book.getTitle() + " is not on shelf " + subject);
            return Code.BOOK_NOT_IN_INVENTORY_ERROR;
        }

        if (books.get(book) == 0) {
            System.out.println("No copies of " + book.getTitle() + " remain on shelf " + subject);
            return Code.BOOK_NOT_IN_INVENTORY_ERROR;
        }

        books.put(book, books.get(book) - 1);
        System.out.println(book.getTitle() + " successfully removed from shelf " + subject);
        return Code.SUCCESS;
    }


    public String listBooks() {
        int total = books.size();
        String bookWord = (total == 1) ? "book" : "books";

        StringBuilder sb = new StringBuilder();
        sb.append(total).append(" ").append(bookWord)
                .append(" on shelf: ").append(this).append("\n");

        for (Map.Entry<Book, Integer> entry : books.entrySet()) {
            sb.append(entry.getKey()).append(" ").append(entry.getValue()).append("\n");
        }

        return sb.toString();
    }
}