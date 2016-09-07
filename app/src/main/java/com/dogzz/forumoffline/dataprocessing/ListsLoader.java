/*
* @Author: dogzz
* @Created: 9/7/2016
*/

package com.dogzz.forumoffline.dataprocessing;

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.widget.Toast;
import com.dogzz.forumoffline.network.DownloadTask;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ListsLoader {

    private static final String BASE_FORUM_URL = "http://www.babyplan.ru/forums/";
    Activity mContext;
    TasksListener mListener;
    String currentUrl = "";

    List<ViewItem> currentList = new ArrayList<>();
    List<List<ViewItem>> backlog = new ArrayList<>();
    public final String LOG_TAG = ListsLoader.class.getName();

    public ListsLoader(Activity mContext, TasksListener mListener) {
        this.mContext = mContext;
        this.mListener = mListener;
    }

    public List<ViewItem> getSavedViewItems() {
        List<ViewItem> result = new ArrayList<>();
//        String path = getFilesDir().getAbsolutePath().concat("/");
        File dir = mContext.getFilesDir();
        FileFilter ff = new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.isDirectory();
            }
        };
        File[] dirs = dir.listFiles(ff);
        for (File d:dirs) {
            result.add(new ViewItem(d.getName(), "", ViewItemType.SAVED));
        }
        return result;
    }

    public void addToBacklog() {
        if (!currentList.isEmpty()) {
            List<ViewItem> cop = new ArrayList<>(currentList);
            backlog.add(cop);
        }
    }

    public void popBacklog() {
        int index = backlog.size() - 1;
        currentList = backlog.get(index);
        backlog.remove(index);
        mListener.onLoadingListFinished();
    }

    public int getBacklogSize() {
        return backlog.size();
    }

    public List<ViewItem> getCurrentList() {
        if (currentList.isEmpty()) {
            DownloadListTask task = new DownloadListTask();
            task.execute(BASE_FORUM_URL);
        }
        return currentList;
    }

    public void renewViewItems(ViewItem header) {
        DownloadListTask task = new DownloadListTask();
        task.execute(header.getUrl());
    }

    public void populateData(int result, String message) {
        if (result == 0) {
            Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
        } else {
            addToBacklog();
            currentList = PageExtractor.extractViewItemList(message);
            mListener.onLoadingListFinished();
        }
    }

    public class DownloadListTask extends DownloadTask {

        @Override
        protected Integer doInBackground(String... urls) {
            Integer result;
            // params comes from the execute() call: params[0] is the BASE_URL.
            try {
                result = downloadUrl(urls[0]);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error: Unable to retrieve source data. The source is inaccessible.");
                result = 0;
            }
            return result;
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(Integer result) {
//            downloadResult = resultMessage;
            populateData(result, resultMessage);
        }
    }
}
