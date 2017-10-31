package com.zackmatthews.genericcomponents.managers;

/**
 * Created by zackmatthews on 10/26/17.
 */

public interface FirebaseManager {
    Object getObjectOnceByKey(String key);
    void writeObjectToDb(String key, Object val);
}
