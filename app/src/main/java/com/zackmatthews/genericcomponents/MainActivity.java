package com.zackmatthews.genericcomponents;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.zackmatthews.genericcomponents.managers.ApplicationManager;
import com.zackmatthews.genericcomponents.managers.MyFirebaseManager;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements OnSuccessListener{

    private ListView listView;
    private AlertDialog dialogView;
    private String tmpUploadDir = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView)findViewById(R.id.listView);
        listView.setAdapter(new FeedAdapter(this));

        if(ApplicationManager.isAdmin) {
            listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                    showModifyPostDialog((FeedItemModel) adapterView.getItemAtPosition(i));
                    return false;
                }
            });
        }
        ((FeedAdapter)listView.getAdapter()).attachToFirebase("posts");
//
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        toolbar.setTitle(getString(R.string.app_name));
//
//        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setVisibility( ApplicationManager.isAdmin ? View.VISIBLE : View.INVISIBLE );
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddPostDialog();
            }
        });
    }

    private void showModifyPostDialog(final FeedItemModel item){

        if(dialogView != null){
            dialogView.dismiss();
        }

        dialogView = new AlertDialog.Builder(this).setTitle("Modify Post?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        item.deletePost();
                    }
                })
                .setNeutralButton("Edit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showEditPostDialog(item);
                    }
                })
                .setCancelable(true)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogView.dismiss();
                    }
                })
                .create();
        dialogView.show();
    }

    private void showEditPostDialog(final FeedItemModel model){
        if(dialogView != null){
            dialogView.dismiss();
        }

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        if(inflater == null) return;
        final View v = inflater.inflate(R.layout.add_post_dialog, null);
        ((EditText)v.findViewById(R.id.et_addpost_title)).setText(model.getTitle());
        ((EditText)v.findViewById(R.id.et_addpost_msg)).setText(model.getMsg());
        ((CheckBox)v.findViewById(R.id.cb_addpost_avaiable)).setChecked(model.isAvailability());
        dialogView = new AlertDialog.Builder(this)
                .setView(v)
                .setCancelable(true)
                .setTitle("Editing post")
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        boolean hasImg = tmpUploadDir != null && tmpUploadDir.length() > 0;
                        FeedItemModel item = new FeedItemModel();
                        item.setTitle(((EditText)v.findViewById(R.id.et_addpost_title)).getText().toString());
                        item.setMsg(((EditText)v.findViewById(R.id.et_addpost_msg)).getText().toString());
                        item.setId(model.getId());
                        item.setAvailability(((CheckBox)v.findViewById(R.id.cb_addpost_avaiable)).isChecked());
                        item.setImg_id(model.getImg_id());

//                        if(hasImg){
//                            item.setImg_id(item.getId());
//                            MyFirebaseManager.getInstance().uploadFile(tmpUploadDir, item.getImg_id(), MainActivity.this);
//                        }

                       item.savePost();
                    }
                })
                .create();
        dialogView.show();

        Button addPictureButton = ((Button)dialogView.findViewById(R.id.btn_addpost_picture));
        addPictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });
    }

    private void showAddPostDialog(){
        if(dialogView != null){
            dialogView.dismiss();
        }
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        if(inflater == null) return;
        final View v = inflater.inflate(R.layout.add_post_dialog, null);

        dialogView = new AlertDialog.Builder(this)
                .setView(R.layout.add_post_dialog).setCancelable(true).setTitle("Add post")
                .setPositiveButton("Post", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        EditText titleText = (EditText)((AlertDialog)dialogInterface).findViewById(R.id.et_addpost_title);
                        EditText msgText = (EditText)((AlertDialog)dialogInterface).findViewById(R.id.et_addpost_msg);
                        boolean hasImg = tmpUploadDir != null && tmpUploadDir.length() > 0;

                        FeedItemModel item = new FeedItemModel();
                        item.setId(String.valueOf(System.currentTimeMillis()));
                        item.setMsg(msgText.getText().toString());
                        item.setTitle(titleText.getText().toString());
                        item.setAvailability(((CheckBox)v.findViewById(R.id.cb_addpost_avaiable)).isChecked());
                        if(hasImg){
                            item.setImg_id(item.getId());
                            MyFirebaseManager.getInstance().uploadFile(tmpUploadDir, item.getImg_id(), MainActivity.this);
                        }

                        MyFirebaseManager.getInstance().writeObjectToDb(MyFirebaseManager.postDir + "/" + item.getId(), item);
                    }
                })
                .create();
        dialogView.show();

        Button addPictureButton = ((Button)dialogView.findViewById(R.id.btn_addpost_picture));
        addPictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });
    }
    static final int REQUEST_TAKE_PHOTO = 1;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = photoFile = createImageFile();

            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.zackmatthews.genericcomponents.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            //Bundle extras = data.getExtras();
            //tmpBitmap = (Bitmap) extras.get("data");
            if(tmpUploadDir != null && tmpUploadDir.length() > 0) {
                ((Button) dialogView.findViewById(R.id.btn_addpost_picture)).setText("Take another picture");
            }
        }
    }

        String mCurrentPhotoPath;

        private File createImageFile(){
            // Create an image file name
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = "JPEG_" + timeStamp + "_";
            File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            File image = null;
            try {
                image = File.createTempFile(
                        imageFileName,  /* prefix */
                        ".jpg",         /* suffix */
                        storageDir      /* directory */
                );
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Save a file: path for use with ACTION_VIEW intents
            if(image != null) {
                mCurrentPhotoPath = image.getAbsolutePath();
                tmpUploadDir = mCurrentPhotoPath;
            }
            return image;
    }

    @Override
    public void onSuccess(Object o) {
        ((FeedAdapter)listView.getAdapter()).notifyDataSetChanged();
        tmpUploadDir = "";
    }
}
