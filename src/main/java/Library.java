import Utilities.Code;
import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.util.*;

/**
 * Represents a library that manages books, shelves, and readers.
 * Handles initialization from a CSV file and all library operations.
 *
 * @author Emiliano Gomez
 * @version 1.4.3
 */
public class Library {

    // Maximum number of books a reader can check out at once
    public static final int LENDING_LIMIT = 5;

    // Name of the library
    private String name;

    // Current maximum library card number (static, shared across all instances)
    private static int libraryCard = 0;

    // List of readers registered to the library
    private List<Reader> readers;

    // HashMap of shelves keyed by subject
    private HashMap<String, Shelf> shelves;

    // HashMap of books and their copy counts
    private HashMap<Book, Integer> books;

    /**
     * Constructs a Library with the given name.
     *
     * @param name the name of the library
     */
    public Library(String name) {
        this.name = name;
        this.readers = new ArrayList<>();
        this.shelves = new HashMap<>();
        this.books = new HashMap<>();
    }

    // -------------------------------------------------------------------------
    // Getters
    // -------------------------------------------------------------------------

    /**
     * Returns the name of the library.
     * @return the library name
     */
    public String getName() { return name; }

    // -------------------------------------------------------------------------
    // init
    // -------------------------------------------------------------------------

    /**
     * Parses the given CSV file to initialize books, shelves, and readers.
     *
     * @param filename the name of the CSV file to parse
     * @return SUCCESS if parsed correctly, or an error Code
     */
    public Code init(String filename) {
        Scanner scan;
        try {
            scan = new Scanner(new File(filename));
        } catch (FileNotFoundException e) {
            return Code.FILE_NOT_FOUND_ERROR;
        }

        // Parse books
        int bookCount = convertInt(scan.nextLine().trim(), Code.BOOK_COUNT_ERROR);
        if (bookCount < 0) {
            return errorCode(bookCount);
        }
        Code result = initBooks(bookCount, scan);
        if (result != Code.SUCCESS) return result;
        listBooks();

        // Parse shelves
        int shelfCount = convertInt(scan.nextLine().trim(), Code.SHELF_COUNT_ERROR);
        if (shelfCount < 0) {
            return errorCode(shelfCount);
        }
        result = initShelves(shelfCount, scan);
        if (result != Code.SUCCESS) return result;
        listShelves();

        // Parse readers
        int readerCount = convertInt(scan.nextLine().trim(), Code.READER_COUNT_ERROR);
        if (readerCount < 0) {
            return errorCode(readerCount);
        }
        result = initReader(readerCount, scan);
        if (result != Code.SUCCESS) return result;
        listReaders();

        return Code.SUCCESS;
    }

    // -------------------------------------------------------------------------
    // initBooks
    // -------------------------------------------------------------------------

    /**
     * Parses books from the scanner and adds them to the library.
     *
     * @param bookCount number of books to parse
     * @param scan      scanner positioned at the first book record
     * @return SUCCESS or an error Code
     */
    private Code initBooks(int bookCount, Scanner scan) {
        if (bookCount < 1) return Code.LIBRARY_ERROR;

        for (int i = 0; i < bookCount; i++) {
            String line = scan.nextLine().trim();
            String[] fields = line.split(",");

            if (fields.length < Book.DUE_DATE_ + 1) {
                return Code.BOOK_RECORD_COUNT_ERROR;
            }

            String isbn    = fields[Book.ISBN_];
            String title   = fields[Book.TITLE_];
            String subject = fields[Book.SUBJECT_];
            int pageCount  = convertInt(fields[Book.PAGE_COUNT_], Code.PAGE_COUNT_ERROR);
            if (pageCount <= 0) return Code.PAGE_COUNT_ERROR;

            String author  = fields[Book.AUTHOR_];
            LocalDate due  = convertDate(fields[Book.DUE_DATE_], Code.DATE_CONVERSION_ERROR);
            if (due == null) return Code.DATE_CONVERSION_ERROR;

            Book book = new Book(isbn, title, subject, pageCount, author, due);
            addBook(book);
        }
        return Code.SUCCESS;
    }

    // -------------------------------------------------------------------------
    // initShelves
    // -------------------------------------------------------------------------

