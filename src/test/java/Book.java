import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * @author shijiawei
 * @version Book.java, v 0.1
 * @date 2018/12/1
 */
public class Book {
    private static int id = 0;

    public static Book getOneBook() {
        Book book = new Book();
        book.setBookId(++id);
        book.setAuthor("施jw"+id);
        book.setBookName("name"+id);
        return book;
    }

    private int bookId;
    private String bookName;
    private String author;

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }


    @Override
    public String toString() {
        return ReflectionToStringBuilder.reflectionToString(this,
                ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
