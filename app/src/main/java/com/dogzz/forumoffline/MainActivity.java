package com.dogzz.forumoffline;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import com.dogzz.forumoffline.dataprocessing.ListsLoader;
import com.dogzz.forumoffline.dataprocessing.TasksListener;
import com.dogzz.forumoffline.dataprocessing.ViewItem;
import com.dogzz.forumoffline.dataprocessing.ViewItemType;
import com.dogzz.forumoffline.uisupport.MyRecyclerAdapter;
import com.dogzz.forumoffline.network.PageDownloader;
import com.dogzz.forumoffline.uisupport.RecyclerItemClickListener;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements TasksListener {
    RecyclerView mRecyclerView;
    MyRecyclerAdapter adapter;
    ListsLoader loader;
    protected List<ViewItem> articlesHeaders = new ArrayList<>();
    public final static String EXTRA_MESSAGE = "com.dogzz.forumoffline.FILENAME";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        if (loader == null) {
            loader = new ListsLoader(this);
        }
        if (adapter == null) {
            adapter = new MyRecyclerAdapter(this, loader.getPageNames());
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
        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, mRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                ViewItem articleHeader = loader.getPageNames().get(position);
                onArticleClicked(articleHeader);
            }

            @Override
            public void onLongItemClick(View view, int position) {
                ViewItem articleHeader = loader.getPageNames().get(position);
                onArticleLongClicked(articleHeader);
            }
        }));


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
                onSavedArticleTaskFinished();
            }
        }
    }


    @Override
    public void onLoadingListFinished() {

    }

    @Override
    public void onSavedArticleTaskFinished() {
//        articlesHeaders.clear();
//        articlesHeaders.addAll(loader.getPageNames());
        if (adapter == null) {
            adapter = new MyRecyclerAdapter(this, loader.getPageNames());
            mRecyclerView.setAdapter(adapter);
        }
        adapter.notifyDataSetChanged();
    }



    public void onArticleClicked(ViewItem header) {
        Intent intent = new Intent(this, ViewActivity.class);
        String fileName = header.getUrl();
        intent.putExtra(EXTRA_MESSAGE, fileName);
        startActivityForResult(intent, 1);
    }

    public void onArticleLongClicked(ViewItem header) {

    }

    public void confirmStartDownload() {
        DialogFragment newFragment = new MyDialogFragment();
        newFragment.show(getSupportFragmentManager(), "A?");
    }



    }
