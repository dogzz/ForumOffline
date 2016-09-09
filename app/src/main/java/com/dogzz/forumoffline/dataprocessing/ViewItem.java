/*
* @Author: dogzz
* @Created: 9/7/2016
*/

package com.dogzz.forumoffline.dataprocessing;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class ViewItem extends RealmObject {
    private String text;
    @PrimaryKey
    private String url;
    private int type;
    private int lastPage;
    private boolean isRead;
    private boolean isFavorite;
    private int firstSavedPage;
    private int lastSavedPage;

    public ViewItem(String text, String url, ViewItemType type) {
        this.text = text;
        this.url = url;
        this.type = type.getTypeId();
    }

    public ViewItem() {}

    public ViewItemType getType() {
        return ViewItemType.fromInt(type);
    }

    public void setType(ViewItemType type) {
        this.type = type.getTypeId();
    }

    public String getLastPage() {
        return String.valueOf(lastPage);
    }

    public void setLastPage(String lastPage) {
        try {
            this.lastPage = Integer.valueOf(lastPage);
        } catch (Exception ex) {
            this.lastPage = 1;
        }
    }

    public String getText() {
        return text;
    }

    public String getUrl() {
        return url;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setLastPage(int lastPage) {
        this.lastPage = lastPage;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public int getFirstSavedPage() {
        return firstSavedPage;
    }

    public void setFirstSavedPage(int firstSavedPage) {
        this.firstSavedPage = firstSavedPage;
    }

    public int getLastSavedPage() {
        return lastSavedPage;
    }

    public void setLastSavedPage(int lastSavedPage) {
        this.lastSavedPage = lastSavedPage;
    }
}
