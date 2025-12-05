package library_system.domain;

import com.fasterxml.jackson.annotation.*;

@JsonTypeName("CD")
public class CD extends Media {

    private String artist;

    public CD() {
        super();
        this.borrowDuration = 7;  // CD تُعار 7 أيام
    }

    public CD(String title, String artist) {
        super(title);
        this.artist = artist;
        this.borrowDuration = 7;
    }

    public String getArtist() { return artist; }
    public void setArtist(String artist) { this.artist = artist; }

    @Override
    @JsonIgnore  // ← هذا السطر اللي هيحل كل المشكلة إن شاء الله
    public FineStrategy getFineStrategy() {
        return new CDFineStrategy();
    }

    @Override
    public String toString() {
        return "CD: " + getTitle() + " - " + artist +
                " | مدة الإعارة: 7 أيام" +
                (isBorrowed() ? " [معار]" : " [متوفر]");
    }
}