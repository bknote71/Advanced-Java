package di.service;

import di.Book;
import di.repository.BookRepository;
import di.annotation.Inject;
import di.annotation.MyService;

@MyService
public class BookService {
    private final BookRepository bookRepository;

    @Inject
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public Long create(String name, Integer price) {
        Book book = new Book(name, price);
        return bookRepository.save(book);
    }

    public Book findById(Long id) {
        return bookRepository.findById(id);
    }
}
