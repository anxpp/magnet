package com.anxpp.magnet;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.anxpp.magnet.Beans.Item;
import com.anxpp.magnet.Utils.HtmlMatching;
import com.anxpp.magnet.Utils.MyListViewAdapter;
import com.anxpp.magnet.Utils.NetUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class BreadActivity extends AppCompatActivity implements ListView.OnScrollListener {
    private ListView listView;
    private List<Item> beanList;
    private String searchKey = "";
    private int page = 1;
    private MyListViewAdapter myListViewAdapter;
    private ProgressDialog progressDialog;
    private ClipboardManager clipboardmanager;
    private HtmlMatching.MatchingListener matchingListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bread);
        searchKey = this.getIntent().getStringExtra("search_key");
//        searchKey = searchKey.replace(" ", "%20");
        //url 转码
        try {
            searchKey=URLEncoder.encode(searchKey,"utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


        init();
        listView.setOnScrollListener(this);
        ExecuteHtml executeHtml = new ExecuteHtml();
        executeHtml.execute();
        myListViewAdapter=new MyListViewAdapter(BreadActivity.this,beanList);
        listView.setAdapter(myListViewAdapter);
    }

    private void init() {
//        matchingListener = new HtmlMatching.MatchingListener() {
//            @Override
//            public String getTitle(Document document, int i) {
//                return null;
//            }
//
//            @Override
//            public String getLink(Document document, int i) {
//                return null;
//            }
//        };
        listView = (ListView) findViewById(R.id.bread_listview);
        beanList = new ArrayList<>();
        progressDialog=new ProgressDialog(BreadActivity.this);
        progressDialog.setMessage("加载中..");
        progressDialog.setCancelable(false);
        clipboardmanager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE){//如果停止滑动
            if (view.getLastVisiblePosition() == (view.getCount() - 1)){//如果到底部
                if (listView.getFooterViewsCount()!=1){
                    page++;
                    new ExecuteHtml().execute();
                }
            }
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }

    private class ExecuteHtml extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                String html;
                html = NetUtils.getHtml("http://www.breadsearch.com/search/" + searchKey + "/" + page);
                return html;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            progressDialog.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            progressDialog.dismiss();
            Log.d("CONTENT", s);
            getData(s);
            myListViewAdapter.notifyDataSetChanged();
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    clipboardmanager.setPrimaryClip(ClipData.newPlainText(null, beanList.get(i).getLink()));
                    Toast.makeText(BreadActivity.this, "磁力链已复制到剪贴板☃", Toast.LENGTH_LONG).show();
                }
            });
        }

        private void getData(String s) {
            try {
                String TAG = "TEST";
                Document document = Jsoup.parse(s);
                int size = document.select("span.list-title>a").size();
                Log.d(TAG, "size is "+size);
                if (size <= 0) {
                    TextView noMessage = new TextView(BreadActivity.this);
                    noMessage.setText("已经到达底部");
                    noMessage.setTextSize(30);
                    noMessage.setGravity(Gravity.CENTER);
                    Toast.makeText(BreadActivity.this, "没有更多内容", Toast.LENGTH_SHORT).show();
                    listView.addFooterView(noMessage, null, false);
                }
                for (int i = 0; i < document.select("span.list-title>a").size(); i++) {
                    Item bean = new Item();
                    String title = document.select("span.list-title>a").get(i).text();
                    String magnetLink = document.select("span.list-label>a").get(i).attr("href");
                    bean.setTitle(title);
                    bean.setLink(magnetLink);
                    beanList.add(bean);
                    Log.d(TAG, "Title=" + title + "  Magnet=" + magnetLink);
                }
            } catch (Exception e) {
                Toast.makeText(BreadActivity.this, "超时", Toast.LENGTH_SHORT).show();
            }
        }
    }
}