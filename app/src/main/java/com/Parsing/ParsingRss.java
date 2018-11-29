package com.Parsing;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;

import com.Entites.Article;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import khaled.newsapp.R;

/**
 * Created by majdichaabene on 10/1/15.
 */


public class ParsingRss extends
        AsyncTask<Void, Void, List<Article>> {

    private String getAndroidPitRssFeed(String feedLink) throws IOException {
        InputStream in = null;
        String rssFeed = null;
        Log.e("KHALED URL", "getAndroidPitRssFeed: "+feedLink );
        try {
            URL url = new URL(feedLink);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            in = conn.getInputStream();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            for (int count; (count = in.read(buffer)) != -1; ) {
                out.write(buffer, 0, count);
            }
            byte[] response = out.toByteArray();
            rssFeed = new String(response, "UTF-8");
        } finally {
            if (in != null) {
                in.close();
            }
        }
        return rssFeed;
    }

    public ParsingRss() {

    }

    private SwipeRefreshLayout swipeRefreshLayout;
    private Context context;
    private String feedLink;
    private int category;

    public ParsingRss(Context context, SwipeRefreshLayout swipeRefreshLayout) {
        this.context = context;
        this.swipeRefreshLayout = swipeRefreshLayout;
    }

    @Override
    protected List<Article> doInBackground(Void... voids) {
        List<Article> result = null;
        try {
            String feed = getAndroidPitRssFeed(feedLink);
            result = parse(feed);
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private List<Article> parse(String rssFeed)
            throws XmlPullParserException, IOException {
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        XmlPullParser xpp = factory.newPullParser();
        xpp.setInput(new StringReader(rssFeed));
        xpp.nextTag();
        return readRss(xpp);
    }

    private List<Article> readRss(XmlPullParser parser)
            throws XmlPullParserException, IOException {
        List<Article> items = new ArrayList();
        parser.require(XmlPullParser.START_TAG, null, "rss");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("channel")) {
                items.addAll(readChannel(parser));
            } else {
                skip(parser);
            }
        }
        return items;
    }

    private List<Article> readChannel(XmlPullParser parser)
            throws IOException, XmlPullParserException {
        List<Article> items = new ArrayList();
        parser.require(XmlPullParser.START_TAG, null, "channel");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("item")) {
                items.add(readItem(parser));
            } else {
                skip(parser);
            }
        }
        return items;
    }

    private Article readItem(XmlPullParser parser)
            throws XmlPullParserException, IOException {
        Realm realm = Realm.getInstance(context);
        Article article = new Article();
        parser.require(XmlPullParser.START_TAG, null, "item");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("title")) {
                article.setTitle(readTitle(parser));
                article.setCategory(category);
            } else if (name.equals("pubDate")) {
                String s = readpubDate(parser);
                article.setPubDate(s);
            } else if (name.equals("description")) {
                article.setImage(readImage(parser));
            } else if (name.equals("link")) {
                article.setLink(readLink(parser));
            } else {
                skip(parser);
            }

        }
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(article);
        realm.commitTransaction();
        return article;
    }

    // Processes title tags in the feed.
    private String readTitle(XmlPullParser parser) throws IOException,
            XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, "title");
        String title = readText(parser);
        parser.require(XmlPullParser.END_TAG, null, "title");
        return title;
    }

    private String readLink(XmlPullParser parser) throws IOException,
            XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, "link");
        String title = readText(parser);
        parser.require(XmlPullParser.END_TAG, null, "link");
        return title;
    }

    private String readImage(XmlPullParser parser) throws IOException,
            XmlPullParserException {

        parser.require(XmlPullParser.START_TAG, null, "description");
        String title = readText(parser);
        title = title.substring(title.indexOf("http"));
        title = title.substring(0, title.indexOf("\""));
        Log.i("\\\\\\\\\\\\\\", title);
        parser.require(XmlPullParser.END_TAG, null, "description");
        return title;
    }

    private String readpubDate(XmlPullParser parser) throws IOException,
            XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, "pubDate");
        String title = readText(parser);
        parser.require(XmlPullParser.END_TAG, null, "pubDate");
        return title;
    }

    private String readText(XmlPullParser parser) throws IOException,
            XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    private void skip(XmlPullParser parser) throws XmlPullParserException,
            IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }

    @Override
    protected void onPostExecute(List<Article> rssFeed) {


    }

}

