package com.anxpp.magnet.Utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.anxpp.magnet.Beans.MessageBean;
import com.anxpp.magnet.R;

import java.util.List;


public class MyListViewAdapter extends BaseAdapter {
    Context context;
    LayoutInflater mInflater;
    List<MessageBean> messageBeanList;
    public MyListViewAdapter(Context context, List<MessageBean> messageItemBeanList) {
        this.context = context;
        this.messageBeanList = messageItemBeanList;
        mInflater=LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return messageBeanList.size();
    }

    @Override
    public Object getItem(int i) {
        return messageBeanList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (convertView==null)
        {
            viewHolder=new ViewHolder();
            convertView=mInflater.inflate(R.layout.mylistview,null);
            viewHolder.titleTV= (TextView) convertView.findViewById(R.id.title_tv);
            convertView.setTag(viewHolder);
        }
        else
        {
            viewHolder= (ViewHolder) convertView.getTag();
        }
        MessageBean bean=messageBeanList.get(i);
        viewHolder.titleTV.setText(bean.getTitle());
        return convertView;
    }
}
class ViewHolder{
    public TextView titleTV;
}
