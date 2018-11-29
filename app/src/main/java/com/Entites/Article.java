package com.Entites;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by majdichaabene on 10/1/15.
 */
public class Article extends RealmObject {
    @PrimaryKey
    private String title;
    private String image;
    private String link;
    private String pubDate;
    private int category;

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getPubDate() {
        return pubDate;
    }

    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }

}
