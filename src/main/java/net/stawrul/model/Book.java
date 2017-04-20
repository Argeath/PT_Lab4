package net.stawrul.model;

import java.time.LocalDate;
import java.util.Date;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.UUID;
import net.stawrul.utils.Oprawa;
import org.springframework.beans.factory.annotation.Required;

/**
 * Klasa encyjna reprezentująca towar w sklepie (książkę).
 */
@Entity
@EqualsAndHashCode(of = "id")
@NamedQueries(value = {
        @NamedQuery(name = Book.FIND_ALL, query = "SELECT b FROM Book b")
})
public class Book {
    public static final String FIND_ALL = "Book.FIND_ALL";

    @Getter
    @Id
    UUID id = UUID.randomUUID();

    @Getter
    @Setter
    String title;

    @Getter
    @Setter
    Integer amount;

    @Getter
    @Setter
    Integer cost;

    @Getter
    @Setter
    Date date;

    @Enumerated(EnumType.STRING)
    @Getter
    @Setter
    Oprawa oprawa;

    public Book() {}

    public Book(UUID uid) {
        id = uid;
    }
}
