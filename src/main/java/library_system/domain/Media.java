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

    // هذا الحقل ضروري جدًا عشان Jackson يحفظ ويحمل الـ borrowDuration
    @JsonProperty("borrowDuration")
    protected int borrowDuration;

    protected Media() {
        this.id = java.util.UUID.randomUUID().toString();
        this.borrowed = false;
    }

    protected Media(String title) {
        this();
        this.title = title;
    }

    // ====== Getters & Setters ======

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public boolean isBorrowed() { return borrowed; }
    public void setBorrowed(boolean borrowed) { this.borrowed = borrowed; }

    // مهم جدًا: Jackson يستخدمه لحفظ وقراءة الـ borrowDuration من الـ JSON
    public int getBorrowDuration() {
        return borrowDuration;
    }

    public void setBorrowDuration(int borrowDuration) {
        this.borrowDuration = borrowDuration;
    }

    @JsonProperty("mediaType")
    public String getMediaType() {
        return this instanceof Book ? "BOOK" :
                this instanceof CD ? "CD" : null;
    }

    @JsonProperty("mediaType")
    public void setMediaType(String mediaType) {
        // لا نحتاج نعمل شيء هنا، Jackson يستخدمه للـ deserialization فقط
    }

    // هذه الدالتين يجب أن تُعرّف في Book و CD
    public abstract FineStrategy getFineStrategy();

    @Override
    public String toString() {
        return title + " (ID: " + id + ") - " +
                (borrowed ? "معار" : "متوفر") +
                " | مدة الإعارة: " + borrowDuration + " يوم";
    }
}