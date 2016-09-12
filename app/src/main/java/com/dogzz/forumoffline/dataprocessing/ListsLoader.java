/*
* @Author: dogzz
* @Created: 9/7/2016
*/

package com.dogzz.forumoffline.dataprocessing;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;
import com.dogzz.forumoffline.R;
import com.dogzz.forumoffline.network.DownloadTask;
import com.dogzz.forumoffline.network.PageDownloader;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ListsLoader {

    private static final String BASE_FORUM_URL = "http://www.babyplan.ru/forums/";
    private Activity mContext;
    private TasksListener mListener;
    private DBProcessor dbProcessor;
    private ViewItem currentHeader;

    private List<ViewItem> currentList;
    private List<List<ViewItem>> backlog = new ArrayList<>();
    private  final String LOG_TAG = ListsLoader.class.getName();

    public ListsLoader(Activity mContext, TasksListener mListener, DBProcessor dbProcessor) {
        this.mContext = mContext;
        this.mListener = mListener;
        this.dbProcessor = dbProcessor;
    }

    public void showSavedViewItems(ViewItem header) {
        if (header != null) {
            currentHeader = header;
        }
        List<ViewItem> result = new ArrayList<>();
//        String path = getFilesDir().getAbsolutePath().concat("/");
        String path = mContext.getFilesDir().getAbsolutePath().concat("/")
                .concat(PageDownloader.generateDirName(currentHeader));
        File dir = (new File(path));
        FileFilter ff = new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.isDirectory();
            }
        };
        File[] dirs = dir.listFiles(ff);
        if (dirs != null) {
            for (File d : dirs) {
                ViewItem item = new ViewItem(
                        currentHeader.getText(),
                        d.getAbsolutePath().concat("/").concat(d.getName()),
                        ViewItemType.SAVED);
                item.setLastPage(d.getName());
                result.add(item);
            }
        }
        if (header != null) {
            addToBacklog();
        }
        currentList = result;
        currentHeader.setLastSavedPage(
                result.isEmpty() ? 0 : result.get(result.size() - 1).getLastPageNumber());
        mListener.onLoadingListFinished();
    }

    private String makeTitleForSavedPage(String name) {
        return mContext.getResources().getString(R.string.page_string) + " " + name;
    }

    private void addToBacklog() {
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
        if (currentList == null) {
            currentList = new ArrayList<>();
            DownloadListTask task = new DownloadListTask();
            task.execute(BASE_FORUM_URL);
        }
        return currentList;
    }

    public void renewViewItems(ViewItem header) {
        DownloadListTask task = new DownloadListTask();
        task.execute(header.getUrl());
    }

    private void populateData(int result, String message) {
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

    /**
     * return list with realm-backed ViewItems, merged with db data
     * @param viewItems viewItems from network
     * @return viewItems merged with same, stored in db
     */
    private List<ViewItem> dbDecorator(List<ViewItem> viewItems) {
//        List<ViewItem> result = new ArrayList<>();
//        for (ViewItem item : viewItems) {
//            result.add(dbProcessor.getMergedViewItem(item));
//            Log.d(LOG_TAG, "Get From DB - Finish");
//        }
//        return result;
        return dbProcessor.getMergedViewItems(viewItems);
    }

    public ViewItem getCurrentHeader() {
        return currentHeader;
    }

    public void setCurrentHeader(ViewItem currentHeader) {
        this.currentHeader = currentHeader;
    }

    private class DownloadListTask extends DownloadTask {

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
