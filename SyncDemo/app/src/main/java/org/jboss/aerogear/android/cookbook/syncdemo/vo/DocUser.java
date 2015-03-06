
package org.jboss.aerogear.android.cookbook.syncdemo.vo;

import org.jboss.aerogear.android.core.RecordId;

import java.io.Serializable;

public class DocUser implements Serializable {

    @RecordId
    private Long id;

    private String userId = "";

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "DocUser{" +
                "id=" + id +
                ", userId='" + userId + '\'' +
                '}';
    }

}
