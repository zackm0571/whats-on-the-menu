package com.zackmatthews.genericcomponents;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.zackmatthews.genericcomponents.managers.MyFirebaseManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zackmatthews on 9/25/17.
 */

public class FeedAdapter extends BaseAdapter implements ValueEventListener{
    private static final int RES_ID = R.layout.feed_item_layout;
    private static final boolean isDEBUG = false;
    private List<FeedItemModel> modelList = new ArrayList<>();
    private Context ctx;

    public FeedAdapter(Context context) {
        this.ctx = context;
    }
    public void attachToFirebase(String key){
        MyFirebaseManager.getInstance().queryObjectLimitedToCountWithListener(key, 100, this);
    }
    @Override
    public int getCount() {
        if(isDEBUG) return 10;
        return modelList.size();
    }

    @Override
    public FeedItemModel getItem(int i) {
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
        TextView title = (TextView)view.findViewById(R.id.title);
        title.setText(getItem(i).title);

        TextView msg = (TextView)view.findViewById(R.id.msg);
        msg.setText(getItem(i).msg);
        return view;
    }

    public List<FeedItemModel> getModelList() {
        return modelList;
    }

    /** Firebase **/
    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        modelList.clear();
        for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
            modelList.add(postSnapshot.getValue(FeedItemModel.class));
        }
        this.notifyDataSetChanged();
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
}
