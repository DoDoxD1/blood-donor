package com.techbonkers.blooddonor;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    ImageButton search;
    RecyclerView donorListView;
    ImageView notificationButton;

    FirebaseFirestore db;
    CollectionReference userRef;
    FirebaseUser mUser;

    AdapterDonors adapterDonors;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        search = root.findViewById(R.id.searchButton);
        search.bringToFront();

//        refrences

        donorListView = root.findViewById(R.id.recyclerView);
        notificationButton = root.findViewById(R.id.notification);
        mUser = FirebaseAuth.getInstance().getCurrentUser();

        db = FirebaseFirestore.getInstance();
        userRef = db.collection("Users");

        ArrayList<User> users = SplashActivity.getDonors();
        setDataToView(users);

        adapterDonors.setOnClickListenter(new AdapterDonors.OnItemClickListener() {
            @Override
            public void OnClick(int position) {
                Intent intent = new Intent(getActivity(),DonorProfileActivity.class);
                intent.putExtra("ID",SplashActivity.getDonorIDs().get(position));
                startActivity(intent);
            }
        });

        notificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(),NotificationsActivity.class));
            }
        });

        return root;
    }



    private void setDataToView(ArrayList<User> names) {
        donorListView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapterDonors = new AdapterDonors(names);
        donorListView.setAdapter(adapterDonors);
        adapterDonors.notifyDataSetChanged();
    }
}