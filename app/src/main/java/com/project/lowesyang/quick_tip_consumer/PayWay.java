package com.project.lowesyang.quick_tip_consumer;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.project.lowesyang.quick_tip_consumer.utils.LocalStorage;

/**
 * Created by LowesYang on 2017/7/5.
 */

public class PayWay extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.pay_way,container,false);
        RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.payway);

        //默认选中支付宝
        radioGroup.check(R.id.paypal);
        savePayway("Paypal");

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                if(checkedId==R.id.paypal){
                    savePayway("PayPal");
                }
                else if(checkedId==R.id.alipay){
                    savePayway("AliPay");
                }
                else if(checkedId==R.id.wechat){
                    savePayway("Wechat Pay");
                }
                else if(checkedId==R.id.credit){
                    savePayway("Credit Card");
                }
                else{
                    savePayway("Bank Card");
                }
            }
        });
        return view;
    }

    private void savePayway(String payway){
        LocalStorage.setItem(getActivity(),"payway",payway);
    }
}
