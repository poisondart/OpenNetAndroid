package ru.openet.nix.opennetclient;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Nix on 07.01.2018.
 */

public class NewsItem implements Parcelable{
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
    private NewsItem(Parcel in){
        mDate = in.readString();
        mTitle = in.readString();
        mDescr = in.readString();
        mLink = in.readString();
    }
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(mDate);
        out.writeString(mTitle);
        out.writeString(mDescr);
        out.writeString(mLink);
    }
    @Override
    public int describeContents() {
        return 0;
    }
    public static final Parcelable.Creator<NewsItem> CREATOR = new Parcelable.Creator<NewsItem>() {
        public NewsItem createFromParcel(Parcel in) {
            return new NewsItem(in);
        }

        public NewsItem[] newArray(int size) {
            return new NewsItem[size];
        }
    };
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
