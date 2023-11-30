package com.xpbiomed.xppda;

import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.*;

public class InboundActivity extends AppCompatActivity {

    private EditText productionOrderEditText;
    private EditText barcodeEditText;
    private TextView currentQuantityTextView;
    private TextView remainingQuantityTextView;
    private TextView totalQuantityTextView;


    private static final String POST_URL = "https://open.feishu.cn/open-apis/bitable/v1/apps/I1RlbBcy8aJEjZsIdalckZFdnse/tables/tblIRpAqIbqfqOCs/records/batch_create";
//    private static final Pattern PATTERN = Pattern.compile("([^#&=]+)=([^#&=]*)");
    private TextView textViewResult;
    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbound);

        productionOrderEditText = findViewById(R.id.productionorderedittext);
        barcodeEditText = findViewById(R.id.barcodeedittext);
        currentQuantityTextView = findViewById(R.id.currentquantitytextview);
        remainingQuantityTextView = findViewById(R.id.remainingquantitytextview);
        totalQuantityTextView = findViewById(R.id.totalquantitytextview);
        // Add your code to handle the Inbound activity
        barcodeEditText.requestFocus();  //程序启动后，默认直接扫码


        //添加文本框更变事件
        barcodeEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                        // 提取参数值
        Map<String, String> params = extractParams(barcodeEditText.toString());

        // 构建 JSON 数据
        String json = buildJson(params);

        // 发送 POST 请求
        sendPostRequest(json);
            }
        });
    }

    private static Map<String, String> extractParams(String input) {
        Map<String, String> paramMap = new HashMap<>();

        // 匹配URL中的参数部分
        Pattern pattern = Pattern.compile("(\\w+)(?:=([^&]+))?"); // 匹配参数名和可能的值
        Matcher matcher = pattern.matcher(input);

        // 提取参数名和参数值，并放入 paramMap 中
        while (matcher.find()) {
            String paramName = matcher.group(1);
            String paramValue = matcher.group(2) != null ? matcher.group(2) : ""; // 处理无赋值情况
            paramMap.put(paramName, paramValue);
        }

        return paramMap;
    }

    private static String buildJson(Map<String, String> params) {
        // 构建 JSON 数据
        return "{\n" +
                "  \"records\": [\n" +
                "    {\n" +
                "      \"fields\": {\n" +
                "        \"货号\": \"" + params.get("model") + "\",\n" +
                "        \"批次\": \"" + params.get("batch") + "\",\n" +
                "        \"序列号\": \"" + params.get("sn") + "\",\n" +
                "        \"生产日期\": \"" + params.get("makeday") + "\",\n" +
                "        \"操作人员\": \"张三\",\n" +
                "        \"唯一码\": \"" + params.get("spm") + "\"\n" +
                "      }\n" +
                "    }\n" +
                "  ]\n" +
                "}";
    }

    private static void sendPostRequest(String json) {
        OkHttpClient client = new OkHttpClient();

        // 构建请求体
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);

        // 构建请求
        Request request = new Request.Builder()
                .url("https://open.feishu.cn/open-apis/bitable/v1/apps/I1RlbBcy8aJEjZsIdalckZFdnse/tables/tblIRpAqIbqfqOCs/records/batch_create")
                .post(requestBody)
                .addHeader("Authorization", "Bearer u-e4usF5epNb8bLEypyBkOeqghk.Hhh4pFVy00glW0aGfh") // 替换为实际的 Authorization 头
                .build();

        // 发送请求
        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                System.out.println("POST请求成功，响应：" + response.body().string());
            } else {
                System.out.println("POST请求失败，响应码：" + response.code());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    private void playNotificationSound() {
        // 播放提示音，这里使用默认提示音
        MediaPlayer mediaPlayer = MediaPlayer.create(this, android.provider.Settings.System.DEFAULT_NOTIFICATION_URI);
        mediaPlayer.start();
    }

    private void updateTextViewResult() {
        // 更新底部的 TextView 中的数字
        count++;
        currentQuantityTextView.setText("当前数量: " + count);
    }
}





