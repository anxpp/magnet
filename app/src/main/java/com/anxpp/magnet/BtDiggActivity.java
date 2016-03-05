package com.anxpp.magnet;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.anxpp.magnet.Beans.MessageBean;
import com.anxpp.magnet.Utils.MyListViewAdapter;
import com.anxpp.magnet.Utils.NetUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.List;

public class BtDiggActivity extends AppCompatActivity implements ListView.OnScrollListener {
    private ListView listView;
    private List<MessageBean> beanList;
    private String searchKey="";
    private MyListViewAdapter myListViewAdapter;
    private ProgressDialog progressDialog;
    private ClipboardManager clipboardmanager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bt_digg);
        searchKey=this.getIntent().getStringExtra("search_key");
        searchKey=searchKey.replace(" ", "+");
        init();
        listView.setOnScrollListener(this);
        ExecuteHtml executeHtml = new ExecuteHtml();
        executeHtml.execute();
        myListViewAdapter=new MyListViewAdapter(BtDiggActivity.this,beanList);
        listView.setAdapter(myListViewAdapter);


    }

    private void init() {
        listView= (ListView) findViewById(R.id.digg_listview);
        beanList=new ArrayList<>();
        progressDialog=new ProgressDialog(BtDiggActivity.this);
        progressDialog.setMessage("加载中...");
        progressDialog.setCancelable(false);
        clipboardmanager= (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState==AbsListView.OnScrollListener.SCROLL_STATE_IDLE){
            if (view.getLastVisiblePosition()==(view.getCount()-1)){
                if (listView.getFooterViewsCount()!=1){
                    new ExecuteHtml().execute();
                }
            }
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }

    private class ExecuteHtml extends AsyncTask<String,Integer,String >{

        //1
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }
        @Override
        protected String doInBackground(String... params) {
            try {
                String html;
                html = NetUtils.getHtml("http://www.shousibaocai.cc/search/" + searchKey);
                return html;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onProgressUpdate(Integer... values) {
            progressDialog.setProgress(values[0]);
        }
        @Override
        protected void onPostExecute(String s) {
            progressDialog.dismiss();
            getData(s);
            myListViewAdapter.notifyDataSetChanged();
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    clipboardmanager.setPrimaryClip(ClipData.newPlainText(null, beanList.get(position).getMagnetLink()));
                    Toast.makeText(BtDiggActivity.this, "已复制到剪贴板", Toast.LENGTH_SHORT).show();
                }
            });
        }

        private void getData(String s) {
            try {
                Document document = Jsoup.parse(s);
                int size=document.select("td.torrent_name>a").size();
                if (size<=0){
                    TextView namesake=new TextView(BtDiggActivity.this);
                    namesake.setText("已经到达底部");
                    namesake.setTextSize(30);
                    namesake.setGravity(Gravity.CENTER);
                    Toast.makeText(BtDiggActivity.this, "没有更多内容", Toast.LENGTH_SHORT).show();
                    listView.addFooterView(namesake,null,false);
                }
                for (int i=0;i<document.select("td.torrent_name>a").size();i++)
                {
                    MessageBean bean=new MessageBean();
                    String title=document.select("td.torrent_name>a").get(i).text();//title
                    String magnetlink=document.select("td.ttth>a").get(i).attr("href");//magnetlink
                    bean.setTitle(title);
                    bean.setMagnetLink(magnetlink);
                    beanList.add(bean);
                }
            }
            catch (Exception e){
                Toast.makeText(BtDiggActivity.this, "网络超时，请更换搜索源", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
