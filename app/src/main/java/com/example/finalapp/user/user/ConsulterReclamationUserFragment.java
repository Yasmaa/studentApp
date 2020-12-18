package com.example.finalapp.user.user;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.finalapp.LogInActivity;
import com.example.finalapp.R;
import com.example.finalapp.Reclamation.Reclamation;
import com.example.finalapp.Reclamation.ReclamationAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ConsulterReclamationUserFragment extends Fragment {

    public static String recId;
    private DatabaseReference myRef;
    private FirebaseAuth mAuth;
    private TextView textView1;
    private TextView textView2;
    private TextView textView3;
    private TextView textView4;
    private ImageView imageView;
    private TextView textView5;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_consulter_reclamation_user, container, false);
        myRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        textView1 = view.findViewById(R.id.textView1);
        textView2 = view.findViewById(R.id.textView2);
        textView3 = view.findViewById(R.id.textView3);
        textView4 = view.findViewById(R.id.textView4);
        textView5 = view.findViewById(R.id.textView5);
        imageView = view.findViewById(R.id.imageView);

        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() == null) {
            Intent intent = new Intent(getActivity(), LogInActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }

        myRef.child("claim").child(recId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Reclamation reclamation = dataSnapshot.getValue(Reclamation.class);
                textView1.setText(reclamation.date);
                textView2.setText(reclamation.categorie);
                textView3.setText(reclamation.type);
                textView4.setText(reclamation.etat);
                textView5.setText(reclamation.details);
                textView4.setTextColor(Color.parseColor(ReclamationAdapter.getStateColor((reclamation.etat))));

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        myRef.child("image").child(recId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String image = dataSnapshot.getValue(String.class);
                Bitmap imageBitmap = decodeFromFirebaseBase64(image);
                imageView.setImageBitmap(imageBitmap);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public static Bitmap decodeFromFirebaseBase64(String image) {
        byte[] decodedByteArray = android.util.Base64.decode(image, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.length);
    }
}
