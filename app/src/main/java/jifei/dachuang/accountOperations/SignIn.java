package jifei.dachuang.accountOperations;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.tbruyelle.rxpermissions2.RxPermissions;

import org.json.JSONArray;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.io.File;
import java.util.List;

import jifei.dachuang.MainActivity;
import jifei.dachuang.R;
import jifei.dachuang.Start;
import jifei.dachuang.camera.CameraActivity;
import jifei.dachuang.helper.FileOperations;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SignIn extends AppCompatActivity
{
    TextInputEditText userName;
    TextInputEditText password;
    boolean flag = false;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in);
        userName=findViewById(R.id.userName);
        userName.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});//控制输入长度
        password=findViewById(R.id.password);
        password.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});//控制输入长度
        //设置可点击文本
        TextView sign=findViewById(R.id.sign);
        TextView forget=findViewById(R.id.forget);
        String st1="注册账号";
        String st2="忘记密码";
        SpannableString ss1=new SpannableString(st1);
        SpannableString ss2=new SpannableString(st2);
        ss1.setSpan(new ClickableSpan()
        {
            @Override
            public void onClick(View widget)
            {
                startActivity(new Intent(SignIn.this,Register.class));
            }
        },0,st1.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        ss2.setSpan(new ClickableSpan()
        {
            @Override
            public void onClick(View widget)
            {
                startActivity(new Intent(SignIn.this,RetrievePassword.class));
            }
        },0,st2.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        sign.setText(ss1);
        sign.setMovementMethod(LinkMovementMethod.getInstance());
        forget.setText(ss2);
        forget.setMovementMethod(LinkMovementMethod.getInstance());
        //登录按钮
        Button button=findViewById(R.id.sign_button);
        button.setOnClickListener(v -> {
            //先要检查用户输入，待完善
            if (userName.getText().toString().length()==0)
            {
                Toast.makeText(SignIn.this,"用户名为空！",Toast.LENGTH_SHORT).show();
            }
            else if(password.getText().toString().length()==0)
            {
                Toast.makeText(SignIn.this,"密码为空！",Toast.LENGTH_SHORT).show();
            }
            else{
                //当然得先存服务器端，待完善
                //以后记得加密存！
                sendSignInRequest();
                //Toast.makeText(SignIn.this,"假装你输入对了",Toast.LENGTH_SHORT).show();
                }
        });
        RxPermissions rxPermissions=new RxPermissions(this);
        rxPermissions.requestEachCombined(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(permission ->
                {
                    if (permission.granted)
                    {
                        (new Thread(() -> {
                            File file=new File(Environment.getExternalStorageDirectory().getPath()+"/Dachuang/12345678/");
                            if(!file.exists())
                            {
                                file.mkdirs();
                            }
                            FileOperations.copyAssets(SignIn.this, file.getAbsolutePath());
                        })).start();
                    }
                    else if (permission.shouldShowRequestPermissionRationale)
                    {//奇怪了，仅拒绝一次，仍然不运行此代码
                        Toast.makeText(SignIn.this,"权限获取失败，请授予读写权限！",Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    else
                    {
                        Toast.makeText(SignIn.this,"权限被默认拒绝，请前往设置授予读写权限！",Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
        //Toast.makeText(SignIn.this,"随便输入就行。",Toast.LENGTH_LONG).show();
    }
    private void sendSignInRequest(){
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try{
                    OkHttpClient client = new OkHttpClient();
                    RequestBody requestBody = new FormBody.Builder()
                            .add("account",userName.getText().toString())
                            .add("password",password.getText().toString())
                            .build();
                    Request request = new Request.Builder()
                            .url("http://118.24.100.115:8000"+"/client/login")
                            .post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    if(responseData.equals("密码错误！")){
                        Toast.makeText(SignIn.this ,responseData,Toast.LENGTH_SHORT);
                    }else if(responseData.equals("该用户名未注册")){
                        Toast.makeText(SignIn.this ,responseData,Toast.LENGTH_SHORT);
                    }
                    else{
                        Start.editor.putString("userName",userName.getText().toString());
                        CameraActivity.editorForCamera.putString("userName",userName.getText().toString());
                        Start.editor.putString("password",password.getText().toString());
                        //注意，本手机号只是默认，实际上在用户注册时获取
                        Start.editor.putString("phoneNumber","123456");
                        Start.editor.commit();
                        startActivity(new Intent(SignIn.this,MainActivity.class));
                        finish();
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

}

