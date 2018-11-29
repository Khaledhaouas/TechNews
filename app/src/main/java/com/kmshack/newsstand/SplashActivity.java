package com.kmshack.newsstand;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;

import com.Entites.Article;
import com.Parsing.ParsingRss;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.kmshack.newsstand.MainActivity;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import io.realm.Realm;
import io.realm.RealmResults;
import khaled.newsapp.R;


public class SplashActivity extends Activity {
    private InterstitialAd interstitial;
    private static int SPLASH_TIME_OUT = 3000;
    private static final String TAG = "SplashActivity";

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_splash);


        new ParsingRss(this).execute();


// Créez l'interstitiel.

        interstitial = new InterstitialAd(this);
        interstitial.setAdUnitId("ca-app-pub-6914440917404990/4977628467");

        // Créez la demande d'annonce.
        AdRequest adRequest2 = new AdRequest.Builder().addTestDevice("F2A71918D03135A22715F40EB7933EEB").build();

        // Lancez le chargement de l'interstitiel.
        interstitial.loadAd(adRequest2);

    }

    // Appelez displayInterstitial() une fois que vous êtes prêt à diffuser un interstitiel.
    public void displayInterstitial() {
        if (interstitial.isLoaded()) {
            interstitial.show();
        }
    }


    public class ParsingRss extends
            AsyncTask<Void, Void, List<Article>> {

        private String getAndroidPitRssFeed(String feedLink) throws IOException {
            InputStream in = null;
            String rssFeed = null;
            try {
                URL url = new URL(
                        feedLink);
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

        private Context context;
        private String feedLink;
        private int category;

        public ParsingRss(Context c) {
            context = c;

        }

        @Override
        protected List<Article> doInBackground(Void... voids) {
            List<Article> result = null;
            String strings[] = {"http://feeds.feedburner.com/Techcrunch/google?format=xml"
                    , "http://feeds.feedburner.com/Techcrunch/Yahoo?format=xml"
                    , "http://feeds.feedburner.com/Techcrunch/facebook?format=xml"
                    , "http://feeds.feedburner.com/Techcrunch/twitter?format=xml"
                    , "http://feeds.feedburner.com/Techcrunch/android?format=xml"
                    , "http://feeds.feedburner.com/Techcrunch/Samsung?format=xml"
                    , "http://feeds.feedburner.com/Techcrunch/apple?format=xml"
                    , "http://feeds.feedburner.com/Techcrunch/microsoft?format=xml"
                    , "http://feeds.feedburner.com/Techcrunch/Amazon?format=xml"
            };
            for (int i = 0; i < strings.length; i++) {

                category = i;
                try {
                    String feed = getAndroidPitRssFeed(strings[i]);
                    result = parse(feed);


                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
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
                Log.e(TAG, "readChannel: " + name);

                if (name.equals("title")) {
                    article.setTitle(readTitle(parser));
                    article.setCategory(category);
                } else if (name.equals("pubDate")) {
                    String s = readpubDate(parser);
                    article.setPubDate(s);
                } else if (name.equals("content:encoded")) {
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

            parser.require(XmlPullParser.START_TAG, null, "content:encoded");
            String title = readText(parser);
            if (title.contains("https://techcrunch.com/wp-content")) {
                title = title.substring(title.indexOf("https://techcrunch.com/wp-content"));
                title = title.substring(0, title.indexOf("alt") - 2);
            } else {
                switch (category) {
                    case 0:
                        title = "https://pbs.twimg.com/profile_images/638746415901114368/e4h_VW4A.png";
                        break;
                    case 1:
                        title = "https://images.techhive.com/images/article/2013/06/yahoo_logo_large-100044513-large.jpg";
                        break;
                    case 2:
                        title = "https://marketingland.com/wp-content/ml-loads/2015/10/facebook-news-articles2-ss-1920-800x450.jpg";
                        break;
                    case 3:
                        title = "https://cdn.mos.cms.futurecdn.net/e77c20d223ec028542bdb004a02b38f9-320-80.jpg";
                        break;
                    case 4:
                        title = "https://static.makeuseof.com/wp-content/uploads/2014/10/android-news-reader-670x335.jpg";
                        break;
                    case 5:
                        title = "http://s7d2.scene7.com/is/image/SamsungUS/samsung-logo-1-1?$seo-twitter-card-jpg$";
                        break;
                    case 6:
                        title = "https://www.compareraja.in/blog/wp-content/uploads/2017/11/cover-evolution-of-apple-logo.jpg";
                        break;
                    case 7:
                        title = "https://searchengineland.com/figz/wp-content/seloads/2014/07/microsoft-logo-blue-1920-800x450.png";
                        break;
                    case 8:
                        title = "https://assets1.csnews.com/files/styles/content_sm/s3/2017-12/amazon%20logo-TEASER_0.jpg?itok=XZfXEEbA";
                        break;
                    default:
                        break;


                }
//                title = "https://myvetahealth.com/wp-content/uploads/2018/10/TechCrunch-Logo.jpg";
            }
            Log.i("\\\\\\\\\\\\\\", title);
            parser.require(XmlPullParser.END_TAG, null, "content:encoded");
            return title;
//            return "https://techcrunch.com/wp-content/uploads/2018/11/DSCF3168.jpg";
        }

        private String readpubDate(XmlPullParser parser) throws IOException,
                XmlPullParserException {
            parser.require(XmlPullParser.START_TAG, null, "pubDate");
            String title = readText(parser);
            parser.require(XmlPullParser.END_TAG, null, "pubDate");
            title = title.substring(0, title.indexOf("+") - 4);
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
            Intent mainClass = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(mainClass);
            displayInterstitial();
            finish();
        }

    }


}