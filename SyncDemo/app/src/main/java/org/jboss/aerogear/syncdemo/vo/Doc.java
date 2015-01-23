package org.jboss.aerogear.syncdemo.vo;

import org.jboss.aerogear.android.core.RecordId;

import java.io.Serializable;

public class Doc implements Serializable {
    @RecordId
    private Long id;
    
    private String docId = "";
    private String userId = "";
    private boolean shared = false;
    private String docName = "";

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isShared() {
        return shared;
    }

    public void setShared(boolean shared) {
        this.shared = shared;
    }

    public String getDocName() {
        return docName;
    }

    public void setDocName(String docName) {
        this.docName = docName;
    }
    
}
