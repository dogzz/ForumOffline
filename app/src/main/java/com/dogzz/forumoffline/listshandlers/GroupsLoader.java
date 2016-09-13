/*
* @Author: dogzz
* @Created: 9/13/2016
*/

package com.dogzz.forumoffline.listshandlers;

import android.app.Activity;
import com.dogzz.forumoffline.dataprocessing.DBProcessor;
import com.dogzz.forumoffline.dataprocessing.TasksListener;
import com.dogzz.forumoffline.dataprocessing.ViewItem;

import java.util.List;

public class GroupsLoader extends ListsLoader {
    public GroupsLoader(Activity mContext, TasksListener mListener, DBProcessor dbProcessor) {
        super(mContext, mListener, dbProcessor);
    }

    @Override
    public List<ViewItem> getCurrentList() {
        return null;
    }

    @Override
    public void renewViewItems(ViewItem header) {

    }

    @Override
    protected void populateData(int result, String message) {

    }
}
