package com.example.newsgateway;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class NewsArticleViewHolder extends RecyclerView.ViewHolder {

    TextView title1;
    TextView date;
    TextView author;
    ImageView image;
    TextView description;
    TextView page_number;

    public NewsArticleViewHolder(@NonNull View itemView) {
        super(itemView);

        title1 = itemView.findViewById(R.id.title1);
        date = itemView.findViewById(R.id.date);
        author = itemView.findViewById(R.id.author);
        image = itemView.findViewById(R.id.image);
        description = itemView.findViewById(R.id.description);
        page_number = itemView.findViewById(R.id.page_number);
    }
}
