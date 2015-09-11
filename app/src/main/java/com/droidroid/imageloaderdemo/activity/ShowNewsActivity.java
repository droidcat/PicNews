package com.droidroid.imageloaderdemo.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.droidroid.imageloaderdemo.R;
import com.droidroid.imageloaderdemo.db.DBManager;
import com.droidroid.imageloaderdemo.projection.slide.SlideSlide;
import com.droidroid.imageloaderdemo.util.ActivityCollector;
import com.droidroid.imageloaderdemo.util.NetworkUtil;
import com.droidroid.imageloaderdemo.view.CYTextView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import java.util.ArrayList;

/**
 * Created by Droidroid on 2015/8/18.
 */
public class ShowNewsActivity extends BaseActivity implements View.OnClickListener {

    private ViewPager viewPager;

    // 用于请求幻灯片资源
    private String url;

    // 存放所有幻灯片
    private ArrayList<SlideSlide> result;

    // 用于配置图片显示
    private DisplayImageOptions options;

    // 适配器
    private NewsPagerAdapter adapter;

    // 数据库管理类
    private DBManager dbManager;

    // 指示按钮-左
    private ImageView leftIndicator;

    // 指示按钮-右
    private ImageView rightIndicator;

    // 索引
    private int index;

    // alpha值
    private float mAlpha = 0;