    /**
     * Parses shelves from the scanner and adds them to the library.
     *
     * @param shelfCount number of shelves to parse
     * @param scan       scanner positioned at the first shelf record
     * @return SUCCESS or an error Code
     */
    private Code initShelves(int shelfCount, Scanner scan) {
        if (shelfCount < 1) return Code.SHELF_COUNT_ERROR;

        for (int i = 0; i < shelfCount; i++) {
            String line   = scan.nextLine().trim();
            String[] fields = line.split(",");

            int shelfNumber = convertInt(fields[Shelf.SHELF_NUMBER_], Code.SHELF_NUMBER_PARSE_ERROR);
            if (shelfNumber < 0) return Code.SHELF_NUMBER_PARSE_ERROR;

            String subject = fields[Shelf.SUBJECT_];
            addShelf(new Shelf(shelfNumber, subject));
        }

        if (shelves.size() != shelfCount) {
            System.out.println("Number of shelves doesn't match expected");
            return Code.SHELF_NUMBER_PARSE_ERROR;
        }
        return Code.SUCCESS;
    }

    // -------------------------------------------------------------------------
    // initReader
    // -------------------------------------------------------------------------

    /**
     * Parses readers from the scanner and adds them to the library.
     *
     * @param readerCount number of readers to parse
     * @param scan        scanner positioned at the first reader record
     * @return SUCCESS or an error Code
     */
    private Code initReader(int readerCount, Scanner scan) {
        if (readerCount <= 0) return Code.READER_COUNT_ERROR;

        for (int i = 0; i < readerCount; i++) {
            String line     = scan.nextLine().trim();
            String[] fields = line.split(",");

            int cardNum  = convertInt(fields[Reader.CARD_NUMBER_], Code.READER_CARD_NUMBER_ERROR);
            String rName = fields[Reader.NAME_];
            String phone = fields[Reader.PHONE_];
            int bookCount = convertInt(fields[Reader.BOOK_COUNT_], Code.BOOK_COUNT_ERROR);

            Reader reader = new Reader(cardNum, rName, phone);
            addReader(reader);

            // Parse books the reader has checked out
            for (int j = 0; j < bookCount; j++) {
                int isbnIndex = Reader.BOOK_START_ + (j * 2);
                int dateIndex = isbnIndex + 1;

                if (dateIndex >= fields.length) break;

                String isbn = fields[isbnIndex];
                Book book   = getBookByISBN(isbn);
                if (book == null) {
                    System.out.println("ERROR");
                    continue;
                }

                LocalDate due = convertDate(fields[dateIndex], Code.DATE_CONVERSION_ERROR);
                book.setDueDate(due);
                checkoutBook(reader, book);
            }
        }
        return Code.SUCCESS;
    }

    // -------------------------------------------------------------------------
    // addBook
    // -------------------------------------------------------------------------

    /**
     * Adds a book to the library's HashMap. If already present, increments count.
     * Also adds the book to the appropriate shelf if one exists.
     *
     * @param newBook the book to add
     * @return SUCCESS or SHELF_EXISTS_ERROR if no matching shelf
     */
    public Code addBook(Book newBook) {
        if (books.containsKey(newBook)) {
            int count = books.get(newBook) + 1;
            books.put(newBook, count);
            System.out.println(count + " copies of " + newBook.getTitle() + " in the stacks");
        } else {
            books.put(newBook, 1);
            System.out.println(newBook.getTitle() + " added to the stacks.");
        }

        // Add to matching shelf if available
        if (shelves.containsKey(newBook.getSubject())) {
            shelves.get(newBook.getSubject()).addBook(newBook);
            return Code.SUCCESS;
        }

        System.out.println("No shelf for " + newBook.getSubject() + " books");
        return Code.SHELF_EXISTS_ERROR;
    }

    // -------------------------------------------------------------------------
    // returnBook (Reader, Book)
    // -------------------------------------------------------------------------

    /**
     * Returns a book from a reader back to the library.
     *
     * @param reader the reader returning the book
     * @param book   the book being returned
     * @return SUCCESS or an error Code
     */
    public Code returnBook(Reader reader, Book book) {
        if (!reader.hasBook(book)) {
            System.out.println(reader.getName() + " doesn't have " + book.getTitle() + " checked out");
            return Code.READER_DOESNT_HAVE_BOOK_ERROR;
        }

        if (!books.containsKey(book)) {
            return Code.BOOK_NOT_IN_INVENTORY_ERROR;
        }

        System.out.println(reader.getName() + " is returning " + book);
        Code result = reader.removeBook(book);

        if (result == Code.SUCCESS) {
            return returnBook(book);
        }

        System.out.println("Could not return " + book);
        return result;
    }

