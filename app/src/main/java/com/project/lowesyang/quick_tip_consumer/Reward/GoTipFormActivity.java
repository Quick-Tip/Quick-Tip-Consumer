package com.project.lowesyang.quick_tip_consumer.Reward;

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
import android.widget.TextView;

import com.project.lowesyang.quick_tip_consumer.R;

import java.util.ArrayList;

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
    private TextView finalAmount;
    private int resultAmount;           // 最终金额
    private EditText customedInput;     // 自定义金额输入框
    private int activeIndex=-1;
    private int currTip=0;
    private String originColor="#cccccc";
    private ArrayList<Button> btnList = new ArrayList<>();
    private int[] moneyList={1,3,5,10,20,50};
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gotip_form);

        finalAmount= (TextView) findViewById(R.id.finalAmount);
        customedInput= (EditText) findViewById(R.id.customedInput);
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
                    GradientDrawable drawable= (GradientDrawable) currBtn.getBackground();
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
    }
}
