package jifei.dachuang.accountOperations;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import jifei.dachuang.MainActivity;
import jifei.dachuang.R;
import jifei.dachuang.Start;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RetrievePassword extends AppCompatActivity
{
    int time=60;
    private TextInputEditText phoneNum;
    private TextInputEditText newP2;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.retrieve_password);
        phoneNum = findViewById(R.id.phoneNum);
        phoneNum.setFilters(new InputFilter[]{new InputFilter.LengthFilter(11)});//控制输入长度
        TextInputEditText checkNum = findViewById(R.id.checkNum);
        checkNum.setFilters(new InputFilter[]{new InputFilter.LengthFilter(6)});//控制输入长度
        TextInputEditText newP1 = findViewById(R.id.newP1);
        newP1.setFilters(new InputFilter[]{new InputFilter.LengthFilter(11)});//控制输入长度
        newP2 = findViewById(R.id.newP2);
        newP2.setFilters(new InputFilter[]{new InputFilter.LengthFilter(6)});//控制输入长度
        Button getnum=findViewById(R.id.button);
        String number = phoneNum.getText().toString();
        getnum.setOnClickListener(v->{
            if(!isMobile(number)){
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
                sendRPwRequest();
            }
        });
    }
    private void sendRPwRequest(){
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try{
                    OkHttpClient client = new OkHttpClient();
                    RequestBody requestBody = new FormBody.Builder()
                            .add("phone_number",phoneNum.getText().toString())
                            .add("new_password",newP2.getText().toString())
                            .build();
                    Request request = new Request.Builder()
                            .url("http://118.24.100.115:8000"+"/passwd/forget")
                            .post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    if(responseData.equals("找不到该预留号码")){
                        Toast.makeText(RetrievePassword.this ,responseData,Toast.LENGTH_SHORT);
                    }
                    else{
                        Start.editor.putString("password",newP2.getText().toString());
                        //注意，本手机号只是默认，实际上在用户注册时获取
                        Start.editor.putString("phoneNumber","123456");
                        Start.editor.commit();
                        startActivity(new Intent(RetrievePassword.this,MainActivity.class));
                        finish();
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });
    }
    public static boolean isMobile(String number) {
    /*
    移动：134、135、136、137、138、139、150、151、152、157(TD)、158、159、178(新)、182、184、187、188
    联通：130、131、132、152、155、156、185、186
    电信：133、153、170、173、177、180、181、189、（1349卫通）
    总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
    */
        String num = "[1][34578]\\d{9}";//"[1]"代表第1位为数字1，"[34578]"代表第二位可以为3、4、5、7、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        if (TextUtils.isEmpty(number)) {
            return false;
        } else {
            //matches():字符串是否在给定的正则表达式匹配
            return number.matches(num);
        }
    }
}
