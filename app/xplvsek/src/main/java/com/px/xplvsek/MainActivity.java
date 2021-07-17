package com.px.xplvsek;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    public void initView(){
        button = (Button) findViewById(R.id.button1);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,
                        "1+1="+getResult(1,1),Toast.LENGTH_SHORT)
                        .show();
            }
        });
    }

    public static String getResult(int a, int b){
        return String.valueOf(a+b);
    }

    public String getResult(String a){
        return "你好 我是"+ a;
    }
}