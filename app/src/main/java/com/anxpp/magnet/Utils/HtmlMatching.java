package com.anxpp.magnet.Utils;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.AsyncTask;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.anxpp.magnet.Beans.MessageBean;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.util.List;


public class HtmlMatching extends AsyncTask<String,Integer,String > {

    private ProgressDialog progressDialog;
    private Context context;
    private MyListViewAdapter myListViewAdapter;
    private ListView listView;
    private List<MessageBean> beanList;
    private String searchKey="";

    public HtmlMatching(Context context,ProgressDialog progressDialog,MyListViewAdapter myListViewAdapter,ListView listView,
                        List<MessageBean> beanList,String searchKey){
        this.context = context;
        this.progressDialog = progressDialog;
        this.myListViewAdapter = myListViewAdapter;
        this.listView = listView;
        this.beanList = beanList;
        this.searchKey = searchKey;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog.show();
    }
    @Override
    protected String doInBackground(String... params) {
        try {
            String html;
            html = NetUtils.getHtml("http://www.shousibaocai.cc/search/" + java.net.URLEncoder.encode(searchKey,"UTF-8"));
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
                        .setPrimaryClip(ClipData.newPlainText(null, beanList.get(position).getMagnetLink()));
                Toast.makeText(context, "已复制到剪贴板", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getData(String s) {
        try {
            Document document = Jsoup.parse(s);
            int size=document.select("td.torrent_name>a").size();
            if (size<=0){
                TextView noMessage=new TextView(context);
                noMessage.setText("已经到达底部");
                noMessage.setTextSize(30);
                noMessage.setGravity(Gravity.CENTER);
                Toast.makeText(context, "没有更多内容", Toast.LENGTH_SHORT).show();
                listView.addFooterView(noMessage,null,false);
            }
            for (int i=0;i<document.select("div.search-item>div.item-title>h3>a").size();i++)
            {
                MessageBean bean=new MessageBean();
                String title=document.select("div.search-item>div.item-title>h3>a").get(i).attr("title");//title
                String magnetLink=document.select("div.search-item>div.item-title>h3>a").get(i).attr("href");//magnetLink

                bean.setTitle(title);
                bean.setMagnetLink("magnet:?xt=urn:btih:" + magnetLink.substring(4,magnetLink.length()));
                beanList.add(bean);
            }
        }
        catch (Exception e){
            Toast.makeText(context, "网络超时，请更换搜索源", Toast.LENGTH_SHORT).show();
        }
    }
}
