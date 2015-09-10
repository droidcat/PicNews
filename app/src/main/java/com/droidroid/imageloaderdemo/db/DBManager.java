package com.droidroid.imageloaderdemo.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.droidroid.imageloaderdemo.Constants;
import com.droidroid.imageloaderdemo.projection.news.LinksNews;
import com.droidroid.imageloaderdemo.projection.news.ItemNews;
import com.droidroid.imageloaderdemo.projection.slide.SlideSlide;

import java.util.ArrayList;

/**
 * Created by Droidroid on 2015/8/19.
 */
public class DBManager {

    private SQLiteDatabase db;

    private static DBManager dbManager;

    // 单例模式
    private DBManager(Context context) {
        DBHelper helper = new DBHelper(context, Constants.DB_NAME, null, Constants.DB_VERSION);
        db = helper.getWritableDatabase();
    }

    public synchronized static DBManager getInstance(Context context) {
        if (dbManager == null) {
            dbManager = new DBManager(context);
        }
        return dbManager;
    }

    // 每次查询15条新闻记录
    public ArrayList<ItemNews> getNewses() {
        Cursor cursor = db.rawQuery("SELECT * FROM news ORDER BY updatetime DESC LIMIT 15"
                , new String[]{});
        ArrayList<ItemNews> list = new ArrayList<>();
        ItemNews news;
        while (cursor.moveToNext()) {
            news = new ItemNews();
            news.setTitle(cursor.getString(cursor.getColumnIndex("title")));
            news.setUpdateTime(cursor.getString(cursor.getColumnIndex("updatetime")));
            news.setThumbnail(cursor.getString(cursor.getColumnIndex("thumbnail")));

            ArrayList<LinksNews> linksArrayList = new ArrayList<>();
            LinksNews links = new LinksNews();
            links.setUrl(cursor.getString(cursor.getColumnIndex("url")));
            linksArrayList.add(links);
            news.setLinks(linksArrayList);

            list.add(news);
        }
        return list;
    }

    // 将新闻保存在数据库
    public void saveNewses(ArrayList<ItemNews> newses) {
        ContentValues values;
        ItemNews news;
        int size = newses.size();
        for (int i = size - 1; i >= 0; i--) {
            news = newses.get(i);
            news.setLinks(newses.get(i).getLinks());
            values = new ContentValues();
            values.put("title", news.getTitle());
            values.put("thumbnail", news.getThumbnail());

            values.put("url", news.getLinks().get(0).getUrl());
            values.put("updatetime", news.getUpdateTime());
            db.insert("news", null, values);
        }
    }

    // 取得新闻幻灯片的记录
    public ArrayList<SlideSlide> getSlide(String url) {
        ArrayList<SlideSlide> list = new ArrayList<>();
        SlideSlide slide;
        Cursor cursor = db.rawQuery("select image,description from slide where url = ?", new String[]{url});
        while (cursor.moveToNext()) {
            slide = new SlideSlide();
            slide.setImage(cursor.getString(cursor.getColumnIndex("image")));
            slide.setTitle(cursor.getString(cursor.getColumnIndex("description")));
            list.add(slide);
        }
        return list;
    }

    // 保存新闻幻灯片的记录
    public void saveSlide(ArrayList<SlideSlide> slides,String url) {
        ContentValues values;
        for (SlideSlide slide : slides) {
            values = new ContentValues();
            values.put("url", url);
            values.put("image", slide.getImage());
            values.put("description", slide.getTitle());
            db.insert("slide", null, values);
        }
    }
}
