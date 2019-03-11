package com.example.yourtaskapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yourtaskapp.model.Task;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private FloatingActionButton fabBtn;

    // Firebase
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    // Recycler
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        toolbar = findViewById(R.id.toolbar_home);

        // Recicler
        recyclerView = findViewById(R.id.recycler);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        fabBtn = findViewById(R.id.fab_btn);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Your Task App");

        mAuth = FirebaseAuth.getInstance();

        FirebaseUser mUser = mAuth.getCurrentUser();

        String userId = mUser != null ? mUser.getUid() : null;

        if (userId != null) {
            mDatabase = FirebaseDatabase.getInstance().getReference().child("TaskNote").child(userId);
            mDatabase.keepSynced(true);
        }

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

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Task, MyViewHolder> adapter = new FirebaseRecyclerAdapter<Task, MyViewHolder>(
                Task.class,
                R.layout.item_data,
                MyViewHolder.class,
                mDatabase
        ) {
            @Override
            protected void populateViewHolder(MyViewHolder viewHolder, Task model, int position) {
                viewHolder.setTitle(model.getTitle());
                viewHolder.setNote(model.getNote());
                viewHolder.setDate(model.getDate());

                viewHolder.myView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        updateTask();
                    }
                });
            }
        };

        recyclerView.setAdapter(adapter);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        View myView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            myView = itemView;
        }

        public void setTitle(String title) {
            TextView mTitle = myView.findViewById(R.id.title);
            mTitle.setText(title);
        }

        public void setNote(String note) {
            TextView mNote = myView.findViewById(R.id.note);
            mNote.setText(note);
        }

        public void setDate(Date date) {
            TextView mDate = myView.findViewById(R.id.date);
            mDate.setText(new SimpleDateFormat("MMMM dd, yyyy", Locale.US).format(date));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                mAuth.signOut();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void updateTask() {
        AlertDialog.Builder myDialog = new AlertDialog.Builder(HomeActivity.this);
        LayoutInflater inflater = LayoutInflater.from(HomeActivity.this);

        View myView = inflater.inflate(R.layout.update_input_field, null);
        myDialog.setView(myView);

        AlertDialog dialog = myDialog.create();

        dialog.show();

    }
}
