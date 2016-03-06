package com.anxpp.magnet;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;

import com.anxpp.magnet.Beans.Item;
import com.anxpp.magnet.Utils.BaseHtmlMatching;
import com.anxpp.magnet.Utils.MyListViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class ResultActivity extends AppCompatActivity{
    private ListView listView;
    private List<Item> itemList;
    private MyListViewAdapter myListViewAdapter;
    private ProgressDialog progressDialog;
    private BaseHtmlMatching.MatchingListener matchingListener;

    String[] info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        Intent intent = getIntent();
        info = intent.getStringArrayExtra("info");
        setTitle(intent.getStringExtra("title"));
        Log.e("URL",info[0]);
        Log.e("TITLE",info[1]);
        Log.e("LINK",info[2]);
        init();
    }

    private void init() {
        listView= (ListView) findViewById(R.id.list_result);
        itemList =new ArrayList<>();
        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("加载中...");
        progressDialog.setCancelable(false);
        myListViewAdapter=new MyListViewAdapter(this, itemList);

        matchingListener = new BaseHtmlMatching.MatchingListener() {
            @Override
            public void setItem(Item item) {
                item.setTitle(info[1]);
                item.setLink(info[2]);
            }
        };

        new BaseHtmlMatching(this, progressDialog, myListViewAdapter, listView, itemList,
                info[0],matchingListener).execute();
        listView.setAdapter(myListViewAdapter);
    }

}
