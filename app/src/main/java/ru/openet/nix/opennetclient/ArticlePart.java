package ru.openet.nix.opennetclient;


/**
 * Created by Nix on 28.01.2018.
 */

public class ArticlePart {
    private String mText;
    private String mCode;
    //private Bitmap mBitmap;
    private String mImageLink;
    private int mType;

    public static final int SIMPLE_TEXT = 0;
    public static final int IMAGE = 1;
    public static final int CODE = 2;

    public ArticlePart(int type, String text) {
        mType = type;
        if(type == SIMPLE_TEXT){
            mText = text;
            mCode = null;
            mImageLink = null;
        }else if(type == CODE){
            mCode = text;
            mText = null;
            mImageLink = null;
        }else if(type == IMAGE){
            mImageLink = text;
            mCode = null;
            mText = null;
        }
        //mBitmap = null;
    }

    public String getText() {
        return mText;
    }

    public void setText(String text) {
        mText = text;
    }

    public String getCode() {
        return mCode;
    }

    public void setCode(String code) {
        mCode = code;
    }

    /*public Bitmap getBitmap() {
        return mBitmap;
    }*/

    /*public void setBitmap(Bitmap bitmap) {
        mBitmap = bitmap;
    }*/

    public int getType() {
        return mType;
    }

    public void setType(int type) {
        mType = type;
    }

    public String getImageLink() {
        return mImageLink;
    }

    public void setImageLink(String imageLink) {
        mImageLink = imageLink;
    }
}
