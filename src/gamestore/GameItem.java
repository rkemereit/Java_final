package gamestore;

import java.io.Serializable;
import java.util.Date;

public class GameItem implements Serializable {
    private static final long serialVersionUID = 1L;

    private int itemID;
    private String title;
    private String publisher;
    private Date releaseDate;
    private double retailPrice;
    private GamePlatform platform;
    private GameGenre genre;
    private boolean isRetro;

    public GameItem(int itemID, String title, String publisher, Date releaseDate, 
                   double retailPrice, GamePlatform platform, 
                   GameGenre genre, boolean isRetro) {
        if (itemID <= 0) throw new IllegalArgumentException("Item ID must be positive");
        if (title == null || title.trim().isEmpty()) throw new IllegalArgumentException("Title cannot be null or empty");
        if (publisher == null || publisher.trim().isEmpty()) throw new IllegalArgumentException("Publisher cannot be null or empty");
        if (releaseDate == null) throw new IllegalArgumentException("Release date cannot be null");
        if (retailPrice < 0) throw new IllegalArgumentException("Retail price cannot be negative");
        if (platform == null) throw new IllegalArgumentException("Platform cannot be null");
        if (genre == null) throw new IllegalArgumentException("Genre cannot be null");

        this.itemID = itemID;
        this.title = title;
        this.publisher = publisher;
        this.releaseDate = new Date(releaseDate.getTime()); // Defensive copy
        this.retailPrice = retailPrice;
        this.platform = platform;
        this.genre = genre;
        this.isRetro = isRetro;
    }

    // Copy constructor
    public GameItem(GameItem other) {
        this.itemID = other.itemID;
        this.title = other.title;
        this.publisher = other.publisher;
        this.releaseDate = new Date(other.releaseDate.getTime()); // Deep copy of Date
        this.retailPrice = other.retailPrice;
        this.platform = other.platform; // Enum is immutable
        this.genre = other.genre; // Enum is immutable
        this.isRetro = other.isRetro;
    }

    // Getters and Setters
    public int getItemID() { return itemID; }
    public void setItemID(int itemID) {
        if (itemID <= 0) {
            throw new IllegalArgumentException("Item ID must be positive");
        }
        this.itemID = itemID;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be null or empty");
        }
        this.title = title;
    }

    public String getPublisher() { return publisher; }
    public void setPublisher(String publisher) {
        if (publisher == null || publisher.trim().isEmpty()) {
            throw new IllegalArgumentException("Publisher cannot be null or empty");
        }
        this.publisher = publisher;
    }

    public Date getReleaseDate() { return releaseDate; }
    public void setReleaseDate(Date releaseDate) {
        if (releaseDate == null) {
            throw new IllegalArgumentException("Release date cannot be null");
        }
        this.releaseDate = releaseDate;
    }

    public double getRetailPrice() { return retailPrice; }
    public void setRetailPrice(double retailPrice) {
        if (retailPrice < 0) {
            throw new IllegalArgumentException("Retail price cannot be negative");
        }
        this.retailPrice = retailPrice;
    }

    public GamePlatform getPlatform() { return platform; }
    public void setPlatform(GamePlatform platform) {
        if (platform == null) {
            throw new IllegalArgumentException("Platform cannot be null");
        }
        this.platform = platform;
    }

    public GameGenre getGenre() { return genre; }
    public void setGenre(GameGenre genre) {
        if (genre == null) {
            throw new IllegalArgumentException("Genre cannot be null");
        }
        this.genre = genre;
    }

    public boolean isRetro() { return isRetro; }
    public void setRetro(boolean isRetro) { this.isRetro = isRetro; }

    @Override
    public String toString() {
        return String.format("%s (%s) - %s - $%.2f", title, platform, genre, retailPrice);
    }
} 