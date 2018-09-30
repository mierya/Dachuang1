package jifei.dachuang.accountOperations;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.support.design.widget.TextInputEditText;


import jifei.dachuang.MainActivity;
import jifei.dachuang.R;
import jifei.dachuang.Start;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Register extends AppCompatActivity
{

    private int flag = -1;
    private boolean flag2=false;
    private TextInputEditText r_userName;
    private TextInputEditText r_password;
    private TextInputEditText rr_password;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        r_userName = findViewById(R.id.r_userName);
        r_userName.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});//控制输入长度
        r_password = findViewById(R.id.r_password);
        r_password.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});//控制输入长度
        rr_password = findViewById(R.id.rr_password);
        rr_password.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});//控制输入长度
        //设置可点击文本
        TextView agreement = findViewById(R.id.agreement);
        String st1 = "阅读协议条款";
        SpannableString ss1 = new SpannableString(st1);
        ss1.setSpan(new ClickableSpan()
        {
            @Override
            public void onClick(View widget)
            {
                setContentView(R.layout.agreement);
                flag2=true;
            }
        }, 0, st1.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        agreement.setText(ss1);
        agreement.setMovementMethod(LinkMovementMethod.getInstance());//阅读后应返回至注册页面
        RadioGroup radioGroup = (RadioGroup)findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override

            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                // TODO Auto-generated method stub
                if(R.id.disagree == i){
                    flag = i;
                }
                else {
                    flag = i;
                }
            }
        });
        //注册按钮
        Button rbutton = findViewById(R.id.register_button);
        rbutton.setOnClickListener(v ->
        {
        //先要检查用户输入，待完善
            if (r_userName.getText().toString().trim().equals(""))
            {
                Toast.makeText(Register.this, "用户名不能为空", Toast.LENGTH_SHORT).show();
            } else if (r_password.getText().toString().trim().equals(""))
            {
                Toast.makeText(Register.this, "密码不能为空", Toast.LENGTH_SHORT).show();
            } else if (!(r_password.getText().toString().trim()).equals(rr_password.getText().toString().trim()))
            {
                Toast.makeText(Register.this, "两次密码输入不一致", Toast.LENGTH_SHORT).show();
            }else if(flag == R.id.disagree){
                Toast.makeText(Register.this, "注册即代表同意该协议条款", Toast.LENGTH_SHORT).show();
            }

            else
            {
                //向服务器发送注册请求
                sendRegisterRequest();
            }
        });
    }
    @Override
    public void onBackPressed()
    {
        //super.onBackPressed();
        if(flag2)
        {
            setContentView(R.layout.register);
            flag2=false;
        }
        else
        {
            finish();
        }

    }

    private void sendRegisterRequest(){
        new Thread(() ->
        {
            try{
                OkHttpClient client = new OkHttpClient();
                RequestBody requestBody = new FormBody.Builder()
                        .add("account",r_userName.getText().toString())
                        .add("password",r_password.getText().toString())
                        .add("phone_number","123456")
                        .build();
                Request request = new Request.Builder()
                        .url("http://118.24.100.115:8000"+"/client/register")
                        .post(requestBody)
                        .build();
                Response response = client.newCall(request).execute();
                String responseData = response.body().string();
                Register.this.runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if(!responseData.contains("account") )
                        {
                            Toast.makeText(Register.this, "注册失败", Toast.LENGTH_SHORT).show();
                        }else{
                            Start.editor.putString("userName",r_userName.getText().toString());
                            Start.editor.putString("password",r_password.getText().toString());
                            //注意，本手机号只是默认，实际上在用户注册时获取
                            Start.editor.putString("phoneNumber","123456");
                            Start.editor.commit();
                            Toast.makeText(Register.this ,"注册成功",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(Register.this,MainActivity.class));
                            finish();
                        }
                    }
                });
            }catch(Exception e){
                e.printStackTrace();
            }
        }).start();
    }
}