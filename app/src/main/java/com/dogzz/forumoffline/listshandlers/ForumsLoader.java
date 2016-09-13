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

import java.util.ArrayList;
import java.util.List;

public class ForumsLoader extends ListsLoader {

    private  final String LOG_TAG = ForumsLoader.class.getName();

    public ForumsLoader(Activity mContext, TasksListener mListener, DBProcessor dbProcessor) {
        super(mContext, mListener, dbProcessor);
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

    @Override
    public List<ViewItem> getCurrentList() {
        if (currentList == null) {
            currentList = new ArrayList<>();
            DownloadListTask task = new DownloadListTask();
            task.execute(BASE_FORUM_URL);
        }
        return currentList;
    }
}
