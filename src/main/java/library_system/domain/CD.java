package library_system.domain;

public class CD extends Media {
    private String artist;

    public CD(String title, String artist) {
        super(title);
        this.artist = artist;
    }

    public String getArtist() { return artist; }

    @Override
    public int getBorrowDuration() {
        return 7;
    }

    @Override
    public FineStrategy getFineStrategy() {
        return new CDFineStrategy();
    }
}
