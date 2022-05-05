package com.example.newsgateway;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class NewsArticleAdapter extends RecyclerView.Adapter<NewsArticleViewHolder> {

    private final ArrayList<NewsArticle> newsArticlesList;
    private final MainActivity mainActivity;

    public NewsArticleAdapter(MainActivity mainActivity, ArrayList<NewsArticle> newsArticlesList) {
        this.mainActivity = mainActivity;
        this.newsArticlesList = newsArticlesList;
    }

    @NonNull
    @Override
    public NewsArticleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NewsArticleViewHolder(
                LayoutInflater.from(parent.getContext()).
                        inflate(R.layout.news_article, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull NewsArticleViewHolder holder, int position) {

        NewsArticle newsArticle = newsArticlesList.get(position);
        Date dt;
        String publishedDt = null;

        String publishedDate = newsArticle.getPublishedAtDate();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH);

        try {
            dt = format.parse(publishedDate);
            SimpleDateFormat changedDateFmt = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.ENGLISH);
            if (dt != null) {
                publishedDt = changedDateFmt.format(dt);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        holder.date.setText(publishedDt);

        String author = newsArticle.getAuthor();
        if(author.isEmpty() || author.equals("null")) {
            holder.author.setVisibility(View.GONE);
        }
        else {
            holder.author.setText(newsArticle.getAuthor());
        }

        ImageView imageView = holder.image;
        String Image1 = newsArticle.getUrlToImage();
        if(Image1.equals("null")) {
            imageView.setImageResource(R.drawable.noimage);
        }
        else {
            Picasso.get().load(Image1).fit().error(R.drawable.brokenimage).placeholder(R.drawable.loading).into(imageView, new Callback() {
                @Override
                public void onSuccess() {
                }
                @Override
                public void onError(Exception e) {
                }
            });
        }
        holder.image.setOnClickListener(v -> openURL(newsArticle.getUrl()));

        holder.description.setText(newsArticle.getDescription());
        holder.description.setMovementMethod(new ScrollingMovementMethod());
        holder.description.setOnClickListener(v -> openURL(newsArticle.getUrl()));

        holder.page_number.setText(String.format(
                Locale.getDefault(),"%d of %d", (position+1), newsArticlesList.size()));

        holder.title1.setText(newsArticle.getTitle());
        holder.title1.setOnClickListener(v -> openURL(newsArticle.getUrl()));

    }

    @Override
    public int getItemCount() {
        return newsArticlesList.size();
    }

    public void openURL(String websiteURL) {
        Uri Uri1 = Uri.parse(websiteURL);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri1);
        mainActivity.startActivity(intent);
    }
}
