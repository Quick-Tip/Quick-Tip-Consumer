package com.project.lowesyang.quick_tip_consumer.Reward;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import com.project.lowesyang.quick_tip_consumer.R;

import java.util.List;

/**
 * Created by LowesYang on 2017/7/6.
 */

public class RewardListAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private List<RewardModel> dataList;

    public RewardListAdapter(Context context, List<RewardModel> dataList) {
        mInflater=LayoutInflater.from(context);
        this.dataList=dataList;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public final class ViewHolder{
        public TextView waitor_name;
        public TextView money;
        public RatingBar stars;
        public TextView date;
        public TextView comment;
        public TextView restaurant;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder=null;
        if(convertView==null){
            holder=new ViewHolder();
            convertView=mInflater.inflate(R.layout.reward_item,null);
            holder.waitor_name= (TextView) convertView.findViewById(R.id.waitor_name);
            holder.money= (TextView) convertView.findViewById(R.id.money);
            holder.stars= (RatingBar) convertView.findViewById(R.id.stars);
            holder.date= (TextView) convertView.findViewById(R.id.date);
            holder.comment= (TextView) convertView.findViewById(R.id.comment);
            holder.restaurant= (TextView) convertView.findViewById(R.id.restaurant);
            convertView.setTag(holder);
        }
        else{
            holder= (ViewHolder) convertView.getTag();
        }

        RewardModel item=dataList.get(position);
        holder.waitor_name.setText(item.waitor_name);
        holder.money.setText(item.money);
        holder.stars.setRating((float)item.stars);
        holder.date.setText(item.date);
        holder.comment.setText(item.comment);
        holder.restaurant.setText(item.restaurant);

        return convertView;
    }
}
