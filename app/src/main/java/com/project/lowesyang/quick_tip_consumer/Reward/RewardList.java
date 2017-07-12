package com.project.lowesyang.quick_tip_consumer.Reward;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.project.lowesyang.quick_tip_consumer.R;
import com.project.lowesyang.quick_tip_consumer.utils.LocalStorage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LowesYang on 2017/7/5.
 */

public class RewardList extends Fragment {
    private RewardListAdapter adapter=null;
    private View loadmoreView=null;
    private TextView completeText=null;
    private ArrayList<RewardModel> dataList=null;
    private boolean isLoading=false;        //是否正在请求数据
    private boolean isEnd=false;            //是否已获取所有数据
    private ListView listView=null;
    private int page=0;         //打赏数据页数
    SwipeRefreshLayout refreshLayout=null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.reward_list,container,false);
        loadmoreView=inflater.inflate(R.layout.load_more,null);
        loadmoreView.setVisibility(View.VISIBLE);       //刷新视图默认不可见
        listView = (ListView) view.findViewById(R.id.rewards);

        // No more history textview
        completeText = new TextView(getActivity());
        completeText.setText("No more history");
        completeText.setGravity(Gravity.CENTER);
        completeText.setPadding(0, 30, 0, 30);

        dataList=new ArrayList<>();
        Button tipBtn= (Button) view.findViewById(R.id.tipBtn);
        refreshLayout= ( SwipeRefreshLayout ) view.findViewById(R.id.refreshList);
        refreshLayout.setColorSchemeResources(R.color.colorPrimary);
        tipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //打开一个新的activity
                Intent intent=new Intent(getActivity(), NFCReadActivity.class);
                startActivity(intent);
            }
        });

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initData();
                refreshLayout.setRefreshing(true);
                getData();
            }
        });
        initData();
        refreshLayout.setRefreshing(true);
        getData();
        // 监听滚动事件
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            private int last_index;
            private int total_index;
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // 若滚动至底部
                if(last_index==total_index && !isLoading && (scrollState==AbsListView.OnScrollListener.SCROLL_STATE_IDLE) && !isEnd){
                    getData();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                last_index=firstVisibleItem+visibleItemCount;
                total_index=totalItemCount;
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                RewardModel model= ( RewardModel ) listView.getItemAtPosition(position);
                // 打开新的activity，并传递获取到的model
                Intent intent=new Intent(getActivity(),RewardDetail.class);
                Bundle mbundle=new Bundle();
                mbundle.putSerializable("model",model);
                intent.putExtras(mbundle);
                startActivity(intent);
            }
        });

        listView.addFooterView(loadmoreView);

        return view;
    }

    private void initData(){
        dataList=new ArrayList<RewardModel>();
        if(isEnd){
            isEnd=false;
            listView.removeFooterView(completeText);
            listView.addFooterView(loadmoreView);
        }
        page=0;
    }

    private void getData(){
        RequestQueue mqueue= Volley.newRequestQueue(getActivity());
        if(isLoading) return;
        isLoading=true;
        final int psize=10;       //每次请求的数据数量
        //可选传入start,end
        JsonObjectRequest jsonRequest=new JsonObjectRequest
                (Request.Method.GET, "http://crcrcry.com.cn/reward?token="+LocalStorage.getItem(getActivity(),"token")+"&p="+page+"&psize="+psize, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject jsonData=response.getJSONObject("data");
                            if(response.getInt("code")==0){
                                LocalStorage.setItem(getActivity(),"token",jsonData.getString("token"));
                                JSONArray jsonArray=jsonData.getJSONArray("rewardList");
                                System.out.println(jsonArray);
                                ArrayList<RewardModel> newList= ( ArrayList<RewardModel> ) JSONArr2List(jsonArray);
                                if(newList.size()<psize){
                                    loadComplete();
                                }
                                dataList.addAll(newList);
                                // set adapter
                                if(adapter==null) {
                                    adapter = new RewardListAdapter(getActivity(), dataList);
                                    listView.setAdapter(adapter);
                                }
                                else{
                                    adapter.updateView(dataList);
                                }
                                page++;
                            }
                            else{
                                Toast.makeText(getActivity(),response.getString("msg"),Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        isLoading=false;
                        refreshLayout.setRefreshing(false);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String msg="";
                        if(error.networkResponse!=null && error.networkResponse.statusCode==401){
                            msg="Invalid token";
                        }
                        else {
                            msg="Network error";
                        }
                        Toast.makeText(getActivity(),msg,Toast.LENGTH_SHORT).show();
                        isLoading=false;
                        refreshLayout.setRefreshing(false);
                    }
                });
        mqueue.add(jsonRequest);

    }

    /**
     * 将JSONArray转换为List
     */
    private List<RewardModel> JSONArr2List(JSONArray jsonArr) throws JSONException {
        List<RewardModel> list=new ArrayList<>();
        JSONObject json=null;
        for(int i=0;i<jsonArr.length();i++){
            json=jsonArr.getJSONObject(i);
            list.add(new RewardModel
                    (json.getString("getterID"),json.getString("getterNickname"),json.getString("money"),
                            json.getInt("star"),json.getString("dayTime"),json.getString("comment"),json.getString("shopNickname")));
        }
        return list;
    }

    // 所有列表已加载完成
    private void loadComplete(){
        if(!isEnd) {
            isEnd = true;
            listView.removeFooterView(loadmoreView);
            listView.addFooterView(completeText);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        adapter=null;
        dataList=null;
    }
}
