import Utilities.Code;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.time.LocalDate;

public class Library {

    public static final int LENDING_LIMIT = 5;

    private String name;
    private static int libraryCard = 0;
    private List<Reader> readers;
    private HashMap<String, Shelf> shelves;
    private HashMap<Book, Integer> books;

    public Library(String name) {
        this.name = name;
        this.readers = new ArrayList<>();
        this.shelves = new HashMap<>();
        this.books = new HashMap<>();
    }

    public String getName() { return name; }

    public Code init(String filename) {
        Scanner scan;
        try {
            scan = new Scanner(new File(filename));
        } catch (FileNotFoundException e) {
            return Code.FILE_NOT_FOUND_ERROR;
        }
        int bookCount = convertInt(scan.nextLine().trim(), Code.BOOK_COUNT_ERROR);
        if (bookCount < 0) return errorCode(bookCount);
        Code result = initBooks(bookCount, scan);
        if (result != Code.SUCCESS) return result;
        listBooks();

        int shelfCount = convertInt(scan.nextLine().trim(), Code.SHELF_COUNT_ERROR);
        if (shelfCount < 0) return errorCode(shelfCount);
        result = initShelves(shelfCount, scan);
        if (result != Code.SUCCESS) return result;
        listShelves();

        int readerCount = convertInt(scan.nextLine().trim(), Code.READER_COUNT_ERROR);
        if (readerCount < 0) return errorCode(readerCount);
        result = initReader(readerCount, scan);
        if (result != Code.SUCCESS) return result;
        listReaders();

        return Code.SUCCESS;
    }

    private Code initBooks(int bookCount, Scanner scan) {
        if (bookCount < 1) return Code.LIBRARY_ERROR;
        for (int i = 0; i < bookCount; i++) {
            String[] fields = scan.nextLine().trim().split(",");
            if (fields.length < Book.DUE_DATE_ + 1) return Code.BOOK_RECORD_COUNT_ERROR;
            int pageCount = convertInt(fields[Book.PAGE_COUNT_], Code.PAGE_COUNT_ERROR);
            if (pageCount <= 0) return Code.PAGE_COUNT_ERROR;
            LocalDate due = convertDate(fields[Book.DUE_DATE_], Code.DATE_CONVERSION_ERROR);
            if (due == null) return Code.DATE_CONVERSION_ERROR;
            addBook(new Book(fields[Book.ISBN_], fields[Book.TITLE_], fields[Book.SUBJECT_], pageCount, fields[Book.AUTHOR_], due));
        }
        return Code.SUCCESS;
    }

    private Code initShelves(int shelfCount, Scanner scan) {
        if (shelfCount < 1) return Code.SHELF_COUNT_ERROR;
        for (int i = 0; i < shelfCount; i++) {
            String[] fields = scan.nextLine().trim().split(",");
            int shelfNumber = convertInt(fields[Shelf.SHELF_NUMBER_], Code.SHELF_NUMBER_PARSE_ERROR);
            if (shelfNumber < 0) return Code.SHELF_NUMBER_PARSE_ERROR;
            addShelf(new Shelf(shelfNumber, fields[Shelf.SUBJECT_]));
        }
        if (shelves.size() != shelfCount) {
            System.out.println("Number of shelves doesn't match expected");
            return Code.SHELF_NUMBER_PARSE_ERROR;
        }
        return Code.SUCCESS;
    }

    private Code initReader(int readerCount, Scanner scan) {
        if (readerCount <= 0) return Code.READER_COUNT_ERROR;
        for (int i = 0; i < readerCount; i++) {
            String[] fields = scan.nextLine().trim().split(",");
            int cardNum   = convertInt(fields[Reader.CARD_NUMBER_], Code.READER_CARD_NUMBER_ERROR);
            int bookCount = convertInt(fields[Reader.BOOK_COUNT_], Code.BOOK_COUNT_ERROR);
            Reader reader = new Reader(cardNum, fields[Reader.NAME_], fields[Reader.PHONE_]);
            addReader(reader);
            for (int j = 0; j < bookCount; j++) {
                int isbnIndex = Reader.BOOK_START_ + (j * 2);
                int dateIndex = isbnIndex + 1;
                if (dateIndex >= fields.length) break;
                Book book = getBookByISBN(fields[isbnIndex]);
                if (book == null) { System.out.println("ERROR"); continue; }
                book.setDueDate(convertDate(fields[dateIndex], Code.DATE_CONVERSION_ERROR));
                checkoutBook(reader, book);
            }
        }
        return Code.SUCCESS;
    }

    public Code addShelf(String shelfSubject) {
        return addShelf(new Shelf(shelves.size() + 1, shelfSubject));
    }

    public Code addShelf(Shelf shelf) {
        if (shelves.containsKey(shelf.getSubject())) {
            System.out.println("ERROR: Shelf already exists " + shelf);
            return Code.SHELF_EXISTS_ERROR;
        }
        int maxNum = 0;
        for (Shelf s : shelves.values()) {
            if (s.getShelfNumber() > maxNum) maxNum = s.getShelfNumber();
        }
        shelf.setShelfNumber(maxNum + 1);
        shelves.put(shelf.getSubject(), shelf);

        for (Map.Entry<Book, Integer> entry : books.entrySet()) {
            if (entry.getKey().getSubject().equals(shelf.getSubject())) {
                for (int i = 0; i < entry.getValue(); i++) {
                    shelf.addBook(entry.getKey());
                }
            }
        }
        return Code.SUCCESS;
    }

    public Shelf getShelf(Integer shelfNumber) {
        for (Shelf shelf : shelves.values()) {
            if (shelf.getShelfNumber() == shelfNumber) return shelf;
        }
        System.out.println("No shelf number " + shelfNumber + " found");
        return null;
    }

    public Shelf getShelf(String subject) {
        if (shelves.containsKey(subject)) return shelves.get(subject);
        System.out.println("No shelf for " + subject + " books");
        return null;
    }

    public int listShelves() { return listShelves(false); }

    public int listShelves(boolean showBooks) {
        for (Shelf shelf : shelves.values()) {
            if (showBooks) System.out.println(shelf.listBooks());
            else System.out.println(shelf);
        }
        return shelves.size();
    }

    // placeholder stubs
    public Code addBook(Book book) { return Code.SUCCESS; }
    public Code addReader(Reader reader) { return Code.SUCCESS; }
    public Code checkoutBook(Reader reader, Book book) { return Code.SUCCESS; }
    public Book getBookByISBN(String isbn) { return null; }
    public int listBooks() { return 0; }
    public int listReaders() { return 0; }

    public static int convertInt(String recordCountString, Code code) {
        try {
            return Integer.parseInt(recordCountString);
        } catch (NumberFormatException e) {
            System.out.println("Value which caused the error: " + recordCountString);
            System.out.println("Error message: " + code.getMessage());
            switch (code) {
                case BOOK_COUNT_ERROR: System.out.println("Error: Could not read number of books"); break;
                case PAGE_COUNT_ERROR: System.out.println("Error: could not parse page count"); break;
                case DATE_CONVERSION_ERROR: System.out.println("Error: Could not parse date component"); break;
                default: System.out.println("Error: Unknown conversion error"); break;
            }
            return code.getCode();
        }
    }

    public static LocalDate convertDate(String date, Code errorCode) {
        if (date.equals("0000")) return LocalDate.of(1970, 1, 1);
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

    public static int getLibraryCardNumber() { return libraryCard + 1; }

    private Code errorCode(int codeNumber) {
        for (Code code : Code.values()) {
            if (code.getCode() == codeNumber) return code;
        }
        return Code.UNKNOWN_ERROR;
    }
}