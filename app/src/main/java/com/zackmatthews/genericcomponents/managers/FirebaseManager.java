package com.zackmatthews.genericcomponents.managers;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

/**
 * Created by zackmatthews on 10/26/17.
 */

public interface FirebaseManager {
    Object getObjectOnceByKey(String key);
    void writeObjectToDb(String key, Object val);
    void loginWithUsernamePassword(String username, String password, OnCompleteListener listener);
    void getObjectWithValueChangeListener(String key, ValueEventListener listener);
    void queryObjectLimitedToCountWithListener(String key, int count, ValueEventListener listener);
    void uploadFile(String path, String fileName, OnSuccessListener listener);
    void deleteObjectInDb(String key);
    String downloadFile(String key, OnSuccessListener listener);
    StorageReference getStorageRef();
}
