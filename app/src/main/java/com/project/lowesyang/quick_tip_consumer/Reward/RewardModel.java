package com.project.lowesyang.quick_tip_consumer.Reward;

/**
 * Created by LowesYang on 2017/7/6.
 */

public class RewardModel {
    public final String waitor_name;
    public final String money;
    public final int stars;
    public final String date;
    public final String comment;
    public final String restaurant;

    public RewardModel(String waitor_name,String money,int stars,String date,String comment,String restaurant){
        this.waitor_name=waitor_name;
        this.money=money;
        this.stars=stars;
        this.date=date;
        this.comment=comment;
        this.restaurant=restaurant;
    }

}
