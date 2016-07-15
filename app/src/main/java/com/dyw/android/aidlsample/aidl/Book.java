package com.dyw.android.aidlsample.aidl;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by wei on 2016/7/14.
 */
public class Book implements Parcelable {
    private int mBookId;

    public int getBookId() {
        return mBookId;
    }

    public void setBookId(int bookId) {
        mBookId = bookId;
    }

    public String getBookName() {
        return mBookName;
    }

    public void setBookName(String bookName) {
        mBookName = bookName;
    }

    private String mBookName;

    protected Book(Parcel in) {
        mBookId = in.readInt();
        mBookName = in.readString();
    }

    @Override
    public String toString() {
        return "mBookId:" + mBookId + ", mBookName:" + mBookName;
    }

    public static final Creator<Book> CREATOR = new Creator<Book>() {
        @Override
        public Book createFromParcel(Parcel in) {
            return new Book(in);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };

    public Book(int bookId, String bookName) {
        mBookId = bookId;
        mBookName = bookName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(mBookId);
        parcel.writeString(mBookName);
    }
}
