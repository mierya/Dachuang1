package jifei.dachuang;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import jifei.dachuang.accountOperations.SignIn;
import jifei.dachuang.guide.Guide;
//不知为何再这个活动动态申请权限会出问题
public class Start extends AppCompatActivity
{
    public static SharedPreferences sp;
    public static SharedPreferences.Editor editor;
    //用来判断启动哪个活动
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //将assets文件夹图片写入文件夹
        sp=getSharedPreferences("date",MODE_PRIVATE);
        editor=getSharedPreferences("date",MODE_PRIVATE).edit();
        boolean first=sp.getBoolean("isFirst",true);//默认为首次启动
        if(first)//首次启动，进入引导页
        {
            startActivity(new Intent(Start.this,Guide.class));
            finish();
        }
        else//判断是否已登入
        {
            String userName=sp.getString("userName","");
            String password=sp.getString("password","");
            if(userName.length()==0||password.length()==0)
            {
                startActivity(new Intent(Start.this,SignIn.class));
            }
            else
            {
                startActivity(new Intent(Start.this,MainActivity.class));
            }
            finish();
        }
    }

}
