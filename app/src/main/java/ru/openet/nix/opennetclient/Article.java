package ru.openet.nix.opennetclient;

import java.util.ArrayList;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.annotations.Ignore;
import io.realm.annotations.Required;

/**
 * Created by Nix on 28.01.2018.
 */

public class Article extends RealmObject{
    @Required
    private String mDate;
    @Required
    private String mTitle;
    @Required
    private String mLink;
    @Ignore
    private ArrayList<ArticlePart> mArticleParts;

    public static String LINK = "mLink";

    public Article() {
        // это для realm
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

    public ArrayList<ArticlePart> getArticleParts() {
        return mArticleParts;
    }

    public void setArticleParts(ArrayList<ArticlePart> articleParts) {
        mArticleParts = articleParts;
    }
}
