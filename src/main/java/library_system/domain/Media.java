package library_system.domain;

import com.fasterxml.jackson.annotation.*;

/**
 * Base class for media items stored in the library (Book, CD).
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "mediaType",
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = Book.class, name = "BOOK"),
        @JsonSubTypes.Type(value = CD.class, name = "CD")
})
public abstract class Media {

    protected String id;
    protected String title;

    /** Number of days the item may be borrowed. */
    @JsonProperty("borrowDuration")
    protected int borrowDuration;

    /** Available quantity for this media item. */
    @JsonProperty("quantity")
    protected int quantity = 1;

    protected Media() {
        this.id = java.util.UUID.randomUUID().toString();
        this.quantity = 1;
    }

    protected Media(String title) {
        this();
        this.title = title;
    }

    /**
     * ID is not persisted to JSON per requirements.
     */
    @JsonIgnore
    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }

    public void setTitle(String title) { this.title = title; }

    public int getBorrowDuration() {
        return borrowDuration;
    }

    public void setBorrowDuration(int borrowDuration) {
        this.borrowDuration = borrowDuration;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = Math.max(0, quantity);
    }

    @JsonProperty("mediaType")
    public String getMediaType() {
        return this instanceof Book ? "BOOK" :
                this instanceof CD ? "CD" : null;
    }

    @JsonProperty("mediaType")
    public void setMediaType(String mediaType) {
        // no-op for Jackson visibility
    }

    public abstract FineStrategy getFineStrategy();

    @Override
    public String toString() {
        String avail = (quantity > 0) ? ("Available (Qty: " + quantity + ")") : "Not Available (Qty: 0)";
        return title + " - " + avail + " | Borrow days: " + borrowDuration;
    }
}