    // 是否隐藏指示按钮
    private boolean isHide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_shownews_pager);
        // 添加当前的activity进管理器中
        ActivityCollector.addActivity(this);
        // 配置图片显示
        options = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.ic_empty)
                .showImageOnFail(R.drawable.ic_error)
                .resetViewBeforeLoading(true)
                .cacheOnDisc(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .displayer(new FadeInBitmapDisplayer(300))
                .build();


        viewPager = (ViewPager) findViewById(R.id.pager_shownews);
        // 指示按钮
        leftIndicator = (ImageView) findViewById(R.id.toleft_shownews);
        rightIndicator = (ImageView) findViewById(R.id.toright_shownews);
        leftIndicator.setAlpha(mAlpha);
        rightIndicator.setAlpha(mAlpha);
        leftIndicator.setOnClickListener(this);
        rightIndicator.setOnClickListener(this);

        adapter = new NewsPagerAdapter();

        // 从上一Activity中取得幻灯片资源的地址
        Intent intent = getIntent();
        url = intent.getStringExtra("url");
        Log.d("url", url);

        // 实例化数据库管理类
        dbManager = DBManager.getInstance(this);
        // 先尝试从数据库中取幻灯片资源
        result = dbManager.getSlide(url);
        // 若资源存在则直接设置viewpager进行显示
        if (result != null && result.size() > 0) {
            adapter.setData(result);
            viewPager.setAdapter(adapter);
            viewPager.setCurrentItem(0);
            adapter.notifyDataSetChanged();
        }
        // 否则，通过网络进行获取
        else {
            // 判断当前网络是否有效
            if (NetworkUtil.isNetWorkAvailable(ShowNewsActivity.this)) {
                // 异步加载资源
                new NetTask2().execute(url);
            } else {
                Toast.makeText(ShowNewsActivity.this, "请开启网络连接", Toast.LENGTH_SHORT).show();
                ShowNewsActivity.this.finish();
            }
        }
    }

    /**
     * 设置按钮渐显效果
     */
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 1 && mAlpha < 255) {
                //通过设置不透明度设置按钮的渐显效果
                mAlpha += 50;
                if (mAlpha > 255)
                    mAlpha = 255;

                leftIndicator.setAlpha(mAlpha / 255);
                leftIndicator.invalidate();
                rightIndicator.setAlpha(mAlpha / 255);
                rightIndicator.invalidate();

                if (!isHide && mAlpha < 255)
                    mHandler.sendEmptyMessageDelayed(1, 100);
            } else if (msg.what == 0 && mAlpha > 0) {
                mAlpha -= 3;

                if (mAlpha < 0)
                    mAlpha = 0;

                leftIndicator.setAlpha(mAlpha / 255);
                leftIndicator.invalidate();
                rightIndicator.setAlpha(mAlpha / 255);
                rightIndicator.invalidate();

                if (isHide && mAlpha > 0)
                    mHandler.sendEmptyMessageDelayed(0, 10);
            }
        }
    };

    private void showImageButtonView() {

        isHide = false;
        mHandler.sendEmptyMessage(1);
    }

    private void hideImageButtonView() {

        new Thread() {
            public void run() {
                try {
                    isHide = true;
                    mHandler.sendEmptyMessageDelayed(0, 300);
                } catch (Exception e) {
                    ;
                }
            }
        }.start();
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_DOWN:
                showImageButtonView();
                break;
            case MotionEvent.ACTION_UP:
                hideImageButtonView();
                break;
        }


        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onClick(View v) {
        int indexToShow = viewPager.getCurrentItem();
        switch (v.getId()) {
            case R.id.toleft_shownews:
                if (indexToShow != 0) {
                    viewPager.setCurrentItem(indexToShow - 1);
                }
                break;
            case R.id.toright_shownews:
                if (indexToShow != result.size() - 1) {
                    viewPager.setCurrentItem(indexToShow + 1);
                }
                break;
        }
    }


    /**
     * 异步任务
     */
    class NetTask2 extends AsyncTask<String, Integer, ArrayList<SlideSlide>> {

        @Override
        protected void onPostExecute(ArrayList<SlideSlide> maps) {
            super.onPostExecute(maps);
            // 获得的资源不为空则进行显示
            if (maps != null && maps.size() > 0) {
                adapter.setData(maps);
                viewPager.setAdapter(adapter);
                viewPager.setCurrentItem(0);
                adapter.notifyDataSetChanged();
                dbManager.saveSlide(maps,url);
            }
            // 否则，直接判断为网络无效
            else {
                new AlertDialog.Builder(ShowNewsActivity.this)
                        .setTitle("无效的网络")
                        .setMessage("请确认网络连接")
                        .setNegativeButton("返回上一级", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ShowNewsActivity.this.finish();
                            }
                        }).show();
            }

        }

        @Override
        protected ArrayList<SlideSlide> doInBackground(String... params) {
            // 获得json数据
            String jsonStr = NetworkUtil.sendRequest(params[0]);
            // 解析获得的json数据
            ArrayList<SlideSlide> result = NetworkUtil.convertJsonToSlides(jsonStr);
            return result;
        }
    }

    /**
     * 适配器类
     */
    class NewsPagerAdapter extends PagerAdapter {

        private ArrayList<SlideSlide> data;

        private LayoutInflater inflater;

        private void setData(ArrayList<SlideSlide> data) {
            this.data = data;
        }

        public NewsPagerAdapter() {
            inflater = getLayoutInflater();
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ((ViewPager) container).removeView((View) object);
        }


        @Override
        public int getCount() {
            return data.size();
        }


        @Override
        public Object instantiateItem(ViewGroup viewGroup, int position) {

            // 页面布局
            View itemPager = inflater.inflate(R.layout.item_pager_imgtxt, viewGroup, false);
            // 显示当前索引
            TextView currentIndex = (TextView) itemPager.findViewById(R.id.index_textview_pager);
            // 存放图片
            ImageView imageView = (ImageView) itemPager.findViewById(R.id.iamge_imageview_pager);
            // 存放新闻描述
            CYTextView textView = (CYTextView) itemPager.findViewById(R.id.description_textview_pager);
            // 进度条
            final ProgressBar spinner = (ProgressBar) itemPager.findViewById(R.id.loading);

            // 显示索引
            currentIndex.setText((position + 1) + "/" + data.size());
            // 显示新闻描述
            textView.SetText("    " + data.get(position).getTitle());
            Log.v("descriptiontext", textView.getText().toString());
            Log.v("imgurl", data.get(position).getImage());
            // 显示图片
            imageLoader.displayImage(data.get(position).getImage(), imageView, options,
                    new SimpleImageLoadingListener() {
                        @Override
                        public void onLoadingStarted(String imageUri, View view) {
                            spinner.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                            String message = null;
                            switch (failReason.getType()) {
                                case IO_ERROR:
                                    message = "Input/Output error";
                                    break;
                                case DECODING_ERROR:
                                    message = "Image can't be decoded";
                                    break;
                                case NETWORK_DENIED:
                                    message = "DownLoads are denied";
                                    break;
                                case OUT_OF_MEMORY:
                                    message = "Out of Memory error";
                                    break;
                                case UNKNOWN:
                                    message = "Unknown error";
                                    break;
                            }
                            Toast.makeText(ShowNewsActivity.this, message, Toast.LENGTH_LONG);
                            spinner.setVisibility(View.GONE);
                        }

                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                            spinner.setVisibility(View.GONE);
                        }

                    });
            viewGroup.addView(itemPager, 0);
            return itemPager;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }
}
