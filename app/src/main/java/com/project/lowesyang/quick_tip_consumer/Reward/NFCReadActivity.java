package com.project.lowesyang.quick_tip_consumer.Reward;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.project.lowesyang.quick_tip_consumer.R;
import com.project.lowesyang.quick_tip_consumer.utils.LoadingAlertDialog;
import com.project.lowesyang.quick_tip_consumer.utils.LocalStorage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

/**
 * Created by LowesYang on 2017/7/6.
 */

public class NFCReadActivity extends AppCompatActivity {
    private boolean NFCSupport=true;
    private NfcAdapter nfcAdapter=null;
    private PendingIntent pi=null;
    private boolean isLoading=false;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfcread);
        nfcInit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (nfcAdapter != null)
            nfcAdapter.enableForegroundDispatch(this, pi, null, null);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (nfcAdapter != null)
            nfcAdapter.disableForegroundDispatch(this);
    }


    private void nfcInit(){
        TextView notice= (TextView) findViewById(R.id.nfc_notice);
        // 监听扫描NFC
        IntentFilter ifilters=new IntentFilter();
        ifilters.addAction(NfcAdapter.ACTION_NDEF_DISCOVERED);  //NDEF
        nfcAdapter=NfcAdapter.getDefaultAdapter(this);
        pi=PendingIntent.getActivity(this,0,new Intent(this,getClass()),0);
        if(nfcAdapter==null){
            NFCSupport=false;
            Toast.makeText(this,"Device dose not support NFC!",Toast.LENGTH_LONG).show();
            notice.setText("Device dose not support NFC!");
        }
        else if(nfcAdapter!=null&&!nfcAdapter.isEnabled()){
            NFCSupport=false;
            Toast.makeText(this,"Please turn on NFC in your setting.",Toast.LENGTH_LONG).show();
            notice.setText("Please turn on NFC in your setting.");
        }
        else{
            NFCSupport=true;
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if(NFCSupport) {

            String response=readNFCTag(intent);
            if(response!=null){
//                Toast.makeText(getApplicationContext(),response,Toast.LENGTH_LONG).show();
                String[] nfcData=response.split(" ");
                final String shopId=nfcData[0];
                final String deskId=nfcData[1];
                System.out.println(nfcData[0]+" "+nfcData[1]);

                // 防止重复发请求
                if(!isLoading) {
                    isLoading=true;
                    RequestQueue mqueue = Volley.newRequestQueue(this);
                    final LoadingAlertDialog loading = new LoadingAlertDialog(this);
                    loading.show();
                    JsonObjectRequest jsonRequest = new JsonObjectRequest
                            (Request.Method.GET, "http://crcrcry.com.cn/nfc?token="+LocalStorage.getItem(getApplicationContext(),"token")+"&shop_id=" + shopId + "&desktop_id=" + deskId, null, new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        JSONObject dataJson = response.getJSONObject("data");
                                        if (response.getInt("code") == 0) {
                                            LocalStorage.setItem(getApplicationContext(), "token", dataJson.getString("token"));
                                            JSONObject deskInfo = dataJson.getJSONObject("desktopInfo");
                                            // 跳转至打赏表单页面
                                            Intent goTipFormIntent = new Intent(getApplicationContext(), GoTipFormActivity.class);
                                            deskInfo.put("shop_id",shopId);
                                            deskInfo.put("desk_id",deskId);
                                            goTipFormIntent.putExtra("deskInfo", deskInfo.toString());

                                            startActivity(goTipFormIntent);
                                            finish();
                                        } else {
                                            Toast.makeText(getApplicationContext(), response.getString("msg"), Toast.LENGTH_LONG).show();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    loading.hide();
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    loading.hide();
                                    String msg = "";
                                    if(error.networkResponse!=null && error.networkResponse.statusCode==401){
                                        msg = "Invalid token";
                                    } else {
                                        msg = "Network error";
                                    }
                                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                                }
                            });
                    mqueue.add(jsonRequest);
                    isLoading=false;
                }
            }
        }
    }

    // 读取NFC标签
    private String readNFCTag(Intent intent){
        String response=null;
        if(NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())){
            //从标签读取数据
            Parcelable[] nfcMsgs=intent.getParcelableArrayExtra(
                    NfcAdapter.EXTRA_NDEF_MESSAGES
            );
            NdefMessage msgs[]=null;
            int contentSize=0;
            if(nfcMsgs!=null){
                msgs=new NdefMessage[nfcMsgs.length];
                // 标签可能存储多个NdefMessage对象，一般情况下只有一个
                for(int i=0;i<nfcMsgs.length;i++){
                    // 转换为NdefMessage对象
                    msgs[i]= (NdefMessage) nfcMsgs[i];
                    // 计算数据的总长度
                    contentSize+=msgs[i].toByteArray().length;
                }
            }
            try{
                if(msgs!=null){
                    // 只读第一个信息
                    NdefRecord record=msgs[0].getRecords()[0];
                    response=parseTextRecord(record);
                    System.out.println(response);
                }
            }
            catch (Exception e){
                Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
            }
        }
        return response;
    }

    private String parseTextRecord(NdefRecord record){
        String response=null;
        // 验证TNF是否为TNF_WELL_KNOWN
        if(record.getTnf()!=NdefRecord.TNF_WELL_KNOWN) {
            System.out.println("不是TNF_WELL_KNOW");
            return null;
        }

        byte[] payload=record.getPayload();
        Byte statusByte=payload[0];
        String textCoding=((statusByte&0x80)==0)?"utf-8":"utf-16";
        int codeLength=statusByte&0x3f;
        try{
            response=new String(payload,codeLength+1,payload.length-codeLength-1,textCoding);
        }
        catch(UnsupportedEncodingException e){
            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
        }
        return response;
    }

}
