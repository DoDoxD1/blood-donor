package com.techbonkers.blooddonor;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class User {

    private    String fullName;
    private    String userMail;
    private    String userPass;
    private    String userAddress;
    private    String userPhone;
    private    String userBloodType;
    private    String photoUri;
    private    Boolean isDonating;

    static FirebaseAuth mAuth;
    static FirebaseUser mUser;
    static User user;
    static FirebaseFirestore db;
    static DocumentReference userRef;

    public User(){

    }

    public static final void getCurrentUserFromDB(){

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        userRef = db.collection("Users").document(mUser.getUid());

        userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
             user = documentSnapshot.toObject(User.class);
            }
        });

    }

    public String getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri(String photoUri) {
        this.photoUri = photoUri;
    }

    public static User getUser() {
        return user;
    }

    public User(String fullName, String userMail, String userPass) {
        this.fullName = fullName;
        this.userMail = userMail;
        this.userPass = userPass;
    }

    public String getFullName() {
        return fullName;
    }

    public String getUserMail() {
        return userMail;
    }

    public String getUserPass() {
        return userPass;
    }

    public String getUserAddress() {
        return userAddress;
    }

    public void setUserAddress(String userAddress) {
        this.userAddress = userAddress;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getUserBloodType() {
        return userBloodType;
    }

    public void setUserBloodType(String userBloodType) {
        this.userBloodType = userBloodType;
    }

    public Boolean getDonating() {
        return isDonating;
    }

    public void setDonating(Boolean donating) {
        isDonating = donating;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setUserMail(String userMail) {
        this.userMail = userMail;
    }

    public void setUserPass(String userPass) {
        this.userPass = userPass;
    }
}
