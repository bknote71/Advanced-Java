package optional;

import java.util.Optional;

public class OnlineBook {
    private String name;
    private Book book;

    public OnlineBook(String name) {
        this.name = name;
    }

    public OnlineBook(String name, Book book) {
        this.name = name;
        this.book = book;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public Optional<Book> optionalOfBook() {
        return Optional.of(book);
    }

    public Optional<Book> optionalOfNullableBook() {
        return Optional.ofNullable(book);
    }


}
