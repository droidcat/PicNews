package com.droidroid.imageloaderdemo.util;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Droidroid on 2015/8/20.
 */
public class ActivityCollector {
    public static List<Activity> activities = new ArrayList<>();
    public static void addActivity(Activity activity){
        activities.add(activity);
    }
    public static void removeActivity(Activity activity){
        activities.remove(activity);
    }
    public static void finishAll(){
        for (Activity activity : activities) {
            if (!activity.isFinishing()){
                activity.finish();
            }
        }
    }
}
