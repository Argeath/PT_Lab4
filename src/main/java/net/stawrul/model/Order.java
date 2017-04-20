package net.stawrul.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.TemporalType.TIMESTAMP;

/**
 * Klasa encyjna reprezentująca zamówienie w sklepie.
 */
@Entity
@Table(name = "orders")
@EqualsAndHashCode(of = "id")
public class Order {

    @Getter
    @Id
    UUID id = UUID.randomUUID();

    @Getter
    @ManyToMany(cascade = {MERGE})
    List<Book> books = new ArrayList<>();

    @Getter
    @Temporal(TIMESTAMP)
    Date creationDate;

    /**
     * Ustawienie pola creationDate na aktualny czas w chwili zapisu zamówienia
     * do bazy danych.
     */
    @PrePersist
    public void prePersist(){
        this.creationDate = new Date();
    }
}
