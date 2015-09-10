package com.droidroid.imageloaderdemo;

import android.app.Application;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

/**
 * Created by Droidroid on 2015/8/18.
 */
public class AppApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ImageLoaderConfiguration conf = ImageLoaderConfiguration.createDefault(getApplicationContext());
        ImageLoader.getInstance().init(conf);

    }
}
