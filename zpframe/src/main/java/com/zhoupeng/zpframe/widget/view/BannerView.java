package com.zhoupeng.zpframe.widget.view;

import android.content.Context;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.zhoupeng.zpframe.R;

import java.util.ArrayList;

/**
 * Created by zhoupeng on 2016/5/13.
 */
public class BannerView extends RelativeLayout {

    private ViewPager mViewPager;
    private TextView mShowMsg;
    private LinearLayout mLinearGroup;
    private BannerAdapter bannerAdapter;
    private ArrayList<ImgData> mDataList;
    private int mCurIndex;
    private Context mContext;

    private boolean isStop;
    private BannerListener bannerListener;

    public interface BannerListener {
        void onBannerItemClick(int position);
    }

    public BannerView(Context context) {
        super(context);
        init(context);
    }

    public BannerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public BannerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        LayoutInflater.from(context).inflate(R.layout.view_banner, this);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mShowMsg = (TextView) findViewById(R.id.tv_showMsg);
        mLinearGroup = (LinearLayout) findViewById(R.id.linear_group);
        mViewPager.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP:
                        startImageTimerTask();
                        break;
                    default:
                        stopImageTimerTask();
                        break;
                }
                return false;
            }
        });
    }

    public void setImageResources(ArrayList<ImgData> dataList) {
        if (null != dataList && dataList.size() > 0) {
            this.setVisibility(View.VISIBLE);
            mDataList = dataList;
        } else {
            this.setVisibility(View.GONE);
            return;
        }
        // 清除
        mLinearGroup.removeAllViews();
        // 图片广告数量
        final int imageCount = dataList.size();
        for (int i = 0; i < imageCount; i++) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(20, 20);
            params.leftMargin = 20;
            ImageView imageView = new ImageView(mContext);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            if (i == 0) {
                params.width = 30;
                imageView.setLayoutParams(params);
                imageView.setBackgroundResource(R.drawable.dot_select);
            } else {
                params.width = 20;
                imageView.setLayoutParams(params);
                imageView.setBackgroundResource(R.drawable.dot_normal);
            }
            mLinearGroup.addView(imageView);
        }

        mShowMsg.setText(dataList.get(0).getMsg());
        mViewPager.addOnPageChangeListener(new PageChangeListener());
        bannerAdapter = new BannerAdapter(mContext, dataList);
        mViewPager.setAdapter(bannerAdapter);
        startImageTimerTask();
    }

    public void setBannerListener(BannerListener bannerListener) {
        this.bannerListener = bannerListener;
    }

    /**
     * 图片轮播(手动控制自动轮播与否，便于资源控件）
     */
    public void startImageCycle() {
        startImageTimerTask();
    }

    /**
     * 暂停轮播—用于节省资源
     */
    public void pushImageCycle() {
        stopImageTimerTask();
    }

    /**
     * 图片滚动任务
     */
    private void startImageTimerTask() {
        stopImageTimerTask();
        // 图片滚动
        mHandler.postDelayed(mImageTimerTask, 3000);
    }

    /**
     * 停止图片滚动任务
     */
    private void stopImageTimerTask() {
        isStop = true;
        mHandler.removeCallbacks(mImageTimerTask);
    }

    private Handler mHandler = new Handler();
    /**
     * 图片自动轮播Task
     */
    private Runnable mImageTimerTask = new Runnable() {
        @Override
        public void run() {
            if (null != mDataList) {
                mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1);
                if (!isStop) {  //if  isStop=true   //当你退出后 要把这个给停下来 不然 这个一直存在 就一直在后台循环
                    mHandler.postDelayed(mImageTimerTask, 3000);
                }
            }
        }
    };

    /**
     * 轮播图片监听
     *
     * @author minking
     */
    private final class PageChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrollStateChanged(int state) {
            if (state == ViewPager.SCROLL_STATE_IDLE)
                startImageTimerTask();
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageSelected(int index) {
            index = index % mDataList.size();
            // 设置当前显示的图片
            mCurIndex = index;
            // 设置图片滚动指示器背
            ImageView imageView = (ImageView) (mLinearGroup.getChildAt(index));
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) imageView.getLayoutParams();
            params.width = 30;
            imageView.setLayoutParams(params);
            imageView.setBackgroundResource(R.drawable.dot_select);
            mShowMsg.setText(mDataList.get(index).getMsg());
            for (int i = 0; i < mDataList.size(); i++) {
                if (index != i) {
                    ImageView tempImageView = (ImageView) mLinearGroup.getChildAt(i);
                    LinearLayout.LayoutParams params1 = (LinearLayout.LayoutParams) tempImageView.getLayoutParams();
                    params1.width = 20;
                    tempImageView.setLayoutParams(params1);
                    tempImageView.setBackgroundResource(R.drawable.dot_normal);
                }
            }
        }
    }

    private class BannerAdapter extends PagerAdapter {
        private ArrayList<ImageView> mImageViewCacheList;
        private ArrayList<ImgData> mAdList = new ArrayList<>();
        private Context mContext;

        public BannerAdapter(Context context, ArrayList<ImgData> dataList) {
            this.mContext = context;
            this.mAdList = dataList;
            mImageViewCacheList = new ArrayList<>();
        }

        @Override
        public int getCount() {
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            String imageUrl = mAdList.get(position % mAdList.size()).getImgUrl();
            ImageView imageView;
            if (mImageViewCacheList.isEmpty()) {
                imageView = new ImageView(mContext);
                imageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            } else {
                imageView = mImageViewCacheList.remove(0);
            }
            imageView.setTag(imageUrl);
            container.addView(imageView);
            imageView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (null != bannerListener) {
                        bannerListener.onBannerItemClick(position % mAdList.size());
                    }
                }
            });
            ImageLoader.getInstance().displayImage(imageUrl, imageView);
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ImageView view = (ImageView) object;
            mViewPager.removeView(view);
            mImageViewCacheList.add(view);
        }
    }
}