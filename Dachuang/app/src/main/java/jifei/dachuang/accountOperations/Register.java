package jifei.dachuang.accountOperations;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import jifei.dachuang.R;

public class Register extends AppCompatActivity
{
    int time=60;
    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        textView=findViewById(R.id.textView);

        new CountDownTimer(61*1000,1000)
        {
            @Override
            public void onTick(long millisUntilFinished)
            {
                textView.setText("注册\n留给李代刺完成\n"+String.valueOf(time--)+"s后自动返回");
            }

            @Override
            public void onFinish()
            {
                finish();
            }
        }.start();
    }
}
