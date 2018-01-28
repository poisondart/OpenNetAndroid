package ru.openet.nix.opennetclient;

import java.util.ArrayList;

/**
 * Created by Nix on 28.01.2018.
 */

public class Article {
    private String mDate;
    private String mTitle;
    private String mLink;
    private ArrayList<ArticlePart> mArticleParts;

    public Article(String date, String title, String link) {
        mDate = date;
        mTitle = title;
        mLink = link;
    }

    public String getDate() {
        return mDate;
    }

    public void setDate(String date) {
        mDate = date;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getLink() {
        return mLink;
    }

    public void setLink(String link) {
        mLink = link;
    }

    public ArrayList<ArticlePart> getArticleParts() {
        return mArticleParts;
    }

    public void setArticleParts(ArrayList<ArticlePart> articleParts) {
        mArticleParts = articleParts;
    }
}
