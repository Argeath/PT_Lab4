package net.stawrul.services;

import net.stawrul.model.Book;
import net.stawrul.model.Order;
import net.stawrul.services.exceptions.ValidationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

/**
 * Komponent (serwis) biznesowy do realizacji operacji na książkach.
 */
@Service
public class BooksService extends EntityService<Book> {

    //Instancja klasy EntityManger zostanie dostarczona przez framework Spring
    //(wstrzykiwanie zależności przez konstruktor).
    public BooksService(EntityManager em) {

        //Book.class - klasa encyjna, na której będą wykonywane operacje
        //Book::getId - metoda klasy encyjnej do pobierania klucza głównego
        super(em, Book.class, Book::getId);
    }

    /**
     * Pobranie wszystkich książek z bazy danych.
     *
     * @return lista książek
     */
    public List<Book> findAll() {
        //pobranie listy wszystkich książek za pomocą zapytania nazwanego (ang. named query)
        //zapytanie jest zdefiniowane w klasie Book
        return em.createNamedQuery(Book.FIND_ALL, Book.class).getResultList();
    }

    @Transactional
    public void addBook(Book book) {
        validate(book);
        save(book);
    }

    private void validate(Book book) {
        if(book.getTitle() == null || book.getTitle().length() < 5)
            throw new ValidationException("Book title is too short");

        if(book.getCost() == null || book.getCost() < 0)
            throw new ValidationException("Book has no cost");

        if(book.getAmount() == null || book.getAmount() < 0)
            throw new ValidationException("Book has no amount");
    }

}
