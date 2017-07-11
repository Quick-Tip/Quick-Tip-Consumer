package com.project.lowesyang.quick_tip_consumer.Reward;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
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

/**
 * Created by LowesYang on 2017/7/11.
 */

public class RewardDetail extends AppCompatActivity {
    private RewardModel model=null;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reward_detail);
        model= ( RewardModel ) getIntent().getSerializableExtra("model");

        setTitle(model.waitor_name);

        // waiter info
        TextView nickname= ( TextView ) findViewById(R.id.waiter_nickname);
        final TextView restaurant= ( TextView ) findViewById(R.id.waiter_restaurant);
        final TextView avgScore= ( TextView ) findViewById(R.id.avg_score);

        // My tip part
        RatingBar detailRating= ( RatingBar ) findViewById(R.id.detail_rating);
        TextView detailDate= ( TextView ) findViewById(R.id.detail_date);
        TextView detailMoney= ( TextView ) findViewById(R.id.detail_money);
        TextView detailRest= ( TextView ) findViewById(R.id.detail_restaurant);
        TextView detailComment= ( TextView ) findViewById(R.id.detail_comment);

        // set infomation
        nickname.setText(model.waitor_name);
        detailRating.setRating(model.stars);
        detailDate.setText(model.date);
        detailMoney.setText(model.money+" $");
        detailRest.setText(model.restaurant);
        detailComment.setText(model.comment);

        final LoadingAlertDialog loading=new LoadingAlertDialog(this);
        loading.show();
        RequestQueue mqueue= Volley.newRequestQueue(this);
        JsonObjectRequest jsonRequest=new JsonObjectRequest
                (Request.Method.GET, "http://crcrcry.com.cn/user/star?token=" + LocalStorage.getItem(this, "token") + "&getterID=" + model.uid,
                        null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject dataJson=response.getJSONObject("data");
                            if(response.getInt("code")==0){
                                LocalStorage.setItem(getApplicationContext(),"token",dataJson.getString("token"));
                                avgScore.setText(dataJson.getString("star"));
                                restaurant.setText(dataJson.getString("nowShop"));
                            }
                            else Toast.makeText(getApplicationContext(),response.getString("msg"),Toast.LENGTH_LONG).show();

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
                        loading.hide();
                    }
                });
        mqueue.add(jsonRequest);
    }
}
