package com.project.lowesyang.quick_tip_consumer.Boot;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.project.lowesyang.quick_tip_consumer.MainActivity;
import com.project.lowesyang.quick_tip_consumer.R;
import com.project.lowesyang.quick_tip_consumer.utils.LoadingAlertDialog;
import com.project.lowesyang.quick_tip_consumer.utils.LocalStorage;
import com.project.lowesyang.quick_tip_consumer.utils.NetworkState;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by LowesYang on 2017/7/7.
 */

public class RegisterActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        NetworkState.checkNetworkState(this);

        final EditTextWithClear userInput= (EditTextWithClear) findViewById(R.id.register_username);
        final EditTextWithClear pswdInput= (EditTextWithClear) findViewById(R.id.register_password);
        final EditTextWithClear confirmInput= (EditTextWithClear) findViewById(R.id.confirm_password);
        Button submitBtn= (Button) findViewById(R.id.submit_register);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username=userInput.getText().toString();
                final String password=pswdInput.getText().toString();
                final String confirmPswd=confirmInput.getText().toString();
                final LoadingAlertDialog loading=new LoadingAlertDialog(v.getContext());
                RequestQueue mqueue= Volley.newRequestQueue(getApplicationContext());
                HashMap<String,Object> data=new HashMap<String, Object>();
                data.put("username",username);
                data.put("password",password);
                data.put("verify",confirmPswd);
                data.put("user_type",0);
                loading.show();
                JsonObjectRequest jsonObjectRequest=new JsonObjectRequest
                        (Request.Method.POST, "http://crcrcry.com.cn/user/register", new JSONObject(data), new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                String msg = null;
                                try {
                                    if (( int ) response.get("code") == 0) {
                                        LocalStorage.setItem(getApplicationContext(), "token", response.getJSONObject("data").getString("token"));
                                        LocalStorage.setItem(getApplicationContext(), "userInfo", response.getJSONObject("data").toString());
                                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                        startActivity(intent);
                                        msg = "Welcome, my new friend!";
                                    } else {
                                        msg = response.getString("msg");
                                    }
                                } catch (JSONException e) {
                                    msg=e.getMessage();
                                    e.printStackTrace();
                                }
                                loading.hide();
                                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                loading.hide();
                                Toast.makeText(getApplicationContext(), "Network error", Toast.LENGTH_SHORT).show();
                            }
                        });
                mqueue.add(jsonObjectRequest);
            }
        });
    }
}
