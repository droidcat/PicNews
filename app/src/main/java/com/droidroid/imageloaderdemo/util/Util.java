package com.droidroid.imageloaderdemo.util;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.droidroid.imageloaderdemo.projection.news.ItemNews;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Droidroid on 2015/8/18.
 */
public class Util {



/*    public static ArrayList<ItemNews> convertJsonToList(String jsonStr){
        ArrayList<ItemNews> list = new ArrayList<>();
        ItemNews news;
        String title;
        String thumbnail;
        String url;
        String updateTime;
        try {
            JSONObject firstObject = new JSONObject(jsonStr);
            JSONObject bodyObject = firstObject.getJSONObject("body");
            JSONArray itemArray = bodyObject.getJSONArray("item");
            for (int i = 0; i < itemArray.length();i++){
                news = new ItemNews();
                JSONObject item = itemArray.getJSONObject(i);
                // 新闻标题
                title = item.getString("title");
                // 新闻缩略图
                thumbnail = item.getString("thumbnail");
                // 新闻url
                url = item.getJSONArray("links").getJSONObject(0).getString("url");
                // 新闻发布时间
                updateTime = item.getString("updateTime");
                news.setTitle(title);
                news.setThumbnail(thumbnail);
                news.getLinks().get(0).setUrl(url);
                news.setUpdateTime(updateTime);
                list.add(news);
            }
            return list;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }*/

/*    public static ArrayList<ItemNews> refreshJsonToList(String jsonStr,Date date){
        ArrayList<ItemNews> list = new ArrayList<>();
        ItemNews news;
        String title;
        String thumbnail;
        String url;
        String updateTime;
        try {
            JSONObject firstObject = new JSONObject(jsonStr);
            JSONObject bodyObject = firstObject.getJSONObject("body");
            JSONArray itemArray = bodyObject.getJSONArray("item");
            for (int i = 0; i < itemArray.length();i++){
                news = new ItemNews();
                JSONObject item = itemArray.getJSONObject(i);
                updateTime = item.getString("updateTime");
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date time = format.parse(updateTime,new ParsePosition(0));
                Log.d(IS_BEFORE,String.valueOf(time.before(date)));
                Log.d(IS_EQUALS,String.valueOf(time.equals(date)));
                if (time.before(date)||time.equals(date)){
                    return list;
                }
                title = item.getString("title");
                thumbnail = item.getString("thumbnail");
                url = item.getJSONArray("links").getJSONObject(0).getString("url");
                news.setTitle(title);
                news.setThumbnail(thumbnail);
                news.getLinks().get(0).setUrl(url);
                news.setUpdateTime(updateTime);
                list.add(news);
            }
            Log.d("Util-list",list.toString());
            return list;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }*/

/*    public static ArrayList<Slide> convertJsonToList2(String jsonStr,String url){
        ArrayList<Slide> list = new ArrayList<>();
        Slide slide;
        String imageUrl;
        String description;
        try{
            JSONObject mainObject = new JSONObject(jsonStr);
            JSONObject bodyObject = mainObject.getJSONObject("body");
            JSONArray slidesArray = bodyObject.getJSONArray("slides");
            for (int i = 0; i < slidesArray.length(); i++){
                slide = new Slide();
                imageUrl = slidesArray.getJSONObject(i).getString("image");
                description = slidesArray.getJSONObject(i).getString("description");
                slide.setImageUrl(imageUrl);
                slide.setDescription(description);
                slide.setUrl(url);
                list.add(slide);
            }
            return list;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }*/

}
