package jifei.dachuang.camera;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileNotFoundException;
import jifei.dachuang.R;

public class CameraActivity extends AppCompatActivity
{
    final int TAKEPHOTO=1;
    private ImageView photo;
    private Uri uri;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera);

        photo=findViewById(R.id.photo);
        File output=new File(getExternalCacheDir(),"cache.jpg");//此处可能不适配7.0及以上系统，以后加上内容提供器
        uri=Uri.fromFile(output);
        Intent intent=new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT,uri);
        intent.putExtra("android.intent.extras.CAMERA_FACING", 1);//调用前置摄像头
        startActivityForResult(intent,TAKEPHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        try
        {
            Bitmap bitmap= BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
            //photo.setImageBitmap(bitmap);
            Glide.with(this).load(bitmap).into(photo);
        }
       catch (FileNotFoundException e)
       {
           e.printStackTrace();
       }
    }
}
