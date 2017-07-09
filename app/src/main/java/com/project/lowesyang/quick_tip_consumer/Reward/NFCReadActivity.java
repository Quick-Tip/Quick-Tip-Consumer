package com.project.lowesyang.quick_tip_consumer.Reward;

import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.project.lowesyang.quick_tip_consumer.R;

import java.io.UnsupportedEncodingException;

/**
 * Created by LowesYang on 2017/7/6.
 */

public class NFCReadActivity extends AppCompatActivity {
    private boolean NFCSupport=true;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfcread);
        View tempBtn=findViewById(R.id.temp_btn);
        tempBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(v.getContext(),GoTipFormActivity.class);
                startActivity(intent);
            }
        });

        nfcInit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        nfcInit();
    }

    private void nfcInit(){
        TextView notice= (TextView) findViewById(R.id.nfc_notice);
        // 监听扫描NFC
        IntentFilter ifilters=new IntentFilter();
        ifilters.addAction(NfcAdapter.ACTION_NDEF_DISCOVERED);  //NDEF
        NfcAdapter nfcAdapter=NfcAdapter.getDefaultAdapter(this);
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
        if(NFCSupport) {
            String response=readNFCTag();
            if(response!=null){

            }
        }
    }

    // 读取NFC标签
    private String readNFCTag(){
        String response=null;
        if(NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())){
            //从标签读取数据
            Parcelable[] nfcMsgs=getIntent().getParcelableArrayExtra(
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
                    // 验证TNF是否为TNF_WELL_KNOWN
                    if(record.getTnf()!=NdefRecord.TNF_WELL_KNOWN)
                        return null;
                    // 验证可变长度类型是否为RTD_TEXT
                    if(!record.getType().equals(NdefRecord.RTD_TEXT))
                        return null;
                    byte[] payload=record.getPayload();
                    Byte statusByte=payload[0];
                    String textCoding=((statusByte&0200)==0)?"utf-8":"utf-16";
                    int codeLength=statusByte&0077;
                    try{
                        response=new String(payload,codeLength+1,payload.length-codeLength-1,textCoding);
                    }
                    catch(UnsupportedEncodingException e){
                        Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                    }
                }
            }
            catch (Exception e){
                Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
            }
        }
        return response;
    }
}
