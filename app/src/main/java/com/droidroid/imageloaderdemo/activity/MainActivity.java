package com.droidroid.imageloaderdemo.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.droidroid.imageloaderdemo.Constants;
import com.droidroid.imageloaderdemo.db.DBManager;
import com.droidroid.imageloaderdemo.R;
import com.droidroid.imageloaderdemo.projection.news.LinksNews;
import com.droidroid.imageloaderdemo.projection.news.ItemNews;
import com.droidroid.imageloaderdemo.util.NetworkUtil;
import com.droidroid.imageloaderdemo.view.RefreshLayout;
import com.droidroid.imageloaderdemo.view.RefreshLvLayout;
import com.droidroid.imageloaderdemo.util.ActivityCollector;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.PauseOnScrollListener;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import me.biubiubiu.justifytext.library.JustifyTextView;

public class MainActivity extends BaseActivity {

    // 显示图片的相关设置
    private DisplayImageOptions options;

    // 存放news
    private ArrayList<ItemNews> result;

    // 刷新标志
    private boolean wantToRefresh = false;

    // 加载标志
    private boolean wantToLoad = false;

    // 用于双击退出程序的判断
    private long waitTime = 0;

    // app启动次数
    private long count;

    // 数据库管理类
    private DBManager dbManager;

    // 新闻列表
    private ListView listView;

    // 继承自SwipeRefreshLayout
    private RefreshLvLayout refreshLvLayout;

    // 适配器
    private NewsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_main_list);

        // 为该activity添加管理
        ActivityCollector.addActivity(this);

        // 实例化数据库管理类
        dbManager = DBManager.getInstance(MainActivity.this);

        // 配置图片显示
        options = new DisplayImageOptions.Builder()
                .showStubImage(R.drawable.ic_stub)
                .showImageForEmptyUri(R.drawable.ic_empty)
                .showImageOnFail(R.drawable.ic_error)
                .cacheInMemory(true)                        // 内存缓存
                .cacheOnDisc(true)                          // sd卡缓存
                .displayer(new RoundedBitmapDisplayer(3))
                .build();

        // 新闻列表
        listView = (ListView) findViewById(R.id.list_main);

        // 实例化图片适配器
        adapter = new NewsAdapter();

        // 取得上个Activity传递的数据
        Intent intent = getIntent();
        result = intent.getParcelableArrayListExtra("news");
        count = intent.getLongExtra("count", 1);
        Log.d("app使用次数", String.valueOf(count));

        // 若取得的数据不为空，则更新列表
        if (result != null && result.size() > 0) {
            adapter.setData(result);
            listView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        } else {
            // 否则执行异步任务
            new NetTask().execute(Constants.FIRST_NEWS_ITEM_URL);
        }

        // 下拉刷新上拉加载布局
        refreshLvLayout = (RefreshLvLayout) findViewById(R.id.refresh_layout);
        refreshLvLayout.setColorSchemeColors(R.color.holo_purple, R.color.holo_orange_dark,
                R.color.holo_blue_bright, R.color.holo_orange_light);

        // 为下拉设置监听
        refreshLvLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // 在UI线程中执行
                refreshLvLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        // 希望更新
                        wantToRefresh = true;

                        // 网络有效且希望更新
                        if (NetworkUtil.isNetWorkAvailable(MainActivity.this) && wantToRefresh) {

                            //开启异步任务加载数据
                            new NetTask().execute(Constants.FIRST_NEWS_ITEM_URL);

                        } else {

                            Toast.makeText(MainActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
                            refreshLvLayout.setRefreshing(false);
                        }


                    }
                }, 1000);
            }
        });

        // 为上拉设置监听
        refreshLvLayout.setOnLoadListener(new RefreshLayout.OnLoadListener() {
            @Override
            public void onLoad() {
                // UI线程中执行
                refreshLvLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        // 希望加载
                        wantToLoad = true;

                        // 网络可用且希望加载
                        if (NetworkUtil.isNetWorkAvailable(MainActivity.this) && wantToLoad) {

                            // 开启异步任务加载数据
                            new NetTask().execute(Constants.FIRST_NEWS_ITEM_URL);
                        }

                    }
                }, 20);
            }
        });

        // 给下一个activity传递新闻详情的url
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, ShowNewsActivity.class);

                ArrayList<LinksNews> linksArrayList = result.get(position).getLinks();
                LinksNews links = linksArrayList.get(0);
                intent.putExtra("url", links.getUrl());
                startActivity(intent);
            }
        });
    }

