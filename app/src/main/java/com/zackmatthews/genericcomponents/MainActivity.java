package com.zackmatthews.genericcomponents;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.zackmatthews.genericcomponents.managers.MyFirebaseManager;

public class MainActivity extends AppCompatActivity{

    private ListView listView;
    private AlertDialog dialogView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView)findViewById(R.id.listView);
        listView.setAdapter(new FeedAdapter(this));
        ((FeedAdapter)listView.getAdapter()).attachToFirebase("posts");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddPostDialog();
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //        .setAction("Action", null).show();

                //MyFirebaseManager.getInstance().writeObjectToDb("messages", "Hello World!");
            }
        });
    }

    private void showAddPostDialog(){
        if(dialogView != null){
            dialogView.dismiss();
        }
        dialogView = new AlertDialog.Builder(this)
                .setView(R.layout.add_post_dialog).setCancelable(true).setTitle("Add post")
                .setPositiveButton("Post", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        EditText titleText = (EditText)((AlertDialog)dialogInterface).findViewById(R.id.et_addpost_title);
                        EditText msgText = (EditText)((AlertDialog)dialogInterface).findViewById(R.id.et_addpost_msg);

                        FeedItemModel item = new FeedItemModel();
                        item.id = String.valueOf(System.currentTimeMillis());
                        item.msg = msgText.getText().toString();
                        item.title = titleText.getText().toString();

                        MyFirebaseManager.getInstance().writeObjectToDb(MyFirebaseManager.postDir + "/" + item.id, item);
                    }
                })
                .create();

        dialogView.show();
    }

}
