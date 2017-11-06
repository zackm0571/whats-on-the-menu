package com.zackmatthews.genericcomponents;

import com.zackmatthews.genericcomponents.managers.MyFirebaseManager;

/**
 * Created by zackmatthews on 9/25/17.
 */

public class FeedItemModel {
    public String id;
    public String title;
    public String msg;

    public void deletePost(){
        MyFirebaseManager.getInstance().deleteObjectInDb(MyFirebaseManager.postDir + "/" + id);
    }
}
