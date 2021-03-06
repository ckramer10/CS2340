package com.sffilps.waterlocater.controllers;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sffilps.waterlocater.R;

public class Settings extends AppCompatActivity {

    private Button signoutButton;
    private Button editProf;
    private Button resetPass;
    private FirebaseAuth mAuth;
    FirebaseUser currentUser;
    DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        signoutButton = (Button) findViewById(R.id.signout_button);
        editProf = (Button) findViewById(R.id.editprofile);
        resetPass = (Button) findViewById(R.id.resetpassword);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        signoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.getInstance().signOut();
                Context context = v.getContext();
                Intent intent = new Intent(context, SplashScreen.class);
                context.startActivity(intent);
                Toast.makeText(Settings.this, "Signed Out.", Toast.LENGTH_LONG).show();
            }
        });

        editProf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, EditProfile.class);
                context.startActivity(intent);
            }
        });

        resetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = currentUser.getEmail();
                mAuth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(Settings.this, "Password reset instructions sent to: " + email, Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(Settings.this, "Email could not be sent.", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        });

    }

    /**
     * makes the back button go to correct role homepage
     */
    public void onBackPressed(){
        final String uID = currentUser.getUid();
        mDatabase.child(uID).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user value
                        String role = ((String) dataSnapshot.child("role").getValue());
                        Intent i = new Intent(Settings.this,HomeScreen.class);
                        Intent i2 = new Intent(Settings.this,HomeScreenWorker.class);

                        if (role.equals("Administrator") || role.equals("Manager") || role.equals("Worker")) {
                            startActivity(i2);
                        } else {
                            startActivity(i);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }
}
