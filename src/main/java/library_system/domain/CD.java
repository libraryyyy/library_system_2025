package library_system.domain;

import com.fasterxml.jackson.annotation.*;

@JsonTypeName("CD")
public class CD extends Media {

    private String artist;

    public CD() {
        super();
        this.borrowDuration = 7;  // CDs are loaned for 7 days
    }

    public CD(String title, String artist) {
        super(title);
        this.artist = artist;
        this.borrowDuration = 7;
    }

    public String getArtist() { return artist; }
    public void setArtist(String artist) { this.artist = artist; }

    @JsonProperty("quantity")
    public int getQuantity() { return super.getQuantity(); }

    @JsonProperty("quantity")
    public void setQuantity(int q) { super.setQuantity(q); }

    @Override
    @JsonIgnore  // Prevents fine strategy from being serialized
    public FineStrategy getFineStrategy() {
        return new CDFineStrategy();
    }

    @Override
    public String toString() {
        return "CD: " + getTitle() + " - " + artist +
                " | Borrow Time: 7 Days" +
                (getQuantity() > 0 ? " [Available]" : " [Not Available]");
    }
}