package com.project.lowesyang.quick_tip_consumer.Reward;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.project.lowesyang.quick_tip_consumer.R;
import com.project.lowesyang.quick_tip_consumer.utils.LocalStorage;

/**
 * Created by LowesYang on 2017/7/9.
 */

public class RewardSuccess extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reward_success);

        Button backHome= ( Button ) findViewById(R.id.backHome);
        backHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 通知打赏历史自动刷新
                LocalStorage.setItem(getApplicationContext(),"success_tip","1");
                finish();
            }
        });
    }
}
