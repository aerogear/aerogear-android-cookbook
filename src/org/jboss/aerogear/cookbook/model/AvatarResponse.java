package org.jboss.aerogear.cookbook.model;

import org.jboss.aerogear.android.RecordId;

import java.io.InputStream;

public class AvatarResponse implements Avatar<String> {

    public AvatarResponse() {
    }

    @RecordId
    private Long id;

    private String name;

    private String photo;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

}
