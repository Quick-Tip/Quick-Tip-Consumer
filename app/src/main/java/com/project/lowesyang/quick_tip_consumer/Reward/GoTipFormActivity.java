package com.project.lowesyang.quick_tip_consumer.Reward;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
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

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by LowesYang on 2017/7/6.
 */

public class GoTipFormActivity extends AppCompatActivity {
    private Button tip_1;
    private Button tip_3;
    private Button tip_5;
    private Button tip_10;
    private Button tip_20;
    private Button tip_50;
    private Button submit;
    private TextView finalAmount;
    private int resultAmount;           // 最终金额
    private EditText customedInput;     // 自定义金额输入框
    private EditText commentInput;
    private RatingBar starRating;
    private int activeIndex=-1;
    private int currTip=0;
    private String originColor="#cccccc";
    private ArrayList<Button> btnList = new ArrayList<>();
    private int[] moneyList={1,3,5,10,20,50};
    private JSONObject deskInfo=null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gotip_form);

        //接收上一个页面传来的intent
        Intent nfcIntent=getIntent();
        try {
            deskInfo = new JSONObject(nfcIntent.getStringExtra("deskInfo"));
            setTitle("Desk: No."+deskInfo.getString("desk_id")+"   Waiter: "+deskInfo.getString("waiter_name"));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        //各组件句柄初始化
        finalAmount= (TextView) findViewById(R.id.finalAmount);
        customedInput= (EditText) findViewById(R.id.customedInput);
        starRating= ( RatingBar ) findViewById(R.id.starRating);
        submit= ( Button ) findViewById(R.id.formSubmitBtn);
        commentInput= ( EditText ) findViewById(R.id.commentInput);
        tip_1 = (Button) findViewById(R.id.tip_1);
        tip_3 = (Button) findViewById(R.id.tip_3);
        tip_5 = (Button) findViewById(R.id.tip_5);
        tip_10 = (Button) findViewById(R.id.tip_10);
        tip_20 = (Button) findViewById(R.id.tip_20);
        tip_50 = (Button) findViewById(R.id.tip_50);

        btnList.add(tip_1);
        btnList.add(tip_3);
        btnList.add(tip_5);
        btnList.add(tip_10);
        btnList.add(tip_20);
        btnList.add(tip_50);

        // 按钮监听事件
        for(int i=0;i<btnList.size();i++){
            final Button currBtn=btnList.get(i);
            final int finalI = i;
            final GradientDrawable drawable= (GradientDrawable) currBtn.getBackground();
            drawable.setStroke(2,Color.parseColor(originColor));
            currBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    customedInput.setText("");
                    if(activeIndex!=-1) {
                        Button oldBtn = btnList.get(activeIndex);
                        GradientDrawable odrawable = (GradientDrawable) oldBtn.getBackground();
                        odrawable.setStroke(2,Color.parseColor(originColor));
                        oldBtn.setTextColor(Color.parseColor("#444444"));
                    }
                    activeIndex = finalI;
                    drawable.setStroke(2,getResources().getColor(R.color.colorSubPrimary));
                    currBtn.setTextColor(getResources().getColor(R.color.colorSubPrimary));

                    resultAmount=moneyList[activeIndex];
                    finalAmount.setText("$ "+resultAmount);
                }
            });
        }

        // EditView监听事件
        customedInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(finalAmount!=null) {
                    if(s.length()>0) {
                        resultAmount = Integer.parseInt(s.toString());
                    }
                    else{
                        resultAmount=0;
                    }
                    finalAmount.setText("$ "+resultAmount);
                    if(activeIndex!=-1) {
                        Button oldBtn = btnList.get(activeIndex);
                        GradientDrawable odrawable = (GradientDrawable) oldBtn.getBackground();
                        odrawable.setStroke(2, Color.parseColor(originColor));
                        oldBtn.setTextColor(Color.parseColor("#444444"));
                        activeIndex=-1;
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int stars= ( int ) starRating.getRating();
                if(stars==0){
                    Toast.makeText(getApplicationContext(),"Rating cannot be zero",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(resultAmount==0){
                    Toast.makeText(getApplicationContext(),"Tip money cannot be zero",Toast.LENGTH_SHORT).show();
                    return;
                }
                RequestQueue mqueue= Volley.newRequestQueue(getApplicationContext());
                HashMap<String,Object> data=new HashMap<String, Object>();
                data.put("token", LocalStorage.getItem(getApplicationContext(),"token"));
                data.put("star",starRating.getRating());
                data.put("money",resultAmount);
                data.put("comment",commentInput.getText().toString());
                try {
                    data.put("getter",deskInfo.getString("waiter_id"));
                    data.put("shop",deskInfo.getString("shop_id"));
                    data.put("desk",deskInfo.getString("desk_id"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                final LoadingAlertDialog loading=new LoadingAlertDialog(v.getContext());
                loading.show();
                JsonObjectRequest jsonRequest=new JsonObjectRequest
                        (Request.Method.POST, "http://crcrcry.com.cn/reward", new JSONObject(data), new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    String msg=response.getString("msg");
                                    if(response.getInt("code")==0){
                                        JSONObject dataJson=response.getJSONObject("data");
                                        LocalStorage.setItem(getApplicationContext(),"token",dataJson.getString("token"));
                                        LocalStorage.setItem(getApplicationContext(),"userInfo",dataJson.getJSONObject("userInfo").toString());
                                        Intent intent=new Intent(getApplicationContext(), RewardSuccess.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                    else Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_LONG).show();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                loading.hide();

                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                loading.hide();
                                String msg="";
                                if(error.networkResponse!=null && error.networkResponse.statusCode==401){
                                    msg="Invalid token";
                                }
                                else {
                                    msg="Network error";
                                }
                                Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_SHORT).show();

                            }
                        });
                mqueue.add(jsonRequest);
            }
        });

    }
}
