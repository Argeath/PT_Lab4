package net.stawrul;

import net.stawrul.model.Book;
import net.stawrul.model.Order;
import net.stawrul.services.BooksService;
import net.stawrul.services.OrdersService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import javax.persistence.EntityManager;

import java.util.UUID;

import net.stawrul.services.exceptions.ValidationException;
import org.junit.Assert;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.times;

@RunWith(MockitoJUnitRunner.class)
public class OrdersServiceTest {

    @Mock
    private EntityManager em;

    @Test
    public void whenOrderedBookNotAvailable_throwsException() {
        Book book = new Book();
        book.setAmount(0);
        book.setCost(25);

        OrdersService ordersService = new OrdersService(em);

        try {
            ordersService.validateBookAvailable(book);
            fail();
        } catch(ValidationException e) {
            assertEquals(e.getMessage(), "Book out of stock");
        }
    }
    
    @Test
    public void whenOrderedBook_checkTotalValue() {
        Order order = new Order();
        Book book = new Book();
        book.setTitle("abcdef");
        book.setAmount(1);
        book.setCost(34);
        order.getBooks().add(book);

        Mockito.when(em.find(Book.class, book.getId())).thenReturn(book);
        
        book = new Book();
        book.setTitle("ghgfd");
        book.setAmount(1);
        book.setCost(61);
        order.getBooks().add(book);

        Mockito.when(em.find(Book.class, book.getId())).thenReturn(book);

        OrdersService ordersService = new OrdersService(em);

        ordersService.placeOrder(order);
        Assert.assertEquals(95, ordersService.getTotalValue(order));
    }
    
    @Test
    public void whenOrderedTwoBooks_placeOrderDecreasesAmountsByOne() {
        Order order = new Order();
        Book book1 = new Book();
        book1.setTitle("abcdef");
        book1.setAmount(2);
        book1.setCost(34);
        order.getBooks().add(book1);

        Mockito.when(em.find(Book.class, book1.getId())).thenReturn(book1);
        
        Book book2 = new Book();
        book2.setTitle("ghgfd");
        book2.setAmount(2);
        book2.setCost(61);
        order.getBooks().add(book2);

        Mockito.when(em.find(Book.class, book2.getId())).thenReturn(book2);

        OrdersService ordersService = new OrdersService(em);

        ordersService.placeOrder(order);
        
        Assert.assertTrue(book1.getAmount().equals(1));
        Assert.assertTrue(book2.getAmount().equals(1));
    }

    @Test
    public void whenOrderIsEmpty_throwsException() {
        Order order = new Order();

        OrdersService ordersService = new OrdersService(em);

        try {
            ordersService.validateOrderNotEmpty(order);
            fail();
        } catch(ValidationException e) {
            assertEquals(e.getMessage(), "Empty order");
        }
    }

    @Test
    public void whenOrderBookNotFound_throwsException() {
        Book book = new Book(UUID.fromString("2c4fa53-2145-488f-a452-c4e5efd6fb95"));

        OrdersService ordersService = new OrdersService(em);

        try {
            ordersService.validateBookNotNull(book);
            fail();
        } catch(ValidationException e) {
            assertEquals(e.getMessage(), "Book not found");
        }
    }

    @Test
    public void whenOrderedBookHasNoCost_throwsException() {
        Book book = new Book();
        book.setTitle("abcdef");
        book.setAmount(2);

        OrdersService ordersService = new OrdersService(em);

        try {
            ordersService.validateBookCostNotNull(book);
            fail();
        } catch(ValidationException e) {
            assertEquals(e.getMessage(), "Book has no cost");
        }
    }

    @Test
    public void whenOrderedBookHasNoAmount_throwsException() {
        Book book = new Book();
        book.setTitle("abcdef");
        book.setCost(34);

        OrdersService ordersService = new OrdersService(em);

        try {
            ordersService.validateBookAmountNotNull(book);
            fail();
        } catch(ValidationException e) {
            assertEquals(e.getMessage(), "Book has no amount");
        }
    }

    @Test
    public void whenOrderIsTooValuable_throwsException() {
        Order order = new Order();
        Book book = new Book();
        book.setTitle("abcdef");
        book.setCost(160);
        book.setAmount(2);
        order.getBooks().add(book);

        Mockito.when(em.find(Book.class, book.getId())).thenReturn(book);


        OrdersService ordersService = new OrdersService(em);

        assertEquals(order.getBooks().size(), 1);

        try {
            ordersService.validateOrderSumCost(order);
            fail();
        } catch(ValidationException e) {
            assertEquals(e.getMessage(), "Order too valuable");
        }
    }

    @Test
    public void whenOrderedBook_throwsNoException() {
        Order order = new Order();
        Book book = new Book();
        book.setTitle("abcdef");
        book.setCost(50);
        book.setAmount(4);
        order.getBooks().add(book);
        order.getBooks().add(book);
        order.getBooks().add(book);

        Mockito.when(em.find(Book.class, book.getId())).thenReturn(book);

        OrdersService ordersService = new OrdersService(em);

        ordersService.validateOrder(order);
    }
}
