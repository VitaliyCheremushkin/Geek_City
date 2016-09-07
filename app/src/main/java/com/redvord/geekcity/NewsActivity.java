package com.redvord.geekcity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.*;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by redvo on 25.02.2016.
 */
public class NewsActivity extends AppCompatActivity {

    private String newsLink;
    private View mCustomView;
    private LinearLayout mContentView;
    private FrameLayout mCustomViewContainer;
    private WebChromeClient.CustomViewCallback mCustomViewCallback;
    private WebView myWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_layout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        String title =  intent.getStringExtra("title");
        String content = intent.getStringExtra("content");
        String image =  intent.getStringExtra("image");
        String category = intent.getStringExtra("category");
        newsLink = intent.getStringExtra("link");

        String style = "<style>\n" +
                "  @font-face {\n" +
                "       font-family: titleFont;\n" +
                "       src: url(\"file:///android_asset/fonts/OpenSansBold.ttf\")\n" +
                "  }\n" +
                "  @font-face {\n" +
                "       font-family: bodyFont;\n" +
                "       src: url(\"file:///android_asset/fonts/OpenSansRegular.ttf\")\n" +
                "  }\n" +
                "  body{\n" +
                "        margin: 0px;\n" +
                "        text-align:left;\n" +
                "  }\n" +
                "  img {\n" +
                "        width: 100%; \n" +
                "        max-width: 100%;\n" +
                "        height:auto;\n" +
                "    \n" +
                "  }\n" +
                "  a{\n" +
                "    color:red\n" +
                "    \n" +
                "  }\n" +
                "  h3{\n" +
                "    padding-left: 4%;\n" +
                "    padding-right: 4%; \n" +
                "    text-align:center;\n" +
                "    font-family:titleFont;\n" +
                "    font-size:16pt;\n" +
                "    \n" +
                "  }\n" +
                "  #article {\n" +
                "    padding-left: 6%;\n" +
                "    padding-right: 6%;\n" +
                "    font-family:bodyFont;\n" +
                "    line-height: 1.4;\n" +
                "    color:RGBA(0,0,0,0.75);\n" +
                "    -webkit-hyphens: auto;\n" +
                "    \n" +
                "  }\n" +
                "  blockquote{\n" +
                "    font-family:'Times New Roman', Times, serif;\n" +
                "    font-style: italic;\n" +
                "    color:RGBA(0,0,0,1);\n" +
                ";\n" +
                "    \n" +
                "  }\n" +
                "</style>";

        String twitterScript =  "<script type=\"text/javascript\" src=\"https://platform.twitter.com/widgets.js\"></script>";
        String instagramScript = "<script src=\"https://platform.instagram.com/en_US/embeds.js\" async=\"\" defer=\"defer\"></script>";

        String mainString = "<html lang=\"ru\">\n" +
                "  <head>"+style+twitterScript+instagramScript+"</head>\n" +
                "  <body>\n" +
                "    <img src='"+image+"'>\n" +
                "    <h3>"+title+"</h3>\n" +
                "    <div id=\"article\" style='font-size:110%;'>\n" +
                "      "+content+"\n" +
                "    </div>\n" +
                "  </body>\n" +
                "</html>" ;

