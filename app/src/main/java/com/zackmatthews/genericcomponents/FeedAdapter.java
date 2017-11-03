package com.zackmatthews.genericcomponents;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.zackmatthews.genericcomponents.managers.LruCacheManager;
import com.zackmatthews.genericcomponents.managers.MyFirebaseManager;

import java.io.File;
import java.io.IOException;
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
            if(inflater != null) {
                view = inflater.inflate(RES_ID, null);
            }
        }
        final FeedItemModel model = getItem(i);
        TextView title = view.findViewById(R.id.title);
        title.setText(model.title);

        TextView msg = view.findViewById(R.id.msg);
        msg.setText(model.msg);

        final ImageView img = view.findViewById(R.id.feed_item_img);

        final boolean isPicsEnabled = true;
        if(!isPicsEnabled) return view;

        File tmpPic = LruCacheManager.getInstance().get(model.id);
        boolean isCached = false;
        img.setImageURI(null);

        if(tmpPic != null){
            Uri photoURI = Uri.fromFile(tmpPic);
            img.setImageURI(photoURI);
            isCached = true;
        }
        else{
            try {
                tmpPic = File.createTempFile(model.id, ".jpg");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        if(!isCached) {
            final Uri photoURI = Uri.fromFile(tmpPic);
            MyFirebaseManager.getInstance().getStorageRef().child(model.id).getFile(photoURI)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            img.setImageURI(photoURI);
                            LruCacheManager.getInstance().put(model.id, new File(photoURI.getPath()));
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    img.setImageURI(null);
                }
            });
        }

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
