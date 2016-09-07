package com.redvord.geekcity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.redvord.geekcity.Model.News;
import com.redvord.geekcity.Model.RestNews;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by redvo on 12.03.2016.
 */
public class SearchActivity extends AppCompatActivity {
    private RestNews restNews;
    private NewsAdapter newsAdapter;
    private List<News> news;
    private LinearLayout headerProgress;
    private int page = 0;
    private String query = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_layout);
        news = new ArrayList<>();
        restNews = new RestNews();

        getIntentData();
        configureUI();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            query = intent.getStringExtra(SearchManager.QUERY);
            updatePage();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.search, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);

        SearchManager searchManager = (SearchManager) SearchActivity.this.getSystemService(Context.SEARCH_SERVICE);

        SearchView searchView = null;
        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(SearchActivity.this.getComponentName()));
        }


        ((EditText)searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text)).setTextColor(Color.WHITE);
        ((EditText)searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text)).setHintTextColor(Color.WHITE);

        int searchImgId = android.support.v7.appcompat.R.id.search_button; // I used the explicit layout ID of searchview's ImageView
        ImageView v = (ImageView) searchView.findViewById(searchImgId);
        v.setImageResource(R.drawable.ic_search);


        searchView.setIconified(false);
        searchView.clearFocus();
        searchView.setQuery(query,false);

        return super.onCreateOptionsMenu(menu);

    }

    //region data handling
    private void getNews(Map map, Callback<List<News>> callback){
        restNews.getService().getPostsForPage(map, callback);
    }

    private void getPostsForPage() {
        Callback<List<News>> callback = new Callback<List<News>>() {
            @Override
            public void success (List < News > getNews, Response response){
                news.remove(news.size() - 1);
                newsAdapter.notifyItemRemoved(news.size());
                newsAdapter.addNews(getNews);
                newsAdapter.setLoaded();
            }

            @Override
            public void failure (RetrofitError error){
                Toast.makeText(SearchActivity.this, error.getMessage().toString(), Toast.LENGTH_LONG).show();
            }
        } ;
        page++;
        Map <String,String> map = new HashMap<>();
        map.put("filter[s]",query);
        map.put("page", String.valueOf(page));
        getNews(map, callback);
    }

    private void updatePage(){
        Callback<List<News>> callback = new Callback<List<News>>() {
            @Override
            public void success(List<News> getnews, Response response) {
                newsAdapter.updateNews(getnews);
                headerProgress.setVisibility(View.GONE);
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(SearchActivity.this, error.getMessage().toString(), Toast.LENGTH_LONG).show();
            }
        };
        Map <String,String> map = new HashMap<>();
        page=1;
        map.put("filter[s]", query);
        map.put("page",String.valueOf(page));
        getNews(map, callback);
    }
    //endregion

    private void configureUI(){
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.searchProgressBar);
        progressBar.getIndeterminateDrawable().setColorFilter(
                Color.RED, android.graphics.PorterDuff.Mode.SRC_IN);

        headerProgress = (LinearLayout) findViewById(R.id.searchHeaderProgress);
        headerProgress.setVisibility(View.VISIBLE);

        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar_actionbar_search);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        toolbar.setTitleTextColor(Color.WHITE);
        setTitle("Поиск");

        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.rvSearch);
        final LinearLayoutManager llm = new LinearLayoutManager(SearchActivity.this);
        recyclerView.setLayoutManager(llm);
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(this));
        newsAdapter = new NewsAdapter(news,recyclerView,SearchActivity.this);
        recyclerView.setAdapter(newsAdapter);

        newsAdapter.setOnLoadMoreListener(new NewsAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                news.add(null);
                newsAdapter.notifyItemInserted(news.size() - 1);

                getPostsForPage();
            }
        });

        final SwipeRefreshLayout mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(true);
                updatePage();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void getIntentData(){
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            query = intent.getStringExtra(SearchManager.QUERY);
            updatePage();
        }
    }
}
