/*
* @Author: dogzz
* @Created: 9/1/2016
*/

package com.dogzz.forumoffline.uisupport;

import android.content.Context;
import android.graphics.PorterDuff;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.dogzz.forumoffline.R;
import com.dogzz.forumoffline.dataprocessing.ViewItem;
import com.dogzz.forumoffline.dataprocessing.ViewItemType;

import java.io.Serializable;
import java.util.List;

public class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.CustomViewHolder> implements Serializable {

    private List<ViewItem> pageFolders;
    private Context mContext;
    private ItemClickListener itemClickListener;
    private int selectedPosition = -1;

    public MyRecyclerAdapter(Context context, List<ViewItem> pageFolders, ItemClickListener itemClickListener) {
        this.pageFolders = pageFolders;
        this.mContext = context;
        this.itemClickListener = itemClickListener;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.headers_list_row, viewGroup, false);
        return new CustomViewHolder(view, itemClickListener);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder customViewHolder, int position) {
        ViewItem header = pageFolders.get(position);
        if (header.getType() == ViewItemType.SECTION) {
            customViewHolder.articleImage.setImageResource(R.drawable.f_icon);
            customViewHolder.articleSubTitle.setVisibility(View.GONE);
            customViewHolder.articleSubTitle.setText("");
        } else {
            customViewHolder.articleImage.setImageResource(R.drawable.t_unread);
            customViewHolder.articleSubTitle.setVisibility(View.VISIBLE);
            customViewHolder.articleSubTitle.setText(header.getLastPage());
        }
        customViewHolder.articleImage.setVisibility(View.VISIBLE);
        //Setting text view title
        customViewHolder.articleTitle.setText(header.getText());
        if (header.isFavorite()) {
            customViewHolder.isStarredImage.setImageResource(R.drawable.ic_star_white_36dp);
            final int newColor = mContext.getResources().getColor(R.color.colorAccent);
            customViewHolder.isStarredImage.setColorFilter(newColor, PorterDuff.Mode.SRC_ATOP);
        } else {
            customViewHolder.isStarredImage.setImageResource(R.drawable.ic_star_border_black_36dp);
            customViewHolder.isStarredImage.setColorFilter(null);
        }
//        customViewHolder.isStarredImage.setVisibility(View.VISIBLE);



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

    public void setPageFolders(List<ViewItem> pageFolders) {
        this.pageFolders = pageFolders;
    }

    static class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView articleImage;
        ImageView isStarredImage;
        TextView articleTitle;
        TextView articleSubTitle;
        CardView articleCard;
        ItemClickListener itemClickListener;

        CustomViewHolder(View view, ItemClickListener itemClickListener) {
            super(view);
            this.itemClickListener = itemClickListener;
            this.articleCard = (CardView) view.findViewById(R.id.articlecard);
            this.articleImage = (ImageView) view.findViewById(R.id.articleimage);
            this.isStarredImage = (ImageView) view.findViewById(R.id.is_starred);
            isStarredImage.setOnClickListener(this);
            view.setOnClickListener(this);
            this.articleTitle = (TextView) view.findViewById(R.id.articletitle);
            this.articleSubTitle = (TextView) view.findViewById(R.id.articlesubtitle);
        }

        @Override
        public void onClick(View view) {
            if (view.getId() == isStarredImage.getId()){
                itemClickListener.onArticleStarred(getAdapterPosition());
            } else {
                itemClickListener.onArticleClicked(getAdapterPosition());
            }
        }
    }
}