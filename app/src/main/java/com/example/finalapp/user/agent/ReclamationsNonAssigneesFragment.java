package com.example.finalapp.user.agent;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.finalapp.LogInActivity;
import com.example.finalapp.R;
import com.example.finalapp.Reclamation.Reclamation;
import com.example.finalapp.Reclamation.ReclamationAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ReclamationsNonAssigneesFragment extends Fragment {

    private DatabaseReference myRef;
    private FirebaseAuth mAuth;
    private Context myContext;

    private ListView listView;
    private ArrayList<Reclamation> myRecList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reclamations_non_assignees, container, false);

        myRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        listView = view.findViewById(R.id.listView);

        myRecList = new ArrayList<>();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startSassingerAUneReclamationFragment(myRecList.get(position).recId);
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() == null) {
            Intent intent = new Intent(myContext, LogInActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }

        Query query = myRef.child("claim").orderByChild("agentId").equalTo(null);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                myRecList.clear();
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    Reclamation reclamation = ds.getValue(Reclamation.class);
                    myRecList.add(0, reclamation);
                }
                ReclamationAdapter adapter = new ReclamationAdapter(myContext, R.layout.row_view, myRecList);
                listView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void startSassingerAUneReclamationFragment(String recId) {
        SassingerAUneReclamationFragment.recId = recId;
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SassingerAUneReclamationFragment(), "MY_FRAGMENT").commit();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        myContext = context;
    }
}
