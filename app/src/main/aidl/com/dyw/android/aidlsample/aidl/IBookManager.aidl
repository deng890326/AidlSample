// IBookManager.aidl
package com.dyw.android.aidlsample.aidl;

// Declare any non-default types here with import statements

import com.dyw.android.aidlsample.aidl.Book;
import com.dyw.android.aidlsample.aidl.IOnBookAddedListener;

interface IBookManager {

    List<Book> getBookList();
    void addBook(in Book book);

    void registerListener(in IOnBookAddedListener listener);
    void unregisterListener(in IOnBookAddedListener listener);
}
