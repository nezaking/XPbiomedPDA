package com.xpbiomed.xppda;

import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.xpbiomed.xppda.model.Feishu;

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
    private static String operator = "张三";
    private EditText productionOrderEditText;
    private EditText barcodeEditText;
    private TextView currentQuantityTextView;
    private TextView remainingQuantityTextView;
    private TextView totalQuantityTextView;
    private ListView barCodeListView;
    private static String feishuAuthor = "Bearer " + Feishu.feishuAccessToken;
    //添加数据适配器
    private ArrayAdapter<String> adapter;
    private List<String> barCodeList;
    private static List<String> barUrlList; //完整的URL列表
    Integer listCount = 0;
    private static String prono; //生产单号


    //    private static final String POST_URL = "https://open.feishu.cn/open-apis/bitable/v1/apps/I1RlbBcy8aJEjZsIdalckZFdnse/tables/tblIRpAqIbqfqOCs/records";
    private static final String POST_URL = "https://open.feishu.cn/open-apis/bitable/v1/apps/I1RlbBcy8aJEjZsIdalckZFdnse/tables/tblIRpAqIbqfqOCs/records/batch_create";
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
        barUrlList = new ArrayList<>();

//        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, barCodeList);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, barCodeList);
        barCodeListView.setAdapter(adapter);
        // Add your code to handle the Inbound activity
        barcodeEditText.requestFocus();  //程序启动后，默认直接扫码
        Log.d("FeishuTokenI", Feishu.feishuAccessToken);
        barcodeEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    String barCode = barcodeEditText.getText().toString().trim();

//                    Toast.makeText(InboundActivity.this,parseUrlModel(barCode),Toast.LENGTH_LONG).show();
                    //添加货号到列表
                    if (!barCode.isEmpty()) {
                        barCodeList.add(parseUrlModel(barCode)); //将扫描内容添加到文本杠list,其和list视图绑定
                        barUrlList.add(barCode);//添加到内置list，用来上传到飞书
                        adapter.notifyDataSetChanged(); //触发事件
                        listCount++; //一次增加一次
                        barcodeEditText.setText("");//扫描完清空文本框
                        updateTextViewResult(barCodeList.size()); //更新下面的小字
                        if (listCount == 5) {    //每两次上传一下，防止万一下子过多时，导致全部白扫
                            // 当行数超过10时，执行发送请求的操作
                            new UploadDataTask().execute(barUrlList); //提交到飞书
                            listCount = 0;  //计数器清零重新开始
//                            barUrlList.clear(); //列表清零
                        }
                    }
                    return true;
                }


                return false;
            }
        });

        //设置上面扫描生产单号的点击事件
        productionOrderEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {


                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    String proOrderNo = productionOrderEditText.getText().toString().trim();
                    if (!proOrderNo.isEmpty()) {
                        prono = proOrderNo;   //添加生产单号
                    }
                }
                return false;
            }
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    //新代码
    private static class UploadDataTask extends AsyncTask<List<String>, Void, Boolean> {
        public boolean status = true; //异步不能直接处理UI，所有要定义状态

        @Override
        protected Boolean doInBackground(List<String>... params) {
            List<String> barCodes = params[0];
            try {
                // 构建 JSON 数据
                JSONArray recordsArray = new JSONArray();
                for (String barCode : barCodes) {
                    JSONObject fieldsObject = parseBarCode(barCode);
                    Log.d("parserA", fieldsObject.toString());
                    JSONObject recordObject = new JSONObject();
                    recordObject.put("fields", fieldsObject);
                    Log.d("parserB", fieldsObject.toString());
                    recordsArray.put(recordObject);
                }

                JSONObject requestBody = new JSONObject();
                Log.d("parserC", recordsArray.toString());
                requestBody.put("records", recordsArray);
                Log.d("parserD", requestBody.toString());
                // 发送 POST 请求
                OkHttpClient client = new OkHttpClient();
                RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), requestBody.toString());
                Request request = new Request.Builder()
                        .url(POST_URL)
                        .addHeader("Authorization", feishuAuthor)
                        .post(body)
                        .build();

                Response response = client.newCall(request).execute();
                Log.d("parserD", recordsArray.toString());
                return response.isSuccessful() && parseResponse(response.body().string());
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                status = true;
                barUrlList.clear();
            } else {
                status = false;
            }
        }

        //解析条码
        protected JSONObject parseBarCode(String barCode) throws JSONException {
            //提取URL里面的字段
            Pattern pattern = Pattern.compile("spm=(.*)&makeday(.*)&model(.+)&batch(.*)&sn(.*)"); // 匹配参数名和可能的值
            Matcher matcher = pattern.matcher(barCode);
            String uniquecode = null, model = null, makeday = null, batch = null, sn = null;
            //正则表达式
            if (matcher.find()) {
                uniquecode = matcher.group(1);
                makeday = matcher.group(2);
                model = matcher.group(3);
                batch = matcher.group(4);
                sn = matcher.group(5);
            }
            JSONObject fieldsObject = new JSONObject();
            fieldsObject.put("生产单号", prono);
            fieldsObject.put("货号", model);
            fieldsObject.put("批次", batch);
            fieldsObject.put("序列号", sn);
            fieldsObject.put("生产日期", makeday);
            fieldsObject.put("唯一码", uniquecode);
            fieldsObject.put("操作人员", operator);
            return fieldsObject;
        }

        //解析返回结果
        private boolean parseResponse(String response) throws JSONException {
            JSONObject jsonResponse = new JSONObject(response);
            return jsonResponse.optInt("code") == 0;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        new InboundActivity.UploadDataTask().execute(barUrlList); //每10条提交一次，如果最后一次不足10条，直接退出即可
        try {
            Feishu.requestAccessToken(); //Token两个小时一更新，退出时刷新Token
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private void playNotificationSound() {
        // 播放提示音，这里使用默认提示音
        MediaPlayer mediaPlayer = MediaPlayer.create(this, android.provider.Settings.System.DEFAULT_NOTIFICATION_URI);
        mediaPlayer.start();
    }

    private void updateTextViewResult(Integer count) {
        // 更新底部的 TextView 中的数字
        currentQuantityTextView.setText("当前数量: " + count);
    }


    private static String parseUrlModel(String url) {
//        Pattern pattern=Pattern.compile("model(\\w.+)&batch");
        return url.substring(115, 125);


    }
}






