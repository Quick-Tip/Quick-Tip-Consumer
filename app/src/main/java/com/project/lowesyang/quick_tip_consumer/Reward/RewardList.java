package com.project.lowesyang.quick_tip_consumer.Reward;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
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
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.project.lowesyang.quick_tip_consumer.R;
import com.project.lowesyang.quick_tip_consumer.utils.LoadingAlertDialog;
import com.project.lowesyang.quick_tip_consumer.utils.LocalStorage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
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
    private boolean isEnd=true;            //是否已获取所有数据
    private SwipeMenuListView listView=null;
    private LoadingAlertDialog loading=null;
    private int page=0;         //打赏数据页数
    private SwipeRefreshLayout refreshLayout=null;
    private TextView datePicker=null;
    // 起始时间,用于根据时间查询打赏历史
    private String start=null;
    private TextView allData=null;      //恢复时间选择器
    DatePickerDialog datePickerDialog=null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.reward_list,container,false);
        loadmoreView=inflater.inflate(R.layout.load_more,null);
        loadmoreView.setVisibility(View.VISIBLE);       //刷新视图默认不可见
        listView = (SwipeMenuListView) view.findViewById(R.id.rewards);
        datePicker= ( TextView ) view.findViewById(R.id.datePicker);
        datePicker.setText("Time Picker");
        loading=new LoadingAlertDialog(getActivity());
        allData= ( TextView ) view.findViewById(R.id.all_data);
        allData.setVisibility(View.INVISIBLE);  //一开始不可见

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
            }
        });

        // date picker initialize
        Calendar calendar=Calendar.getInstance();
        datePickerDialog=new DatePickerDialog(getActivity(),DatePickerDialog.THEME_HOLO_LIGHT,new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                start=year+"-"+(monthOfYear+1)+"-"+dayOfMonth;
                datePicker.setText("From "+start);
                initData();
                allData.setVisibility(View.VISIBLE);
            }
        },calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMaxDate((new Date()).getTime());


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

        // 选项点击事件
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

        // set creator in list item
        listView.setMenuCreator(new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem deleteItem=new SwipeMenuItem(getActivity());
                deleteItem.setBackground(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));
                deleteItem.setIcon(R.drawable.ic_delete);
                deleteItem.setWidth(300);

                menu.addMenuItem(deleteItem);
            }
        });

        // 滑动事件，解决与下拉刷新的事件冲突
        listView.setOnSwipeListener(new SwipeMenuListView.OnSwipeListener() {
            @Override
            public void onSwipeStart(int position) {
                refreshLayout.setEnabled(false);
            }

            @Override
            public void onSwipeEnd(int position) {
                refreshLayout.setEnabled(true);
            }
        });

        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(final int position, SwipeMenu menu, int index) {
                final RewardModel item=dataList.get(position);
                AlertDialog.Builder confirmBox=new AlertDialog.Builder(getActivity());
                confirmBox
                        .setMessage("This history cannot be recovered if deleted!")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                loading.show();
                                RequestQueue mqueue=Volley.newRequestQueue(getActivity());
                                HashMap<String,Object> data=new HashMap<String, Object>();
                                data.put("token",LocalStorage.getItem(getActivity(),"token"));
                                data.put("id",item.id);
                                JsonObjectRequest jsonRequest=new JsonObjectRequest
                                        (Request.Method.PUT, "http://crcrcry.com.cn/reward", new JSONObject(data), new Response.Listener<JSONObject>() {
                                            @Override
                                            public void onResponse(JSONObject response) {
                                                try {
                                                    if(response.getInt("code")==0){
                                                        dataList.remove(position);
                                                        adapter.updateView(dataList);
                                                    }
                                                    Toast.makeText(getActivity(),response.getString("msg"),Toast.LENGTH_SHORT).show();
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                                loading.hide();
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
                                                loading.hide();
                                                Toast.makeText(getActivity(),msg,Toast.LENGTH_SHORT).show();
                                            }

                                        });
                                mqueue.add(jsonRequest);
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("No",null)
                        .show();
                return true;
            }
        });

        //打开时间选择器
        datePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show();
            }
        });

        //恢复所有数据列表，清空时间选择器
        allData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start=null;
                datePicker.setText("Time Picker");
                allData.setVisibility(View.INVISIBLE);
                initData();
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initData();
    }

    @Override
    public void onResume() {
        super.onResume();

        // 判断是否要自动刷新打赏历史
        if(LocalStorage.getItem(getActivity(),"success_tip")=="1"){
            LocalStorage.setItem(getActivity(),"success_tip","0");
            initData();
        }
    }

    private void initData(){
        dataList=new ArrayList<RewardModel>();
        isEnd=true;
        listView.removeFooterView(completeText);
        listView.removeFooterView(loadmoreView);
        page=0;
        refreshLayout.setRefreshing(true);
        getData();
    }

    private void getData(){
        String url="http://crcrcry.com.cn/reward?token="+LocalStorage.getItem(getActivity(),"token");
        if(start!=null){
            url+="&start="+start;
        }
        RequestQueue mqueue= Volley.newRequestQueue(getActivity());
        if(isLoading) return;
        isLoading=true;
        final int psize=10;       //每次请求的数据数量
        //可选传入start,end
        JsonObjectRequest jsonRequest=new JsonObjectRequest
                (Request.Method.GET, url+"&p="+page+"&psize="+psize, null, new Response.Listener<JSONObject>() {
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
                                else if(isEnd){
                                    isEnd=false;
                                    listView.addFooterView(loadmoreView);
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
                    (json.getString("id"), json.getString("getterID"), json.getString("getterNickname"), json.getString("money"),
                            json.getInt("star"), json.getString("dayTime"), json.getString("comment"), json.getString("shopNickname")));
        }
        return list;
    }

    // 所有列表已加载完成
    private void loadComplete(){
        isEnd = true;
        listView.removeFooterView(loadmoreView);
        listView.addFooterView(completeText);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        adapter=null;
        dataList=null;
    }
}
