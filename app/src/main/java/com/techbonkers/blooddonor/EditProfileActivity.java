package com.techbonkers.blooddonor;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public class EditProfileActivity extends AppCompatActivity {

    TextView userName,userMail;
    EditText userAddress,userPhone,userBlood;
    Button saveButton;
    Toolbar toolbar;
    Switch donatingBlood;
    ProgressBar progressBar;
    ImageView imageView;

    FirebaseFirestore db;
    CollectionReference userRef;
    FirebaseAuth mAuth;
    FirebaseUser user;

    String uID;
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        refrences
        userName = findViewById(R.id.username);
        userMail = findViewById(R.id.usermail);
        saveButton = findViewById(R.id.saveButton);
        userBlood = findViewById(R.id.bloodType);
        userPhone = findViewById(R.id.phoneNumber);
        userAddress = findViewById(R.id.address);
        donatingBlood = findViewById(R.id.switch1);
        progressBar = findViewById(R.id.progressBar);
        imageView = findViewById(R.id.profileImage);

        db = FirebaseFirestore.getInstance();
        userRef = db.collection("Users");
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        uID = user.getUid();

        if(user.getPhotoUrl()!=null){
            Glide.with(this)
                    .load(user.getPhotoUrl()).into(imageView);
        }

        LoadUserData();
        Log.i("aunu", "onCreate: "+userRef + uID);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(EditProfileActivity.this)
                        .setIcon(R.drawable.save)
                        .setTitle("Save Details")
                        .setMessage("Do you want to save new details!")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                progressBar.setVisibility(View.VISIBLE);
                                checkForChanges();
                            }
                        }).setNegativeButton("No",null).show();
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (intent.resolveActivity(getPackageManager())!= null){
                    startActivityForResult(intent,1001);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==1001){
            switch (resultCode){
                case RESULT_OK:
                    bitmap = (Bitmap) data.getExtras().get("data");
                    imageView.setImageBitmap(bitmap);
            }
        }
    }

    private void uploadProfileToDB(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        String uID =FirebaseAuth.getInstance().getCurrentUser().getUid();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
        final StorageReference reference = FirebaseStorage.getInstance().getReference().child("profileImages").child(uID+".jpeg");
        reference.putBytes(baos.toByteArray()).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        User CurrentUser = User.getUser();
                        CurrentUser.setPhotoUri(uri.toString());
                        userRef.document(user.getUid()).set(CurrentUser);
                        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder().setPhotoUri(uri).build();
                        user.updateProfile(request).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                            }
                        });
                    }
                });
            }
        });
    }

    private void LoadUserData() {
                User user = User.getUser();
                userAddress.setText(user.getUserAddress());
                userBlood.setText(user.getUserBloodType());
                userPhone.setText(user.getUserPhone());
                userName.setText(user.getFullName());
                userMail.setText(user.getUserMail());
                donatingBlood.setChecked(user.getDonating());
    }

    private void checkForChanges() {
        String address = userAddress.getText().toString().trim(), phone = userPhone.getText().toString().trim(), blood = userBlood.getText().toString().trim();
        Boolean isDonating = donatingBlood.isChecked();
            saveChangesToDB(address,phone,blood,isDonating);
            if (bitmap!=null) {
                uploadProfileToDB(bitmap);
            }
        }

        private void savePhoneToDB(final String phone) {
            userRef.document(uID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);
                user.setUserPhone(phone);
                userRef.document(uID).set(user);
                startActivity(new Intent(getApplicationContext(),SplashActivity.class));
                finishAffinity();
            }
        });
    }

    private void saveBloodToDB(final String blood) {
        userRef.document(uID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);
                user.setUserBloodType(blood);
                userRef.document(uID).set(user);
                startActivity(new Intent(getApplicationContext(),SplashActivity.class));
                finishAffinity();
            }
        });
    }

    private void saveAddressToDB(final String address) {
        userRef.document(uID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);
                user.setUserAddress(address);
                userRef.document(uID).set(user);
                startActivity(new Intent(getApplicationContext(),SplashActivity.class));
                finishAffinity();
            }
        });
    }

    private void saveChangesToDB(final String address, final String phone, final String blood, final  Boolean isDonating) {
                User user = User.getUser();
                user.setUserAddress(address);
                user.setUserBloodType(blood);
                user.setUserPhone(phone);
                user.setDonating(isDonating);
                userRef.document(uID).set(user);
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(),"Changes saved if any",Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(),SplashActivity.class));
                finishAffinity();
    }
}