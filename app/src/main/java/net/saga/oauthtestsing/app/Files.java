package net.saga.oauthtestsing.app;

import org.jboss.aerogear.android.RecordId;

import java.net.URL;

public class Files {

    @RecordId
    private String id;
    private String title;
    private String webContentLink;
    private String iconLink;

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

    public String getWebContentLink() {
        return webContentLink;
    }

    public void setWebContentLink(String webContentLink) {
        this.webContentLink = webContentLink;
    }

    public String getIconLink() {
        return iconLink;
    }

    public void setIconLink(String iconLink) {
        this.iconLink = iconLink;
    }

    @Override
    public String toString() {
        return "Files{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", webContentLink='" + webContentLink + '\'' +
                '}';
    }

}
