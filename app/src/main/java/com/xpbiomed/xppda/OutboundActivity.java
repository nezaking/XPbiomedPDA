package com.xpbiomed.xppda;

import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.util.Log;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OutboundActivity extends AppCompatActivity {

    private EditText salesOrderedittext;
    private EditText outbarcodeEditText;
    private TextView outcurrentQuantityTextView;
    private TextView outremainingQuantityTextView;
    private TextView outtotalQuantityTextView;
    private ListView outListView;
    private static String feishuAuthor = "Bearer u-fy8cTYC.JeRXBIecRuKt6TghkmHhh4rxUO00h1.0aLe4";
    private static String operator = "张三";
    //添加数据适配器
    private ArrayAdapter<String> adapter;
    private List<String> barCodeList;
    private static List<String> barUrlList; //完整的URL列表
    public Integer listCount;
    public static String saleno;


    private static final String POST_URL = "https://open.feishu.cn/open-apis/bitable/v1/apps/I1RlbBcy8aJEjZsIdalckZFdnse/tables/tblIRpAqIbqfqOCs/records";
    //    private static final Pattern PATTERN = Pattern.compile("([^#&=]+)=([^#&=]*)");
    private TextView textViewResult;
    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outbound);
        //控件注解
        //控件注解
        salesOrderedittext = findViewById(R.id.salesorderedittext);
        outbarcodeEditText = findViewById(R.id.outbarcodeedittext);
        outcurrentQuantityTextView = findViewById(R.id.outcurrentquantitytextview);
        outremainingQuantityTextView = findViewById(R.id.outremainingquantitytextview);
        outtotalQuantityTextView = findViewById(R.id.outtotalquantitytextview);
        outListView = findViewById(R.id.outlistview);
        barCodeList = new ArrayList<>();
        barUrlList = new ArrayList<>();

//        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, barCodeList);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, barCodeList);
        // Add your code to handle the Inbound activity
        salesOrderedittext.requestFocus();  //程序启动后，默认直接定位到销售订单

        salesOrderedittext.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    if (!salesOrderedittext.getText().toString().isEmpty()) {
                        saleno = salesOrderedittext.getText().toString().trim(); //赋值
//                        Toast.makeText(OutboundActivity.this,saleno,Toast.LENGTH_LONG).show();

                    }
                    return true;
                }

                return false;
            }
        });

        outbarcodeEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER)
                {
//                    Toast.makeText(OutboundActivity.this,outbarcodeEditText.getText().toString(),Toast.LENGTH_LONG).show();
                    String barCode=outbarcodeEditText.getText().toString();
                    barCodeList.add(barCode);
                    barUrlList.add(barCode);
                    adapter.notifyDataSetChanged(); //触发事件
                    listCount++; //纪录自增
                    if (listCount==5){
                        new UploadDataTask().execute(barUrlList); //超过五条就自动提交
                        listCount=0; //清零
                        return true;
                    }
                }
                return false;
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        new UploadDataTask().execute(barUrlList); //每10条提交一次，如果最后一次不足10条，直接退出即可
    }

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
                barUrlList.clear(); //清空列表
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
            fieldsObject.put("销售单号", saleno);
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

    private void playNotificationSound() {
        // 播放提示音，这里使用默认提示音
        MediaPlayer mediaPlayer = MediaPlayer.create(this, android.provider.Settings.System.DEFAULT_NOTIFICATION_URI);
        mediaPlayer.start();
    }

    private void updateTextViewResult(Integer count) {
        // 更新底部的 TextView 中的数字
        outcurrentQuantityTextView.setText("当前数量: " + count);
    }


    private static String parseUrlModel(String url) {
//        Pattern pattern=Pattern.compile("model(\\w.+)&batch");
        return url.substring(115, 125);


    }
}






