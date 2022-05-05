package com.example.newsgateway;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private Menu opt_menu;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private ViewPager2 viewPager;
    private ArrayAdapter<String> arrayAdapter;
    private NewsArticleAdapter newsArticleAdapter;
    private ArrayList<NewsArticle> newsArticleArrayList = new ArrayList<>();

    private String NewsID = "";
    private String currentNewsName;
    private final HashMap<String, HashSet<String>> newsData = new HashMap<>();
    public HashMap<String,ArrayList<NewsArticle>> newsArticleData = new HashMap<>();
    private final ArrayList<News> news = new ArrayList<>();
    private final ArrayList<String> NewsNames = new ArrayList<>();
    private final ArrayList<String> listofNews = new ArrayList<>();
    private ArrayList<News> newsList = new ArrayList<>();
    private ArrayList<String> newsDataList = new ArrayList<>();
    private  ArrayList<String> newsDataListAll = new ArrayList<>();

    private ArrayList<String> list1 = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDrawerLayout = findViewById(R.id.drawer_layout);
        mDrawerList = findViewById(R.id.left_drawer_list);
        mDrawerList.setOnItemClickListener(
                (parent, view, position, id) -> {
                    selectItemInDrawer(position);
                    Log.d(TAG,"output");
                    mDrawerLayout.closeDrawer(mDrawerList);
                }
        );

        mDrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                R.string.drawer_open,
                R.string.drawer_close
        );

        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        if(newsList.isEmpty()) {
            NewsDownload();
        }

        newsArticleAdapter = new NewsArticleAdapter(this, newsArticleArrayList);
        viewPager = findViewById(R.id.viewpager);
        viewPager.setAdapter(newsArticleAdapter);

    }

    // selecting item from the drawer
    @SuppressLint("NotifyDataSetChanged")
    private void selectItemInDrawer(int position) {
        String srcNewsID = "";
        viewPager.setBackground(null);
        String currentSourceNews = newsDataList.get(position);
        newsArticleArrayList.clear();
        this.currentNewsName = currentSourceNews;
        for(News s: newsList) {
            if(s.getName().equals(currentSourceNews)){
                srcNewsID = s.getId();
                break;
            }
        }
        if(srcNewsID != null) {
            NewsID = srcNewsID;
            NewsArticleRunnable clr = new NewsArticleRunnable(this, NewsID);
            new Thread(clr).start();
        }
        else {
            Log.d(TAG, "selectItemInDrawer: current news source id is null");
        }
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    public void downloadFailed(){

    }
    @Override
    protected void onSaveInstanceState(Bundle bundle) {
        Log.d(TAG,"change");
        bundle.putStringArrayList("news_source_name", newsDataList);
        bundle.putString("current_news_src_id", NewsID);
        bundle.putString("current_news_src_name", currentNewsName);
        bundle.putStringArrayList("news", newsDataListAll);
        NewsArticleRunnable clr = new NewsArticleRunnable(this, NewsID);
        new Thread(clr).start();
        super.onSaveInstanceState(bundle);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        viewPager.setBackground(null);
        newsDataList = savedInstanceState.getStringArrayList("news_source_name");
        NewsID = savedInstanceState.getString("current_news_src_id");
        currentNewsName = savedInstanceState.getString("current_news_src_name");
        setTitle(currentNewsName);
        newsDataListAll = savedInstanceState.getStringArrayList("news");
        NewsArticleRunnable clr = new NewsArticleRunnable(this, NewsID);
        new Thread(clr).start();
    }

    public void NewsDownload() {
        String DATA_URL = "https://newsapi.org/v2/sources?apiKey=6841e0a3d0d94044a14d0e7ae8fbda5b";
        Uri.Builder buildURL = Uri.parse(DATA_URL).buildUpon();
        String urlToUse = buildURL.build().toString();
        RequestQueue queue = Volley.newRequestQueue(this);

        Response.Listener<JSONObject> listener = response -> {
            try {
                JSONArray sources = response.getJSONArray("sources");
                for(int i = 0; i < sources.length(); i++) {
                    JSONObject jsonNews = (JSONObject) sources.get(i);
                    String id = jsonNews.getString("id");
                    String name = jsonNews.getString("name");
                    String category = jsonNews.getString("category");
                    News newsdata = new News(id, name, category);
                    if(!listofNews.contains(category)) {
                        listofNews.add(category);
                    }
                    if(!NewsNames.contains(name)) {
                        NewsNames.add(name);
                    }
                    news.add(newsdata);
                }
                runOnUiThread(() -> {
                    newsNames(NewsNames);
                    NewsOverview(listofNews);
                    UpdateData(news);
                });
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        };
        Response.ErrorListener error = error1 -> {
            try {
                JSONObject jsonObject = new JSONObject(new String(error1.networkResponse.data));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        };
        JsonObjectRequest jsonObjectRequest =
                new JsonObjectRequest(Request.Method.GET, urlToUse,
                        null, listener, error) {
                    @Override
                    public Map<String, String> getHeaders() {
                        HashMap<String, String> headers = new HashMap<>();
                        headers.put("User-Agent", "");
                        return headers;
                    }
                };
        queue.add(jsonObjectRequest);
    }

    public void newsNames(ArrayList<String> newsName) {
        if(newsName == null)
            return;
        if(newsDataList.isEmpty()) {
            newsDataList = newsName;
        }
        newsDataListAll.addAll(newsName);
        mDrawerList.setAdapter(new ArrayAdapter<>(this, R.layout.drawer_item, newsDataList));

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
        setTitle("News Gateway" + " (" + newsDataList.size() + ") ");
    }

    public void NewsOverview(ArrayList<String> list) {
        if(list == null) return;
        list1= list;
    }

    public void UpdateData(ArrayList<News> news) {
        if(news == null) {
            return;
        }
        newsList = news;

        HashSet<String> temp = new HashSet<>();
        for (News n : news) {
            String category = n.getCategory();
            String name = n.getName();
            if (!newsData.containsKey(n.getCategory())) {
                newsData.put(n.getCategory(), new HashSet<>());
            }
            Objects.requireNonNull(newsData.get(category)).add(name);

            if (!newsArticleData.containsKey(n.getCategory())) {
              newsArticleData.put(n.getCategory(), new ArrayList<>());
            }

            temp.add(name);
        }
        newsData.put("All",temp);

        ArrayList<String> tempList = new ArrayList<>(newsData.keySet());
        for (String s : tempList)
            opt_menu.add(s);

        newsDataList.addAll(new HashSet<>());
        Log.d(TAG,"newsList"+newsList);
        arrayAdapter = new ArrayAdapter<>(this, R.layout.drawer_item, newsDataList);
        mDrawerList.setAdapter(arrayAdapter);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateData1(ArrayList<NewsArticle> articleList) {
        newsArticleArrayList.clear();
        setTitle(currentNewsName);
        newsArticleArrayList.addAll(articleList);
        newsArticleAdapter.notifyDataSetChanged();
        viewPager.setCurrentItem(0);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        opt_menu = menu;
        return true;
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        newsDataList.clear();
        HashSet<String> clist = newsData.get(item.getTitle().toString());
        if (clist != null) {
            newsDataList.addAll(clist);
        }
        setTitle("News Gateway" + " (" + newsDataList.size() + ") ");
        arrayAdapter.notifyDataSetChanged();
        return super.onOptionsItemSelected(item);
    }

}