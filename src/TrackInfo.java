/**
 * Created by FORESTER on 20.03.18.
 */
public class TrackInfo {

    private String title;
    private String author;

    public TrackInfo(String author, String title) {
        this.title = title;
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }
}
