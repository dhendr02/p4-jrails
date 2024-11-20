package jrails;

import books.Book;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import static jrails.Model.reset;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.*;

public class ModelTest {

    private Model model;

    @Before
    public void setUp() throws Exception {
        model = new Model(){};
    }

    @Test
    public void id() {
        assertThat(model.id(), notNullValue());
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void modelTest() throws IOException {
        Book b1 = new Book();
        Book b2 = new Book();

        b1.title = "The Count of Monte Cristo";
        b1.author = "Alexandre Dumas";
        b1.num_copies = 100;

        b2.title = "Crime and Punishment";
        b2.author = "Fyodor Dostoevsky";
        b2.num_copies = 50;

        System.out.println("Saving b1...");
        b1.save();
        System.out.println("b1 saved with ID: " + b1.id());

        System.out.println("Saving b2...");
        b2.save();
        System.out.println("b2 saved with ID: " + b2.id());

        System.out.println("The ID of book 1 is: " + b1.id());



//        Model.reset();

        // Check the database content

    }


    @Test
    public void test2() throws IOException {
        Model.reset(); // Ensure a clean database before the test

        // Create and save the first book
        Book b = new Book();
        b.title = "Programming Languages: Build, Prove, and Compare";
        b.author = "Norman Ramsey";
        b.num_copies = 999;
        b.save(); // Assigns ID 1

        // Update and save the same book (still ID 1)
        b.num_copies = 42;
        b.save();

        // Create and save a second book
        Book b2 = new Book();
        b2.title = "Programming Languages: Build, Prove, and Compare";
        b2.author = "Norman Ramsey";
        b2.num_copies = 999;
        b2.save(); // Assigns ID 2

        // Create and save a third book
        Book b3 = new Book();
        b3.title = "Third Book Example";
        b3.author = "Another Author";
        b3.num_copies = 50;
        b3.save(); // Assigns ID 3

        // Retrieve b3 from the database
        Book foundB3 = Model.find(Book.class, 3);
        assert foundB3 != null : "b3 should exist in the database";
        assert foundB3.title.equals("Third Book Example") : "b3's title should match";
        assert foundB3.author.equals("Another Author") : "b3's author should match";
        assert foundB3.num_copies == 50 : "b3's num_copies should match";

        // Fetch all books and verify count
        List<Book> books = Model.all(Book.class);
        assert books.size() == 3 : "There should be exactly 3 books in the database.";

        // Remove b from the database and verify
        b.destroy();
        books = Model.all(Book.class);
        assert books.size() == 2 : "After destroying b, there should be 2 books in the database.";
    }

    @Test
    public void testAllMethod() throws IOException {
        // Reset the database to ensure a clean state
        Model.reset();

        // Create and save multiple Book instances
        Book book1 = new Book();
        book1.title = "Book One";
        book1.author = "Author One";
        book1.num_copies = 10;
        book1.save();

        Book book2 = new Book();
        book2.title = "Book Two";
        book2.author = "Author Two";
        book2.num_copies = 20;
        book2.save();

        Book book3 = new Book();
        book3.title = "Book Three";
        book3.author = "Author Three";
        book3.num_copies = 30;
        book3.save();

        // Fetch all Book instances from the database
        List<Book> books = Model.all(Book.class);

        // Assert the size of the returned list
        assert books.size() == 3 : "Expected 3 books in the database";

        // Assert each book's data
        Book retrievedBook1 = books.get(0);
        Book retrievedBook2 = books.get(1);
        Book retrievedBook3 = books.get(2);

        assert retrievedBook1.title.equals("Book One") : "Title of book1 should match";
        assert retrievedBook1.author.equals("Author One") : "Author of book1 should match";
        assert retrievedBook1.num_copies == 10 : "Number of copies for book1 should match";

        assert retrievedBook2.title.equals("Book Two") : "Title of book2 should match";
        assert retrievedBook2.author.equals("Author Two") : "Author of book2 should match";
        assert retrievedBook2.num_copies == 20 : "Number of copies for book2 should match";

        assert retrievedBook3.title.equals("Book Three") : "Title of book3 should match";
        assert retrievedBook3.author.equals("Author Three") : "Author of book3 should match";
        assert retrievedBook3.num_copies == 30 : "Number of copies for book3 should match";

        System.out.println("[DEBUG] All books retrieved: " + books);
    }

    @Test
    public void test_destroy() throws IOException {
        Model.reset();
        Book b = new Book();
        b.title = "Book One";
        b.author = "Author One";
        b.num_copies = 10;
        b.save();
        Book b2 = Model.find(Book.class, b.id());
        b.num_copies = 123456;
        b.save();
        b.destroy();
//        b.save();
    }

    @Test
    public void testFileWriteAndRead() {
        Model.reset(); // Clear the database

        // Create and save a new model
        Book book = new Book();
        book.title = "Test Book";
        book.author = "Test Author";
        book.num_copies = 1;
        book.save();

        // Check file content
        try (BufferedReader reader = new BufferedReader(new FileReader("Model_DB.csv"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("[DEBUG] File content: " + line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Read and verify in-memory data
        List<Book> books = Model.all(Book.class);
        assert books.size() == 1 : "Expected 1 book in the database.";
        Book retrievedBook = books.get(0);
        assert retrievedBook.title.equals("Test Book");
        assert retrievedBook.author.equals("Test Author");
        assert retrievedBook.num_copies == 1;
    }

    @Test
    public void testFindMethod() {
        System.out.println("[DEBUG] Running testFindMethod...");

        // Reset the database to ensure a clean state
        Model.reset();
        System.out.println("[DEBUG] Database reset.");

        // Create and save a new Book model
        Book book = new Book();
        book.title = "Find Test Book";
        book.author = "Test Author";
        book.num_copies = 2;
        book.save();
        System.out.println("[DEBUG] Saved book with ID: " + book.id());

        // Retrieve the model by ID
        Book retrievedBook = Model.find(Book.class, book.id());
        if (retrievedBook == null) {
            System.out.println("[DEBUG] No book found with ID: " + book.id());
        } else {
            System.out.println("[DEBUG] Retrieved book: " +
                    "Title = " + retrievedBook.title +
                    ", Author = " + retrievedBook.author +
                    ", Num Copies = " + retrievedBook.num_copies);
        }

        // Assertions
        assertNotNull("Book should not be null.", retrievedBook);
        assertEquals("Find Test Book", retrievedBook.title);
        assertEquals("Test Author", retrievedBook.author);
        assertEquals(2, retrievedBook.num_copies);

        System.out.println("[DEBUG] TestFindMethod completed successfully.");
    }

    @Test
    public void testSaveBehavior() {
        System.out.println("[DEBUG] Running testSaveBehavior...");

        // Reset the database to ensure a clean state
        Model.reset();
        System.out.println("[DEBUG] Database reset.");

        // Step 1: Save a new model (zero ID)
        Book book1 = new Book();
        book1.title = "Book One";
        book1.author = "Author One";
        book1.num_copies = 10;
        book1.save();
        System.out.println("[DEBUG] Book1 saved with ID: " + book1.id());

        // Verify that the book is saved in the database
        Book retrievedBook1 = Model.find(Book.class, book1.id());
        assertNotNull("Book1 should be found in the database.", retrievedBook1);
        assertEquals("Book One", retrievedBook1.title);
        assertEquals("Author One", retrievedBook1.author);
        assertEquals(10, retrievedBook1.num_copies);

        // Step 2: Update the same model (non-zero ID)
        book1.num_copies = 20; // Modify a field
        book1.save();
        System.out.println("[DEBUG] Book1 updated with ID: " + book1.id());

        // Verify that the row in the database is updated
        Book updatedBook1 = Model.find(Book.class, book1.id());
        assertNotNull("Updated Book1 should be found in the database.", updatedBook1);
        assertEquals(20, updatedBook1.num_copies);

        // Step 3: Attempt to save a model with a non-zero ID not in the database
        Model.reset(); // Clear the database to simulate a missing model
        System.out.println("[DEBUG] Database reset again.");

        try {
            book1.save();
            fail("Saving a model with a non-zero ID not in the database should throw an error.");
        } catch (IllegalArgumentException e) {
            System.out.println("[DEBUG] Caught expected exception: " + e.getMessage());
            assertEquals(
                    "Model with ID " + book1.id() + " does not exist in the database.",
                    e.getMessage()
            );
        }

        // Final verification: The database should be empty after reset
        List<Book> allBooks = Model.all(Book.class);
        assertTrue("The database should be empty after reset.", allBooks.isEmpty());

        System.out.println("[DEBUG] testSaveBehavior completed successfully.");
    }








}