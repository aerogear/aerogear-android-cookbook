package org.jboss.aerogear.cookbook.model;

import org.jboss.aerogear.android.RecordId;

import java.io.InputStream;

public class AvatarRequest implements Avatar<InputStream>{

    public AvatarRequest() {
    }

    @RecordId
    private Long id;

    private String name;

    private InputStream photo;

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

    public InputStream getPhoto() {
        return photo;
    }

    public void setPhoto(InputStream photo) {
        this.photo = photo;
    }

}
