/*
* @Author: dogzz
* @Created: 9/7/2016
*/

package com.dogzz.forumoffline.listshandlers;

import android.app.Activity;
import android.util.Log;
import com.dogzz.forumoffline.dataprocessing.*;
import com.dogzz.forumoffline.network.DownloadTask;
import com.dogzz.forumoffline.network.PageDownloader;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class  ListsLoader {

    static final String BASE_FORUM_URL = "http://www.babyplan.ru/forums/";
    Activity mContext;
    TasksListener mListener;
    DBProcessor dbProcessor;
    private ViewItem currentHeader;

    List<ViewItem> currentList;
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

    void addToBacklog() {
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

    public abstract List<ViewItem> getCurrentList();

    public abstract void renewViewItems(ViewItem header);

    protected abstract void populateData(int result, String message);

    /**
     * return list with realm-backed ViewItems, merged with db data
     * @param viewItems viewItems from network
     * @return viewItems merged with same, stored in db
     */
    List<ViewItem> dbDecorator(List<ViewItem> viewItems) {
        return dbProcessor.getMergedViewItems(viewItems);
    }

    public ViewItem getCurrentHeader() {
        return currentHeader;
    }

    public void setCurrentHeader(ViewItem currentHeader) {
        this.currentHeader = currentHeader;
    }

    class DownloadListTask extends DownloadTask {

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
