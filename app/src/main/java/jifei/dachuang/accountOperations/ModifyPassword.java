package jifei.dachuang.accountOperations;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import jifei.dachuang.R;
import jifei.dachuang.Start;

public class ModifyPassword extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.modify_password);
        EditText current_pwd = findViewById(R.id.pwd_0);
        EditText new_pwd1 = findViewById(R.id.pwd_1);
        EditText new_pwd2 = findViewById(R.id.pwd_2);
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
                    //更改数据库中用户密码
                    Toast.makeText(this, "密码修改成功", Toast.LENGTH_SHORT).show();
                    finish();
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
}
