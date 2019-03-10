package com.example.yourtaskapp;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.yourtaskapp.model.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;

public class HomeActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private FloatingActionButton fabBtn;

    // Firebase
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        toolbar = findViewById(R.id.toolbar_home);
        fabBtn = findViewById(R.id.fab_btn);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Your Task App");

        mAuth = FirebaseAuth.getInstance();

        FirebaseUser mUser = mAuth.getCurrentUser();

        String userId = mUser != null ? mUser.getUid() : null;

        mDatabase = FirebaseDatabase.getInstance().getReference().child("TaskNote").child(userId);

        fabBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder myDialog = new AlertDialog.Builder(HomeActivity.this);

                LayoutInflater inflater = LayoutInflater.from(HomeActivity.this);

                View myView = inflater.inflate(R.layout.custom_input_field, null);

                myDialog.setView(myView);

                final AlertDialog dialog = myDialog.create();

                final EditText title = myView.findViewById(R.id.edt_title);
                final EditText note = myView.findViewById(R.id.edt_note);
                Button btnSave = myView.findViewById(R.id.btn_save);

                btnSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String mTitle = title.getText().toString().trim();
                        String mNote = note.getText().toString().trim();

                        if (TextUtils.isEmpty(mTitle)) {
                            title.setError("Required field...");
                            return;
                        }

                        if (TextUtils.isEmpty(mNote)) {
                            note.setError("Required field...");
                            return;
                        }

                        Date date = new Date();
                        Task newTask = new Task(mTitle, mNote, date);

                        mDatabase.push().setValue(newTask);

                        Toast.makeText(getApplicationContext(), "Task insert", Toast.LENGTH_SHORT).show();

                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });
    }
}
