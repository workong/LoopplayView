package com.zhoupeng.zpframe;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.zhoupeng.zpframe.widget.view.BannerView;
import com.zhoupeng.zpframe.widget.view.ImgData;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private BannerView bannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
    }

    private void initView() {
        bannerView = (BannerView) findViewById(R.id.bannerView);
        bannerView.setBannerListener(new BannerView.BannerListener() {
            @Override
            public void onBannerItemClick(int position) {
                Toast.makeText(MainActivity.this, "hehe.." + position, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initData() {

        String[] urls = {"http://www.topcio.cn/u/cms/www/201511/10105340wn5n.jpg",
                "http://img66.gkzhan.com/9/20151101/635819637695541716851.jpg",
                "http://www.21cbr.com/uploads/allimg/141109/35669-141109200303349.jpg",
                " http://photocdn.sohu.com/20151204/mp46377850_1449201643671_3.jpeg"};

        String msgs[] = {"激活后Hi好Hi好你解决",
                "送餐车是吃撒初三从",
                "啦啦啦啦啦啦啦啦",
                "好看好看很纠结",
                "及手机偶奇偶理解了"};

        ArrayList<ImgData> dataList = new ArrayList<>();
        for (int i = 0; i < urls.length; i++) {
            ImgData imgData = new ImgData();
            imgData.setImgUrl(urls[i]);
            imgData.setMsg(msgs[i]);
            dataList.add(imgData);
        }
        bannerView.setImageResources(dataList);
    }
}