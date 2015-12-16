package it.jaschke.alexandria.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author igiagante on 6/11/15.
 */
public class Book implements Parcelable {

    private String id;
    private String title;
    private String subtitle;
    private String thumbnail;
    private String description;
    private String authors;
    private String categories;
    private boolean favorite;

    public Book() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAuthors() {
        return authors;
    }

    public void setAuthors(String authors) {
        this.authors = authors;
    }

    public String getCategories() {
        return categories;
    }

    public void setCategories(String categories) {
        this.categories = categories;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int i) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(subtitle);
        dest.writeString(thumbnail);
        dest.writeString(description);
        dest.writeString(authors);
        dest.writeString(categories);
        dest.writeInt(favorite ? 1 : 0);
    }

    public static final Parcelable.Creator<Book> CREATOR = new Parcelable.Creator<Book>() {
        public Book createFromParcel(Parcel in) {
            return new Book(in);
        }

        public Book[] newArray(int size) {
            return new Book[size];
        }
    };

    private Book(Parcel in) {
        id = in.readString();
        title = in.readString();
        subtitle = in.readString();
        thumbnail = in.readString();
        description = in.readString();
        authors = in.readString();
        categories = in.readString();
        favorite = in.readInt() == 1;
    }
}
