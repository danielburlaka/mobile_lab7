package ua.kpi.comsys.io8303.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "books")
public class BookEntity {
    @NonNull
    @PrimaryKey(autoGenerate = true)
    public int uid;

    @ColumnInfo(name = "search")
    private String search;

    @ColumnInfo(name = "title")
    private String Title;

    @ColumnInfo(name = "subtitle")
    private String Subtitle;

    @ColumnInfo(name = "isbn13")
    private String isbn13;

    @ColumnInfo(name = "price")
    private String Price;

    @ColumnInfo(name = "image")
    private String Image;

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getSubtitle() {
        return Subtitle;
    }

    public void setSubtitle(String subtitle) {
        Subtitle = subtitle;
    }

    public String getIsbn13() {
        return isbn13;
    }

    public void setIsbn13(String isbn13) {
        this.isbn13 = isbn13;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String type) {
        Price = type;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public BookItem toItem() {
        BookItem result = new BookItem();
        result.setTitle(Title);
        result.setSubtitle(Subtitle);
        result.setPrice(Price);
        result.setIsbn13(isbn13);
        result.setImage(Image);
        return result;
    }

    public String toString() {
        return "{\"search\":\"" + search + "\"," +
                "\"title\":\"" + Title + "\"," +
                "\"subtitle\":\"" + Subtitle + "\"," +
                "\"isbn13\": \""+ isbn13 + "\"," +
                "\"price\":\"" + Price + "\"," +
                "\"image\":\"" + Image + "\"}";
    }
}
