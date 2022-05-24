package com.techbonkers.blooddonor;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DonorProfileActivity extends AppCompatActivity {

    Handler handler = new Handler();

    class exRunn implements Runnable{
        String urlS;
        Bitmap bm = null;

        exRunn(String urlS){
            this.urlS = urlS;
        }

        @Override
        public void run() {

            try {
                URL url = new URL(urlS);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.connect();
                InputStream inputStream = httpURLConnection.getInputStream();
                bm = BitmapFactory.decodeStream(inputStream);
            }catch (Exception e){
                e.printStackTrace();
            }
            handler.post(new Runnable() {
                @Override
                public void run() {
                    imageView.setImageBitmap(bm);
                }
            });
        }
    }

    ProgressBar progressBar;
    TextView phone,name,mail,address,bloodType,donationCount,visitCount;
    Button callButton;
    ImageView imageView;
    String uID="";

    FirebaseFirestore db;
    CollectionReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donor_profile);

//        refrences

        progressBar = findViewById(R.id.progressBar);
        phone = findViewById(R.id.phoneNumber);
        name = findViewById(R.id.username);
        mail = findViewById(R.id.usermail);
        address = findViewById(R.id.address);
        bloodType = findViewById(R.id.bloodType);
        imageView = findViewById(R.id.profile);

        db = FirebaseFirestore.getInstance();
        userRef = db.collection("Users");

        uID = getIntent().getExtras().getString("ID");

        LoadRespUserData(uID);

    }

    private void LoadRespUserData(String uID) {
        userRef.document(uID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);
                setDetailsToViews(user);
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void setDetailsToViews(User user) {

        name.setText(user.getFullName());
        mail.setText(user.getUserMail());
        phone.setText(user.getUserPhone());
        address.setText(user.getUserAddress());
        bloodType.setText(user.getUserBloodType());
        if (user.getPhotoUri()!=null) {
            exRunn loadImage = new exRunn(user.getPhotoUri());
            new Thread(loadImage).start();
        }else {
            imageView.setImageResource(R.drawable.arihant);
        }

    }

}