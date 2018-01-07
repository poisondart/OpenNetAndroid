package ru.openet.nix.opennetclient;

/**
 * Created by Nix on 07.01.2018.
 */

public class NewsItem {
    private String mDate;
    private String mTitle;
    private String mDescr;

    public NewsItem() {
    }

    public NewsItem(String date, String title, String descr) {
        mDate = date;
        mTitle = title;
        mDescr = descr;
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
}
