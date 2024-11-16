package jrails;

import books.Book;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

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
    public void modelTest() {
        Book b1 = new Book();
        Book b2 = new Book();

        b1.title = "The Count of Monte Cristo";
        b1.author = "Alexandre Dumas";
        b1.num_copies = 100;

        b2.title = "Crime and Punishment";
        b2.author = "Fyodor Dostoevsky";
        b2.num_copies = 50;

        b1.save();
        b2.save();

        assert(b1.id() != b2.id());

        Book b3 = Model.find(Book.class, b1.id());
        assert(b3 == null);

        List<Book> books = Model.all(Book.class);

        b1.destroy();
        b2.destroy();
        b3.destroy();
    }
}