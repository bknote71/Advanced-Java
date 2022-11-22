package di;

public class Book {
    public static Long BOOK_ID = 0L;
    Long id;
    String name;
    Integer price;

    public Book(String name, Integer price) {
        this.id = BOOK_ID++;
        this.name = name;
        this.price = price;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getPrice() {
        return price;
    }
}