//    @Override
//    public void onBackPressed() {
//
//        long currentTime = System.currentTimeMillis();
//        if (currentTime - waitTime > 3000) {
//            Toast.makeText(MainActivity.this, "再点一次退出", Toast.LENGTH_SHORT).show();
//            waitTime = currentTime;
//        } else {
//            ActivityCollector.finishAll();
//        }
//    }

    /**
     * 退出程序处理
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - waitTime) > 2500) {
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                waitTime = System.currentTimeMillis();
            } else {
                ActivityCollector.finishAll();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /*
     * 异步加载网络数据
     */
    class NetTask extends AsyncTask<String, Integer, ArrayList<ItemNews>> {

        @Override
        protected ArrayList<ItemNews> doInBackground(String... params) {

            /**
             * 希望更新
             */
            if (wantToRefresh && !wantToLoad) {
                String url0 = params[0];
                String updateTime;
                ItemNews newsItem;
                // 单条新闻url
                String urlSingle;
                // 单条新闻返回的json
                String jsonSingle;
                // 新闻索引
                int i = 0;
                // 列表首项或当前系统的时间，用于比较
                Date date;

                // 将时间字符串格式化为Date类
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                if (result != null && result.size() > 0) {
                    // 列表首项的时间
                    updateTime = result.get(0).getUpdateTime();
                    date = format.parse(updateTime, new ParsePosition(0));
                } else {
                    // 时间设置为一小时前
                    Date oneHourAgo = new Date(System.currentTimeMillis() - 1000 * 60 * 60);
                    String oneHourAgoStr = format.format(oneHourAgo);
                    date = format.parse(oneHourAgoStr, new ParsePosition(0));
                }

                // 更新的list，可能为空
                ArrayList<ItemNews> refreshedList = new ArrayList<>();

                while (true) {
                    urlSingle = url0.substring(0, url0.length()) + (i + 1);
                    jsonSingle = NetworkUtil.sendRequest(urlSingle);
                    newsItem = NetworkUtil.convertJsonToNews(jsonSingle);
                    updateTime = newsItem.getUpdateTime();

                    Date updateDate = format.parse(updateTime, new ParsePosition(0));
                    // 只需最新数据
                    if (updateDate.before(date) || updateDate.equals(date)) {
                        break;
                    }
                    refreshedList.add(newsItem);
                    i++;
                }

                if (refreshedList != null && refreshedList.size() > 0) {

                    // 将结果存入数据库
                    dbManager.saveNewses(refreshedList);
                }
                return refreshedList;

            }
            /**
             * 希望加载
             */
            else if (wantToLoad && !wantToRefresh) {

                String url0 = params[0];
                ItemNews newsItem;

                String urlSingle;
                String jsonSingle;

                // 列表最后一项的索引
                int lastIndex = result.size() - 1;

                // 最后一项的更新时间
                String lastItemUpdateTime = result.get(lastIndex).getUpdateTime();

                // 将时间字符串格式化为date类
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = format.parse(lastItemUpdateTime, new ParsePosition(0));

                // 更新的list，可能为空
                ArrayList<ItemNews> lodedList = new ArrayList<>();

                while (lodedList.size() < 5) {

                    urlSingle = url0.substring(0, url0.length()) + (lastIndex + 2);
                    jsonSingle = NetworkUtil.sendRequest(urlSingle);
                    newsItem = NetworkUtil.convertJsonToNews(jsonSingle);
                    String lastTime = newsItem.getUpdateTime();

                    Date lastDate = format.parse(lastTime, new ParsePosition(0));
                    if (lastDate.after(date) || lastDate.equals(date)) {
                        lastIndex++;
                        continue;
                    }
                    lodedList.add(newsItem);
                    lastIndex++;
                }
                return lodedList;
            }
            // 首次开启app时，若首页面没加载到数据，在此尝试加载
            if (count == 1) {

                String url0 = params[0];
                String urlSingle;
                String jsonSingle;
                ItemNews newsItem;
                int i = 0;

                // 更新的list，可能为空
                ArrayList<ItemNews> newsList = new ArrayList<>();

                while (i < 15) {
                    urlSingle = url0.substring(0, url0.length()) + (i + 1);
                    jsonSingle = NetworkUtil.sendRequest(urlSingle);
                    newsItem = NetworkUtil.convertJsonToNews(jsonSingle);
                    newsList.add(newsItem);
                    i++;
                }

                if (newsList != null && newsList.size() > 0) {

                    dbManager.saveNewses(newsList);
                }

                return newsList;
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<ItemNews> maps) {
            super.onPostExecute(maps);

            if (wantToRefresh && !wantToLoad) {

                // 若下拉刷新后取得的list不为空，则显示
                if (maps != null && maps.size() > 0) {

                    result.addAll(0, maps);
                    adapter.setData(result);
                    adapter.notifyDataSetChanged();
                    refreshLvLayout.setRefreshing(false);
                    Toast.makeText(MainActivity.this, "更新了" + maps.size() + "条新闻"
                            , Toast.LENGTH_SHORT).show();

                } else {
                    // 停止刷新，提示数据已经是最新
                    refreshLvLayout.setRefreshing(false);
                    Toast.makeText(MainActivity.this, "已经是最新的", Toast.LENGTH_SHORT).show();
                }
                wantToRefresh = false;
                return;
            }
            if (!wantToRefresh && wantToLoad) {
                // 若上拉加载返回的数据不为空，则显示
                if (maps != null && maps.size() > 0) {

                    result.addAll(maps);
                    adapter.setData(result);
                    adapter.notifyDataSetChanged();
                }
                // 停止加载
                refreshLvLayout.setLoading(false);
                wantToLoad = false;
                return;
            }
            if (count == 1) {
                result = maps;
                adapter.setData(result);
                listView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
            wantToLoad = false;
            wantToRefresh = false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        listView.setOnScrollListener(new PauseOnScrollListener(imageLoader, true, true));
    }

    /**
     * 自定义adapter
     */

    class NewsAdapter extends BaseAdapter {

        ArrayList<ItemNews> data;

        private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

        private class Viewholder {
            ImageView imageView;
            JustifyTextView titleView;
            TextView timeTextView;
        }

        private void setData(ArrayList<ItemNews> result) {
            this.data = result;
        }

        @Override
        public int getCount() {
            return result.size();
        }

        @Override
        public Object getItem(int position) {
            return result.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            final Viewholder viewholder;
            if (convertView == null) {
                view = getLayoutInflater().inflate(R.layout.item_list_imgtxt, parent, false);
                viewholder = new Viewholder();
                viewholder.titleView = (JustifyTextView) view.findViewById(R.id.title_textview_list);
                viewholder.imageView = (ImageView) view.findViewById(R.id.image_imageview_list);
                viewholder.timeTextView = (TextView) view.findViewById(R.id.time_textview_list);
                view.setTag(viewholder);
            } else {
                viewholder = (Viewholder) view.getTag();
            }
            viewholder.titleView.setText(data.get(position).getTitle()+"\n");
            viewholder.timeTextView.setText(data.get(position).getUpdateTime());
            /**
             * 参数1：图片url
             * 参数2：显示图片的控件
             * 参数3：显示图片的设置
             * 参数4：监听器
             */
            imageLoader.displayImage(data.get(position).getThumbnail(), viewholder.imageView, options, animateFirstListener);
            return view;
        }
    }

    private static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

        static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            if (loadedImage != null) {
                ImageView imageView = (ImageView) view;
                // 是否第一次显示
                boolean firstDisplay = !displayedImages.contains(imageUri);
                if (firstDisplay) {
                    // 图片淡入效果
                    FadeInBitmapDisplayer.animate(imageView, 500);
                    displayedImages.add(imageUri);
                }
            }
        }
    }
}
