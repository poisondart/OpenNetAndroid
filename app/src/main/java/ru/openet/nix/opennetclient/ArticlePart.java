package ru.openet.nix.opennetclient;

import android.os.Parcel;
import android.os.Parcelable;

import io.realm.RealmObject;
import io.realm.annotations.Required;

/**
 * Created by Nix on 28.01.2018.
 */

public class ArticlePart extends RealmObject implements Parcelable{
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
    public static final int VIDEO_ITEM = 5;

    public static String ARTICLE_LINK = "mArticleLink";

    public ArticlePart() {
        //это для realm
    }

    private ArticlePart(Parcel in){
        mArticleLink = in.readString();
        mText = in.readString();
        mContentLink = in.readString();
        mType = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mArticleLink);
        parcel.writeString(mText);
        parcel.writeString(mContentLink);
        parcel.writeInt(mType);
    }
    public static final Parcelable.Creator<ArticlePart> CREATOR = new Parcelable.Creator<ArticlePart>() {
        public ArticlePart createFromParcel(Parcel in) {
            return new ArticlePart(in);
        }

        public ArticlePart[] newArray(int size) {
            return new ArticlePart[size];
        }
    };
    @Override
    public int describeContents() {
        return 0;
    }

    public ArticlePart(int type, String text, String link) {
        mType = type;
        mArticleLink = link;
        if(type == SIMPLE_TEXT || type == CODE || type == LIST_ITEM){
            mText = text;
            mContentLink = null;
        }else if(type == IMAGE || type == VIDEO_ITEM){
            mContentLink = text;
            mText = null;
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
    public void initVideoId(){
        mContentLink = mContentLink.substring(30, 41);
    }
}
