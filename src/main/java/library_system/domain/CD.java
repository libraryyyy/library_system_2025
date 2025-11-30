package library_system.domain;

public class CD extends Media {
    private String artist;
    public CD() {
        super();
    }

    /**
     * Creates a new CD.
     *
     * @param title  CD title.
     * @param artist artist or band name.
     */
    public CD(String title, String artist) {
        super(title);
        this.artist = artist;
    }
    /**
     * @return artist or band name.
     */
    public String getArtist() { return artist; }
    /**
     * Sets the artist (for JSON).
     */
    public void setArtist(String artist) {
        this.artist = artist;
    }
    /**
     * CDs can be borrowed for 7 days.
     */
    @Override
    public int getBorrowDuration() {
        return 7;
    }

    @Override
    public FineStrategy getFineStrategy() {
        return new CDFineStrategy();
    }
    @Override
    public String toString() {
        return "CD: " + title + " (" + artist + ")";
    }

}
