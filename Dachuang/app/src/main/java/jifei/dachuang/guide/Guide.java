package jifei.dachuang.guide;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import jifei.dachuang.R;
import jifei.dachuang.Start;
import jifei.dachuang.accountOperations.SignIn;

/*
本类非原创
*/
public class Guide extends AppCompatActivity implements ViewPager.OnPageChangeListener
{

    private ViewPager viewPager;
    private int []imageIdArray;//图片资源的数组
    private List<View> viewList;//图片资源的集合
    private ViewGroup viewGroup;//放置圆点

    //实例化原点View
    private ImageView point;
    private ImageView []pointArray;

    //最后一页的按钮
    private ImageButton start;
    private LinearLayout.LayoutParams layoutParams;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);//全屏
        setContentView(R.layout.guide);
        start = findViewById(R.id.start);//立即体验按钮
        start.setOnClickListener(v -> {
            Start.editor.putBoolean("isFirst",false);
            Start.editor.apply();//设为非首次启动
            startActivity(new Intent(Guide.this,SignIn.class));
            finish();
        });

        //加载ViewPager
        initViewPager();

        //加载底部圆点
        initPoint();

        Toast.makeText(Guide.this,"引导页，左右滑动切换。",Toast.LENGTH_LONG).show();
    }

    //加载底部圆点
    private void initPoint()
    {
        //这里实例化LinearLayout
        viewGroup = findViewById(R.id.point);
        //根据ViewPager的item数量实例化数组
        pointArray = new ImageView[viewList.size()];
        //循环新建底部圆点ImageView，将生成的ImageView保存到数组中
        int size = viewList.size();
        for (int i = 0;i<size;i++)
        {
            point = new ImageView(this);
            layoutParams = new LinearLayout.LayoutParams(15,15);

            //第一个页面需要设置为选中状态，这里采用两张不同的图片
            if (i == 0)
            {
                Glide.with(this).load(R.mipmap.enable).into(point);
            }
            else
            {
                layoutParams.leftMargin=20;
                Glide.with(this).load(R.mipmap.disable).into(point);
            }
            point.setLayoutParams(layoutParams);
            point.setPadding(30,0,30,0);//left,top,right,bottom
            pointArray[i] = point;

            //将数组中的ImageView加入到ViewGroup
            viewGroup.addView(pointArray[i]);
        }
    }

    //加载图片ViewPager
    private void initViewPager()
    {
        viewPager = findViewById(R.id.page);
        //实例化图片资源
        imageIdArray = new int[]{R.mipmap.guide1,R.mipmap.guide2,R.mipmap.guide3};
        viewList = new ArrayList<>();
        //获取一个Layout参数，设置为全屏
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);

        //循环创建View并加入到集合中
        for (int tem : imageIdArray)
        {
            //new ImageView并设置全屏和图片资源
            ImageView imageView = new ImageView(this);
            imageView.setLayoutParams(params);
            Glide.with(this).load(tem).into(imageView);
            //将ImageView加入到集合中
            viewList.add(imageView);
        }

        //View集合初始化好后，设置Adapter
        viewPager.setAdapter(new GuideAdapter(viewList));
        //设置滑动监听
        viewPager.addOnPageChangeListener(this);
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
    {

    }

    //滑动后的监听
    @Override
    public void onPageSelected(int position)
    {
        //循环设置当前页的标记图
        int length = imageIdArray.length;
        for (int i = 0;i<length;i++)
        {
            pointArray[position].setBackgroundResource(R.mipmap.enable);
            if (position != i)
            {
                pointArray[i].setBackgroundResource(R.mipmap.disable);
            }
        }

        //判断是否是最后一页，若是则显示按钮
        if (position == imageIdArray.length - 1)
        {
            start.setVisibility(View.VISIBLE);
        }
        else
        {
            start.setVisibility(View.GONE);
        }
    }
    @Override
    public void onPageScrollStateChanged(int state)
    {

    }
}