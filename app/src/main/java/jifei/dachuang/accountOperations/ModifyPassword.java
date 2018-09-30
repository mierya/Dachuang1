package jifei.dachuang.accountOperations;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import jifei.dachuang.MainActivity;
import jifei.dachuang.R;
import jifei.dachuang.Start;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ModifyPassword extends AppCompatActivity
{

    private EditText current_pwd;
    private EditText new_pwd1;
    private EditText new_pwd2;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.modify_password);
        current_pwd = findViewById(R.id.pwd_0);
        new_pwd1 = findViewById(R.id.pwd_1);
        new_pwd2 = findViewById(R.id.pwd_2);
        Button setting_button = findViewById(R.id.setting_button);

        setting_button.setOnClickListener(v ->
        {
            if (current_pwd.getText().toString().equals(Start.sp.getString("password","")))
            {
                if (new_pwd1.getText().toString().equals(new_pwd2.getText().toString()) && new_pwd1.getText().toString().length() != 0)
                {
                    SharedPreferences.Editor editor = getSharedPreferences("password", MODE_PRIVATE).edit();
                    editor.putString("pwd", new_pwd1.getText().toString());
                    editor.commit();
                    sendMPwRequest();
                } else
                {
                    Toast.makeText(this, "两次密码不匹配", Toast.LENGTH_SHORT).show();
                }
            } else
            {
                Toast.makeText(this, "原密码错误", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void sendMPwRequest(){
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try{
                    OkHttpClient client = new OkHttpClient();
                    RequestBody requestBody = new FormBody.Builder()
                            .add("new_password",new_pwd2.getText().toString())
                            .build();
                    Request request = new Request.Builder()
                            .url("http://118.24.100.115:8000"+"/passwd/reset")
                            .post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    ModifyPassword.this.runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            Toast.makeText(ModifyPassword.this ,"密码修改成功",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(ModifyPassword.this,MainActivity.class));
                            finish();
                        }
                    });

                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

}
