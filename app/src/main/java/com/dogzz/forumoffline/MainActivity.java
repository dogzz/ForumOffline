package com.dogzz.forumoffline;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import com.dogzz.forumoffline.dataprocessing.*;
import com.dogzz.forumoffline.uisupport.ItemClickListener;
import com.dogzz.forumoffline.uisupport.MyRecyclerAdapter;
import io.realm.Realm;
import io.realm.RealmConfiguration;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements TasksListener, ItemClickListener {
    RecyclerView mRecyclerView;
    MyRecyclerAdapter adapter;
    ListsLoader loader;
    protected List<ViewItem> articlesHeaders = new ArrayList<>();
    public final static String EXTRA_MESSAGE = "com.dogzz.forumoffline.FILENAME";
    private Realm realm;
    private RealmConfiguration realmConfig;
    private DBProcessor dbProcessor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        // Create the Realm configuration
        realmConfig = new RealmConfiguration.Builder(this).build();
        // Open the Realm for the UI thread.
        realm = Realm.getInstance(realmConfig);
        dbProcessor = new DBProcessor(realm, this);

        if (loader == null) {
            loader = new ListsLoader(this, this, dbProcessor);
        }
        if (adapter == null) {
            adapter = new MyRecyclerAdapter(this, loader.getCurrentList(), this);
            mRecyclerView.setAdapter(adapter);
        }
        adapter.notifyDataSetChanged();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmStartDownload();
            }
        });

        GridLayoutManager lManager = new GridLayoutManager(this, 1);
        mRecyclerView.setLayoutManager(lManager);
        int pagesDisplayed = 1;
    }

    @Override
    public void onBackPressed() {
        if (loader.getBacklogSize() > 0) {
            loader.popBacklog();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_CANCELED) {
                adapter.notifyDataSetChanged();
            }
        }
    }


    @Override
    public void onLoadingListFinished() {
        if (adapter == null) {
            adapter = new MyRecyclerAdapter(this, loader.getCurrentList(), this);
            mRecyclerView.setAdapter(adapter);
        } else {
            adapter.setPageFolders(loader.getCurrentList());
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onSavedArticleTaskFinished() {
        loader.showSavedViewItems(null);
    }


    @Override
    public void onArticleClicked(int position) {
        ViewItem header = loader.getCurrentList().get(position);
        if (header.getType() == ViewItemType.SAVED) {
            Intent intent = new Intent(this, ViewActivity.class);
            String fileName = header.getUrl();

            intent.putExtra(EXTRA_MESSAGE, fileName);
            startActivityForResult(intent, 1);
        } else if (header.getType() == ViewItemType.THREAD){
            loader.showSavedViewItems(header);
//            confirmStartDownload();
        } else {
            loader.renewViewItems(header);
        }
    }

    @Override
    public void onArticleLongClicked(int position) {

    }

    @Override
    public void onArticleStarred(int position) {
        ViewItem header = loader.getCurrentList().get(position);
        dbProcessor.markItemStarred(header);
        adapter.notifyItemChanged(position);
    }

    public void confirmStartDownload() {
        MyDialogFragment newFragment = new MyDialogFragment();
        newFragment.setHeader(loader.getCurrentHeader());
        newFragment.show(getSupportFragmentManager(), "A?");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close(); // Remember to close Realm when done.
    }


}
