package com.zackmatthews.genericcomponents.managers;

import com.google.android.gms.tasks.OnCompleteListener;

/**
 * Created by zackmatthews on 10/26/17.
 */

public interface FirebaseManager {
    Object getObjectOnceByKey(String key);
    void writeObjectToDb(String key, Object val);
    void loginWithUsernamePassword(String username, String password, OnCompleteListener listener);
}
