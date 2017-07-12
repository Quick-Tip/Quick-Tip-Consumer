package com.project.lowesyang.quick_tip_consumer.Reward;

import java.io.Serializable;

/**
 * Created by LowesYang on 2017/7/6.
 */

public class RewardModel implements Serializable{
    public final String id;
    public final String uid;
    public final String waitor_name;
    public final String money;
    public final int stars;
    public final String date;
    public final String comment;
    public final String restaurant;

    public RewardModel(String id,String uid,String waitor_name,String money,int stars,String date,String comment,String restaurant){
        this.id=id;
        this.uid=uid;
        this.waitor_name=waitor_name;
        this.money=money;
        this.stars=stars;
        this.date=date;
        this.comment=comment;
        this.restaurant=restaurant;
    }

}
