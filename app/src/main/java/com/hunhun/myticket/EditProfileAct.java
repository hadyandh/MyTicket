package com.hunhun.myticket;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hunhun.myticket.interfaces.FirestorageCallback;
import com.hunhun.myticket.model.User;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;

import static com.hunhun.myticket.RegisterOneAct.USERNAME_KEY;

public class EditProfileAct extends AppCompatActivity {

    private ImageView imgPhotoUser;
    private EditText edtName, edtBio, edtEmail, edtPassword, edtUsername;
    private Button btnSave, btnAddPhoto;
    private LinearLayout btnBack;

    private DatabaseReference reference;

    private String name, bio, email, username, password, urlPhoto;
    private Uri photoLocation;
    private Integer photoCode = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        imgPhotoUser = findViewById(R.id.photoUser);
        edtName = findViewById(R.id.edt_name);
        edtBio = findViewById(R.id.edt_bio);
        edtEmail = findViewById(R.id.edt_email);
        edtPassword = findViewById(R.id.edt_password);
        edtUsername = findViewById(R.id.edt_username);
        btnSave = findViewById(R.id.btn_save);
        btnAddPhoto = findViewById(R.id.btn_add_photo);
        btnBack = findViewById(R.id.btnBack);

        reference = FirebaseDatabase.getInstance().getReference("Users").child(getUsername());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                urlPhoto = snapshot.child("url_photo").getValue(String.class);
                edtBio.setText(snapshot.child("bio").getValue(String.class));
                edtName.setText(snapshot.child("name").getValue(String.class));
                edtEmail.setText(snapshot.child("email").getValue(String.class));
                edtPassword.setText(snapshot.child("password").getValue(String.class));
                edtUsername.setText(snapshot.child("username").getValue(String.class));

                Picasso.get().load(urlPhoto).centerCrop().fit().into(imgPhotoUser);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onLoading(true);

                name = edtName.getText().toString();
                bio = edtBio.getText().toString();
                email = edtEmail.getText().toString();
                password = edtPassword.getText().toString();
                username = edtUsername.getText().toString();

                if (photoLocation != null) {

                    getUploadImage(new FirestorageCallback() {
                        @Override
                        public void getUrlPhoto(String url) {
                            updateDataUser(name, username, password, email, bio, url);
                        }
                    });

                } else {
                    updateDataUser(name, username, password, email, bio, urlPhoto);
                }
            }
        });

        btnAddPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findPhoto();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    public String getUsername(){
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.USERNAME_FILE), MODE_PRIVATE);
        return sharedPreferences.getString(USERNAME_KEY, null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == photoCode && resultCode == RESULT_OK && data != null && data.getData() != null){
            photoLocation = data.getData();
            Picasso.get().load(photoLocation).centerCrop().fit().into(imgPhotoUser);
        }
    }

    private void findPhoto(){
        Intent pic = new Intent();
        pic.setType("image/*");
        pic.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(pic, photoCode);
    }

    //menggunakan callback untuk mendapatkan data Asynchronous Firebase
    private void getUploadImage(final FirestorageCallback firestorageCallback){

        //Upload image in firebase storage
        StorageReference storage = FirebaseStorage.getInstance().getReference("Photousers").child(getUsername());
        final StorageReference storagePhoto = storage.child("photo_profile.jpg");

        UploadTask uploadTask = storagePhoto.putFile(photoLocation);
        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return storagePhoto.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    urlPhoto = downloadUri.toString();

                    firestorageCallback.getUrlPhoto(urlPhoto);
                } else {
                    Toasty.error(EditProfileAct.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                    Log.d("Error Upload", task.getException().toString());
                }
            }
        });
    }

    private void updateDataUser(String name, String username, String password, String email, String bio, String url){
        User user = new User(name, username, password, email, bio, url);
        Map<String, Object> userValues = user.toMapUpdate();

        reference.updateChildren(userValues)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toasty.success(EditProfileAct.this, "Data is updated", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        onLoading(false);
                        Toasty.error(EditProfileAct.this, "Sorry the data failed to update", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void onLoading(Boolean loading){
        if (loading){
            btnSave.setEnabled(false);
            btnSave.setText("Loading...");
        } else {
            btnSave.setEnabled(true);
            btnSave.setText("SAVE");
        }
    }
}