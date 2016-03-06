package com.anxpp.magnet;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.AbsListView;
import android.widget.ListView;

import com.anxpp.magnet.Beans.Item;
import com.anxpp.magnet.Utils.HtmlMatching;
import com.anxpp.magnet.Utils.MyListViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class SsbcActivity extends AppCompatActivity implements ListView.OnScrollListener {
    private ListView listView;
    private List<Item> beanList;
    private MyListViewAdapter myListViewAdapter;
    private ProgressDialog progressDialog;
    private String searchKey;
    private HtmlMatching.MatchingListener matchingListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ssbc);
        searchKey = this.getIntent().getStringExtra("search_key");
        searchKey = searchKey.replace(" ", "+");
        init();
    }

    private void init() {
        listView= (ListView) findViewById(R.id.digg_listview);
        beanList=new ArrayList<>();
        progressDialog=new ProgressDialog(SsbcActivity.this);
        progressDialog.setMessage("加载中...");
        progressDialog.setCancelable(false);
        listView.setOnScrollListener(this);
        myListViewAdapter=new MyListViewAdapter(SsbcActivity.this,beanList);

        matchingListener = new HtmlMatching.MatchingListener() {
            @Override
            public void setItem(Item item) {
                item.setTitle("div.search-item>div.item-title>h3>a");
                item.setLink("div.search-item>div.item-title>h3>a");
            }
        };

        new HtmlMatching(this, progressDialog, myListViewAdapter, listView, beanList,
                "http://www.shousibaocai.cc/search/", searchKey,matchingListener).execute();
        listView.setAdapter(myListViewAdapter);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState==AbsListView.OnScrollListener.SCROLL_STATE_IDLE){
            if (view.getLastVisiblePosition()==(view.getCount()-1)){
                if (listView.getFooterViewsCount()!=1){
                    new HtmlMatching(this,progressDialog,myListViewAdapter,listView,beanList,
                            "http://www.shousibaocai.cc/search/",searchKey,matchingListener).execute();
                }
            }
        }
    }
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {}

}
