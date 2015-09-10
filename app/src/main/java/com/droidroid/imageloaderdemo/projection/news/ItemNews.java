package com.droidroid.imageloaderdemo.projection.news;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by Droidroid on 2015/9/10.
 */
public class ItemNews implements Parcelable{

    private String updateTime;

    private ArrayList<LinksNews> links;

    private String thumbnail;

    private String title;





    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public ArrayList<LinksNews> getLinks() {
        return links;
    }

    public void setLinks(ArrayList<LinksNews> links) {
        this.links = links;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(updateTime);
        dest.writeString(thumbnail);
        dest.writeString(title);
        dest.writeList(links);
    }

    public static final Parcelable.Creator CREATOR = new Creator() {
        @Override
        public Object createFromParcel(Parcel source) {
            ItemNews item = new ItemNews();
            item.updateTime = source.readString();
            item.thumbnail = source.readString();
            item.title = source.readString();
            item.links = new ArrayList<>();
            source.readList(item.links,getClass().getClassLoader());
            return item;
        }

        @Override
        public Object[] newArray(int size) {
            return new Object[size];
        }
    };
}
