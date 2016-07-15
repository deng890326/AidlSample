package com.dyw.android.aidlsample;

import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import com.dyw.android.aidlsample.aidl.Book;
import com.dyw.android.aidlsample.aidl.IBookManager;
import com.dyw.android.aidlsample.aidl.IOnBookAddedListener;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by wei on 2016/7/14.
 */
public class BookService extends Service {

    private static final String TAG = "BookService";
    CopyOnWriteArrayList<Book> mBooks = new CopyOnWriteArrayList<>();
    RemoteCallbackList<IOnBookAddedListener> mOnBookAddedListeners = new RemoteCallbackList<>();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new IBookManager.Stub() {
            @Override
            public List<Book> getBookList() throws RemoteException {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Log.i("dyw", TAG + " getBookList");
                return mBooks;
            }

            @Override
            public void addBook(Book book) throws RemoteException {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Log.i("dyw", TAG + " addBook, book:" + book);
                BookService.this.addBook(book);
            }

            @Override
            public void registerListener(IOnBookAddedListener listener) throws RemoteException {
                Log.i("dyw", TAG + " registerListener, listener:" + listener);
                mOnBookAddedListeners.register(listener);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    Log.i("dyw", TAG + "listener count = " + mOnBookAddedListeners.getRegisteredCallbackCount());
                }
            }

            @Override
            public void unregisterListener(IOnBookAddedListener listener) throws RemoteException {
                Log.i("dyw", TAG + " unregisterListener, listener:" + listener);
                mOnBookAddedListeners.unregister(listener);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    Log.i("dyw", TAG + "listener count = " + mOnBookAddedListeners.getRegisteredCallbackCount());
                }
            }
        };
    }

    private void addBook(Book book) throws RemoteException {
        mBooks.add(book);
        int n = mOnBookAddedListeners.beginBroadcast();
        for (int i = 0; i < n; i++) {
            IOnBookAddedListener listener = mOnBookAddedListeners.getBroadcastItem(i);
            listener.onBookAdded(book);
        }
        mOnBookAddedListeners.finishBroadcast();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            addBook(new Book(0, "Android入门"));
            addBook(new Book(1, "iOS入门"));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
