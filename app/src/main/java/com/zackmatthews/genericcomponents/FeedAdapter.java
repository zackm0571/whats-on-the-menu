package com.zackmatthews.genericcomponents;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
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
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.zackmatthews.genericcomponents.managers.LruCacheManager;
import com.zackmatthews.genericcomponents.managers.MyFirebaseManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

import de.hdodenhof.circleimageview.CircleImageView;

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
        TextView msg, title, availability;
        CircleImageView img;
        ProgressBar progressBar;
        ViewGroup imageHolder;
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
                holder.imageHolder = view.findViewById(R.id.imageHolder);
                holder.availability = view.findViewById(R.id.availability);
                view.setTag(holder);
            }
        }
        else{
            holder = (ViewHolder)view.getTag();
        }

        if(holder == null) return view;

        FeedItemModel model = getItem(i);

        holder.title.setText(model.title);
        holder.msg.setText(model.msg);

        holder.availability.setText(model.getAvailabiiltyString());
        holder.availability.setTextColor(model.getColorForAvailabilityStatus());

        boolean isPicsEnabled = true;
        holder.imageHolder.setVisibility(View.GONE);

        if(!isPicsEnabled || model.img_id == null) {
            return view;
        }
        new LoadImageTask(model, holder).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, null);
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
            try {
                FeedItemModel modelObj = postSnapshot.getValue(FeedItemModel.class);
                if (modelObj != null){
                    modelList.add(modelObj);
                }
            }
            catch(DatabaseException e){
                //Can't convert to FeedItemModel, check data
                e.printStackTrace();
            }
        }
        this.notifyDataSetChanged();
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
}

class LoadImageTask extends AsyncTask<Void, Void, Bitmap>
                    implements OnSuccessListener<FileDownloadTask.TaskSnapshot>,
                     OnFailureListener{
    private FeedItemModel model;
    private FeedAdapter.ViewHolder holder;
    private File tmpPic = null;
    private Uri tmpPhotoURI;
    private Bitmap bmp;

    public LoadImageTask(FeedItemModel model, FeedAdapter.ViewHolder holder){
        this.model = model;
        this.holder = holder;
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Bitmap doInBackground(Void... arg) {
        bmp = LruCacheManager.getInstance().get(model.img_id);
        if(bmp == null){
            try {
                tmpPic = File.createTempFile(model.img_id, ".jpg");
            } catch (IOException e) {
                e.printStackTrace();
            }

            tmpPhotoURI = Uri.fromFile(tmpPic);

            MyFirebaseManager.getInstance().getStorageRef().child(model.img_id).getFile(tmpPhotoURI)
                    .addOnSuccessListener(this).addOnFailureListener(this);
        }
        return bmp;
    }

    @Override
    protected void onPostExecute(Bitmap bmp) {
        super.onPostExecute(bmp);
        boolean isCached = bmp != null;

        if(isCached){
            holder.img.setImageBitmap(bmp);
            holder.img.setVisibility(View.VISIBLE);
            holder.imageHolder.setVisibility(View.VISIBLE);
            holder.progressBar.setVisibility(View.GONE);
        }
        else{
            holder.imageHolder.setVisibility(View.VISIBLE);
            holder.progressBar.setVisibility(View.VISIBLE);
            holder.img.setVisibility(View.GONE);
        }
    }

    @Override
    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
        bmp = BitmapFactory.decodeFile(tmpPhotoURI.getPath());
        holder.img.setImageBitmap(bmp);
        holder.img.setVisibility(View.VISIBLE);
        holder.imageHolder.setVisibility(View.VISIBLE);
        holder.progressBar.setVisibility(View.GONE);
        LruCacheManager.getInstance().put(model.img_id, bmp);
    }

    @Override
    public void onFailure(@NonNull Exception e) {
        holder.img.setImageBitmap(null);
        holder.img.setVisibility(View.GONE);
        holder.imageHolder.setVisibility(View.GONE);
        holder.progressBar.setVisibility(View.GONE);
    }
}
