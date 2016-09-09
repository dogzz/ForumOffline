/*
* @Author: dogzz
* @Created: 9/9/2016
*/

package com.dogzz.forumoffline.dataprocessing;

import android.util.Log;
import android.view.View;
import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmResults;

import java.util.*;

public class DBProcessor {
    private Realm realm;
    private TasksListener mListener;
    private final String LOG_TAG = DBProcessor.class.getName();

    public DBProcessor(Realm realm, TasksListener mListener) {
        this.realm = realm;
        this.mListener = mListener;
    }

    public ViewItem getMergedViewItem(final String text, final String url, final ViewItemType type) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Log.d(LOG_TAG, "Execute: Get From DB - Start");
                ViewItem item = realm.where(ViewItem.class).equalTo("url", url).findFirst();
                Log.d(LOG_TAG, "Execute: Get From DB - End");
                if (item == null) {
                    Log.d(LOG_TAG, "Create record - Start");
                    item = realm.createObject(ViewItem.class, url);
                    item.setText(text);
                    item.setType(type);
                    Log.d(LOG_TAG, "Create record - End");
                }

            }
        });
        Log.d(LOG_TAG, "Get From DB - Start");
        return realm.where(ViewItem.class).equalTo("url", url).findFirst();
        }

    public ViewItem getMergedViewItem(ViewItem item) {
        return getMergedViewItem(item.getText(), item.getUrl(), item.getType());
    }

    public List<ViewItem> getMergedViewItems(final List<ViewItem> items) {
        String[] urls = new String[items.size()];
        for (int i = 0; i < items.size(); i++) {
            urls[i] = items.get(i).getUrl();
        }
        Log.d(LOG_TAG, "Get stored records if any - Start");
        final RealmResults<ViewItem> results = realm.where(ViewItem.class).in("url", urls, Case.INSENSITIVE).findAll();
        Log.d(LOG_TAG, "Get stored records if any - Finish");
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                for (ViewItem origItem : items) {
                    ViewItem result = results.where().equalTo("url", origItem.getUrl()).findFirst();
                    if (result == null) {
                        result = realm.createObject(ViewItem.class, origItem.getUrl());
                    }
                    result.setText(origItem.getText());
                    result.setType(origItem.getType());
                }
            }
        });
        Log.d(LOG_TAG, "Transaction - Finish");
        return results;
    }

    public void markItemStarred(final ViewItem header) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                header.setFavorite(!header.isFavorite());
            }
        });
    }
}