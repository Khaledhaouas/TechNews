package com.Adapters;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.Entites.Article;
import com.kmshack.newsstand.ArticleActivity;
import com.utils.ImageLoader;

import java.util.List;

import khaled.newsapp.R;


public class ArticleCustomAdapter extends ArrayAdapter<Article> {

    Context context;
    int layoutResourceId;
    List<Article> items = null;
    private LayoutInflater inflater;
    ImageLoader imgloader;

    public ArticleCustomAdapter(Context context, int layoutResourceId, List<Article> items) {
        super(context, layoutResourceId, items);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.items = items;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imgloader = new ImageLoader(context);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ArticleHolder holder = null;

        if (row == null) {
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new ArticleHolder();
            holder.image = (ImageView) row.findViewById(R.id.post_image);
            holder.titre = (TextView) row.findViewById(R.id.post_title);
            holder.date = (TextView) row.findViewById(R.id.post_date);
            holder.relative = (RelativeLayout) row.findViewById(R.id.item);
            row.setTag(holder);
        } else {
            holder = (ArticleHolder) row.getTag();
        }


        final Article item = items.get(position);

        imgloader.DisplayImage(item.getImage(), holder.image);
        holder.titre.setText(Html.fromHtml(item.getTitle()));
        holder.date.setText(item.getPubDate());


        row.findViewById(R.id.sharebtn).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        shareTextUrl(item.getLink());
                    }
                }
        );

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(context, ArticleActivity.class);
                i.putExtra("url", item.getLink());
                i.putExtra("category", item.getCategory());
                context.startActivity(i);
            }
        });
        holder.titre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(context, ArticleActivity.class);
                i.putExtra("url", item.getLink());
                i.putExtra("category", item.getCategory());
                context.startActivity(i);
            }
        });

        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.card_animation);
        holder.relative.startAnimation(animation);


        return row;
    }

    class ArticleHolder {

        ImageView image;
        TextView titre;
        TextView date;
        RelativeLayout relative;


    }

    private void shareTextUrl(String url) {
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

        // Add data to the intent, the receiving app will decide
        // what to do with it.
        share.putExtra(Intent.EXTRA_SUBJECT, "Title Of The Post");
        share.putExtra(Intent.EXTRA_TEXT, url);

        context.startActivity(Intent.createChooser(share, "Share link!"));
    }


}