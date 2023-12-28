package com.xpbiomed.xppda.model;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

/**
 * Created by Nezakin on 2023/12/28.
 */


public class Feishu {
    public static final String ConfigFile = "set.properties";   //配置文件文件名
    public static final String feishuApp_id = "cli_a5b8915aa62b100e";   //飞书自建应用appid
    public static final String feishuApp_Secret = "ZNu3VHKDhuFLeNPCocI0wd6iKvwbdYyi";   //飞书自建应用AppSecret
    public static  String feishuAccessToken = "u-dncCtDxFN5eE3laSM4FReaghm.3hh4hzW200llK0aHuh";   //飞书自建应用AppSecret
    //基础资料目录
    public static final String BasePath = "BaseData";
    //装箱入库目录
    public static final String gmPath = "装箱入库";
    //发货目录
    public static final String rdPath = "销售出库";
    //退货目录
    public static final String rtPath = "销售退货";
    // 文件后缀
    public static final String FileType = ".dat";
    // 用户列表文件
    public static final String UserPath = "/BaseData/PDALogin.txt";


    public static final String GODOWN_MAIN_TABLE = "godownMainBill";    //入库主单
    public static final String ORDER_MAIN_TABLE = "orderMainBill";     //订单主单
    public static final String RETURN_MAIN_TABLE = "returnMainBill";   //退货主单
    public static final String ALLOT_MAIN_TABLE = "allotMainBill";     //调拨主单
    public static final String CHECK_MAIN_TABLE = "checkMainBill";     //盘点主单
    public static final String GodownX_MAIN_TABLE = "godownxMainBill";   //关联箱主单

    public static final String B_INVENTORY_File = "BaseInventory";  //产品资料
    public static final String B_CUSTOMER_File = "BaseCustomer";    //客户资料
    public static final String B_WAREHOUSE_File = "BaseWarehouse"; //仓库资料

    public static final String GodownBillingType = "-PD_Billing";   //入库明细单后缀
    public static final String OrderBillingType = "-OD_Billing";   //出库明细单后缀
    public static final String ReturnBillingType = "-RD_Billing";   //退货明细单后缀
    public static final String AllotBillingType = "-AD_Billing";   //调拨明细单后缀
    public static final String GodownXBillingType = "-XD_Billing";   //关联箱明细单后缀

    public static final String GodownScanType = "-PD_Scan";   //入库扫码单后缀
    public static final String OrderScanType = "-OD_Scan";   //出库扫码单后缀
    public static final String ReturnScanType = "-RD_Scan";   //退货扫码单后缀
    public static final String AllotScanType = "-AD_Scan";   //调拨扫码单后缀
    public static final String CheckScanType = "-CD_Scan";   //调拨扫码单后缀
    public static final String GodownXScanType = "-XD_Scan";   //关联箱扫码单后缀



    //删除指定路径的文件
    public static void DelDataFile(String filename) {
        File file = new File(filename);
        if (file.exists()) {
            file.delete();
        }

    }

    //获取飞书Token,飞书Token两个小时更新一次
    public static void requestAccessToken() throws JSONException {
        // 指定 Feishu API 的 URL
        String url = "https://open.feishu.cn/open-apis/auth/v3/tenant_access_token/internal";
//        String url = "https://open.feishu.cn/open-apis/auth/v3/app_access_token/internal";
        String feishuAccessToken="";
        // 创建请求参数
        RequestParams params = new RequestParams(url);
        params.setAsJsonContent(true);

        // 构建 JSON 数据
        JSONObject jsonInput = new JSONObject();
        jsonInput.put("app_id", feishuApp_id);
        jsonInput.put("app_secret", feishuApp_Secret);
        params.setBodyContent(jsonInput.toString());

        // 发送 POST 请求
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                // 解析 JSON 响应
                try {
                    JSONObject jsonResponse = new JSONObject(result);
                    String tenantAccessToken = jsonResponse.getString("tenant_access_token");
                    Feishu.feishuAccessToken=tenantAccessToken;
                    // 处理获取到的 tenant_access_token
                    Log.d("feishutoken", "Tenant Access Token: " + tenantAccessToken);
                    Log.d("feishutokenB", "Tenant Access Token: " + Feishu.feishuAccessToken);
                } catch (JSONException e) {
                    Log.e("feishutokenerror", "Error parsing JSON: " + e.getMessage());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                // 处理错误情况
                Log.e("feishutoken", "Error: " + ex.getMessage());
            }

            @Override
            public void onCancelled(CancelledException cex) {
                // 请求被取消时调用
            }

            @Override
            public void onFinished() {
                // 请求完成时调用
            }
        });
    }

}
