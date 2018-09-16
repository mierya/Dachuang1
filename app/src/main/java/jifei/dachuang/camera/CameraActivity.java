package jifei.dachuang.camera;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import jifei.dachuang.R;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import java.text.SimpleDateFormat;

public class CameraActivity extends AppCompatActivity
{
    final int TAKEPHOTO=1;
    private ImageView photo;
    private Uri uri;
    private File output;
    private Date curDate;
    private SimpleDateFormat formatter;
    public static SharedPreferences.Editor editorForCamera;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera);
        photo=findViewById(R.id.photo);
        output=new File(getExternalCacheDir(),"cache.jpg");//此处可能不适配7.0及以上系统，以后加上内容提供器
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
            formatter = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
            curDate = new Date(System.currentTimeMillis());
        }
       catch (FileNotFoundException e)
       {
           e.printStackTrace();
       }
    }
    protected void sendPhotoToServer(){
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    OkHttpClient client = new OkHttpClient();
                    String str = editorForCamera.toString() + formatter.format(curDate);//传至服务器的文件名为username+时间
                    RequestBody image = RequestBody.create(MediaType.parse("image/jpg"), output);// MediaType.parse() 里面是上传的文件类型。
                    RequestBody requestBody = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("picture",str,image)
                            .build();
                    Request request = new Request.Builder()
                            .url("http://118.24.100.115:8000" + "/client/upload")
                            .post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    Toast.makeText(CameraActivity.this ,responseData,Toast.LENGTH_SHORT);
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
    }
}
