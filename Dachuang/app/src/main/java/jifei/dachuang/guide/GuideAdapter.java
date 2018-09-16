package jifei.dachuang.guide;

import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import java.util.List;
/*
本类非原创
*/
public class GuideAdapter extends PagerAdapter
{
    private List<View> viewList;
    public GuideAdapter(List<View> viewList)
    {
        this.viewList = viewList;
    }

    //返回页面的个数
    @Override
    public int getCount()
    {
        if (viewList != null){
            return viewList.size();
        }
        return 0;
    }

    //判断对象是否生成界面
    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object)
    {
        return view == object;
    }

    //初始化position位置的界面
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position)
    {
        container.addView(viewList.get(position));
        return viewList.get(position);
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object)
    {
        container.removeView(viewList.get(position));
    }
}