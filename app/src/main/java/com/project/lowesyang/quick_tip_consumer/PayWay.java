package com.project.lowesyang.quick_tip_consumer;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

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
        radioGroup.check(R.id.alipay);
        return view;
    }
}
