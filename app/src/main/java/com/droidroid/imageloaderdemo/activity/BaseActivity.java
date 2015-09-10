package com.droidroid.imageloaderdemo.activity;

import android.app.Activity;
import com.nostra13.universalimageloader.core.ImageLoader;


/**
 * Created by Droidroid on 2015/8/19.
 */
public class BaseActivity extends Activity {

    protected ImageLoader imageLoader = ImageLoader.getInstance();
}
