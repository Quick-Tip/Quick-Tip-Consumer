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
import com.project.lowesyang.quick_tip_consumer.utils.LocalStorage;
import com.project.lowesyang.quick_tip_consumer.utils.NetworkState;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by LowesYang on 2017/7/7.
 */

public class LoginActivity extends AppCompatActivity {
    private Button submitLogin;
    private Button gotoRegister;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle("Login");
        NetworkState.checkNetworkState(this);

        String token= ( String ) LocalStorage.getItem(getApplicationContext(),"token");
        if(token!=null){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        submitLogin= (Button) findViewById(R.id.submit_login);
        gotoRegister= (Button) findViewById(R.id.gotoRegister);

        final EditTextWithClear userInput= (EditTextWithClear) findViewById(R.id.login_username);
        final EditTextWithClear pswdInput= (EditTextWithClear) findViewById(R.id.login_password);

        submitLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final String username=userInput.getText().toString();
                final String password=pswdInput.getText().toString();
                RequestQueue mQueue= Volley.newRequestQueue(getApplicationContext());
                HashMap<String,Object> data=new HashMap<String, Object>();

                data.put("username",username);
                data.put("password",password);
                JsonObjectRequest jsonRequest=new JsonObjectRequest
                        (Request.Method.POST, "http://crcrcry.com.cn/user/login", new JSONObject(data), new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                String msg = null;
                                try {
                                    if (( int ) response.get("code") == 0) {
                                        LocalStorage.setItem(getApplicationContext(), "token", response.getJSONObject("data").getString("token"));
                                        LocalStorage.setItem(getApplicationContext(), "userInfo", response.getJSONObject("data").getString("userInfo"));
                                        System.out.println(LocalStorage.getItem(getApplicationContext(), "token"));
                                        Intent intent = new Intent(v.getContext(), MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                        msg = "Welcome back!";
                                    } else {
                                        msg = response.getString("msg");
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    msg = e.getMessage();
                                }
                                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();

                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
                mQueue.add(jsonRequest);
            }
        });

        gotoRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(v.getContext(),RegisterActivity.class);
                startActivity(intent);
            }
        });
    }
}
