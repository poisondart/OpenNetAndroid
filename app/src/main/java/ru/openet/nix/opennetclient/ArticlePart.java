package ru.openet.nix.opennetclient;

import io.realm.RealmObject;
import io.realm.annotations.Required;

/**
 * Created by Nix on 28.01.2018.
 */

public class ArticlePart extends RealmObject{
    @Required
    private String mArticleLink;
    private String mText;
    private String mContentLink;
    private int mType;

    public static final int SIMPLE_TEXT = 0;
    public static final int IMAGE = 1;
    public static final int CODE = 2;
    public static final int LIST_ITEM = 3;
    public static final int ETRA_LINKS_ITEM = 4;

    public static String ARTICLE_LINK = "mArticleLink";

    public ArticlePart() {
        //это для realm
    }

    public ArticlePart(int type, String text, String link) {
        mType = type;
        mArticleLink = link;
        if(type == SIMPLE_TEXT || type == CODE){
            mText = text;
            //mCode = null;
            mContentLink = null;
        }else if(type == IMAGE){
            mContentLink = text;
            //mCode = null;
            mText = null;
        }else if(type == LIST_ITEM){
            mText = text;
            //mCode = null;
            mContentLink = null;
        }
    }

    public ArticlePart(int type, String text, String contentLink, String link){
        mType = type;
        mText = text;
        mContentLink = contentLink;
        mArticleLink = link;
    }

    public String getText() {
        return mText;
    }

    public void setText(String text) {
        mText = text;
    }

    public int getType() {
        return mType;
    }

    public void setType(int type) {
        mType = type;
    }

    public String getContentLink() {
        return mContentLink;
    }

    public void setContentLink(String contentLink) {
        mContentLink = contentLink;
    }

    public String getArticleLink() {
        return mArticleLink;
    }
}
