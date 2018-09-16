package com.cecs453.trainerx;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.cecs453.trainerx.model.Exercise;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class AddCustomExerciseActivity extends AppCompatActivity {
    public static final String TAG = "AddCustomWorkout";
    private static final int SYSTEM_ALERT_WINDOW_PERMISSION = 2084;

    private FirebaseFirestore db;
    private ArrayList<String> types;
    private Spinner spinnerview;
    private EditText editTextView;
    private Exercise exercise;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_custom_exercise);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Add Custom Exercise");
        toolbar.setTitleTextColor(Color.WHITE);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        types = getIntent().getStringArrayListExtra("types");
        spinnerview = findViewById(R.id.customworkoutdropdown);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, types);
        spinnerview.setAdapter(adapter);

        exercise = new Exercise();
        SharedPreferences sharedPreferences = getSharedPreferences("TrainerXPreferences", 0);
        String id = sharedPreferences.getString("id", "id");
        exercise.setCreator(id);

        editTextView = findViewById(R.id.AddCustomWorkoutName);

        Button save = findViewById(R.id.CustomeSave);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = editTextView.getText().toString();
                if (TextUtils.isEmpty(name)) {
                    Toast.makeText(AddCustomExerciseActivity.this, "Exercise name cannot be blank.", Toast.LENGTH_SHORT).show();
                } else {
                    exercise.setName(editTextView.getText().toString());
                    exercise.setType(types.get(spinnerview.getSelectedItemPosition()));
                    exercise.setIsCustom(true);
                    writeData();
                }
            }
        });

        Button cancel = findViewById(R.id.CustomCancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_custom_workout, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        // handle arrow click here
        if (id == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        if (id == R.id.action_stopwatch) {
            if (Settings.canDrawOverlays(this)) {
                // continue here - permission was granted

                Log.d(TAG, "permission floating");
                startService(new Intent(AddCustomExerciseActivity.this, FloatingViewService.class));
            } else {

                AlertDialog alertDialog = new AlertDialog.Builder(AddCustomExerciseActivity.this).create();
                alertDialog.setTitle("Permission to draw over other apps required!");
                alertDialog.setMessage("*To show the stopwatch, System Overlay permission has to be granted.\n\n" +
                        "*This permission has to be granted only once.\n\n" +
                        "*Please tap on the icon to see the stopwatch after granting the permission.");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "GRANT",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                        Uri.parse("package:" + getPackageName()));
                                startActivityForResult(intent, SYSTEM_ALERT_WINDOW_PERMISSION);
                            }
                        });
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "DENY",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.show();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void writeData() {
        db = FirebaseFirestore.getInstance();
        db.collection("exercises")
                .add(exercise)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddCustomExerciseActivity.this, "Error writing data. Please try again.", Toast.LENGTH_SHORT).show();
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }

    @Override
    protected void onPause() {
        super.onPause();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editTextView.getWindowToken(), 0);
    }
}
