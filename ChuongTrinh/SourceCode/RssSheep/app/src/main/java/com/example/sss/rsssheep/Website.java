package com.example.sss.rsssheep;

/**
 * Created by SSS on 30/04/2017.
 */

public class Website {
    int id;
    String title, rssLink, group;

    public Website(){

    }

    public Website(String title, String group, String rssLink) {
        this.title = title;
        this.rssLink = rssLink;
        this.group = group;
    }

    public String getGroup() {
        return this.group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public int getId() {

        return this.id;

    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRssLink() {
        return this.rssLink;
    }

    public void setRssLink(String rssLink) {
        this.rssLink = rssLink;
    }
}
