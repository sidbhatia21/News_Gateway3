package com.example.newsgateway;

import android.net.Uri;

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
import java.util.Map;

public class NewsArticleRunnable implements Runnable{
    private final ArrayList<NewsArticle> currentNewsArticleList = new ArrayList<>();

    private final MainActivity mainActivity;
    private static final String DATA_URL = "https://newsapi.org/v2/top-headlines";
    private static final String apiKey = "6841e0a3d0d94044a14d0e7ae8fbda5b";
    private final String NewsSrcId;

    NewsArticleRunnable(MainActivity mainActivity,String NewsSrdId) {
        this.mainActivity = mainActivity;
        this.NewsSrcId=NewsSrdId;
    }

    @Override
    public void run() {

        Uri.Builder buildURL = Uri.parse(DATA_URL).buildUpon();
        buildURL.appendQueryParameter("sources", NewsSrcId);
        buildURL.appendQueryParameter("apikey", apiKey);
        String urlToUse = buildURL.build().toString();
        currentNewsArticleList.clear();
        RequestQueue queue = Volley.newRequestQueue(mainActivity);                                // creating a request queue

        Response.Listener<JSONObject> listener =                                                // creating success listener which will call parseJson
                response -> ResultData(response.toString());

        Response.ErrorListener error = error1 -> {                                             // error listener
            JSONObject jsonObject;
            try {
                jsonObject = new JSONObject(new String(error1.networkResponse.data));
                parseJSON(null);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        };

        JsonObjectRequest jsonObjectRequest =                                                  // Request a string response from the provided URL.
                new JsonObjectRequest(Request.Method.GET, urlToUse,
                        null, listener, error){
                    @Override
                    public Map<String, String> getHeaders() {
                        HashMap<String, String> headers = new HashMap<>();
                        headers.put("User-Agent", "");
                        return headers;
                    }
                };

        queue.add(jsonObjectRequest);
    }

    private void ResultData(String s) {

        if (s == null) {
            //Log.d(TAG, "handleResults: Failure in data download");
            mainActivity.runOnUiThread(mainActivity::downloadFailed);
            return;
        }

        final ArrayList<NewsArticle> newsList = parseJSON(s);
        if (newsList == null) {
            mainActivity.runOnUiThread(mainActivity::downloadFailed);
            return;
        }

        mainActivity.runOnUiThread(() -> mainActivity.updateData1(newsList));
    }

    private ArrayList<NewsArticle> parseJSON(String s) {

        ArrayList<NewsArticle> newsList = new ArrayList<>();
        try {
            JSONObject JObjMain = new JSONObject(s);
            JSONArray articles = JObjMain.getJSONArray("articles");
            for (int i = 0; i < articles.length(); i++) {
                JSONObject jsonStories = (JSONObject) articles.get(i);
                String author = jsonStories.getString("author");
                String title = jsonStories.getString("title");
                String description = jsonStories.getString("description");
                String url = jsonStories.getString("url");
                String urlToImage = jsonStories.getString("urlToImage");
                String publishedAt = jsonStories.getString("publishedAt");

                newsList.add(
                        new NewsArticle(author,title,description,url,urlToImage,publishedAt));
            }
            return newsList;
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
