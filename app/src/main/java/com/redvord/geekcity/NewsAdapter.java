package com.redvord.geekcity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.redvord.geekcity.Model.News;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by redvo on 24.01.2016.
 */
class NewsAdapter extends RecyclerView.Adapter{

    private Context context;
    private List<News> news;

    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;

    private int  totalItemCount,visibleItemCount, pastVisibleItems;
    private boolean loading;
    private OnLoadMoreListener onLoadMoreListener;

    NewsAdapter(List<News> news, RecyclerView recyclerView, Context context) {
        this.context = context;
        this.news = news;

        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {

            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView
                    .getLayoutManager();

            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    if (dy > 0)
                    {
                        visibleItemCount = linearLayoutManager.getChildCount();
                        totalItemCount = linearLayoutManager.getItemCount();
                        pastVisibleItems = linearLayoutManager.findFirstVisibleItemPosition();

                        if (!loading) {
                            if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                                loading = true;
                                if (onLoadMoreListener != null) {
                                    onLoadMoreListener.onLoadMore();
                                }
                            }
                        }
                    }
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        return news.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        if (viewType == VIEW_ITEM) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(
                    R.layout.item_layout, viewGroup, false);

            viewHolder = new NewsViewHolder(view);
        } else {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(
                    R.layout.progress_item, viewGroup, false);

            viewHolder = new ProgressViewHolder(v);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int i) {
        if (holder instanceof NewsViewHolder) {
            String image = news.get(i).featured_image.guid;
            ((NewsViewHolder)holder).newsTitle.setText(stripHtml(news.get(i).title));

            String excerpt = news.get(i).excerpt;
            excerpt = removeTagsFromExcerpt(excerpt);
            ((NewsViewHolder)holder).newsSubTitle.setText(stripHtml(excerpt));

            ((NewsViewHolder)holder).news = news.get(i);
            Picasso.with(context)
                    .load(image)
                    .placeholder(R.drawable.placeholder)
                    .into(((NewsViewHolder)holder).newsImage);
        }
    }

    @Override
    public int getItemCount() {
        return news.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    //region onBindViewHolder methods
    private String stripHtml(String html){
        return Html.fromHtml(html).toString();
    }

    private String removeTagsFromExcerpt(String excerpt){
        excerpt =  excerpt.substring(0,excerpt.length()-5);
        excerpt =  excerpt.substring(3,excerpt.length());
        return excerpt;
    }
    //endregion

    //region News changing
    void addNews(List<News> news){
        this.news.addAll(news);
        notifyDataSetChanged();
    }

    void updateNews(List<News> news){
        this.news.clear();
        this.news.addAll(news);
        notifyDataSetChanged();
    }
    //endregion

    void setLoaded() {
        loading = false;
    }

    interface OnLoadMoreListener {
        void onLoadMore();
    }

    private static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;
        public ProgressViewHolder(View v) {
            super(v);
            progressBar = (ProgressBar) v.findViewById(R.id.progressBar1);
            progressBar.getIndeterminateDrawable().setColorFilter(Color.parseColor("#E53935"),android.graphics.PorterDuff.Mode.SRC_IN);
        }
    }

    private static class NewsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        News news;
        TextView newsTitle;
        TextView newsSubTitle;
        ImageView newsImage;

        NewsViewHolder(View itemView) {
            super(itemView);

            newsTitle = (TextView) itemView.findViewById(R.id.news_title);
            newsSubTitle = (TextView) itemView.findViewById(R.id.news_subtitle);
            newsImage = (ImageView) itemView.findViewById(R.id.news_image);

            Typeface fontLight = Typeface.createFromAsset(newsTitle.getContext().getAssets(), "fonts/OpenSansLight.ttf");
            Typeface fontRegular = Typeface.createFromAsset(newsTitle.getContext().getAssets(), "fonts/OpenSansSemiBold.ttf");

            newsTitle.setTypeface(fontRegular);
            newsSubTitle.setTypeface(fontLight);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Context context = v.getContext();
            Intent intent = new Intent(context, NewsActivity.class);
            intent.putExtra("content", news.content);
            intent.putExtra("title", news.title);
            intent.putExtra("image", news.featured_image.guid);
            intent.putExtra("category", news.terms.category.get(0).name);
            intent.putExtra("link", news.link);
            context.startActivity(intent);
        }
    }
}