/*
* @Author: dogzz
* @Created: 9/13/2016
*/

package com.dogzz.forumoffline.listshandlers;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;
import com.dogzz.forumoffline.dataprocessing.DBProcessor;
import com.dogzz.forumoffline.dataprocessing.PageExtractor;
import com.dogzz.forumoffline.dataprocessing.TasksListener;
import com.dogzz.forumoffline.dataprocessing.ViewItem;
import com.dogzz.forumoffline.network.DownloadTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FavouritesLoader extends ListsLoader {

    private  final String LOG_TAG = FavouritesLoader.class.getName();

    public FavouritesLoader(Activity mContext, TasksListener mListener, DBProcessor dbProcessor) {
        super(mContext, mListener, dbProcessor);
    }

    @Override
    public List<ViewItem> getCurrentList() {
        if (currentList == null) {
            currentList = dbProcessor.getStarredViewItems();
        }

        return currentList;
    }

    @Override
    public void renewViewItems(ViewItem header) {
        DownloadListTask task = new DownloadListTask();
        task.execute(header.getUrl());
    }

    @Override
    protected void populateData(int result, String message) {
        if (result == 0) {
            Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
        } else {
            addToBacklog();
            Log.d(LOG_TAG, "Extract and merge list - Start");
            currentList = dbDecorator(PageExtractor.extractViewItemList(message));
            Log.d(LOG_TAG, "Extract and merge list - Finish");
//            currentList = PageExtractor.extractViewItemList(message);
            mListener.onLoadingListFinished();
        }
    }

    class DownloadPagesCount extends DownloadTask {

        @Override
        protected Integer doInBackground(String... urls) {
            Integer result;
            try {
                result = downloadUrl(urls[0]);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error: Unable to retrieve source data. The source is inaccessible.");
                result = 0;
            }
            return result;
        }

        @Override
        protected void onPostExecute(Integer result) {
            populateData(result, resultMessage);
        }
    }
}
