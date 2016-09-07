package com.redvord.geekcity;

import android.app.SearchManager;
import android.content.Context;
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
import com.crashlytics.android.Crashlytics;
import com.redvord.geekcity.Model.News;
import com.redvord.geekcity.Model.RestNews;
import io.fabric.sdk.android.Fabric;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private RestNews restNews;
    private NewsAdapter newsAdapter;
    private List<News> news;
    private RecyclerView recyclerView;
    private LinearLayout HeaderProgress;
    private Spinner spinner;
    private MenuItem searchMenuItem;
    private String category = "";
    private int page = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);
        restNews = new RestNews();
        news = new ArrayList<>();

        configureUI();

        addItemsToSpinner();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.search, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);

        SearchManager searchManager = (SearchManager) MainActivity.this.getSystemService(Context.SEARCH_SERVICE);

        SearchView searchView = null;
        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(MainActivity.this.getComponentName()));
        }

        searchMenuItem = menu.findItem(R.id.action_search);

        ((EditText)searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text)).setTextColor(Color.WHITE);
        ((EditText)searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text)).setHintTextColor(Color.WHITE);

        int searchImgId = android.support.v7.appcompat.R.id.search_button;
        ImageView v = (ImageView) searchView.findViewById(searchImgId);
        v.setImageResource(R.drawable.ic_search);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onResume() {
        invalidateOptionsMenu();
        super.onResume();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        searchMenuItem.collapseActionView();
        return super.onPrepareOptionsMenu(menu);
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
                Toast.makeText(MainActivity.this, error.getMessage().toString(), Toast.LENGTH_LONG).show();
            }
        } ;
        page++;
        Map <String,String> map = new HashMap<>();
        map.put("filter[category_name]",category);
        map.put("page", String.valueOf(page));
        getNews(map, callback);
    }

    private void updatePage(){
        Callback<List<News>> callback = new Callback<List<News>>() {
            @Override
            public void success(List<News> getnews, Response response) {
                newsAdapter.updateNews(getnews);
                HeaderProgress.setVisibility(View.GONE);
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(MainActivity.this, error.getMessage().toString(), Toast.LENGTH_LONG).show();
            }
        };
        Map <String,String> map = new HashMap<>();
        page=1;
        map.put("filter[category_name]",category);
        map.put("page",String.valueOf(page));
        getNews(map,callback);
    }
    //endregion

    //region configureUI
    private void addItemsToSpinner() {
        ArrayList<String> categoryList = new ArrayList<>();
        categoryList.add("Главная");
        categoryList.add("Фильмы");
        categoryList.add("Сериалы");
        categoryList.add("Комиксы");
        categoryList.add("Игры");
        categoryList.add("Шмот");
        categoryList.add("Техно");

        CustomSpinnerAdapter spinAdapter = new CustomSpinnerAdapter(
                getApplicationContext(), categoryList);

        spinner.setAdapter(spinAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapter, View v,
                                       int position, long id) {
                LinearLayout parent = (LinearLayout) v;
                TextView t = (TextView) parent.findViewById(R.id.newsCategory);
                t.setTextColor(Color.WHITE);

                switch (position) {
                    case 0:
                        page = 0;
                        category = "";
                        updatePage();
                        recyclerView.smoothScrollToPosition(0);
                        break;
                    case 1:
                        page = 0;
                        category = "moviespod";
                        updatePage();
                        recyclerView.smoothScrollToPosition(0);
                        break;
                    case 2:
                        page = 0;
                        category = "tvshow";
                        updatePage();
                        recyclerView.smoothScrollToPosition(0);
                        break;
                    case 3:
                        page = 0;
                        category = "comics-2";
                        updatePage();
                        recyclerView.smoothScrollToPosition(0);
                        break;
                    case 4:
                        page = 0;
                        category = "games";
                        updatePage();
                        recyclerView.smoothScrollToPosition(0);
                        break;
                    case 5:
                        page = 0;
                        category = "shopping";
                        updatePage();
                        recyclerView.smoothScrollToPosition(0);
                        break;
                    case 6:
                        page = 0;
                        category = "tech";
                        updatePage();
                        recyclerView.smoothScrollToPosition(0);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });

    }

    private void configureUI(){
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar2);
        progressBar.getIndeterminateDrawable().setColorFilter(
                Color.RED, android.graphics.PorterDuff.Mode.SRC_IN);

        HeaderProgress = (LinearLayout) findViewById(R.id.linlaHeaderProgress);
        HeaderProgress.setVisibility(View.VISIBLE);

        setTitle("");

        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("");

        recyclerView = (RecyclerView)findViewById(R.id.rv);
        final LinearLayoutManager llm = new LinearLayoutManager(MainActivity.this);
        recyclerView.setLayoutManager(llm);
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(this));
        newsAdapter = new NewsAdapter(news, recyclerView,MainActivity.this);
        recyclerView.setAdapter(newsAdapter);

        spinner = (Spinner) findViewById(R.id.spinner_toolbar);

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
    //endregion
}
