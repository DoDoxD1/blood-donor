package com.techbonkers.blooddonor;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firestore.v1.TargetOrBuilder;

public class DonateFragment extends Fragment{

    Button button;
    TextView textView,donateMain;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View root = inflater.inflate(R.layout.fragment_donate, container, false);

        button = root.findViewById(R.id.button);
        textView = root.findViewById(R.id.donateHead);
        donateMain = root.findViewById(R.id.donateMain);

        final User user = User.getUser();
        if (user.getDonating()){
            button.setText("Hurray!");
            textView.setText("You are an awesome saviour!");
            donateMain.setText("We are glad that you care to donate you precious drops of blood that can save lives!");
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (user.getDonating()){
                    Toast.makeText(getActivity(),"You are already a saviour",Toast.LENGTH_SHORT).show();
                }
                else {
                    startActivity(new Intent(getActivity(), EditProfileActivity.class));
                }
            }
        });


        return root;
    }
}