    // -------------------------------------------------------------------------
    // returnBook (Book)
    // -------------------------------------------------------------------------

    /**
     * Returns a book to its shelf.
     *
     * @param book the book to return to the shelf
     * @return SUCCESS or SHELF_EXISTS_ERROR if no matching shelf
     */
    public Code returnBook(Book book) {
        if (!shelves.containsKey(book.getSubject())) {
            System.out.println("No shelf for " + book);
            return Code.SHELF_EXISTS_ERROR;
        }
        return shelves.get(book.getSubject()).addBook(book);
    }

    // -------------------------------------------------------------------------
    // addBookToShelf (deprecated)
    // -------------------------------------------------------------------------

    /**
     * Adds a book to a specific shelf.
     * @deprecated since version 1.4.0
     *
     * @param book  the book to add
     * @param shelf the shelf to add it to
     * @return SUCCESS or an error Code
     */
    @Deprecated
    private Code addBookToShelf(Book book, Shelf shelf) {
        if (returnBook(book) == Code.SUCCESS) return Code.SUCCESS;

        if (!shelf.getSubject().equals(book.getSubject())) {
            return Code.SHELF_SUBJECT_MISMATCH_ERROR;
        }

        Code result = shelf.addBook(book);
        if (result == Code.SUCCESS) {
            System.out.println(book + " added to shelf");
            return Code.SUCCESS;
        }

        System.out.println("Could not add " + book + " to shelf");
        return result;
    }

    // -------------------------------------------------------------------------
    // listBooks
    // -------------------------------------------------------------------------

    /**
     * Lists all books in the library and returns the total count.
     *
     * @return total number of book copies in the library
     */
    public int listBooks() {
        int total = 0;
        for (Map.Entry<Book, Integer> entry : books.entrySet()) {
            System.out.println(entry.getValue() + " copies of " + entry.getKey());
            total += entry.getValue();
        }
        return total;
    }

    // -------------------------------------------------------------------------
    // checkoutBook
    // -------------------------------------------------------------------------

    /**
     * Checks out a book to a reader.
     *
     * @param reader the reader checking out the book
     * @param book   the book to check out
     * @return SUCCESS or an error Code
     */
    public Code checkoutBook(Reader reader, Book book) {
        if (!readers.contains(reader)) {
            System.out.println(reader.getName() + " doesn't have an account here");
            return Code.READER_NOT_IN_LIBRARY_ERROR;
        }

        if (reader.getBookCount() >= LENDING_LIMIT) {
            System.out.println(reader.getName() + " has reached the lending limit, (" + LENDING_LIMIT + ")");
            return Code.BOOK_LIMIT_REACHED_ERROR;
        }

        if (!books.containsKey(book)) {
            System.out.println("ERROR: could not find " + book);
            return Code.BOOK_NOT_IN_INVENTORY_ERROR;
        }

        Shelf shelf = getShelf(book.getSubject());
        if (shelf == null) {
            System.out.println("no shelf for " + book.getSubject() + " books!");
            return Code.SHELF_EXISTS_ERROR;
        }

        if (shelf.getBookCount(book) < 1) {
            System.out.println("ERROR: no copies of " + book + " remain");
            return Code.BOOK_NOT_IN_INVENTORY_ERROR;
        }

        Code result = reader.addBook(book);
        if (result != Code.SUCCESS) {
            System.out.println("Couldn't checkout " + book);
            return result;
        }

        Code removeResult = shelf.removeBook(book);
        if (removeResult == Code.SUCCESS) {
            System.out.println(book + " checked out successfully");
        }
        return removeResult;
    }

    // -------------------------------------------------------------------------
    // getBookByISBN
    // -------------------------------------------------------------------------

    /**
     * Returns the Book with the matching ISBN, or null if not found.
     *
     * @param isbn the ISBN to search for
     * @return the matching Book, or null
     */
    public Book getBookByISBN(String isbn) {
        for (Book book : books.keySet()) {
            if (book.getISBN().equals(isbn)) {
                return book;
            }
        }
        System.out.println("ERROR: Could not find a book with isbn: " + isbn);
        return null;
    }

    // -------------------------------------------------------------------------
    // listShelves
    // -------------------------------------------------------------------------

    /**
     * Lists all shelves without showing books.
     *
     * @return number of shelves
     */
    public int listShelves() {
        return listShelves(false);
    }

