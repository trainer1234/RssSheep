package com.example.sss.rsssheep;

import java.util.Date;

/**
 * Created by SSS on 23/04/2017.
 */

public class PostData {
    int id;
    String postThumbUrl, postTitle, postLink, postDate, postContent, postRSSGroup;
    String postLife;
    boolean bookmark;

    public PostData(){

    }

    public PostData(String postTitle, String postDate, String postContent, String postLink, String postRSSGroup, boolean bookmark, String postLife){
        this.postTitle = postTitle;
        this.postLink = postLink;
        this.postDate = postDate;
        this.postContent = postContent;
        this.bookmark = bookmark;
        this.postLife = postLife;
    }

    public PostData(String postTitle, String postDate, String postContent, String postLink, String postRSSGroup, String postThumbUrl, boolean bookmark, String postLife) {
        this.postThumbUrl = postThumbUrl;
        this.postTitle = postTitle;
        this.postLink = postLink;
        this.postDate = postDate;
        this.postContent = postContent;
        this.bookmark = bookmark;
        this.postLife = postLife;

    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPostThumbUrl() {
        return this.postThumbUrl;
    }

    public void setPostThumbUrl(String postThumbUrl) {
        this.postThumbUrl = postThumbUrl;
    }

    public String getPostTitle() {
        return this.postTitle;
    }

    public void setPostTitle(String postTitle) {
        this.postTitle = postTitle;
    }

    public String getPostLink() {
        return this.postLink;
    }

    public void setPostLink(String postLink) {
        this.postLink = postLink;
    }

    public String getPostDate() {
        return this.postDate;
    }

    public void setPostDate(String postDate) {
        this.postDate = postDate;
    }

    public String getPostContent() {
        return this.postContent;
    }

    public void setPostContent(String postContent) {
        this.postContent = postContent;
    }

    public boolean isBookmark() {
        return this.bookmark;
    }

    public void setBookmark(boolean mark) {
        this.bookmark = mark;
    }

    public String getPostRSSGroup() {
        return this.postRSSGroup;
    }

    public void setPostRSSGroup(String postRSSGroup) {
        this.postRSSGroup = postRSSGroup;
    }

    public String getPostLife() {
        return this.postLife;
    }

    public void setPostLife(String postLife) {
        this.postLife = postLife;
    }
}

