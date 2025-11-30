package library_system.domain;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;


/**
 * Abstract base class for all borrowable media in the library.
 * Used for polymorphism:
 * - {@link Book}
 * - {@link CD}
 */

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "mediaType"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = Book.class, name = "BOOK"),
        @JsonSubTypes.Type(value = CD.class, name = "CD")
})

public abstract class Media {
    /** Media title (book title or CD title). */
    protected String title;
    protected boolean borrowed;

    protected Media() {
    }

    protected Media(String title) {
        this.title = title;
        this.borrowed = false;
    }

    /**
     * @return media title.
     */
    public String getTitle() { return title; }
    public void setTitle(String title) {
        this.title = title;
    }
    /**
     * @return true if currently borrowed.
     */
    public boolean isBorrowed() { return borrowed; }
    /**
     * Sets borrowed flag.
     *
     * @param borrowed boolean flag.
     */
    public void setBorrowed(boolean borrowed) { this.borrowed = borrowed; }
    /**
     * @return how many days this media can be borrowed.
     */
    public abstract int getBorrowDuration();
    /**
     * @return the fine calculation strategy for this media type.
     */
    public abstract FineStrategy getFineStrategy();
    @Override
    public String toString() {
        return this.getClass().getSimpleName() + ": " + title;
    }


}
