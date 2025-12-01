package library_system.domain;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * @author sana
 * @version 1.0
 */

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "mediaType"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = Book.class, name = "BOOK"),
        @JsonSubTypes.Type(value = CD.class,  name = "CD")
})
public abstract class Media {

    /** Title of the media item (book title or CD title). */
    protected String title;

    /** Whether the media is currently borrowed. */
    protected boolean borrowed;

    /**
     * Default constructor for JSON serialization/deserialization.
     * Required by Jackson.
     */
    protected Media() {
        // empty
    }

    /**
     * Constructs a media item with a title.
     *
     * @param title title of the media
     */
    protected Media(String title) {
        this.title = title;
        this.borrowed = false;
    }

    /**
     * @return the title of the media item
     */
    public String getTitle() {
        return title;
    }

    /**
     * Updates the media title (used by JSON).
     *
     * @param title new title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return true if the media is currently borrowed
     */
    public boolean isBorrowed() {
        return borrowed;
    }

    /**
     * Sets the borrowed status.
     *
     * @param borrowed true → currently borrowed
     */
    public void setBorrowed(boolean borrowed) {
        this.borrowed = borrowed;
    }

    /**
     * @return number of days this media type can be borrowed
     */
    public abstract int getBorrowDuration();

    /**
     * @return the fine calculation strategy used by this media type
     */
    public abstract FineStrategy getFineStrategy();

    /**
     * @return readable string representation of the media item
     */
    @Override
    public String toString() {
        return getClass().getSimpleName() + ": " + title;
    }
}
