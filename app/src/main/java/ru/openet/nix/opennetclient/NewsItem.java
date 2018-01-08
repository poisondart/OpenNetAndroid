package ru.openet.nix.opennetclient;

/**
 * Created by Nix on 07.01.2018.
 */

public class NewsItem {
    private String mDate;
    private String mTitle;
    private String mDescr;
    private String mLink;

    public NewsItem() {
    }

    public NewsItem(String date, String title, String descr, String link) {
        mDate = date;
        mTitle = title;
        mDescr = descr;
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

    public String getDescr() {
        return mDescr;
    }

    public void setDescr(String descr) {
        mDescr = descr;
    }

    public String getLink() {
        return mLink;
    }

    public void setLink(String link) {
        mLink = link;
    }
}