    /**
     * Lists all shelves, optionally showing their books.
     *
     * @param showBooks if true, lists books on each shelf
     * @return number of shelves
     */
    public int listShelves(boolean showBooks) {
        for (Shelf shelf : shelves.values()) {
            if (showBooks) {
                System.out.println(shelf.listBooks());
            } else {
                System.out.println(shelf);
            }
        }
        return shelves.size();
    }

    // -------------------------------------------------------------------------
    // addShelf
    // -------------------------------------------------------------------------

    /**
     * Creates a new shelf with the given subject and adds it to the library.
     *
     * @param shelfSubject the subject of the new shelf
     * @return SUCCESS or an error Code
     */
    public Code addShelf(String shelfSubject) {
        int nextNumber = shelves.size() + 1;
        return addShelf(new Shelf(nextNumber, shelfSubject));
    }

    /**
     * Adds a shelf to the library's HashMap of shelves.
     *
     * @param shelf the shelf to add
     * @return SUCCESS or SHELF_EXISTS_ERROR if already exists
     */
    public Code addShelf(Shelf shelf) {
        if (shelves.containsKey(shelf.getSubject())) {
            System.out.println("ERROR: Shelf already exists " + shelf);
            return Code.SHELF_EXISTS_ERROR;
        }

        // Assign shelf number as largest current + 1
        int maxNum = 0;
        for (Shelf s : shelves.values()) {
            if (s.getShelfNumber() > maxNum) maxNum = s.getShelfNumber();
        }
        shelf.setShelfNumber(maxNum + 1);
        shelves.put(shelf.getSubject(), shelf);

        // Add all matching books to the new shelf
        for (Map.Entry<Book, Integer> entry : books.entrySet()) {
            if (entry.getKey().getSubject().equals(shelf.getSubject())) {
                for (int i = 0; i < entry.getValue(); i++) {
                    shelf.addBook(entry.getKey());
                }
            }
        }
        return Code.SUCCESS;
    }

    // -------------------------------------------------------------------------
    // getShelf
    // -------------------------------------------------------------------------

    /**
     * Returns the shelf with the matching shelf number.
     *
     * @param shelfNumber the shelf number to look up
     * @return the matching Shelf, or null
     */
    public Shelf getShelf(Integer shelfNumber) {
        for (Shelf shelf : shelves.values()) {
            if (shelf.getShelfNumber() == shelfNumber) {
                return shelf;
            }
        }
        System.out.println("No shelf number " + shelfNumber + " found");
        return null;
    }

    /**
     * Returns the shelf with the matching subject.
     *
     * @param subject the subject to look up
     * @return the matching Shelf, or null
     */
    public Shelf getShelf(String subject) {
        if (shelves.containsKey(subject)) {
            return shelves.get(subject);
        }
        System.out.println("No shelf for " + subject + " books");
        return null;
    }

    // -------------------------------------------------------------------------
    // listReaders
    // -------------------------------------------------------------------------

    /**
     * Lists all readers and returns the total count.
     *
     * @return number of readers
     */
    public int listReaders() {
        for (Reader reader : readers) {
            System.out.println(reader);
        }
        return readers.size();
    }

    /**
     * Lists all readers, optionally showing their checked-out books.
     *
     * @param showBooks if true, shows each reader's books
     * @return number of readers
     */
    public int listReaders(boolean showBooks) {
        if (showBooks) {
            for (Reader reader : readers) {
                System.out.println(reader.getName() + "(#" + reader.getCardNumber() + ") has the following books:");
                System.out.println(reader.getBooks());
            }
        } else {
            for (Reader reader : readers) {
                System.out.println(reader);
            }
        }
        return readers.size();
    }

    // -------------------------------------------------------------------------
    // getReaderByCard
    // -------------------------------------------------------------------------

    /**
     * Returns the reader with the given library card number.
     *
     * @param cardNumber the card number to search for
     * @return the matching Reader, or null
     */
    public Reader getReaderByCard(int cardNumber) {
        for (Reader reader : readers) {
            if (reader.getCardNumber() == cardNumber) {
                return reader;
            }
        }
        System.out.println("Could not find a reader with card #" + cardNumber);
        return null;
    }

    // -------------------------------------------------------------------------
    // addReader
    // -------------------------------------------------------------------------

