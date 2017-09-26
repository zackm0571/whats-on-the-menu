package com.zackmatthews.genericcomponents;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zackmatthews on 9/25/17.
 */

public class FeedAdapter extends BaseAdapter {
    private static final int RES_ID = R.layout.feed_item_layout;
    private static final boolean isDEBUG = true;
    private List<FeedItemModel> modelList = new ArrayList<>();
    private Context ctx;

    public FeedAdapter(Context context) {
        this.ctx = context;
    }

    @Override
    public int getCount() {
        if(isDEBUG) return 10;
        return modelList.size();
    }

    @Override
    public Object getItem(int i) {
        return modelList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view == null){
            LayoutInflater inflater = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(RES_ID, null);
        }
        return view;
    }

    public List<FeedItemModel> getModelList() {
        return modelList;
    }
}
