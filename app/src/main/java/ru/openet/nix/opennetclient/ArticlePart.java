package ru.openet.nix.opennetclient;


/**
 * Created by Nix on 28.01.2018.
 */

public class ArticlePart {
    private String mText;
    //private String mCode;
    private String mContentLink;
    private int mType;

    public static final int SIMPLE_TEXT = 0;
    public static final int IMAGE = 1;
    public static final int CODE = 2;
    public static final int LIST_ITEM = 3;
    public static final int ETRA_LINKS_ITEM = 4;

    public ArticlePart(int type, String text) {
        mType = type;
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

    public ArticlePart(int type, String text, String contentLink){
        mType = type;
        mText = text;
        mContentLink = contentLink;
    }

    public String getText() {
        return mText;
    }

    public void setText(String text) {
        mText = text;
    }

    /*public String getCode() {
        return mCode;
    }*/

    /*public void setCode(String code) {
        mCode = code;
    }*/

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
}