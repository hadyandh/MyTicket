package com.hunhun.myticket;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hunhun.myticket.interfaces.FirestorageCallback;
import com.hunhun.myticket.model.User;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

import es.dmoral.toasty.Toasty;

import static com.hunhun.myticket.RegisterOneAct.USERNAME_KEY;
import static com.hunhun.myticket.RegisterOneAct.PASS_KEY;
import static com.hunhun.myticket.RegisterOneAct.EMAIL_KEY;

public class RegisterTwoAct extends AppCompatActivity {

    private LinearLayout btnBack;
    private Button btnContinue, btnAddPhoto;
    private ImageView photoUser;
    private EditText edtName, edtBio;

    private Uri photoLocation;
    private Integer photoCode = 100;
    private String name, bio, username, email, password, uriPhoto;

    private DatabaseReference reference;
    private StorageReference storage;
    private UploadTask uploadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_two);

        getUserDataLocal();

        reference = FirebaseDatabase.getInstance().getReference("Users").child(username);
        storage = FirebaseStorage.getInstance().getReference("Photousers").child(username);

        photoUser = findViewById(R.id.photo_user);
        edtBio = findViewById(R.id.edt_bio);
        edtName = findViewById(R.id.edt_name);
        btnAddPhoto = findViewById(R.id.btn_add_photo);
        btnContinue = findViewById(R.id.btnContinue);
        btnBack = findViewById(R.id.btnBack);

        btnAddPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findPhoto();
            }
        });

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = edtName.getText().toString();
                bio = edtBio.getText().toString();

                if (TextUtils.isEmpty(name)){
                    Toasty.error(RegisterTwoAct.this, "Please, enter your name", Toast.LENGTH_SHORT).show();
                }
                else if (TextUtils.isEmpty(bio)) {
                    Toasty.error(RegisterTwoAct.this, "Please, enter your bio", Toast.LENGTH_SHORT).show();
                } else {

                    btnContinue.setEnabled(false);
                    btnContinue.setText("Loading ...");

                    //memanggil callback yang telah ada data url photo
                    getUploadImage(new FirestorageCallback() {
                        @Override
                        public void getUrlPhoto(String url) {

                            //store data in firebase db realtime
                            User user = new User(name, username, password, email, bio, url, 100);
                            reference.setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    SharedPreferences sp = getSharedPreferences(getString(R.string.USERNAME_FILE), MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sp.edit();
                                    editor.putString(getString(R.string.USERNAME_KEY), username);
                                    editor.apply();

                                    startActivity(new Intent(RegisterTwoAct.this, SuccessRegisterAct.class));
                                }
                            });

                        }
                    });

                }
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == photoCode && resultCode == RESULT_OK && data != null && data.getData() != null){
            photoLocation = data.getData();
            Picasso.get().load(photoLocation).centerCrop().fit().into(photoUser);
        }
    }

    private void findPhoto(){
        Intent pic = new Intent();
        pic.setType("image/*");
        pic.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(pic, photoCode);
    }

    public void getUserDataLocal(){
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.USERNAME_FILE), MODE_PRIVATE);
        username = sharedPreferences.getString(USERNAME_KEY, null);
        email = sharedPreferences.getString(EMAIL_KEY,  null);
        password = sharedPreferences.getString(PASS_KEY, null);
    }

    //menggunakan callback untuk mendapatkan data Asynchronous Firebase
    public void getUploadImage(final FirestorageCallback firestorageCallback){

        //Upload image in firebase storage
        final StorageReference storagePhoto = storage.child("photo_profile.jpg");

        //check if user upload image or not
        if (photoLocation == null){
            //if user not upload image then app will upload default image
            //how to get data from imageView
            photoUser.setDrawingCacheEnabled(true);
            photoUser.buildDrawingCache();
            Bitmap bitmap = ((BitmapDrawable) photoUser.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            uploadTask = storagePhoto.putBytes(data);
        } else {
            uploadTask = storagePhoto.putFile(photoLocation);
        }

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
                    uriPhoto = downloadUri.toString();

                    firestorageCallback.getUrlPhoto(uriPhoto);
                } else {
                    Toasty.error(RegisterTwoAct.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                    Log.d("Error Upload", task.getException().toString());
                }
            }
        });
    }
}