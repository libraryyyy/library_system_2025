package library_system.domain;

public abstract class Media {
    protected String title;
    protected boolean borrowed;

    public Media(String title) {
        this.title = title;
        this.borrowed = false;
    }

    public String getTitle() { return title; }

    public boolean isBorrowed() { return borrowed; }

    public void setBorrowed(boolean borrowed) { this.borrowed = borrowed; }

    public abstract int getBorrowDuration();

    public abstract FineStrategy getFineStrategy();
}
