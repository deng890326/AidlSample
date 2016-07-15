// IOnBookAddedListener.aidl
package com.dyw.android.aidlsample.aidl;

import com.dyw.android.aidlsample.aidl.Book;
// Declare any non-default types here with import statements

interface IOnBookAddedListener {
    void onBookAdded(in Book book);
}
