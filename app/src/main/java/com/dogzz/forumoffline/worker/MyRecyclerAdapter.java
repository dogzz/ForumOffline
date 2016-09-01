/*
* @Author: dogzz
* @Created: 9/1/2016
*/

package com.dogzz.forumoffline.worker;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.dogzz.forumoffline.R;
import it.sephiroth.android.library.picasso.Picasso;

import java.io.Serializable;
import java.util.List;

public class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.CustomViewHolder> implements Serializable {
    private List<String> pageFolders;
    private Context mContext;
    private int selectedPosition = -1;

    public MyRecyclerAdapter(Context context, List<String> pageFolders) {
        this.pageFolders = pageFolders;
        this.mContext = context;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.headers_list_row, viewGroup, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder customViewHolder, int position) {
        String header = pageFolders.get(position);
        customViewHolder.articleImage.setVisibility(View.GONE);
        //Setting text view title
        customViewHolder.articleTitle.setText(header);
        customViewHolder.isSavedImage.setVisibility(View.GONE);
        customViewHolder.articleSubTitle.setVisibility(View.GONE);
        customViewHolder.articleSubTitle.setText("");


        if(selectedPosition == position){
            customViewHolder.articleCard.setCardBackgroundColor(mContext.getResources().getColor(R.color.colorAccent));
        }else{
            customViewHolder.articleCard.setCardBackgroundColor(mContext.getResources().getColor(R.color.cardview_light_background));
            customViewHolder.articleTitle.setTextColor(mContext.getResources().getColor(R.color.textColor));
            customViewHolder.articleSubTitle.setTextColor(mContext.getResources().getColor(R.color.textColor));
        }
    }

    @Override
    public int getItemCount() {
        return (null != pageFolders ? pageFolders.size() : 0);
    }

    public void selectItem(int position) {
        notifyItemChanged(selectedPosition);
        selectedPosition = position;
        notifyItemChanged(selectedPosition);
    }

    public void unselectAllItems() {
        notifyItemChanged(selectedPosition);
        selectedPosition = -1;
        notifyItemChanged(selectedPosition);
    }

    static class CustomViewHolder extends RecyclerView.ViewHolder {
        ImageView articleImage;
        ImageView isSavedImage;
        TextView articleTitle;
        TextView articleSubTitle;
        CardView articleCard;

        CustomViewHolder(View view) {
            super(view);
            this.articleCard = (CardView) view.findViewById(R.id.articlecard);
            this.articleImage = (ImageView) view.findViewById(R.id.articleimage);
            this.isSavedImage = (ImageView) view.findViewById(R.id.issaved);
            this.articleTitle = (TextView) view.findViewById(R.id.articletitle);
            this.articleSubTitle = (TextView) view.findViewById(R.id.articlesubtitle);
        }
    }
}