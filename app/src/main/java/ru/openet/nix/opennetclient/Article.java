package ru.openet.nix.opennetclient;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by Nix on 28.01.2018.
 */

public class Article extends RealmObject{
    @Required
    private String mDate;
    @PrimaryKey
    @Required
    private String mTitle;
    @Required
    private String mLink;

    public static String LINK = "mLink";

    public Article() {
        // это для realm
    }
    public Article(Article article){
        mDate = article.getDate();
        mTitle = article.getTitle();
        mLink = article.getLink();
    }
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

}
