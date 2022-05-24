package com.techbonkers.blooddonor;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class AdapterDonors extends RecyclerView.Adapter<AdapterDonors.AdapterDonorsViewHolder> {

    Handler handler = new Handler();

    class exRunn implements Runnable{
        String urlS;
        AdapterDonorsViewHolder holder;
        Bitmap bm = null;

        exRunn(String urlS, AdapterDonorsViewHolder holder){
            this.urlS = urlS;
            this.holder = holder;
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
//                Toast.makeText(MainActivity.this,"Error!",Toast.LENGTH_SHORT).show();
            }
            handler.post(new Runnable() {
                @Override
                public void run() {
                    holder.donorImage.setImageBitmap(bm);
//                    progressBar.setVisibility(View.GONE);
                }
            });
        }
    }

    private ArrayList<User> data;
    private OnItemClickListener mListenter;

    public interface OnItemClickListener{
        void OnClick(int position);
    }

    public void setOnClickListenter(OnItemClickListener listenter){
        mListenter = listenter;
    }

    public AdapterDonors(ArrayList<User> data){
        this.data =  data;
    }

    @NonNull
    @Override
    public AdapterDonorsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.donor_list_item,parent,false);
        return new AdapterDonorsViewHolder(view,mListenter);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterDonorsViewHolder holder, int position) {

        User user = data.get(position);
        String name = user.getFullName();
        holder.donorName.setText(name);

        String address = user.getUserAddress();
        holder.donorAddress.setText(address);

        String photoUri = user.getPhotoUri();
        if (photoUri!=null|| TextUtils.isEmpty(photoUri)){
            exRunn loadImage = new exRunn(photoUri,holder);
            new Thread(loadImage).start();
        }

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public static class AdapterDonorsViewHolder extends RecyclerView.ViewHolder {
        TextView donorName, donorAddress, donorDis;
        ImageView donorImage;
        CardView donorCard;
        public AdapterDonorsViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            donorName = itemView.findViewById(R.id.name);
            donorAddress = itemView.findViewById(R.id.address);
            donorDis = itemView.findViewById(R.id.distance);
            donorImage = itemView.findViewById(R.id.image);
            donorCard = itemView.findViewById(R.id.card);

            donorCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.OnClick(position);
                        }
                    }
                }
            });
        }
    }

}
