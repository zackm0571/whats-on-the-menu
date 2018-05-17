package com.zackmatthews.genericcomponents;

import android.graphics.Color;

import com.google.firebase.database.DatabaseReference;
import com.zackmatthews.genericcomponents.managers.MyFirebaseManager;

import java.util.HashMap;

/**
 * Created by zackmatthews on 9/25/17.
 */

public class FeedItemModel {
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
        this.isModified = true;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        this.isModified = true;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
        this.isModified = true;
    }

    public String getImg_id() {
        return img_id;
    }

    public void setImg_id(String img_id) {
        this.img_id = img_id;
        this.isModified = true;
    }

    public boolean isAvailability() {
        return availability;
    }

    public void setAvailability(boolean availability) {
        this.availability = availability;
        this.isModified = true;
    }

    private String id;
    private String title;
    private String msg;
    private String img_id;
    private boolean availability;
    private boolean isModified = false;
    private static final String hexGreen = "#006400";
    public String getAvailabiiltyString(){
        return availability ? "Available" : "Out";
    }

    public int getColorForAvailabilityStatus(){

        return availability ?  Color.parseColor(hexGreen) : Color.RED;
    }

    public void deletePost(){
        MyFirebaseManager.getInstance().deleteObjectInDb(MyFirebaseManager.postDir + "/" + id);
    }
    public void savePost(){
        DatabaseReference ref = MyFirebaseManager.getInstance().getDb().getReference(MyFirebaseManager.postDir).child(id);
        ref.child("title").setValue(title);
        ref.child("msg").setValue(msg);
        ref.child("availability").setValue(availability);
        ref.push();

        //.writeObjectToDb(MyFirebaseManager.postDir + "/" + id, this);
    }
}