        myWebView = (WebView) findViewById(R.id.webView);
        MyWebChromeClient mWebChromeClient = new MyWebChromeClient();
        myWebView.setWebChromeClient(mWebChromeClient);
        myWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (!url.startsWith("https://") && !url.startsWith("http://")){
                    url = "http://" + url;
                }
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
                return true;
            }

        });

        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        setTitle(category);

        myWebView.loadDataWithBaseURL("", mainString, "text/html", "UTF-8", "");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_share:
                shareLink();
                return true;
            case R.id.action_open_in_browser:
                openLinkInBrowser();
                return true;
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.open_in_browser,menu);
        getMenuInflater().inflate(R.menu.share, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (mCustomViewContainer != null)
            super.onBackPressed();
        else if (myWebView.canGoBack())
            myWebView.goBack();
        else
            super.onBackPressed();
    }

    private void hideSystemUI() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE
        );
    }

    private void showSystemUI() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    //region Menu buttons
    private void openLinkInBrowser() {
        if (!newsLink.startsWith("https://") && !newsLink.startsWith("http://")){
            newsLink = "http://" + newsLink;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(newsLink));
        startActivity(intent);
    }

    private void shareLink(){
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");
        sendIntent.putExtra(Intent.EXTRA_TEXT, newsLink);
        startActivity(Intent.createChooser(sendIntent, "Поделиться:"));
    }
    //endregion

    //region settingsForChromeClient
    private void setMarginsForWebView(){
        int actionBarHeight = getActionBarHeight();
        Point point = getNavigationBarSize(getApplicationContext());
        int statusbar = getStatusBarHeight();
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) myWebView.getLayoutParams();
        params.topMargin = actionBarHeight+statusbar;
        params.bottomMargin = point.y;
        myWebView.setLayoutParams(params);
    }

    private int getActionBarHeight(){
        TypedValue tv = new TypedValue();
        int actionBarHeight = 0;
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,getResources().getDisplayMetrics());
        }
        return actionBarHeight;
    }

    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    private static Point getNavigationBarSize(Context context) {
        Point appUsableSize = getAppUsableScreenSize(context);
        Point realScreenSize = getRealScreenSize(context);
        if (appUsableSize.x < realScreenSize.x) {
            return new Point(realScreenSize.x - appUsableSize.x, appUsableSize.y);
        }
        if (appUsableSize.y < realScreenSize.y) {
            return new Point(appUsableSize.x, realScreenSize.y - appUsableSize.y);
        }
        return new Point();
    }

    private static Point getAppUsableScreenSize(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;
    }

    private static Point getRealScreenSize(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();
        if (Build.VERSION.SDK_INT >= 17) {
            display.getRealSize(size);
        } else if (Build.VERSION.SDK_INT >= 14) {
            try {
                size.x = (Integer) Display.class.getMethod("getRawWidth").invoke(display);
                size.y = (Integer) Display.class.getMethod("getRawHeight").invoke(display);
            } catch (IllegalAccessException e) {} catch (InvocationTargetException e) {} catch (NoSuchMethodException e) {}
        }
        return size;
    }
    //endregion

    public class MyWebChromeClient extends WebChromeClient {
        FrameLayout.LayoutParams LayoutParameters = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);

        @Override
        public void onShowCustomView(View view, CustomViewCallback callback) {
            if (mCustomView != null) {
                callback.onCustomViewHidden();
                return;
            }
            mContentView = (LinearLayout) findViewById(R.id.activity_news);
            mContentView.setVisibility(View.GONE);
            mCustomViewContainer = new FrameLayout(NewsActivity.this);
            mCustomViewContainer.setLayoutParams(LayoutParameters);
            mCustomViewContainer.setBackgroundResource(android.R.color.black);
            view.setLayoutParams(LayoutParameters);
            mCustomViewContainer.addView(view);
            mCustomView = view;
            mCustomViewCallback = callback;
            mCustomViewContainer.setVisibility(View.VISIBLE);
            setContentView(mCustomViewContainer);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
            hideSystemUI();
        }

        @Override
        public void onHideCustomView() {
            if (mCustomView == null) {
                return;
            } else {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                showSystemUI();
                mCustomView.setVisibility(View.GONE);
                mCustomViewContainer.removeView(mCustomView);
                mCustomView = null;
                mCustomViewContainer.setVisibility(View.GONE);
                mCustomViewCallback.onCustomViewHidden();
                mContentView.setVisibility(View.VISIBLE);
                setMarginsForWebView();
                setContentView(mContentView);
            }
        }
    }
}

