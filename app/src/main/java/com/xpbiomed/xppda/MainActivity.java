package com.xpbiomed.xppda;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 设置按钮点击事件
        ImageButton settingsButton = findViewById(R.id.settingsButton);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 处理设置按钮点击事件，你可以在这里添加相关逻辑
            }
        });

        // 入库按钮点击事件
        ImageButton buttonInbound = findViewById(R.id.inBoundButton);
        buttonInbound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 启动入库Activity
                startActivity(new Intent(MainActivity.this, InboundActivity.class));
            }
        });

        // 出库按扭点击事件
        ImageButton buttonOutbound =findViewById(R.id.outBoundButton);
        buttonOutbound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, OutboundActivity.class));
            }
        });

    }
}
