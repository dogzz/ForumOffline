/*
* @Author: dogzz
* @Created: 9/7/2016
*/

package com.dogzz.forumoffline.dataprocessing;

public class ViewItem {
    String text;
    String url;
    ViewItemType type;

    public ViewItem(String text, String url, ViewItemType type) {
        this.text = text;
        this.url = url;
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public String getUrl() {
        return url;
    }

    public ViewItemType getType() {
        return type;
    }
}
