package com.droidroid.imageloaderdemo.projection.news;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Droidroid on 2015/9/10.
 */
public class LinksNews implements Parcelable{


    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(url);
    }

    public static final Parcelable.Creator CREATOR = new Creator(){

        @Override
        public Object createFromParcel(Parcel source) {
            LinksNews links = new LinksNews();
            links.url =  source.readString();
            return links;
        }

        @Override
        public Object[] newArray(int size) {
            return new Object[size];
        }
    };
}
