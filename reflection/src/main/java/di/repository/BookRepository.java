package di.repository;

import di.Book;
import di.annotation.MyRepository;

import java.util.HashMap;
import java.util.Map;

@MyRepository
public class BookRepository {

    private Map<Long, Book> map = new HashMap<>();

    public Long save(Book book) {
        map.put(book.getId(), book);
        return book.getId();
    }

    public Book findById(Long id) {
        return map.get(id);
    }
}
