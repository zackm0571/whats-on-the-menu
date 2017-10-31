package com.zackmatthews.genericcomponents.managers;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by zackmatthews on 10/31/17.
 */

public class MyFirebaseManager implements FirebaseManager{
    private static final String postDir = "posts";
    private static MyFirebaseManager instance;
    private FirebaseAuth mAuth;

    public FirebaseAuth getFirebaseAuth() {
        if(mAuth == null) {
            mAuth = FirebaseAuth.getInstance();
        }
        return mAuth;
    }


    public static MyFirebaseManager getInstance(){
        if(instance == null){
            instance = new MyFirebaseManager();
        }
        return instance;
    }

    @Override
    public Object getObjectOnceByKey(String key) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference(key);

        return null;
    }

    @Override
    public void getObjectWithValueChangeListener(String key, ValueEventListener listener) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference(key);
        ref.addValueEventListener(listener);
    }

    @Override
    public void queryObjectLimitedToCountWithListener(String key, int count, ValueEventListener listener) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference(key);
        Query query = ref.limitToLast(count);
        query.addValueEventListener(listener);
    }

    @Override
    public void writeObjectToDb(String key, Object val) {
        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(key);

        myRef.setValue(val);
    }

    @Override
    public void loginWithUsernamePassword(String username, String password, OnCompleteListener listener) {
        getFirebaseAuth().signInWithEmailAndPassword(username, password)
                .addOnCompleteListener(listener);
    }
}
