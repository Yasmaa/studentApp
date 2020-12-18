package com.example.finalapp.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.finalapp.LogInActivity;
import com.example.finalapp.user.agent.AgentFragment;
import com.example.finalapp.R;
import com.example.finalapp.user.agent.ConsulterReclamationAgentFragment;
import com.example.finalapp.user.agent.ReclamationsNonAssigneesFragment;
import com.example.finalapp.user.agent.SassingerAUneReclamationFragment;
import com.example.finalapp.user.user.AjouterReclamationFragment;
import com.example.finalapp.user.user.ConsulterReclamationUserFragment;
import com.example.finalapp.user.user.UserFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

public class PrincipalActivity extends AppCompatActivity {
    private DrawerLayout drawer;
    private DatabaseReference myRef;
    private FirebaseAuth mAuth;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        myRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        Toolbar toolbar = findViewById(R.id.material_toolbar);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView email = headerView.findViewById(R.id.email);
        email.setText(mAuth.getCurrentUser().getEmail());
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId()==R.id.home){
                    drawer.closeDrawer(GravityCompat.START);
                    startApropiateFragment();
                }
                if(item.getItemId()==R.id.change_password){
                    drawer.closeDrawer(GravityCompat.START);
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ChangePasswordFragment(), "MY_FRAGMENT").commit();
                }
                if(item.getItemId()==R.id.log_out){
                    deleteToken();
                    mAuth.signOut();
                    Intent intent = new Intent(PrincipalActivity.this, LogInActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
                return false;
            }
        });


        if (getIntent().getExtras() != null && !getIntent().getExtras().get("type").toString().isEmpty()) {
            if (getIntent().getExtras().get("type").toString().equals("1")) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ConsulterReclamationUserFragment(), "MY_FRAGMENT").commit();
                return;
            }
            if (getIntent().getExtras().get("type").toString().equals("2")) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ConsulterReclamationAgentFragment(), "MY_FRAGMENT").commit();
                return;
            }
        }

        startApropiateFragment();
        saveToken();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Fragment f = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            if(f instanceof ConsulterReclamationUserFragment
            || f instanceof AjouterReclamationFragment) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new UserFragment(), "MY_FRAGMENT").commit();
                return;
            }
            if(f instanceof ConsulterReclamationAgentFragment
            || f instanceof ReclamationsNonAssigneesFragment) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AgentFragment(), "MY_FRAGMENT").commit();
                return;
            }
            if(f instanceof SassingerAUneReclamationFragment) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ReclamationsNonAssigneesFragment(), "MY_FRAGMENT").commit();
                return;
            }
            if(f instanceof ChangePasswordFragment) {
                startApropiateFragment();
                return;
            }
            super.onBackPressed();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() == null) {
            Intent intent = new Intent(this, LogInActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }



    private void startApropiateFragment() {
        uid = mAuth.getCurrentUser().getUid();
        myRef.child("user").child(uid).child("type").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String type = dataSnapshot.getValue(String.class);
                if (type.equals("1")) {
                    startUserFragment();
                }
                if (type.equals("2")) {
                    startAgentFragment();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    private void saveToken() {
        uid = mAuth.getCurrentUser().getUid();
        myRef.child("user").child(uid).child("type").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String type = dataSnapshot.getValue(String.class);
                if (type.equals("1")) {
                    updateTokenUser();
                }
                if (type.equals("2")) {
                    updateTokenAgent();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    private void deleteToken() {
        uid = mAuth.getCurrentUser().getUid();
        myRef.child("user").child(uid).child("type").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String type = dataSnapshot.getValue(String.class);
                if (type.equals("1")) myRef.child("token").child("user").child(uid).setValue(null);;
                if (type.equals("2")) myRef.child("token").child("agent").child(uid).setValue(null);;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }



    private void startUserFragment() {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new UserFragment(), "MY_FRAGMENT").commit();
    }

    private void startAgentFragment() {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AgentFragment(), "MY_FRAGMENT").commit();
    }

    private void updateTokenUser(){
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if(task.isSuccessful()) {
                            String uid = mAuth.getCurrentUser().getUid();
                            String token = task.getResult().getToken();
                            myRef.child("token").child("user").child(uid).setValue(token)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                //Toast.makeText(PrincipalActivity.this, "Token saved user", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                        } else  {

                        }
                    }
                });
    }

    private void updateTokenAgent(){
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if(task.isSuccessful()) {
                            String uid = mAuth.getCurrentUser().getUid();
                            String token = task.getResult().getToken();
                            myRef.child("token").child("agent").child(uid).setValue(token)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                //Toast.makeText(PrincipalActivity.this, "Token saved agent", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                        } else  {

                        }
                    }
                });
    }
}
