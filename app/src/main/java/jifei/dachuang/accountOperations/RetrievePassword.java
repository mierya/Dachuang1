package jifei.dachuang.accountOperations;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import jifei.dachuang.MainActivity;
import jifei.dachuang.R;
import jifei.dachuang.Start;

public class RetrievePassword extends AppCompatActivity
{
    int time=60;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.retrieve_password);
        TextInputEditText phoneNum = findViewById(R.id.phoneNum);
        phoneNum.setFilters(new InputFilter[]{new InputFilter.LengthFilter(11)});//控制输入长度
        TextInputEditText checkNum = findViewById(R.id.checkNum);
        checkNum.setFilters(new InputFilter[]{new InputFilter.LengthFilter(6)});//控制输入长度
        TextInputEditText newP1 = findViewById(R.id.newP1);
        newP1.setFilters(new InputFilter[]{new InputFilter.LengthFilter(11)});//控制输入长度
        TextInputEditText newP2 = findViewById(R.id.newP2);
        newP2.setFilters(new InputFilter[]{new InputFilter.LengthFilter(6)});//控制输入长度
        Button getnum=findViewById(R.id.button);
        getnum.setOnClickListener(v->{
            if(phoneNum.getText().toString().length() < 11){
                Toast.makeText(RetrievePassword.this,"电话号码错误",Toast.LENGTH_SHORT).show();
            }
            else{
                getnum.setClickable(false);
                new CountDownTimer(61*1000,1000)
                {
                    @Override
                    public void onTick(long millisUntilFinished)
                    {
                        getnum.setText(String.valueOf(time--)+"s后重新获取");
                    }
                    @Override
                    public void onFinish()
                    {
                        time=60;
                        getnum.setClickable(true);
                    }
                }.start();

            }
        });

        Button button=findViewById(R.id.ok);
        button.setOnClickListener(v -> {
            //先要检查用户输入，待完善
            if (newP1.getText().toString().length()==0)
            {
                Toast.makeText(RetrievePassword.this,"密码为空！",Toast.LENGTH_SHORT).show();
            }
            else if(!newP2.getText().toString().equals(newP1.getText().toString()))
            {
                Toast.makeText(RetrievePassword.this,"两次密码输入不一致！！",Toast.LENGTH_SHORT).show();
            }
            else{
                //当然得先存服务器端，待完善
                //以后记得加密存！
                Start.editor.putString("userName",newP1.getText().toString());
                Start.editor.putString("password",newP2.getText().toString());
                //注意，本手机号只是默认，实际上在用户注册时获取
                Start.editor.putString("phoneNumber","12345678");
                Start.editor.commit();
                startActivity(new Intent(RetrievePassword.this,MainActivity.class));
                //Toast.makeText(RetrievePassword.this,"假装你输入对了",Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}
