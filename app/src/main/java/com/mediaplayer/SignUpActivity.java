package com.mediaplayer;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mediaplayer.Loader.ShowLoader;
import com.mediaplayer.Model.UserModel;

public class SignUpActivity extends AppCompatActivity {
    private static final int REQUESCODE = 100;
    ImageView imageView;
    EditText nameEdit, mobileEdit, emailEdit, passwordEdit, cPasswordEdit;
    String pictureUrl, nameString, mobileString, emailString, passwordString, cPasswordString;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    ShowLoader showLoader;
    private int PReqCode = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        showLoader = new ShowLoader(SignUpActivity.this);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        imageView=findViewById(R.id.profileImageView);
        nameEdit = findViewById(R.id.name);
        mobileEdit = findViewById(R.id.phone);
        emailEdit = findViewById(R.id.email);
        passwordEdit = findViewById(R.id.password);
        cPasswordEdit = findViewById(R.id.cPassword);
        checkAndRequestForPermission();
    }

    public void loginImplementation(View view){
        startActivity(new Intent(SignUpActivity.this,SignInActivity.class));
        finish();
    }
    public void openImageImplementation(View view){
        openGallery();
    }

    public void signUpImplementation(View view) {
        nameString = nameEdit.getText().toString();
        mobileString = mobileEdit.getText().toString();
        emailString = emailEdit.getText().toString();
        passwordString = passwordEdit.getText().toString();
        cPasswordString = cPasswordEdit.getText().toString();

        if (nameString.isEmpty() || mobileString.isEmpty() || emailString.isEmpty() || passwordString.isEmpty() || cPasswordString.isEmpty()) {
            showLoader.PresentToast(getString(R.string.empty));
        } else {
            if(passwordString.equalsIgnoreCase(cPasswordString)){
                showLoader.showProgressDialog();
                firebaseAuth.createUserWithEmailAndPassword(emailString,passwordString).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        UserModel userModel = new UserModel(pictureUrl, nameString, mobileString, emailString);
                        firebaseFirestore.collection("Users").document(authResult.getUser().getUid()).set(userModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                firebaseAuth.getCurrentUser().sendEmailVerification();
                                showLoader.PresentToast("User register successfully,Verification link has been sent to email.");
                                startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                showLoader.dismissDialog();
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
            } else{
                showLoader.PresentToast(getString(R.string.passwordmatch));
            }
        }
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, REQUESCODE);
    }

    private void checkAndRequestForPermission() {
        if (ContextCompat.checkSelfPermission(SignUpActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(SignUpActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(SignUpActivity.this, "Please accept for required permission", Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(SignUpActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PReqCode);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            Uri pickedImgUri = data.getData();
            imageView.setImageURI(pickedImgUri);
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
                        if (pictureUrl != null) {
                            Glide.with(SignUpActivity.this)
                                    .load(pictureUrl).apply(RequestOptions.circleCropTransform())
                                    .into(imageView);
                        } else {
                            imageView.setBackgroundResource(R.drawable.at);
                        }
                        showLoader.dismissDialog();
                        showLoader.PresentToast("Picture uploaded successfully");
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
}
