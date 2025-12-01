package library_system.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * @author sana
 * @version 1.0
 */
@JsonTypeName("CD")
public class CD extends Media {

    /** The artist or band associated with this CD. */
    private String artist;

    /**
     * Default constructor required for JSON serialization/deserialization.
     */
    public CD() {
        super();
    }

    /**
     * Constructs a new CD with the given metadata.
     *
     * @param title  the title of the CD
     * @param artist the artist or band name
     */
    public CD(String title, String artist) {
        super(title);
        this.artist = artist;
    }

    /**
     * @return the CD's artist or band name
     */
    public String getArtist() {
        return artist;
    }

    /**
     * Updates the CD's artist (used by JSON deserialization).
     *
     * @param artist new artist name
     */
    public void setArtist(String artist) {
        this.artist = artist;
    }

    /**
     * {@inheritDoc}
     * CDs can be borrowed for 7 days.
     *
     * @return borrow duration in days
     */
    @Override
    public int getBorrowDuration() {
        return 7;
    }

    /**
     * Returns the fine calculation strategy for CDs.
     * CDs use {@link CDFineStrategy} which sets the fine rate to 20 NIS/day.
     *
     * @return CDFineStrategy instance
     */
    @Override
    @JsonIgnore
    public FineStrategy getFineStrategy() {
        return new CDFineStrategy();
    }

    /**
     * @return a readable string representation of the CD
     */
    @Override
    public String toString() {
        return "CD: " + getTitle() + " (" + artist + ")";
    }
}
