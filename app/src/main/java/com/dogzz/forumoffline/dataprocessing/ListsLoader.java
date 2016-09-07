/*
* @Author: dogzz
* @Created: 9/7/2016
*/

package com.dogzz.forumoffline.dataprocessing;

import android.app.Activity;
import android.content.Context;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ListsLoader {

    Activity mContext;
    TasksListener mListener;
    String currentUrl = "";
    List<ViewItem> currentList = new ArrayList<>();
    List<List<ViewItem>> backlog = new ArrayList<>();

    public ListsLoader(Activity mContext, TasksListener mListener) {
        this.mContext = mContext;
        this.mListener = mListener;
    }

    public List<ViewItem> getPageNames() {
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
        List<ViewItem> cop = new ArrayList<>(currentList);
        backlog.add(cop);
    }

    public void popBacklog() {
        int index = backlog.size() - 1;
        backlog.get(index);
        backlog.remove(index);
    }
}
