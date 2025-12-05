package library_system.domain;

import com.fasterxml.jackson.annotation.*;


@JsonTypeName("CD")
public class CD extends Media {

    private String artist;

    public CD() { super(); }

    public CD(String title, String artist) {
        super(title);
        this.artist = artist;
    }

    public String getArtist() { return artist; }
    public void setArtist(String artist) { this.artist = artist; }

    @Override
    public int getBorrowDuration() {
        return 7;
    }

    @Override
    @JsonIgnore
    public FineStrategy getFineStrategy() {
        return new CDFineStrategy();
    }
}