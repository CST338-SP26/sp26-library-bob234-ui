import Utilities.Code;
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
}