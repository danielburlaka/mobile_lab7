package ua.kpi.comsys.io8303.model;
public class BookItem {

    private String title;
    private String subtitle;
    private String isbn13;
    private String price;
    private String image;

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public String getIsbn13() {
        return isbn13;
    }

    public String getPrice() {
        return price;
    }

    public String getImage() {
        return image;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public void setIsbn13(String isbn13) {
        this.isbn13 = isbn13;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String toString() {
        return "{\"title\":\"" + title + "\"," +
                "\"subtitle\":\"" + subtitle + "\"," +
                "\"isbn13\": \""+ isbn13 + "\"," +
                "\"price\":\"" + price + "\"," +
                "\"image\":\"" + image + "\"}";
    }

    public BookEntity toEntity(String search) {
        BookEntity result = new BookEntity();
        result.setSearch(search);
        result.setTitle(title);
        result.setSubtitle(subtitle);
        result.setIsbn13(isbn13);
        result.setImage(image);
        result.setPrice(price);
        System.out.println("Converting to entity " + search);
        return result;
    }
}
