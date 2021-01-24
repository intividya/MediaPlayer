package com.mediaplayer;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.mediaplayer.Loader.ShowLoader;
import com.mediaplayer.LocalStorage.SaveLocalData;
import com.mediaplayer.Model.UserModel;

public class SignInActivity extends AppCompatActivity {
    EditText emailEdit, passwordEdit;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    ShowLoader showLoader;
    SaveLocalData saveLocalData;
    String emailString, passwordString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        emailEdit = findViewById(R.id.email);
        passwordEdit = findViewById(R.id.password);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        showLoader = new ShowLoader(SignInActivity.this);
        saveLocalData = new SaveLocalData(SignInActivity.this);
    }

    public void signInImplementation(View view) {
        emailString = emailEdit.getText().toString();
        passwordString = passwordEdit.getText().toString();
        if (emailString.isEmpty() || passwordString.isEmpty()) {
            showLoader.PresentToast(getString(R.string.epEmpty));
        } else {
            showLoader.showProgressDialog();
            firebaseAuth.signInWithEmailAndPassword(emailString, passwordString).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    showLoader.dismissDialog();
                    if (authResult.getUser().isEmailVerified() == true) {
                        getUser();
                    } else {
                        showLoader.PresentToast(getString(R.string.verify_email));
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    showLoader.dismissDialog();
                    showLoader.PresentToast(e.getMessage());
                }
            });
        }
    }

    public void getUser() {
        firebaseFirestore.collection("Users").document(firebaseAuth.getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                showLoader.dismissDialog();
                UserModel userModel = new UserModel();
                userModel = documentSnapshot.toObject(UserModel.class);
                Gson gson = new Gson();
                String userJson = gson.toJson(userModel);
                saveLocalData.saveValue("USER_INFO", userJson);
                startActivity(new Intent(SignInActivity.this, FolderActivity.class));
                emailEdit.setText("");
                passwordEdit.setText("");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showLoader.dismissDialog();
                showLoader.PresentToast(e.getMessage());
            }
        });
    }

    public void signUpImplementation(View view) {
        startActivity(new Intent(SignInActivity.this, SignUpActivity.class));
    }

    public void sendForgotPasswordImplementation(View view) {
        emailString = emailEdit.getText().toString();
        if (emailString.isEmpty()) {
            showLoader.PresentToast("Please enter email address");
        } else {
            showLoader.showProgressDialog();
            firebaseAuth.sendPasswordResetEmail(emailString).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    showLoader.dismissDialog();
                    showLoader.PresentToast(getString(R.string.forgot_email));
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    showLoader.dismissDialog();
                    showLoader.PresentToast(e.getMessage());
                }
            });
        }
    }
}
