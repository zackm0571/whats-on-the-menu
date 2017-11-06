package com.zackmatthews.genericcomponents;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
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

    static class ViewHolder{
        TextView msg, title;
        ImageView img;
        ProgressBar progressBar;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if(view == null){
            LayoutInflater inflater = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if(inflater != null) {
                view = inflater.inflate(RES_ID, null);
                holder = new ViewHolder();
                holder.msg = view.findViewById(R.id.msg);
                holder.title = view.findViewById(R.id.title);
                holder.img = view.findViewById(R.id.feed_item_img);
                holder.progressBar = view.findViewById(R.id.progressBar);
                view.setTag(holder);
            }
        }
        else{
            holder = (ViewHolder)view.getTag();
        }

        if(holder == null) return view;

        final FeedItemModel model = getItem(i);

        holder.title.setText(model.title);
        holder.msg.setText(model.msg);


        final boolean isPicsEnabled = true;
        if(!isPicsEnabled) return view;

        Bitmap bmp = LruCacheManager.getInstance().get(model.id);

        boolean isCached = false;
        File tmpPic = null;
        holder.img.setImageBitmap(null);

        if(bmp != null){
            holder.img.setImageBitmap(bmp);
            holder.progressBar.setVisibility(View.GONE);
            isCached = true;
        }
        else{
            holder.progressBar.setVisibility(View.VISIBLE);
            try {
                tmpPic = File.createTempFile(model.id, ".jpg");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if(!isCached) {
            final Uri tmpPhotoURI = Uri.fromFile(tmpPic);
            final ViewHolder tmpHolder = holder;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    MyFirebaseManager.getInstance().getStorageRef().child(model.id).getFile(tmpPhotoURI)
                            .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                    Bitmap _bmp = BitmapFactory.decodeFile(tmpPhotoURI.getPath());
                                    tmpHolder.img.setImageBitmap(_bmp);
                                    tmpHolder.progressBar.setVisibility(View.GONE);
                                    LruCacheManager.getInstance().put(model.id, _bmp);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            tmpHolder.img.setImageBitmap(null);
                            tmpHolder.progressBar.setVisibility(View.GONE);
                        }
                    });
                }
            }).start();

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
