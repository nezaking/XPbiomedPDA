package com.xpbiomed.xppda;

import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.*;

public class InboundActivity extends AppCompatActivity {
    private static String user="张三";
    private EditText productionOrderEditText;
    private EditText barcodeEditText;
    private TextView currentQuantityTextView;
    private TextView remainingQuantityTextView;
    private TextView totalQuantityTextView;
    private ListView barCodeListView;
    private static String feishuAuthor = "Bearer u-eCGP_gsTd59r8AX7VEiu2AghkYHhh4VFiy001gW0aKfl";
    //添加数据适配器
    private ArrayAdapter<String> adapter;
    private List<String> barCodeList;


//    private static final String POST_URL = "https://open.feishu.cn/open-apis/bitable/v1/apps/I1RlbBcy8aJEjZsIdalckZFdnse/tables/tblIRpAqIbqfqOCs/records";
    private static final String POST_URL = "https://open.feishu.cn/open-apis/bitable/v1/apps/I1RlbBcy8aJEjZsIdalckZFdnse/tables/tblIRpAqIbqfqOCs/records";
    //    private static final Pattern PATTERN = Pattern.compile("([^#&=]+)=([^#&=]*)");
    private TextView textViewResult;
    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbound);
        //控件注解
        productionOrderEditText = findViewById(R.id.productionorderedittext);
        barcodeEditText = findViewById(R.id.barcodeedittext);
        currentQuantityTextView = findViewById(R.id.currentquantitytextview);
        remainingQuantityTextView = findViewById(R.id.remainingquantitytextview);
        totalQuantityTextView = findViewById(R.id.totalquantitytextview);
        barCodeListView = findViewById(R.id.barCodeListView);
        barCodeList = new ArrayList<>();

//        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, barCodeList);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, barCodeList);
        barCodeListView.setAdapter(adapter);
        // Add your code to handle the Inbound activity
        barcodeEditText.requestFocus();  //程序启动后，默认直接扫码
        barcodeEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    String barCode = barcodeEditText.getText().toString().trim();
//                    Toast.makeText(InboundActivity.this,parseUrlModel(barCode),Toast.LENGTH_LONG).show();
                    //添加货号到列表
                    if (!barCode.isEmpty()) {
                        barCodeList.add(parseUrlModel(barCode));
                        adapter.notifyDataSetChanged();
                        barcodeEditText.setText("");

                        if (barCodeList.size() >= 3) {
                            // 当行数超过10时，执行发送请求的操作
//                            Map<String, String> params = extractParams(barCode);

                            // 构建 JSON 数据
                            String json = buildJson(barCode);
                            Toast.makeText(InboundActivity.this,json,Toast.LENGTH_LONG).show();
                            // 发送 POST 请求
                            sendPostRequest(json);
                        }
                    }
                    return true;
                }


                return false;
            }
        });


    }
//添加事件

//    private static Map<String, String> extractParams(String input) {
//        Map<String, String> paramMap = new HashMap<>();
//
//        // 匹配URL中的参数部分
//        Pattern pattern = Pattern.compile("spm=(.*)&makeday(\\d+)&model(.+)&batch(.*)&sn(.*)"); // 匹配参数名和可能的值
//        Matcher matcher = pattern.matcher(input);
//
//        // 提取参数名和参数值，并放入 paramMap 中
//        while (matcher.find()) {
//            String paramName = matcher.group(1);
//            String paramValue = matcher.group(2) != null ? matcher.group(2) : ""; // 处理无赋值情况
//            paramMap.put(paramName, paramValue);
//        }
//
//        return paramMap;
//    }

    private static String buildJson(String url) {
        // 将url转为json格式
        Pattern pattern = Pattern.compile("spm=(.*?)&makeday(.*?)&model(.*?)&batch(.*?)&sn(.*)"); // 匹配参数名和可能的值
        Matcher matcher = pattern.matcher(url);
        String uniquecode="无";
        String makeday="无";
        String model="无";
        String batch="无";
        String sn="无";
        if (matcher.find()){
            uniquecode=matcher.group(1);
            makeday=matcher.group(2);
            model=matcher.group(3);
            batch=matcher.group(4);
            sn=matcher.group(5);
        }
        return "{\n" +
                "  \"records\": [\n" +
                "    {\n" +
                "      \"fields\": {\n" +
                "        \"货号\": \"" + model + "\",\n" +
                "        \"批次\": \"" + batch + "\",\n" +
                "        \"序列号\": \"" + sn + "\",\n" +
                "        \"生产日期\": \"" + makeday + "\",\n" +
                "        \"操作人员\": \"" + user + "\",\n" +
                "        \"唯一码\": \"" + uniquecode + "\"\n" +
                "      }\n" +
                "    }\n" +
                "  ]\n" +
                "}";
    }

    private static void sendPostRequest(String json) {
        new PostRequestTask().execute(json);
    }

    private static class PostRequestTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            OkHttpClient client = new OkHttpClient();
            String json = params[0];

            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);

            Request request = new Request.Builder()
                    .url(POST_URL)
                    .post(requestBody)
                    .addHeader("Authorization", feishuAuthor)
                    .build();

            try {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
//                    System.out.println();
                } else {
//                    System.out.println("POST请求失败，响应码：" + response.code());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
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

//    http://www.xpbiomed.com/productinfo/1396386.html#C3580-0500-2349389-05122025#spm=Ta760724US&makeday1701423233&modelC3580-0500&batch2349389&snXP1999
    private static String parseUrlModel(String url) {
//        Pattern pattern=Pattern.compile("model(\\w.+)&batch");
        return  url.substring(115,125);


    }
}






