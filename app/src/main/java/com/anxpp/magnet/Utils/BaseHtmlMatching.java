package com.anxpp.magnet.Utils;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.anxpp.magnet.Beans.Item;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.net.URLEncoder;
import java.util.List;


public class BaseHtmlMatching extends AsyncTask<String,Integer,String > {

    private ProgressDialog progressDialog;
    private Context context;
    private MyListViewAdapter myListViewAdapter;
    private ListView listView;
    private List<Item> beanList;
    private String addr="";
    private MatchingListener matchingListener;

    public BaseHtmlMatching(Context context, ProgressDialog progressDialog, MyListViewAdapter myListViewAdapter, ListView listView,
                            List<Item> beanList, String addr, MatchingListener matchingListener){
        this.context = context;
        this.progressDialog = progressDialog;
        this.myListViewAdapter = myListViewAdapter;
        this.listView = listView;
        this.beanList = beanList;
        this.addr = addr;
        this.matchingListener = matchingListener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog.show();
    }
    @Override
    protected String doInBackground(String... params) {
        try {
            Log.e("doInBackground-addr",addr);
            String html;
            addr = addr.replace("/","qwesa");
            addr = addr.replace(":","poiop");
            addr = addr.replace("-","pdlgj");
            addr = URLEncoder.encode(addr,"UTF-8");
            addr = addr.replace("qwesa","/");
            addr = addr.replace("poiop",":");
            addr = addr.replace("pdlgj","-");
            Log.e("doInBackground-addr",addr);
            html = NetUtils.getHtml(addr);
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
                ////获取剪贴板管理服务->添加到剪切板
                ((ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE))
                        .setPrimaryClip(ClipData.newPlainText(null, beanList.get(position).getLink()));
                Toast.makeText(context, "已复制到剪贴板", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getData(String s) {
        Log.e("getData-s",s);
        try {
            Item item = new Item();
            matchingListener.setItem(item);
            Document document = Jsoup.parse(s);
            int size=document.select(item.getTitle()).size();
            if (size<=0){
                TextView noMessage=new TextView(context);
                noMessage.setText("已经到达底部");
                noMessage.setTextSize(20);
                noMessage.setGravity(Gravity.CENTER);
                Toast.makeText(context, "没有更多内容", Toast.LENGTH_SHORT).show();
                listView.addFooterView(noMessage,null,false);
            }
            for (int i=0;i<document.select(item.getTitle()).size();i++)
            {
                Item bean=new Item();
                String title = document.select(item.getTitle()).get(i).attr("title");
                String magnetLink = document.select(item.getLink()).get(i).attr("href");

                bean.setTitle(title);
                bean.setLink("magnet:?xt=urn:btih:" + magnetLink.substring(4,magnetLink.length()));
                beanList.add(bean);
            }
        }
        catch (Exception e){
            Toast.makeText(context, "网络超时，请更换搜索源", Toast.LENGTH_SHORT).show();
            Log.e("getData",e.getMessage());
            e.printStackTrace();
        }
    }
    public interface MatchingListener{
        void setItem(Item item);
    }
}
