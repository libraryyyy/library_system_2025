package library_system.domain;

import com.fasterxml.jackson.annotation.*;

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
    protected boolean borrowed;

    protected Media() {}

    protected Media(String title) {
        this.id = java.util.UUID.randomUUID().toString();
        this.title = title;
        this.borrowed = false;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public boolean isBorrowed() { return borrowed; }
    public void setBorrowed(boolean borrowed) { this.borrowed = borrowed; }

    @JsonProperty("mediaType")
    public String getMediaType() {
        return this instanceof Book ? "BOOK" :
                this instanceof CD ? "CD" : null;
    }

    @JsonProperty("mediaType")
    public void setMediaType(String mediaType) {
        // Jackson uses this for deserialization
    }

    public abstract int getBorrowDuration();
    public abstract FineStrategy getFineStrategy();

    @Override
    public String toString() {
        return title + " (ID: " + id + ") - " + (borrowed ? "Borrowed" : "Available");
    }
}