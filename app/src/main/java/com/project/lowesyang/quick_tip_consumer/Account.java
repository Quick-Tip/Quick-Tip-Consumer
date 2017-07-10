package com.project.lowesyang.quick_tip_consumer;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.project.lowesyang.quick_tip_consumer.utils.LoadingAlertDialog;
import com.project.lowesyang.quick_tip_consumer.utils.LocalStorage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by LowesYang on 2017/7/6.
 */

public class Account extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.account,container,false);
        final Button submitBtn = ( Button ) view.findViewById(R.id.edit_submit);
        final EditText nickname= ( EditText ) view.findViewById(R.id.nickname);
        final JSONObject userInfo= ( JSONObject ) LocalStorage.getItem(view.getContext(),"userInfo");
        try {
            nickname.setText(( CharSequence ) userInfo.get("nickname"));
            submitBtn.setEnabled(false);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        nickname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    if(nickname.getText()!=userInfo.get("nickname")){
                        submitBtn.setEnabled(true);
                    }
                    else{
                        submitBtn.setEnabled(false);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final LoadingAlertDialog loading=new LoadingAlertDialog(getActivity());
                RequestQueue mqueue= Volley.newRequestQueue(v.getContext());
                loading.show();
                HashMap<String,Object> data=new HashMap<String, Object>();
                data.put("token",LocalStorage.getItem(v.getContext(),"token"));
                String name=nickname.getText().toString();
                if(name.length()<6){
                    Toast.makeText(getActivity(),"Name should have more than 4 characters",Toast.LENGTH_SHORT).show();
                    return;
                }
                data.put("nickname",nickname.getText().toString());
                JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.PUT, "http://crcrcry.com.cn/user", new JSONObject(data), new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String msg="";
                        try {
                            msg=response.getString("msg");
                            if((int)response.get("code")==0){
                                LocalStorage.setItem(getActivity(),"token",response.getJSONObject("data").getString("token"));
                                userInfo.put("nickname",nickname.getText().toString());
                                LocalStorage.setItem(getActivity(),"userInfo",userInfo.toString());
                                submitBtn.setEnabled(false);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            msg=e.getMessage();
                        }
                        loading.hide();
                        Toast.makeText(getActivity(),msg,Toast.LENGTH_LONG).show();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loading.hide();
                        String msg="";
                        if(error.networkResponse.statusCode==401){
                            msg="Invalid token";
                        }
                        else {
                            msg="Network error";
                        }
                        Toast.makeText(getActivity(),"Invalid token",Toast.LENGTH_SHORT).show();
                    }
                });
                mqueue.add(jsonObjectRequest);
            }
        });

        return view;
    }

}