    /**
     * Adds a reader to the library.
     *
     * @param reader the reader to add
     * @return SUCCESS or an error Code
     */
    public Code addReader(Reader reader) {
        if (readers.contains(reader)) {
            System.out.println(reader.getName() + " already has an account!");
            return Code.READER_ALREADY_EXISTS_ERROR;
        }

        for (Reader r : readers) {
            if (r.getCardNumber() == reader.getCardNumber()) {
                System.out.println(r.getName() + " and " + reader.getName() + " have the same card number!");
                return Code.READER_CARD_NUMBER_ERROR;
            }
        }

        readers.add(reader);
        System.out.println(reader.getName() + " added to the library!");

        if (reader.getCardNumber() > libraryCard) {
            libraryCard = reader.getCardNumber();
        }
        return Code.SUCCESS;
    }

    // -------------------------------------------------------------------------
    // removeReader
    // -------------------------------------------------------------------------

    /**
     * Removes a reader from the library if they have no checked-out books.
     *
     * @param reader the reader to remove
     * @return SUCCESS or an error Code
     */
    public Code removeReader(Reader reader) {
        if (readers.contains(reader)) {
            if (reader.getBookCount() > 0) {
                System.out.println(reader.getName() + " must return all books!");
                return Code.READER_STILL_HAS_BOOKS_ERROR;
            }
            readers.remove(reader);
            return Code.SUCCESS;
        }

        System.out.println(reader + " is not part of this Library");
        return Code.READER_NOT_IN_LIBRARY_ERROR;
    }

    // -------------------------------------------------------------------------
    // convertInt
    // -------------------------------------------------------------------------

    /**
     * Converts a String to an integer. Prints error messages on failure.
     *
     * @param recordCountString the string to convert
     * @param code              the Code context for error messaging
     * @return the parsed integer, or the negative error code on failure
     */
    public static int convertInt(String recordCountString, Code code) {
        try {
            return Integer.parseInt(recordCountString);
        } catch (NumberFormatException e) {
            System.out.println("Value which caused the error: " + recordCountString);
            System.out.println("Error message: " + code.getMessage());

            switch (code) {
                case BOOK_COUNT_ERROR:
                    System.out.println("Error: Could not read number of books");
                    break;
                case PAGE_COUNT_ERROR:
                    System.out.println("Error: could not parse page count");
                    break;
                case DATE_CONVERSION_ERROR:
                    System.out.println("Error: Could not parse date component");
                    break;
                default:
                    System.out.println("Error: Unknown conversion error");
                    break;
            }
            return code.getCode();
        }
    }

    // -------------------------------------------------------------------------
    // convertDate
    // -------------------------------------------------------------------------

    /**
     * Converts a date string in "yyyy-mm-dd" format to a LocalDate.
     * Returns 01-Jan-1970 as a default on any parse error.
     *
     * @param date      the date string to parse
     * @param errorCode the Code context for error messaging
     * @return the parsed LocalDate, or 01-Jan-1970 on failure
     */
    public static LocalDate convertDate(String date, Code errorCode) {
        if (date.equals("0000")) {
            return LocalDate.of(1970, 1, 1);
        }

        String[] parts = date.split("-");
        if (parts.length != 3) {
            System.out.println("ERROR: date conversion error, could not parse " + date);
            System.out.println("Using default date (01-jan-1970)");
            return LocalDate.of(1970, 1, 1);
        }

        int year  = convertInt(parts[0], Code.DATE_CONVERSION_ERROR);
        int month = convertInt(parts[1], Code.DATE_CONVERSION_ERROR);
        int day   = convertInt(parts[2], Code.DATE_CONVERSION_ERROR);

        if (year < 0 || month < 0 || day < 0) {
            System.out.println("Error converting date: Year " + year);
            System.out.println("Error converting date: Month " + month);
            System.out.println("Error converting date: Dat " + day);
            System.out.println("Using default date (01-jan-1970)");
            return LocalDate.of(1970, 1, 1);
        }

        return LocalDate.of(year, month, day);
    }

    // -------------------------------------------------------------------------
    // getLibraryCardNumber
    // -------------------------------------------------------------------------

    /**
     * Returns the next available library card number.
     *
     * @return libraryCard + 1
     */
    public static int getLibraryCardNumber() {
        return libraryCard + 1;
    }

    // -------------------------------------------------------------------------
    // errorCode
    // -------------------------------------------------------------------------

    /**
     * Returns the Code matching the given code number, or UNKNOWN_ERROR.
     *
     * @param codeNumber the integer code to look up
     * @return the matching Code enum value
     */
    private Code errorCode(int codeNumber) {
        for (Code code : Code.values()) {
            if (code.getCode() == codeNumber) {
                return code;
            }
        }
        return Code.UNKNOWN_ERROR;
    }
}