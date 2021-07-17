package com.px.xphvsek;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.px.xphvsek.sign.TestFunction;

public class MainActivity extends AppCompatActivity {

    Button button;
    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    public void initView(){
        button = (Button) findViewById(R.id.button1);
        textView = (TextView) findViewById(R.id.text1);
        String content = textView.getText().toString();
        textView.setText(content + " eg. 1^2 + 1^2 = " + TestFunction.getSign(1, 1));
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String buttonContent = button.getText().toString();
                Log.d("ccc", buttonContent);
                button.setText(changeStatus(buttonContent));
                Toast.makeText(MainActivity.this, "切换服务", Toast.LENGTH_SHORT)
                        .show();
            }
        });
    }

    public String changeStatus(String buttonContent){
        return buttonContent == "开启" ? "关闭":"开启";
    }
}