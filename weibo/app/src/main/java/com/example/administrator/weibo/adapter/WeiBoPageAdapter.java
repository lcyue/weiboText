package com.example.administrator.weibo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.example.administrator.weibo.R;
import com.example.administrator.weibo.weibo.WeiBoPage;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/11/23.
 */
public class WeiBoPageAdapter extends BaseAdapter {
    Context  context;
    ArrayList<WeiBoPage> list;
    LayoutInflater layoutInflater;
    public  WeiBoPageAdapter(Context context, ArrayList<WeiBoPage> list){
        this.context = context;
        this.list = list;
        layoutInflater = LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null){
            view = layoutInflater.inflate(R.layout.weibo_listview_imet, null);
        }
        return view;
    }
}
