package com.zackmatthews.genericcomponents;

import android.graphics.Color;

import com.zackmatthews.genericcomponents.managers.MyFirebaseManager;

import java.util.HashMap;

/**
 * Created by zackmatthews on 9/25/17.
 */

public class FeedItemModel {
    public String id;
    public String title;
    public String msg;
    public String img_id;
    public boolean availability;

    public String getAvailabiiltyString(){
        return availability ? "Available" : "Out";
    }

    public int getColorForAvailabilityStatus(){

        return availability ? Color.GREEN : Color.RED;
    }

    public void deletePost(){
        MyFirebaseManager.getInstance().deleteObjectInDb(MyFirebaseManager.postDir + "/" + id);
    }
}
