package com.mediaplayer;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.mediaplayer.Adapter.FolderAdapter;
import com.mediaplayer.Loader.ShowLoader;
import com.mediaplayer.LocalStorage.SaveLocalData;
import com.mediaplayer.Model.FolderModel;
import com.mediaplayer.Model.UserModel;

import java.util.ArrayList;
import java.util.List;

public class FolderActivity extends AppCompatActivity {
    FirebaseFirestore firebaseFirestore;
    SaveLocalData saveLocalData;
    ShowLoader showLoader;
    String userDataString;
    UserModel userModel;
    ArrayList<FolderModel> folderModelArrayList = new ArrayList<>();
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder);
        listView = findViewById(R.id.listView);
        firebaseFirestore = FirebaseFirestore.getInstance();
        saveLocalData = new SaveLocalData(FolderActivity.this);
        showLoader = new ShowLoader(FolderActivity.this);
        userDataString = saveLocalData.getValue("USER_INFO");
        Gson gson = new Gson();
        userModel = gson.fromJson(userDataString, UserModel.class);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddItemDialog(FolderActivity.this);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        showLoader.showProgressDialog();
        getFoldersList();
    }

    private void getFoldersList() {
        folderModelArrayList.clear();
        firebaseFirestore.collection("Folder").whereEqualTo("email", userModel.getEmail()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                showLoader.dismissDialog();
                List<DocumentChange> documents = queryDocumentSnapshots.getDocumentChanges();
                for (int i = 0; i < documents.size(); i++) {
                    FolderModel folderModel = new FolderModel();
                    folderModel = documents.get(i).getDocument().toObject(FolderModel.class);
                    folderModelArrayList.add(folderModel);
                }

                FolderAdapter folderAdapter = new FolderAdapter(FolderActivity.this, folderModelArrayList);
                listView.setAdapter(folderAdapter);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showLoader.dismissDialog();
            }
        });
    }

    private void showAddItemDialog(Context c) {
        final EditText taskEditText = new EditText(c);
        AlertDialog dialog = new AlertDialog.Builder(c)
                .setTitle("Add a new folder")
                .setView(taskEditText)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String folderName = String.valueOf(taskEditText.getText());
                        String docId = firebaseFirestore.collection("Folder").document().getId();
                        FolderModel folderModel = new FolderModel(docId,folderName, userModel.getEmail(),null);
                        showLoader.showProgressDialog();
                        firebaseFirestore.collection("Folder").document(docId).set(folderModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                getFoldersList();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                showLoader.dismissDialog();
                                showLoader.PresentToast(e.getMessage());
                            }
                        });
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();
    }
}
