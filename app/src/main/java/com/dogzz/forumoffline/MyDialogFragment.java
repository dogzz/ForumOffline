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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.dogzz.forumoffline.dataprocessing.ViewItem;
import com.dogzz.forumoffline.network.PageDownloader;

import java.util.ArrayList;
import java.util.List;

public class MyDialogFragment extends DialogFragment {

    ViewItem header;
    int maxPage;
    private EditText eTextStart;
    private EditText eTextEnd;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        final View view = inflater.inflate(R.layout.dialog_layout, null);
        eTextStart = (EditText) view.findViewById(R.id.startPage);
        eTextEnd = (EditText) view.findViewById(R.id.endPage);
        eTextStart.setText(String.valueOf(Math.min(header.getLastSavedPage() + 1, header.getLastPageNumber())));
        eTextEnd.setText(header.getLastPage());
        builder.setView(view)
                // Add action buttons
                .setPositiveButton(R.string.download, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {


                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        MyDialogFragment.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        final AlertDialog d = (AlertDialog) getDialog();
        if(d != null)
        {
            Button positiveButton = d.getButton(Dialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    Boolean wantToCloseDialog = false;

                    try {
                        int start = Integer.parseInt(eTextStart.getText().toString());
                        int end = Integer.parseInt(eTextEnd.getText().toString());
                        if (header.getLastPageNumber() < end ) end = header.getLastPageNumber();
                        if (start > end) {
                            Toast.makeText(view.getContext(), "Start page should be less then end", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(view.getContext(), "Download started", Toast.LENGTH_LONG).show();
                            wantToCloseDialog = true;
                            PageDownloader downloader = new PageDownloader(getActivity(), false, 0);
                            for (int i = (start - 1); i <= (end - 1); i++) {
                                downloader.saveArticleOffline(header, i);
                            }
                        }
                    } catch (Exception ex) {
                        Snackbar.make(view, ex.getMessage(), Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                    if(wantToCloseDialog)
                        d.dismiss();
                }
            });
        }
    }

    public void setHeader(ViewItem header) {
        this.header = header;
    }
}
