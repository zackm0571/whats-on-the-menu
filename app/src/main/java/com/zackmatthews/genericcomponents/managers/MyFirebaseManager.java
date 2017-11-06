package com.zackmatthews.genericcomponents.managers;

import android.net.Uri;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;

/**
 * Created by zackmatthews on 10/31/17.
 */

public class MyFirebaseManager implements FirebaseManager{
    public static final String postDir = "posts";
    public static final String imageDir = "images";
    private static MyFirebaseManager instance;

    public FirebaseDatabase getDb() {
        if(db == null){
            db = FirebaseDatabase.getInstance();
        }
        return db;
    }

    private FirebaseDatabase db;
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
        DatabaseReference ref = getDb().getReference(key);

        return null;
    }

    @Override
    public void deleteObjectInDb(String key) {
        DatabaseReference ref = getDb().getReference().child(key);
        ref.removeValue();
    }

    @Override
    public void getObjectWithValueChangeListener(String key, ValueEventListener listener) {
        DatabaseReference ref = getDb().getReference(key);
        ref.addValueEventListener(listener);
    }

    @Override
    public void queryObjectLimitedToCountWithListener(String key, int count, ValueEventListener listener) {
        DatabaseReference ref = getDb().getReference(key);
        Query query = ref.limitToLast(count);
        query.addValueEventListener(listener);
    }

    @Override
    public void writeObjectToDb(String key, Object val) {
        // Write a message to the database
        DatabaseReference myRef = getDb().getReference(key);
        myRef.setValue(val);
    }

    @Override
    public void uploadFile(String path, String fileName, OnSuccessListener listener) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        Uri file = Uri.fromFile(new File(path));
        StorageReference imgRef = storageRef.child( fileName);
        imgRef.putFile(file).addOnSuccessListener(listener);
    }

    @Override
    public String downloadFile(String key, OnSuccessListener listener) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        
        return null;
    }

    @Override
    public StorageReference getStorageRef() {
        return FirebaseStorage.getInstance().getReference();
    }

    @Override
    public void loginWithUsernamePassword(String username, String password, OnCompleteListener listener) {
        getFirebaseAuth().signInWithEmailAndPassword(username, password)
                .addOnCompleteListener(listener);
    }
}
