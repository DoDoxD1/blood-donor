package com.techbonkers.blooddonor;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileFragment extends Fragment {

    TextView userName,userPhone,userMail,userBlood,userAddress;
    ImageView editProfileButton, profile;
    Button logoutButton;

    FirebaseAuth mAuth;
    FirebaseUser user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_profile, container, false);

//        refrences
        editProfileButton = root.findViewById(R.id.editProfileButton);
        logoutButton = root.findViewById(R.id.logoutButton);
        userName = root.findViewById(R.id.username);
        userMail = root.findViewById(R.id.usermail);
        userBlood = root.findViewById(R.id.bloodType);
        userAddress = root.findViewById(R.id.address);
        userPhone = root.findViewById(R.id.phoneNumber);
        profile = root.findViewById(R.id.profile    );

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        if(user.getPhotoUrl()!=null){
            Glide.with(this)
                    .load(user.getPhotoUrl()).into(profile);
        }

        setUserDetails();

        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(),EditProfileActivity.class));
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(getActivity())
                        .setIcon(R.drawable.logout)
                        .setTitle("Logout")
                        .setMessage("Do you want to logout!")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                logoutUser();
                            }
                        }).setNegativeButton("No",null).show();
            }
        });

        return root;
    }

    private void setUserDetails() {
        
        User user = User.getUser();

        if (user!=null) {
            userName.setText(user.getFullName());
            userMail.setText(user.getUserMail());
            userPhone.setText(user.getUserPhone());
            userAddress.setText(user.getUserAddress());
            userBlood.setText(user.getUserBloodType());
        }
        else {
            FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    User user = documentSnapshot.toObject(User.class);
                    userName.setText(user.getFullName());
                    userMail.setText(user.getUserMail());
                    userPhone.setText(user.getUserPhone());
                    userAddress.setText(user.getUserAddress());
                    userBlood.setText(user.getUserBloodType());
                }
            })  ;
        }

    }

    private void logoutUser() {
        mAuth.signOut();
        User.user = null;
        startActivity(new Intent(getActivity(),LogInActivity.class));
        getActivity().finishAffinity();
    }
}