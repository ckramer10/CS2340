package com.sffilps.waterlocater.controllers;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.vision.text.Text;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sffilps.waterlocater.R;
import com.sffilps.waterlocater.model.Person;
import com.sffilps.waterlocater.model.WaterReport;

import java.util.ArrayList;
import java.util.List;

public class EditProfile extends AppCompatActivity {
    private Spinner spinner;
    private TextView nameView;
    private TextView emailView;
    private TextView addressView;
    private Button submit;
    private Button cancelButton;

    private FirebaseAuth mAuth;
    private TextView name;
    FirebaseUser currentUser;
    DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        nameView = (TextView) findViewById(R.id.editName);
        emailView = (TextView) findViewById(R.id.editEmail);
        addressView = (TextView) findViewById(R.id.editAddress);

        submit = (Button) findViewById(R.id.submit_changes);
        cancelButton = (Button) findViewById(R.id.cancel_changes);


        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");




        final String uID = currentUser.getUid();
        /**
         * method querying database and choosing which role to select
         */
        mDatabase.child(uID).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Person currUser = dataSnapshot.getValue(Person.class);
                        nameView.setText(currUser.name);
                        emailView.setText(currUser.email);
                        addressView.setText(currUser.homeAddress);
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        /**
         * method submitting to database
         */
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProgressDialog progressDialog = new ProgressDialog(EditProfile.this);
                progressDialog.setMessage("Submitting Changes...");
                progressDialog.show();
                String uID = currentUser.getUid();
                Person newUser = new Person(nameView.getText().toString(), emailView.getText().toString(),
                        spinner.getItemAtPosition(spinner.getSelectedItemPosition()).toString(),
                        uID, addressView.getText().toString());
                DatabaseReference currentUserDB = mDatabase.child(uID);
                currentUserDB.setValue(newUser);

                currentUser.updateEmail(emailView.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {

                                }
                            }
                        });
                Context context = v.getContext();
                Intent intent = new Intent(context, Settings.class);
                context.startActivity(intent);
                progressDialog.dismiss();
                Toast.makeText(EditProfile.this, "Profile has been updated",Toast.LENGTH_SHORT).show();

            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              onBackPressed();
            }
        });
    }

    /**
     * makes back button direct to home screen worker
     */
    public void onBackPressed() {
        Context context = getApplicationContext();
        Intent intent = new Intent(context, Settings.class);
        context.startActivity(intent);
    }
}
