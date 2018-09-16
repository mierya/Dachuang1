package jifei.dachuang.fragments;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.File;

import jifei.dachuang.R;
import jifei.dachuang.Start;
import jifei.dachuang.helper.ExtendedViewPager;
import jifei.dachuang.helper.TouchImageView;

/*
五一假期
大家全都出去浪了
只有我我宅在寝室写代码……

更改Jdk版本为1.8
Lambda表达式用起来666

暂时实现预览、缩放功能，实际上在此之前应该加上照片列表，点击后才进入预览界面

用户下载后的图片存放在内置存储/软件拼音/用户手机号中
暂且将将手机号默认为12345678（实际上在注册时获取）
*/
public class Album extends Fragment
{
    private File[] allFiles;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        File file=new File(Environment.getExternalStorageDirectory().getPath()+"/Dachuang/"+ Start.sp.getString("phoneNumber","12345678")+File.separator);
        allFiles = file.listFiles();

        View view=inflater.inflate(R.layout.album,container,false);
        ExtendedViewPager mViewPager = view.findViewById(R.id.preview);
        mViewPager.setAdapter(new TouchImageAdapter());
        return view;
    }

    public class TouchImageAdapter extends PagerAdapter
    {
        @Override
        public int getCount()
        {
            return allFiles.length;
        }

        @Override
        @NonNull
        public View instantiateItem(@NonNull ViewGroup container, int position)
        {
            TouchImageView img = new TouchImageView(container.getContext());
            img.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            //Glide.with(container.getContext()).load(bitmap[position]).into(img);
            //用Glide不知为何显示不出来，这个必须解决，不然极其容易OOM
            img.setImageBitmap(BitmapFactory.decodeFile(allFiles[position].getAbsolutePath()));
            container.addView(img, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            return img;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object)
        {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object)
        {
            return view == object;
        }

    }
}
