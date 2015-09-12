package com.droidroid.imageloaderdemo.util;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.droidroid.imageloaderdemo.projection.news.BodyNews;
import com.droidroid.imageloaderdemo.projection.news.MainNews;
import com.droidroid.imageloaderdemo.projection.news.ItemNews;
import com.droidroid.imageloaderdemo.projection.slide.MainSlide;
import com.droidroid.imageloaderdemo.projection.slide.SlideSlide;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;

/**
 * Created by Droidroid on 2015/9/10.
 */
public class NetworkUtil {

    public static String sendRequest(String urlStr) {
        BufferedReader reader;
        InputStream is = null;
        HttpURLConnection connection = null;
        try {
            URL url = new URL(urlStr);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            is = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(is, "utf-8"));
            String line = null;
            StringBuilder result = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
            return result.toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return null;
    }


    public static ArrayList<ItemNews> convertJsonToNewses(String jsonStr) {
        Gson gson = new Gson();
        MainNews mainNews = gson.fromJson(jsonStr, MainNews.class);
        BodyNews bodyNews = mainNews.getBody();
        ArrayList<ItemNews> newsArrayList = bodyNews.getItem();

        Log.d("gson_test", newsArrayList.get(0).getLinks().get(0).getUrl() + "konf");

        return newsArrayList;
    }

    public static ArrayList<SlideSlide> convertJsonToSlides(String jsonStr) {
        Gson gson = new Gson();
        MainSlide mainSlide = gson.fromJson(jsonStr, MainSlide.class);
        ArrayList<SlideSlide> slide = mainSlide.getBody().getSlides();
        return slide;
    }

    // 判断网络是否可用
    public static boolean isNetWorkAvailable(Activity activity) {

        Context context = activity.getApplicationContext();
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        return (info != null && info.isAvailable());
        /*
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(
                Context.CONNECTIVITY_SERVICE
        );
        if (connectivityManager == null) {
            return false;
        } else {
            NetworkInfo[] networkInfos = connectivityManager.getAllNetworkInfo();
            if (networkInfos != null && networkInfos.length > 0) {
                for (int i = 0; i < networkInfos.length; i++) {
                    if (networkInfos[i].getState() == NetworkInfo.State.CONNECTED) {
                        Log.d("panduan","NetWorkAvaliable");
                        return true;
                    }
                }
            }
        }
        return false;*/
    }


    public static ItemNews convertJsonToSingleNews(String jsonSingle) {
        Gson gson = new Gson();
        MainNews mainNews = gson.fromJson(jsonSingle, MainNews.class);
        BodyNews bodyNews = mainNews.getBody();
        ArrayList<ItemNews> newsArrayList = bodyNews.getItem();

        Log.d("gson_test", newsArrayList.get(0).getLinks().get(0).getUrl() + "konf");

        return newsArrayList.get(0);
    }
}
