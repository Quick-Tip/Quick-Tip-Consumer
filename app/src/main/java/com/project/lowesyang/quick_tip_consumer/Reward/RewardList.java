package com.project.lowesyang.quick_tip_consumer.Reward;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.project.lowesyang.quick_tip_consumer.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LowesYang on 2017/7/5.
 */

public class RewardList extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.reward_list,container,false);
        ListView listView = (ListView) view.findViewById(R.id.rewards);
        Button tipBtn= (Button) view.findViewById(R.id.tipBtn);
        tipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //打开一个新的activity
                Intent intent=new Intent(getActivity(), NFCReadActivity.class);
                startActivity(intent);
            }
        });
        RewardListAdapter adapter=new RewardListAdapter(getActivity(),getData());
        listView.setAdapter(adapter);
        return view;
    }

    private List<RewardModel> getData(){
        List<RewardModel> list = new ArrayList<RewardModel>();


        list.add(new RewardModel("LowesYang","$"+100,3,"2017.7.5","非常棒！","留学生食堂"));
        list.add(new RewardModel("LowesYang","$"+100,3,"2017.7.5","非常棒！","留学生食堂"));
        list.add(new RewardModel("LowesYang","$"+100,3,"2017.7.5","非常棒！","留学生食堂"));


        return list;
    }
}
