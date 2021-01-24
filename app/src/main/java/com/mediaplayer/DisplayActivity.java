package com.mediaplayer;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.GridView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.mediaplayer.Adapter.ImageAdapter;
import com.mediaplayer.Loader.ShowLoader;
import com.mediaplayer.Model.FolderModel;

import java.util.ArrayList;

public class DisplayActivity extends AppCompatActivity {
    private static final int REQUESCODE = 100;
    ShowLoader showLoader;
    FirebaseFirestore firebaseFirestore;
    String pictureUrl, folderDataString;
    FolderModel folderModel;
    ArrayList<String> imagesList = new ArrayList<>();
    GridView gridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        firebaseFirestore = FirebaseFirestore.getInstance();
        showLoader = new ShowLoader(DisplayActivity.this);
        gridView = findViewById(R.id.gridView);
        folderDataString = getIntent().getStringExtra("FolderData");
        Gson gson = new Gson();
        folderModel = gson.fromJson(folderDataString, FolderModel.class);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        showLoader.showProgressDialog();
        getPictureList();
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, REQUESCODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            Uri pickedImgUri = data.getData();
            updateUserInfo(pickedImgUri);
        }
    }

    private void updateUserInfo(Uri pickedImgUri) {
        showLoader.showProgressDialog();
        StorageReference mStorage = FirebaseStorage.getInstance().getReference().child("users_photos");
        final StorageReference imageFilePath = mStorage.child(pickedImgUri.getLastPathSegment());
        imageFilePath.putFile(pickedImgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        pictureUrl = "" + uri;
                        System.out.println("Picture: " + pictureUrl);
                        if (folderModel.getArrayList() != null) {
                            imagesList = folderModel.getArrayList();
                        }
                        imagesList.add(pictureUrl);
                        FolderModel fModel = new FolderModel(folderModel.getDocId(), folderModel.getFolderName(), folderModel.getEmail(), imagesList);
                        firebaseFirestore.collection("Folder").document(folderModel.getDocId()).set(fModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                getPictureList();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                showLoader.dismissDialog();
                                showLoader.PresentToast(e.getMessage());
                            }
                        });
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showLoader.dismissDialog();
                showLoader.PresentToast(e.getMessage());
            }
        });
    }

    private void getPictureList() {
        firebaseFirestore.collection("Folder").document(folderModel.getDocId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot documentSnapshot = task.getResult();
                if (documentSnapshot.exists()) {
                    ArrayList<String> imagesList = (ArrayList<String>) documentSnapshot.get("arrayList");
                    if (imagesList!=null) {
                        ImageAdapter imageAdapter = new ImageAdapter(DisplayActivity.this, imagesList);
                        gridView.setAdapter(imageAdapter);
                    } else {
                        showLoader.PresentToast(getString(R.string.noimage));
                    }
                }
                showLoader.dismissDialog();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }
}
