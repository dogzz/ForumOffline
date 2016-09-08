/*
* @Author: dogzz
* @Created: 9/1/2016
*/

package com.dogzz.forumoffline;


import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import com.dogzz.forumoffline.dataprocessing.ViewItem;
import com.dogzz.forumoffline.network.PageDownloader;

public class MyDialogFragment extends DialogFragment {

    ViewItem header;
    int maxPage;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        final View view = inflater.inflate(R.layout.dialog_layout, null);
        final EditText eTextStart = (EditText) view.findViewById(R.id.startPage);
        final EditText eTextEnd = (EditText) view.findViewById(R.id.endPage);

        builder.setView(view)
                // Add action buttons
                .setPositiveButton(R.string.download, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Snackbar.make(view, "Download started", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        try {
                            int start = Integer.parseInt(eTextStart.getText().toString());
                            int end = Integer.parseInt(eTextEnd.getText().toString());
//                            String startUrl = header.getUrl();
                            for (int i = (start - 1) * 20; i <= (end - 1) * 20; i = i + 20) {

                                PageDownloader downloader = new PageDownloader(getActivity(), false, 0);
                                downloader.saveArticleOffline(header, i);
                            }
                        } catch (Exception ex) {
                            Snackbar.make(view, ex.getMessage(), Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }

                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        MyDialogFragment.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }

    public void setHeader(ViewItem header) {
        this.header = header;
    }
}
