import java.util.HashMap;
import java.util.Map;
import Utilities.Code;


/**
 * Name: Emiliano Gomez-Salgado
 * Assignment: Project 01 Part 03/04: Shelf.java
 */


public class Shelf {

    // Index for shelf number when parsing a String array
    public static final int SHELF_NUMBER_ = 0;

    // Index for subject when parsing a String array
    public static final int SUBJECT_ = 1;

    // Maps each book on this shelf to its available copy count
    private HashMap<Book, Integer> books;

    // The unique number identifying this shelf
    private int shelfNumber;

    // The subject category of books stored on this shelf
    private String subject;

    /**
     * No-argument constructor. Deprecated and will be removed in future versions.
     */
    public Shelf() {
        books = new HashMap<>();
    }

    /**
     * Constructs a Shelf with a given shelf number and subject.
     *
     * @param shelfNumber the unique number for this shelf
     * @param subject     the subject category for this shelf
     */
    public Shelf(int shelfNumber, String subject) {
        this.shelfNumber = shelfNumber;
        this.subject     = subject;
        this.books       = new HashMap<>();
    }

    /**
     * Returns the shelf number.
     * @return the shelf number
     */
    public int getShelfNumber() { return shelfNumber; }

    /**
     * Sets the shelf number.
     * @param shelfNumber the shelf number to set
     */
    public void setShelfNumber(int shelfNumber) { this.shelfNumber = shelfNumber; }

    /**
     * Returns the subject of this shelf.
     * @return the subject
     */
    public String getSubject() { return subject; }

    /**
     * Sets the subject of this shelf.
     * @param subject the subject to set
     */
    public void setSubject(String subject) { this.subject = subject; }

    /**
     * Returns the books HashMap.
     * @return the books HashMap
     */
    public HashMap<Book, Integer> getBooks() { return books; }

    /**
     * Sets the books HashMap.
     * @param books the HashMap to set
     */
    public void setBooks(HashMap<Book, Integer> books) { this.books = books; }

    /**
     * Checks equality based on shelfNumber and subject only.
     * @param o the object to compare
     * @return true if shelfNumber and subject match
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Shelf)) return false;
        Shelf other = (Shelf) o;
        return shelfNumber == other.shelfNumber
                && subject != null && subject.equals(other.subject);
    }

    /**
     * Generates hash code based on shelfNumber and subject only.
     * @return the hash code
     */
    @Override
    public int hashCode() {
        int result = shelfNumber;
        result = 31 * result + (subject != null ? subject.hashCode() : 0);
        return result;
    }

    /**
     * Returns a string representation in the format: "shelfNumber : subject"
     * @return formatted string of this shelf
     */
    @Override
    public String toString() {
        return shelfNumber + " : " + subject;
    }

    /**
     * Returns the count of the given book on this shelf.
     * @param book the book to look up
     * @return the count of the book, or -1 if the book is not on this shelf
     */
    public int getBookCount(Book book) {
        if (!books.containsKey(book)) {
            return -1;
        }
        return books.get(book);
    }

    /**
     * Adds a book to this shelf.
     * If the book already exists, increments its count.
     * If the book is new and subjects match, adds it with a count of 1.
     * If subjects do not match, returns a mismatch error.
     *
     * @param book the book to add
     * @return SUCCESS if added, SHELF_SUBJECT_MISMATCH_ERROR if subjects don't match
     */
    public Code addBook(Book book) {
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

        return Code.SHELF_SUBJECT_MISMATCH_ERROR;
    }

    /**
     * Removes one copy of a book from this shelf.
     * Returns an error if the book is not on the shelf or has no copies remaining.
     *
     * @param book the book to remove
     * @return SUCCESS if removed, BOOK_NOT_IN_INVENTORY_ERROR otherwise
     */
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

    /**
     * Returns a formatted string listing all books on this shelf and their counts.
     * Uses "book" for exactly 1 book and "books" for any other count.
     *
     * @return a formatted string of all books on the shelf
     */
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