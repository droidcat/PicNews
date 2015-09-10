package com.droidroid.imageloaderdemo.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.droidroid.imageloaderdemo.Constants;
import com.droidroid.imageloaderdemo.db.DBManager;
import com.droidroid.imageloaderdemo.R;
import com.droidroid.imageloaderdemo.projection.news.ItemNews;
import com.droidroid.imageloaderdemo.util.NetworkUtil;
import com.droidroid.imageloaderdemo.util.ActivityCollector;

import java.util.ArrayList;

/**
 * Created by Droidroid on 2015/8/20.
 */
public class WelcomeActivity extends Activity {


    // 进度条
    private ProgressBar bar;

    // 显示加载状态
    private TextView textView;

    // 存放结果
    private ArrayList<ItemNews> result;

    private SharedPreferences preferences;

    private SharedPreferences.Editor editor;

    // 数据库管理类
    private DBManager dbManager;

    // app使用次数
    private long count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_welcome_layout);
        // 收集Activity
        ActivityCollector.addActivity(this);

        // 实例化数据库管理类
        dbManager = DBManager.getInstance(WelcomeActivity.this);

        // 进度条
        bar = (ProgressBar) findViewById(R.id.progress_bar);
        bar.setMax(100);
        bar.setProgress(0);

        // 状态显示
        textView = (TextView) findViewById(R.id.progress_text);


        // 如果网络可用并且是第一次使用app
        if (NetworkUtil.isNetWorkAvailable(WelcomeActivity.this) && isFirstOpen()) {
            // 开启异步任务利用网络加载数据
            new Task().execute(Constants.FIRST_NEWS_ITEM_URL);
        }
        // 如果网络不可用并且是第一次使用app
        else if (!NetworkUtil.isNetWorkAvailable(WelcomeActivity.this) && isFirstOpen()) {
            // 重置开启的次数
            resetCount();
            // 显示警告对话框
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    showErrorDialog(WelcomeActivity.this);
                }
            }, 3000);
        }
        // 其余情况尝试从数据库取出数据
        else {
            // 取出最新的15条新闻
            result = dbManager.getNewses();
            if (result != null && result.size() > 0) {

                Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                // 注意News类需要实现Parcelable接口
                intent.putParcelableArrayListExtra("news", result);
                intent.putExtra("count", count);
                startActivity(intent);
                WelcomeActivity.this.finish();

            }
            // 其余的情况
            else {
                if (NetworkUtil.isNetWorkAvailable(WelcomeActivity.this)) {
                    new Task().execute(Constants.FIRST_NEWS_ITEM_URL);
                } else {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            showErrorDialog(WelcomeActivity.this);
                        }
                    }, 3000);
                }
            }
        }
    }

    // 第一次开启app时网络无连接，则将app开启次数重置为0
    private void resetCount() {
        preferences = getSharedPreferences("picnews", MODE_PRIVATE);
        editor = preferences.edit();
        editor.putLong("count", 0);
        editor.commit();
    }

    // 显示错误对话框
    private void showErrorDialog(Context context) {
        new AlertDialog.Builder(context)
                .setTitle("网络错误")
                .setMessage("网络连接失败，请确认网络连接")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
//                            android.os.Process.killProcess(android.os.Process.myPid());
//                            System.exit(0);
                        ActivityCollector.finishAll();
                    }
                }).show();
    }

    // 判断是否初次使用
    public boolean isFirstOpen() {
        preferences = getSharedPreferences("picnews", MODE_PRIVATE);
        count = preferences.getLong("count", 0);
        editor = preferences.edit();
        editor.putLong("count", ++count);
        editor.commit();
        if (count > 1) {
            return false;
        }
        return true;
    }


    /**
     * 异步任务
     */
    class Task extends AsyncTask<String, Integer, ArrayList<ItemNews>> {

        int progress = 0;

        @Override
        protected void onPostExecute(ArrayList<ItemNews> newses) {
            textView.setText("加载完成");

            if (newses != null && newses.size() > 0) {
                // 将结果存入数据库
                dbManager.saveNewses(newses);
            }
            Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
            intent.putParcelableArrayListExtra("news", newses);
            startActivity(intent);
            WelcomeActivity.this.finish();
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            bar.setProgress(values[0]);
        }

        @Override
        protected ArrayList<ItemNews> doInBackground(String... params) {
            String url0 = params[0];
            String[] urls = new String[15];
            String[] jsons = new String[15];
            ItemNews newsItem;
            ArrayList<ItemNews> newsList = new ArrayList<>();

            int i = 0;
            while (i < 15) {
                urls[i] = url0.substring(0, url0.length()) + (i + 1);
                jsons[i] = NetworkUtil.sendRequest(urls[i]);
                newsItem = NetworkUtil.convertJsonToNews(jsons[i]);
                newsList.add(newsItem);
                i++;
                // 更新进度条
                progress += 7;
                publishProgress(progress);
            }


            return newsList;
        }
    }
}
