package com.dyw.android.aidlsample;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dyw.android.aidlsample.aidl.Book;
import com.dyw.android.aidlsample.aidl.IBookManager;
import com.dyw.android.aidlsample.aidl.IOnBookAddedListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private IBookManager mService;

    private IOnBookAddedListener mOnBookAddedListener = new IOnBookAddedListener.Stub() {
        @Override
        public void onBookAdded(Book book) throws RemoteException {
            Log.i("dyw", TAG + "， onBookAdded, 线程名：" + Thread.currentThread().getName());
            Toast.makeText(MainActivity.this, "已添加：#" + book.getBookId() + ", " + book.getBookName(), Toast.LENGTH_LONG).show();
            mMyAdapter.addBook(book);
            mMyAdapter.notifyDataSetChanged();
        }
    };

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = IBookManager.Stub.asInterface(service);
            try {
                mService.registerListener(mOnBookAddedListener);
                mMyAdapter.setBooks(mService.getBookList());
                mMyAdapter.notifyDataSetChanged();

                service.linkToDeath(new IBinder.DeathRecipient() {
                    @Override
                    public void binderDied() {
                        Log.i("dyw", "binderDied: tname:" + Thread.currentThread().getName());
                    }
                }, 0);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
            Log.i("dyw", "onServiceDisconnected: tname:" + Thread.currentThread().getName());
        }
    };

    RecyclerView mRecyclerView;
    MyAdapter mMyAdapter;

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView mTextView;
        Book mBook;

        public MyViewHolder(View itemView) {
            super(itemView);
            mTextView = (TextView) itemView;
        }

        void bindView(Book book) {
            mBook = book;
            mTextView.setText("#" + book.getBookId() + ", " + book.getBookName());
        }
    }

    class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

        private List<Book> mBooks = new ArrayList<>();

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MyViewHolder(new TextView(MainActivity.this));
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            holder.bindView(mBooks.get(position));
        }

        @Override
        public int getItemCount() {
            return mBooks.size();
        }

        public List<Book> getBooks() {
            return mBooks;
        }

        public void setBooks(List<Book> books) {
            mBooks = books;
        }

        public void addBook(Book book) {
            mBooks.add(book);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final EditText editText = (EditText) findViewById(R.id.bookName);
        Button button = (Button) findViewById(R.id.add);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mService.addBook(new Book(mService.getBookList().size(), editText.getText().toString()));
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
        mRecyclerView = (RecyclerView) findViewById(R.id.list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mMyAdapter = new MyAdapter();
        mRecyclerView.setAdapter(mMyAdapter);

        Intent service = new Intent(this, BookService.class);
        bindService(service, mServiceConnection, Context.BIND_AUTO_CREATE);
        Log.i("dyw", TAG + "，onCreate,  线程名：" + Thread.currentThread().getName());
    }

    @Override
    protected void onDestroy() {
        if (mService.asBinder().isBinderAlive()) {
            try {
                mService.unregisterListener(mOnBookAddedListener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        unbindService(mServiceConnection);
        super.onDestroy();
    }
}
