package net.stawrul;

import net.stawrul.model.Book;
import net.stawrul.model.Order;
import net.stawrul.services.BooksService;
import net.stawrul.services.OrdersService;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import javax.persistence.EntityManager;
import javax.validation.*;

import java.util.UUID;

import net.stawrul.services.exceptions.ValidationException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.times;

@RunWith(MockitoJUnitRunner.class)
public class OrdersServiceTest {

    @Mock
    private EntityManager em;

    private static Validator validator;

    @BeforeClass
    public static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void whenOrderedBookNotAvailable_placeOrderThrowsOutOfStockEx() {
        //Arrange
        Order order = new Order();
        Book book = new Book();
        book.setAmount(0);
        book.setCost(25);
        order.getBooks().add(book);

        Mockito.when(em.find(Book.class, book.getId())).thenReturn(book);

        OrdersService ordersService = new OrdersService(em);

        try {
            ordersService.placeOrder(order);
            fail();
        } catch(ValidationException e) {
            assertEquals(e.getMessage(), "Book out of stock");
        }
    }

    @Test
    public void tooShortBooksTitle() {
        Book book = new Book();
        book.setTitle("abc");
        book.setCost(32);
        book.setAmount(2);

        BooksService booksService = new BooksService(em);

        try {
            booksService.addBook(book);
            fail();
        } catch(ValidationException e) {
            assertEquals(e.getMessage(), "Book title is too short");
        }
    }

    @Test
    public void emptyBooksCost() {
        Book book = new Book();
        book.setTitle("abcdef");
        book.setAmount(2);

        BooksService booksService = new BooksService(em);

        try {
            booksService.addBook(book);
            fail();
        } catch(ValidationException e) {
            assertEquals(e.getMessage(), "Book has no cost");
        }
    }

    @Test
    public void emptyBooksAmount() {
        Book book = new Book();
        book.setTitle("abcdef");
        book.setCost(25);

        BooksService booksService = new BooksService(em);

        try {
            booksService.addBook(book);
            fail();
        } catch(ValidationException e) {
            assertEquals(e.getMessage(), "Book has no amount");
        }
    }

    @Test
    public void isBookValid() {
        Book book = new Book();
        book.setTitle("abcdef");
        book.setCost(34);
        book.setAmount(2);

        BooksService booksService = new BooksService(em);

        booksService.addBook(book);

        Mockito.verify(em, times(1)).persist(book);
    }

    @Test
    public void emptyOrdersBookList() {
        Order order = new Order();

        OrdersService ordersService = new OrdersService(em);

        try {
            ordersService.placeOrder(order);
            fail();
        } catch(ValidationException e) {
            assertEquals(e.getMessage(), "Empty order");
        }
    }

    @Test
    public void notFoundOrdersBook() {
        Order order = new Order();
        Book book = new Book(UUID.fromString("2c4fa53-2145-488f-a452-c4e5efd6fb95"));
        order.getBooks().add(book);

        OrdersService ordersService = new OrdersService(em);

        try {
            ordersService.placeOrder(order);
            fail();
        } catch(ValidationException e) {
            assertEquals(e.getMessage(), "Book not found");
        }
    }

    @Test
    public void emptyBookCost() {
        Order order = new Order();
        Book book = new Book();
        book.setTitle("abcdef");
        book.setAmount(2);
        order.getBooks().add(book);

        Mockito.when(em.find(Book.class, book.getId())).thenReturn(book);

        OrdersService ordersService = new OrdersService(em);

        try {
            ordersService.placeOrder(order);
            fail();
        } catch(ValidationException e) {
            assertEquals(e.getMessage(), "Book has no cost");
        }
    }

    @Test
    public void emptyBookAmount() {
        Order order = new Order();
        Book book = new Book();
        book.setTitle("abcdef");
        book.setCost(34);
        order.getBooks().add(book);

        Mockito.when(em.find(Book.class, book.getId())).thenReturn(book);

        OrdersService ordersService = new OrdersService(em);

        try {
            ordersService.placeOrder(order);
            fail();
        } catch(ValidationException e) {
            assertEquals(e.getMessage(), "Book has no amount");
        }
    }

    @Test
    public void isOrderValid() {
        Order order = new Order();
        Book book = new Book();
        book.setTitle("abcdef");
        book.setCost(34);
        book.setAmount(2);
        order.getBooks().add(book);

        Mockito.when(em.find(Book.class, book.getId())).thenReturn(book);

        OrdersService ordersService = new OrdersService(em);

        ordersService.placeOrder(order);

        Mockito.verify(em, times(1)).persist(order);
    }

    @Test
    public void whenOrderIsTooValuable() {
        Order order = new Order();
        Book book = new Book();
        book.setTitle("abcdef");
        book.setCost(71);
        book.setAmount(2);
        order.getBooks().add(book);
        order.getBooks().add(book);

        Mockito.when(em.find(Book.class, book.getId())).thenReturn(book);

        book = new Book();
        book.setTitle("abcdefgh");
        book.setCost(55);
        book.setAmount(3);
        order.getBooks().add(book);

        Mockito.when(em.find(Book.class, book.getId())).thenReturn(book);

        book = new Book();
        book.setTitle("fghdfs");
        book.setCost(49);
        book.setAmount(1);
        order.getBooks().add(book);

        Mockito.when(em.find(Book.class, book.getId())).thenReturn(book);

        OrdersService ordersService = new OrdersService(em);

        assertEquals(order.getBooks().size(), 4);

        try {
            ordersService.placeOrder(order);
            fail();
        } catch(ValidationException e) {
            assertEquals(e.getMessage(), "Order too valuable");
        }
    }

    @Test
    public void whenOrderedBookAvailable_placeOrderDecreasesAmountByOne() {
        //Arrange
        Order order = new Order();
        Book book = new Book();
        book.setAmount(1);
        book.setCost(38);
        order.getBooks().add(book);

        Mockito.when(em.find(Book.class, book.getId())).thenReturn(book);

        OrdersService ordersService = new OrdersService(em);

        //Act
        ordersService.placeOrder(order);

        //Assert
        //dostępna liczba książek zmniejszyła się:
        assertEquals(0, (int)book.getAmount());
        //nastąpiło dokładnie jedno wywołanie em.persist(order) w celu zapisania zamówienia:
        Mockito.verify(em, times(1)).persist(order);
    }
}
